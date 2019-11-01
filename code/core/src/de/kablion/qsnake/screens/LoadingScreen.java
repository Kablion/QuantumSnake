package de.kablion.qsnake.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import de.kablion.qsnake.Application;

import de.kablion.qsnake.constants.DIM;
import de.kablion.qsnake.constants.PATHS;

public class LoadingScreen implements Screen {

    /**
     * Screen in Which all needed assets are load into RAM / app.assets
     */

    private final Application app;

    private final Stage stage;
    private final Table rootTable;
    private ProgressBar progressBar;


    public LoadingScreen(final Application app) {
        this.app = app;
        this.stage = new Stage(new ExtendViewport(DIM.SCREEN_WIDTH, DIM.SCREEN_HEIGHT), app.batch);
        this.rootTable = new Table();
    }

    /**
     * Defines the Assets that should be load into RAM and in which order
     */
    private void queueAssets() {
        app.assets.load(PATHS.ENTITY_SPRITES, TextureAtlas.class);

        app.assets.load(PATHS.QSNAKE_ATLAS, TextureAtlas.class);
    }

    @Override
    public void show() {
        Gdx.app.log("Screen","LOADING");

        initLoadingSkin();
        initStage();
        initProgressBar();

        queueAssets();
    }

    private void initLoadingSkin() {
        app.skins.loading = new Skin(Gdx.files.internal(PATHS.LOADING_SKIN), new TextureAtlas(PATHS.LOADING_ATLAS));
    }

    private void initSkins() {
        app.skins.qsnake = new Skin();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(PATHS.DEFAULT_FONT));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        parameter.color = Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);
        app.skins.qsnake.add("default", font);

        app.skins.qsnake.addRegions(app.assets.get(PATHS.QSNAKE_ATLAS, TextureAtlas.class));
        app.skins.qsnake.load(Gdx.files.internal(PATHS.QSNAKE_SKIN));
    }

    private void initStage() {
        rootTable.clear();
        stage.clear();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);
    }

    private void initProgressBar() {
        progressBar = new ProgressBar(0, 1, 0.01f, false, app.skins.loading, "default-horizontal");
        progressBar.setSize(290, progressBar.getPrefHeight());
        progressBar.setAnimateInterpolation(Interpolation.pow5Out);
        progressBar.setAnimateDuration(1);

        rootTable.add(progressBar).expand().center().fillX();
    }

    private void update(float delta) {
        progressBar.setValue(app.assets.getProgress());
        stage.act();
        if (app.assets.update() & Math.abs(progressBar.getVisualValue() - progressBar.getMaxValue()) < 0.001f) {

            //SKINS
            initSkins();

            // If everything is loaded continue to the MainMenu
            app.setScreen(app.menuScreen);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.clear();
    }
}
