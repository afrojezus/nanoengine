package engine;

import engine.item.GameItem;

public interface IHud {

    GameItem[] getGameItems();

    default void cleanup() {
        GameItem[] gameItems = getGameItems();
        for (GameItem gameItem : gameItems)
            gameItem.getMesh().cleanUp();
    }
}
