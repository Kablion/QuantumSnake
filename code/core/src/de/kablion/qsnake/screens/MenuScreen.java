package de.kablion.qsnake.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import de.kablion.qsnake.Application;
import de.kablion.qsnake.constants.DIM;
import de.kablion.qsnake.constants.STRINGS;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class MenuScreen implements Screen {

    private enum MenuPage {
        MAIN,STATISTICS
    }

    /**
     * Screen to navigate around
     */

    private final Application app;
    private final Stage stage;
    private Table rootTable = new Table();
    private Table menuTable = new Table();
    private Label titleLabel;

    private MenuPage currentPage;

    TextButton tempButton;

    public MenuScreen(final Application app) {
        this.app = app;
        this.stage = new Stage(new ExtendViewport(DIM.SCREEN_WIDTH, DIM.SCREEN_HEIGHT), app.batch);

        stage.setDebugAll(true);
    }

    @Override
    public void show() {
        Gdx.app.log("Screen:","MAIN_MENU");

        stage.clear();
        rootTable.clear();
        menuTable.clear();

        initStage();
        setPage(MenuPage.MAIN);

        Gdx.input.setInputProcessor(stage);

    }

    private void initStage() {
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Title Label
        titleLabel = new Label("Test", app.skins.qsnake);
        titleLabel.setFontScale(DIM.FONT_MENU_TITLE);
        rootTable.add(titleLabel).padBottom(DIM.MAIN_MENU_TITLE_PAD_BOTTOM);
        rootTable.row();

        rootTable.add(menuTable).expand().fill();
    }

    private void update(float delta) {
        stage.act();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        stage.draw();
    }

    private void setPage(MenuPage menuPage) {
        currentPage = menuPage;

        menuTable.clear();
        Table buttonsTable = new Table();
        switch (menuPage) {

            case MAIN :
                titleLabel.setText(STRINGS.MAIN_MENU_TITLE);

                // Game Mode Slider

                menuTable.add(buttonsTable);
                menuTable.row();

                // Play Button
                tempButton = new TextButton(STRINGS.MAIN_MENU_PLAY, app.skins.qsnake);
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        app.setScreen(app.gameScreen);
                    }
                });
                buttonsTable.add(tempButton).expandX().fill().padBottom(DIM.MAIN_MENU_BUTTONS_DISTANCE);
                buttonsTable.row();

                // Statistics Button
                tempButton = new TextButton(STRINGS.MAIN_MENU_STATS, app.skins.qsnake);
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        setPage(MenuPage.STATISTICS);
                    }
                });
                buttonsTable.add(tempButton).expandX().fill().padBottom(DIM.MAIN_MENU_BUTTONS_DISTANCE);
                buttonsTable.row();

                // Exit Button
                tempButton = new TextButton(STRINGS.MAIN_MENU_EXIT, app.skins.qsnake);
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                Gdx.app.exit();
                            }
                        },0.5f);
                    }
                });
                buttonsTable.add(tempButton).expandX().fill().padBottom(DIM.MAIN_MENU_BUTTONS_DISTANCE);
                break;
            case STATISTICS:
                titleLabel.setText(STRINGS.STATS_MENU_TITLE);

                buttonsTable = new Table();
                menuTable.add(buttonsTable);
                menuTable.row();

                // Back Button
                tempButton = new TextButton(STRINGS.STATS_MENU_BACK, app.skins.qsnake);
                tempButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        setPage(MenuPage.MAIN);
                    }
                });
                buttonsTable.add(tempButton).expandX().fill().padBottom(DIM.MAIN_MENU_BUTTONS_DISTANCE);
                buttonsTable.row();
                break;
        }
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
