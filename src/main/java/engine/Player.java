package engine;

import engine.graph.Camera;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;


public class Player {
    private Camera camera;

    private static float walkSpeed = 0.035f, runSpeed = 0.06f, sprintSpeed = 0.12f;
    private float speed = runSpeed;

    private Vector3f vector = new Vector3f(7,1,7);
    private Vector3f rotation = new Vector3f();
    private Vector3f previous = new Vector3f();

    private boolean moveForward=false, moveBackward=false, strafeLeft=false, strafeRight=false;
    private boolean onGround=false, jumping=false, falling=false;
    private boolean walking=false, running=true, sprinting=false;
    private Scene scene;
    private Window window;
    private MouseInput mouseInput;
    private float yBottom=0;
    private float yMotion= 0;

    private final float gravity = 0.0138f;


    public Player() {
    }


    public void init(Camera camera, Scene scene) {
        this.camera = camera;
        this.scene = scene;
    }

    public void translatePlayer() {
        camera.setRotation(rotation.x, rotation.y, rotation.z);
        camera.setPosition(-vector.x, -vector.y-1.4f, -vector.z);
    }

    public void update() {
        updatePreviousVector();
        updateMotion();
        input();
        individualPhysics();
    }

    public void individualPhysics() {
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

    /*public void collisions(float offset) {
        if(!onTopOfCollidableMesh(offset))
            yBottom = 0;


    }*/

    public void input(){
        if(window.isKeyPressed(GLFW_KEY_W)){
            moveForward = true;
        }else{
            moveForward = false;
        }

        if((window.isKeyPressed(GLFW_KEY_S))){
            moveBackward = true;
        }else{
            moveBackward = false;
        }

        if (window.isKeyPressed(GLFW_KEY_A)){
            strafeLeft = true;
        }else{
            strafeLeft = false;
        }

        if(window.isKeyPressed(GLFW_KEY_D)){
            strafeRight = true;
        }else{
            strafeRight = false;
        }

        if((window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) && !(window.isKeyPressed(GLFW_KEY_LEFT_CONTROL))){
            sprinting = true;
            running = false;
            walking = false;
        }else if(!(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) && (window.isKeyPressed(GLFW_KEY_LEFT_CONTROL))){
            walking = true;
            sprinting = false;
            running = false;
        }else if(!(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT))&& !(window.isKeyPressed(GLFW_KEY_LEFT_CONTROL))){
            walking = false;
            sprinting = false;
            running = true;
        }

        if(window.isKeyPressed(GLFW_KEY_SPACE) && onGround){
            yMotion = 0.2f;
        }

        if(mouseInput.isInWindow()) {

            if (mouseInput.isLocked()) {
                Vector2f rotVec = mouseInput.getDisplVec();
                float mouseDX = rotVec.x * 0.8f * 0.16f;
                float mouseDY = rotVec.y * 0.8f * 0.16f;
                if (rotation.y + mouseDX >= 360) {
                    rotation.y = rotation.y + mouseDX - 360;
                } else if (rotation.y + mouseDX < 0) {
                    rotation.y = 360 - rotation.y + mouseDX;
                } else {
                    rotation.y += mouseDX;
                }
                if (rotation.x - mouseDY >= -89 && rotation.x - mouseDY <= 89) {
                    rotation.x += -mouseDY;
                } else if (rotation.x - mouseDY < -89) {
                    rotation.x = -89;
                } else if (rotation.x - mouseDY > 89) {
                    rotation.x = 89;
                }
            } else {
                if (mouseInput.isLeftButtonPressed())
                    mouseInput.lockCursor(window);
            }
        }
    }

    public void updatePreviousVector(){
        previous.x = vector.x;
        previous.y = vector.y;
        previous.z = vector.z;
    }

    public void updateMotion(){
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
    }
}
