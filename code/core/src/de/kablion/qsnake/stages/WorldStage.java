package de.kablion.qsnake.stages;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import de.kablion.qsnake.Application;
import de.kablion.qsnake.constants.PREFERENCES;
import de.kablion.qsnake.entities.Particle;
import de.kablion.qsnake.entities.Player;
import de.kablion.qsnake.constants.DIM;

public class WorldStage extends Stage {

    /**
     * Stage that handles and represents the Game World
     */

    private final Application app;

    private boolean borderDeadly = false;
    private Rectangle worldBorder = new Rectangle();

    private Player player;

    private Particle particle;

    private float secondsPlayed = 0;

    private boolean paused;

    public WorldStage(Application app) {
        super(new FitViewport(DIM.WORLD_WIDTH, DIM.WORLD_HEIGHT), app.batch);
        this.app = app;
    }

    private void initCamera() {
        getCamera().position.set(DIM.WORLD_WIDTH / 2f, DIM.WORLD_HEIGHT / 2f, 0);
        getViewport().setWorldSize(DIM.WORLD_WIDTH, DIM.WORLD_HEIGHT);

        worldBorder = new Rectangle(0,0, DIM.WORLD_WIDTH, DIM.WORLD_HEIGHT);

        if(app.settings.getBoolean(PREFERENCES.SETTINGS_DEBUG_SCREEN_WRAPPING)) {
            getViewport().setWorldSize(DIM.WORLD_WIDTH*3, DIM.WORLD_HEIGHT*3);
            Actor borderActor = new Actor();
            borderActor.setBounds(worldBorder.x, worldBorder.y, worldBorder.width, worldBorder.height);
            borderActor.setDebug(true);
            addActor(borderActor);
        }

    }

    private void initPlayer() {
        player = new Player(app, this);
        addActor(player);
        addActor(player.getTail());
    }

    private void initCoordinateIndicator() {
        addActor(new Actor(){
            @Override
            public void drawDebug(ShapeRenderer shapes) {
                //Draw Coordinate Indicator
                //x Axis in Red
                shapes.setColor(Color.RED);
                shapes.line(0,0,DIM.COORDINATE_LINE_LENGTH,0);

                //y Axis in Green
                shapes.setColor(Color.FOREST);
                shapes.line(0,0,0,DIM.COORDINATE_LINE_LENGTH);
            }
        });
    }

    public void reset() {
        clear();

        if(app.settings.getBoolean(PREFERENCES.SETTINGS_DEBUG_ENTITIES)) {
            this.setDebugAll(true);
        } else {
            this.setDebugAll(false);
        }

        paused = false;
        secondsPlayed = 0;
        initCamera();
        initPlayer();
        initCoordinateIndicator();
        createParticle();
    }

    public void createParticle() {
        if (particle != null) particle.remove();
        particle = new Particle(app);
        addActor(particle);
    }

    @Override
    public void act(float delta) {
        if (!paused) {
            super.act(delta);
            secondsPlayed += delta;
        }
    }

    @Override
    public void draw() {
        super.draw();
    }

    public void setPaused(boolean pause) {
        paused = pause;
    }

    public boolean isPaused() {
        return paused;
    }

    public void gameOver() {
        setPaused(true);
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, false);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public Player getPlayer() {
        return player;
    }

    public Rectangle getBorder() {
        return worldBorder;
    }

    public Particle getParticle() {
        return particle;
    }

    public float getSecondsPlayed() {
        return secondsPlayed;
    }

    public boolean isBorderDeadly() {
        return borderDeadly;
    }
}