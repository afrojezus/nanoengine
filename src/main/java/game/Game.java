package game;

import engine.*;
import engine.graph.*;
import engine.graph.lights.DirectionalLight;
import engine.graph.lights.PointLight;
import engine.graph.weather.Fog;
import engine.item.GameItem;
import engine.item.SkyBox;
import engine.loaders.assimp.StaticMeshesLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Game implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Scene scene;

    private static final float CAMERA_POS_STEP = 0.40f;

    private float angleInc;

    private float lightAngle;

    private boolean firstTime;

    private boolean sceneChanged;

    private Vector3f pointLightPos;

    private Hud hud;

    // Player movement
    private Vector3f vector = new Vector3f(0,0,0);
    private Vector3f rotation = new Vector3f();
    private Vector3f previous = new Vector3f();
    private static float walkSpeed = 0.035f, runSpeed = 0.06f, sprintSpeed = 0.12f;
    private float speed = runSpeed;

    private boolean moveForward=false, moveBackward=false, strafeLeft=false, strafeRight=false;
    private boolean onGround=false, jumping=false, falling=false;
    private boolean walking=false, running=true, sprinting=false;

    private float yBottom=0;
    private float yMotion= 0;

    private final float gravity = 0.0138f;

    public Game() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        angleInc = 0;
        lightAngle = 90;
        firstTime = true;
        hud = new Hud();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        scene = new Scene();

        Mesh[] miko_m = StaticMeshesLoader.load("src/models/miko/miko.obj", "src/models/miko");
        GameItem miko_gi = new GameItem(miko_m);

        Mesh[] miko_small_m = StaticMeshesLoader.load("src/models/miko/miko.obj", "src/models/miko");
        GameItem miko_small_gi = new GameItem(miko_small_m);
        miko_small_gi.setPosition(5, 0,0);
        miko_small_gi.setScale(0.5f);

        Mesh[] house_m = StaticMeshesLoader.load("src/models/house/house.obj", "src/models/house");
        GameItem house_gi = new GameItem(house_m);
        house_gi.setPosition(-5, 0,-10);
        house_gi.setScale(2f);

        Mesh[] terrainMesh = StaticMeshesLoader.load("src/models/terrain/terrain.obj", "src/models/terrain");
        GameItem terrain = new GameItem(terrainMesh);
        terrain.setScale(100.0f);

        scene.setGameItems(new GameItem[]{miko_gi, terrain, miko_small_gi, house_gi});

        // Shadows
        scene.setRenderShadows(true);

        // Fog
        Vector3f fogColour = new Vector3f(0f, 0f, 0f);
        scene.setFog(new Fog(true, fogColour, 0.02f));

        // Setup  SkyBox
        float skyBoxScale = 100.0f;
        SkyBox skyBox = new SkyBox("src/models/skybox.obj", "src/textures/skybox.png");
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();

        camera.getPosition().x = -17.0f;
        camera.getPosition().y =  17.0f;
        camera.getPosition().z = -30.0f;
        camera.getRotation().x = 20.0f;
        camera.getRotation().y = 140.f;

        hud.init(window);
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        sceneLight.setDirectionalLight(directionalLight);

        pointLightPos = new Vector3f(0.0f, 25.0f, 0.0f);
        Vector3f pointLightColour = new Vector3f(1.0f, 1.0f, 1.0f);
        PointLight.Attenuation attenuation = new PointLight.Attenuation(1, 0.0f, 0);
        PointLight pointLight = new PointLight(pointLightColour, pointLightPos, lightIntensity, attenuation);
        sceneLight.setPointLightList( new PointLight[] {pointLight});
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        sceneChanged = false;
        if(window.isKeyPressed(GLFW_KEY_W)){
            moveForward = true;
            sceneChanged = true;
        }else{
            moveForward = false;
        }

        if((window.isKeyPressed(GLFW_KEY_S))){
            moveBackward = true;
            sceneChanged = true;
        }else{
            moveBackward = false;
        }

        if (window.isKeyPressed(GLFW_KEY_A)){
            strafeLeft = true;
            sceneChanged = true;
        }else{
            strafeLeft = false;
        }

        if(window.isKeyPressed(GLFW_KEY_D)){
            strafeRight = true;
            sceneChanged = true;
        }else{
            strafeRight = false;
        }

        if((window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) && !(window.isKeyPressed(GLFW_KEY_LEFT_CONTROL))){
            sprinting = true;
            running = false;
            walking = false;
            sceneChanged = true;
        }else if(!(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) && (window.isKeyPressed(GLFW_KEY_LEFT_CONTROL))){
            walking = true;
            sprinting = false;
            running = false;
            sceneChanged = true;
        }else if(!(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT))&& !(window.isKeyPressed(GLFW_KEY_LEFT_CONTROL))){
            walking = false;
            sprinting = false;
            running = true;
            sceneChanged = true;
        }

        if(window.isKeyPressed(GLFW_KEY_SPACE) && onGround){
            yMotion = 0.1f;
            sceneChanged = true;
        }



        /*if (window.isKeyPressed(GLFW_KEY_UP)) {
            sceneChanged = true;
            pointLightPos.y += 0.5f;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            sceneChanged = true;
            pointLightPos.y -= 0.5f;
        }*/
    }

    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {
        previous.x = vector.x;
        previous.y = vector.y;
        previous.z = vector.z;

        if(walking && !sprinting){
            speed = walkSpeed;
        }else if(!walking && sprinting){
            speed = sprintSpeed;
        }else if(running){
            speed = runSpeed;
        }
        if(moveForward){
            vector.x += Math.sin(rotation.y*Math.PI/180)*speed;
            vector.z += -Math.cos(rotation.y*Math.PI/180)*speed;
        }
        if(moveBackward){
            vector.x -= Math.sin(rotation.y*Math.PI/180)*speed;
            vector.z -= -Math.cos(rotation.y*Math.PI/180)*speed;
        }
        if(strafeLeft){
            vector.x += Math.sin((rotation.y-90)*Math.PI/180)*speed;
            vector.z += -Math.cos((rotation.y-90)*Math.PI/180)*speed;
        }
        if(strafeRight){
            vector.x += Math.sin((rotation.y+90)*Math.PI/180)*speed;
            vector.z += -Math.cos((rotation.y+90)*Math.PI/180)*speed;
        }

        if (mouseInput.isInWindow()) {
            // Update camera based on mouse
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            sceneChanged = true;
        }

        camera.movePosition(vector.x, vector.y-0.1f, vector.z);

        lightAngle += angleInc;
        if (lightAngle < 0) {
            lightAngle = 0;
        } else if (lightAngle > 180) {
            lightAngle = 180;
        }
        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();

        // Update view matrix
        camera.updateViewMatrix();

        collisions(-0.9f);

        onGround = (vector.y == yBottom && !jumping && !falling);
        jumping = (yMotion > 0);
        falling = (yMotion < 0);
        vector.y += yMotion;
        if (vector.y > yBottom)
            yMotion -= gravity;
        if (vector.y <= yBottom)
            vector.y = yBottom;
        yMotion = 0;

    }

    private void collisions(float offset) {

    }


    @Override
    public void render(Window window) {
        if (firstTime) {
            sceneChanged = true;
            firstTime = false;
        }
        renderer.render(window, camera, scene, sceneChanged);
        hud.render(window);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();

        scene.cleanup();
    }
}
