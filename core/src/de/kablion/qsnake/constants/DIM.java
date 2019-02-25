package de.kablion.qsnake.constants;

import com.badlogic.gdx.Gdx;

public class DIM {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;

    private static final float aspectRatio = (float) Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
    public static final int WORLD_WIDTH = 1000;
    public static final int WORLD_HEIGHT = (int)(WORLD_WIDTH * aspectRatio);

    // Font Scales
    public static final float FONT_TEXT_BUTTON = 1f;
    public static final float FONT_INGAME_STATS = 0.52f;
    public static final float FONT_DIALOG_TITLE = 1.32f;
    public static final float FONT_MENU_TITLE = 2f;
    public static final float FONT_MENU_LABEL = 1.2f;
    public static final float FONT_STATS_LABEL = 0.8f;

    // HUD Paddings
    public static final float PAD_INGAME_STATS = 20;
    public static final float PAD_PAUSE_BUTTON = 20;
    public static final float PAD_STEER_LEFT_BUTTON = 20;
    public static final float PAD_STEER_RIGHT_BUTTON = 20;
}
