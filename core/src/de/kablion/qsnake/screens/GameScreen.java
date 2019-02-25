package de.kablion.qsnake.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import de.kablion.qsnake.Application;
import de.kablion.qsnake.stages.HUDStage;
import de.kablion.qsnake.stages.WorldStage;

public class GameScreen implements Screen {

    /**
     * Screen in Which the actual game plays
     */

    private final Application app;

    public WorldStage worldStage;
    public HUDStage hudStage;


    public GameScreen(final Application app) {
        this.app = app;
    }

    @Override
    public void show() {
        Gdx.app.log("Screen:","GAME");

        worldStage = new WorldStage(app);
        hudStage = new HUDStage(app);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hudStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void update(float delta) {
        worldStage.act(delta);
        hudStage.act(delta);
    }

    private void draw() {
        worldStage.draw();
        hudStage.draw();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);
        draw();
    }

    @Override
    public void resize(int width, int height) {
        worldStage.resize(width, height);
        hudStage.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        worldStage.dispose();
        hudStage.dispose();
    }

    public void gameOver() {
        // TODO
    }
}
