package de.kablion.qsnake.constants;

import com.badlogic.gdx.Gdx;

public class DIMENSIONS {
    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 480;

    private static final float aspectRatio = (float) Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
    public static final int WORLD_WIDTH = 1000;
    public static final int WORLD_HEIGHT = (int)(WORLD_WIDTH * aspectRatio);
}
