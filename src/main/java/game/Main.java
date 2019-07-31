package game;

import engine.GameEngine;
import engine.IGameLogic;

public class Main {
    public static void main(String[] args) {
        try {
            IGameLogic gameLogic = new Nanodesu();
            GameEngine gameEng = new GameEngine("NANODESU", 800, 600, true, gameLogic);
            gameEng.run();
        } catch(Exception excp) {
          excp.printStackTrace();;
          System.exit(-1);
        }
    }
}
