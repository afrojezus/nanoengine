/**
 * Special thanks to lwjglgamedev for getting this started. :)
 */
package game;

import engine.GameEngine;
import engine.IGameLogic;
import engine.Window;

public class Main {
    public static void main(String[] args) {
        try {
            boolean vSync = false;
            IGameLogic gameLogic = new Game();
            Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = false;
            opts.showFps = true;
            opts.compatibleProfile = true;
            opts.antialiasing = false;
            opts.frustumCulling = false;
            GameEngine gameEng = new GameEngine("NANOENGINE", vSync, opts, gameLogic);
            gameEng.run();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}
