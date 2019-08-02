package game;

import engine.*;
import engine.graph.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Nanodesu implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private static final float CAMERA_POS_STEP = 0.05f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private Camera camera;

    private Scene scene;

    private float lightAngle;

    private Hud hud;

    public Nanodesu() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0,0,0);
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        float reflectance = 1f;

        scene = new Scene();

        Mesh mesh_miko = OBJLoader.loadMesh("models/miko/miko.obj");
        Mesh mesh_cube = OBJLoader.loadMesh("models/cube.obj");
        Texture texture = new Texture("src/textures/lolicube.png");
        Material material = new Material(texture, reflectance);

        mesh_miko.setMaterial(material);
        mesh_cube.setMaterial(material);
        GameItem gi_miko = new GameItem(mesh_miko);
        gi_miko.setScale(0.5f);
        gi_miko.setPosition(0, -1, -2);

        GameItem gi_cube = new GameItem(mesh_cube);
        gi_cube.setScale(0.5f);
        gi_cube.setPosition(3, 0, -2);
        float skyBoxScale = 50.0f;
        float terrainScale = 10;
        int terrainSize = 3;
        float minY = -0.1f;
        float maxY = 0.1f;
        int textInc = 40;
        Terrain terrain = new Terrain(terrainSize, terrainScale, minY, maxY, "src/textures/heightmap.png", "src/textures/terrain.png", textInc);

        GameItem[] gi = new GameItem[]{
                gi_cube,
                gi_miko
        };


        scene.setGameItems(gi);

        // Setup  SkyBox
        SkyBox skyBox = new SkyBox("models/skybox.obj", "src/textures/skybox.png");
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        setupLights();

        hud = new Hud("NANODESU_PROTOTYPE_TEST 1");
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(1, 1, 0);
        sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity));
    }
    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            cameraInc.y = 1;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
            // Update camera position
            camera.movePos(cameraInc.x * CAMERA_POS_STEP,
                    cameraInc.y * CAMERA_POS_STEP,
                    cameraInc.z * CAMERA_POS_STEP);

            // Update camera based on mouse
            if (mouseInput.isRightButtonPressed()) {
                Vector2f rotVec = mouseInput.getDisplVec();
                camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

                hud.rotateCompass(camera.getRot().y);
            }


        // Update directional light direction, intensity and colour
        SceneLight sceneLight = scene.getSceneLight();
        DirectionalLight directionalLight = sceneLight.getDirectionalLight();
        lightAngle += 0.5f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
            sceneLight.getSkyBoxLight().set(0.3f, 0.3f, 0.3f);
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            sceneLight.getSkyBoxLight().set(factor, factor, factor);
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            sceneLight.getSkyBoxLight().set(1.0f, 1.0f, 1.0f);
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        try {
            hud.updateSize(window);
            renderer.render(window, camera, scene, hud);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
        if (hud != null) {
            hud.cleanup();
        }
    }
}
