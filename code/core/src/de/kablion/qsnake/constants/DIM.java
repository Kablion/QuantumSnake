package de.kablion.qsnake.constants;

import com.badlogic.gdx.Gdx;

public class DIM {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;

    private static final float aspectRatio = (float) Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
    public static final int WORLD_WIDTH = 750;
    public static final int WORLD_HEIGHT = (int)(WORLD_WIDTH * aspectRatio);

    public static final float COORDINATE_LINE_LENGTH = 50;

    // Player
    public static final float PLAYER_SPEED = 100; // Units per second
    public static final float PLAYER_ROTATION_SPEED = 120; // Degrees per second
    public static final float PLAYER_HEAD_WIDTH = 42;
    public static final float PLAYER_HEAD_HEIGHT = 36;
    public static final float PLAYER_HEAD_HITBOX_RADIUS = 25;
    public static final float PLAYER_HEAD_HITBOX_OFFSET = -5;

    // Player Tail
    public static final float PARTICLE_CONTAINER_DISTANCE = 20;

    // Particle Container
    public static final float PARTICLE_CONTAINER_WIDTH = 30;
    public static final float PARTICLE_CONTAINER_LENGTH = 50;

    // Particle
    public static final float PARTICLE_RADIUS = 20;

    // Font Scales
    public static final float FONT_TEXT_BUTTON = 1f;
    public static final float FONT_INGAME_STATS = 0.52f;
    public static final float FONT_DIALOG_TITLE = 1.32f;
    public static final float FONT_MENU_TITLE = 2f;
    public static final float FONT_MENU_LABEL = 1.2f;
    public static final float FONT_STATS_LABEL = 0.8f;
    public static final float FONT_GAMEOVER_STATS = 0.75f;

    // HUD Paddings
    public static final float PAD_INGAME_STATS = 20;
    public static final float PAD_PAUSE_BUTTON = 20;
    public static final float PAD_STEER_LEFT_BUTTON = 20;
    public static final float PAD_STEER_RIGHT_BUTTON = 20;

    // Pause Window
    public static final float PAUSE_WINDOW_WIDTH = 700;
    public static final float PAUSE_WINDOW_HEIGHT = 540;
    public static final float PAUSE_WINDOW_BUTTON_HEIGHT = 120;
    public static final float PAUSE_WINDOW_RESUME_WIDTH = 440;
    public static final float PAUSE_WINDOW_MAIN_MENU_WIDTH = 520;

    // Game Over Window
    public static final float GAMEOVER_WINDOW_WIDTH = 700;
    public static final float GAMEOVER_WINDOW_HEIGHT = 540;
    public static final float GAMEOVER_WINDOW_BUTTON_HEIGHT = 100;
    public static final float GAMEOVER_WINDOW_AGAIN_WIDTH = 440;
    public static final float GAMEOVER_WINDOW_MAIN_MENU_WIDTH = 520;


    // Main Menu
    public static final float MAIN_MENU_TITLE_PAD_BOTTOM = 75;
    public static final float MAIN_MENU_BUTTONS_WIDTH = 420;
    public static final float MAIN_MENU_BUTTONS_DISTANCE = 20;

}
