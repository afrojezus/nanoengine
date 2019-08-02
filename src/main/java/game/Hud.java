package game;

import engine.GameItem;
import engine.IHud;
import engine.TextItem;
import engine.Window;
import engine.graph.FontTexture;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.OBJLoader;
import org.joml.Vector4f;

import java.awt.*;

public class Hud implements IHud {

    private static final Font FONT = new Font("ProFontWindows", Font.PLAIN, 12);

    private static final String CHARSET = "ISO-8859-1";

    private final GameItem[] gameItems;

    private final TextItem statusTextItem;

    private final GameItem compassItem;

    public Hud(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItem(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));

        // Compass
        Mesh mesh = OBJLoader.loadMesh("models/compass.obj");
        Material material = new Material();
        material.setAmbientColour(new Vector4f(1,0,0,1));
        mesh.setMaterial(material);
        compassItem = new GameItem(mesh);
        compassItem.setScale(1.0f);

        // Rotate compass to fit screen coords.
        compassItem.setRotation(0f, 0f, 180f);

        gameItems = new GameItem[]{statusTextItem, compassItem};
    }

    public void setStatusText(String statusText) {
        this.statusTextItem.setText(statusText);
    }

    public void rotateCompass(float angle) {
        this.compassItem.setRotation(0,0,180 + angle);
    }

    @Override
    public GameItem[] getGameItems() {
        return gameItems;
    }

    public void updateSize(Window window) {
        // Placement of HUD elements
        this.statusTextItem.setPosition(10f, window.getHeight() - 20f, 0);
        this.compassItem.setPosition(window.getWidth() - 50f, 50f, 0);
    }
}