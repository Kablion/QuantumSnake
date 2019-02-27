package de.kablion.qsnake.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import de.kablion.qsnake.Application;
import de.kablion.qsnake.entities.Particle;
import de.kablion.qsnake.entities.Player;
import de.kablion.qsnake.constants.DIM;

public class WorldStage extends Stage {

    /**
     * Stage that handles and represents the Game World
     */

    private final Application app;

    private Player player;

    private Particle particle;

    private boolean paused;

    public WorldStage(Application app) {
        super(new ExtendViewport(DIM.WORLD_WIDTH, DIM.WORLD_HEIGHT), app.batch);
        this.app = app;

        this.setDebugAll(true);

        initCamera();
    }

    private void initCamera() {
        getCamera().position.set(DIM.WORLD_WIDTH / 2f, DIM.WORLD_HEIGHT / 2f, 0);
    }

    private void initPlayer() {
        player = new Player(app, this);
        addActor(player);
        addActor(player.getTail());
    }

    public void reset() {
        clear();
        paused = false;
        initPlayer();
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
        getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public Player getPlayer() {
        return player;
    }

    public Particle getParticle() {
        return particle;
    }
}