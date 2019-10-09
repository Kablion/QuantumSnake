package de.kablion.qsnake.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
import de.kablion.qsnake.constants.PREFERENCES;
import de.kablion.qsnake.constants.STRINGS;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class MenuScreen implements Screen {

    private enum MenuPage {
        MAIN,STATISTICS,SETTINGS
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
    CheckBox tempCheckBox;

    public MenuScreen(final Application app) {
        this.app = app;
        this.stage = new Stage(new ExtendViewport(DIM.SCREEN_WIDTH, DIM.SCREEN_HEIGHT), app.batch);

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

        if(app.settings.getBoolean(PREFERENCES.SETTINGS_DEBUG_MENU)) {
            stage.setDebugAll(true);
        } else {
            stage.setDebugAll(false);
        }

        switch (menuPage) {

            case MAIN :
                showMainMenu();
                break;
            case STATISTICS:
                showStatsMenu();
                break;
            case SETTINGS:
                showSettingsMenu();
                break;
        }
    }

    private void showMainMenu() {
        Table buttonsTable = new Table();
        titleLabel.setText(STRINGS.MAIN_MENU_TITLE);

        // Game Mode Slider
        //TODO

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

        // Settings Button
        tempButton = new TextButton(STRINGS.MAIN_MENU_SETTINGS, app.skins.qsnake);
        tempButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setPage(MenuPage.SETTINGS);
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
    }

    private void showStatsMenu() {
        titleLabel.setText(STRINGS.STATS_MENU_TITLE);

        Table buttonsTable = new Table();
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
    }

    private void showSettingsMenu() {
        titleLabel.setText(STRINGS.SETTINGS_MENU_TITLE);

        Table settingsTable = new Table();
        menuTable.add(settingsTable);
        menuTable.row();

        //Debug Screen Wrapping
        tempCheckBox = new CheckBox(STRINGS.SETTINGS_MENU_DEBUG_SCREEN_WRAPPING, app.skins.qsnake);
        tempCheckBox.setChecked(app.settings.getBoolean(PREFERENCES.SETTINGS_DEBUG_SCREEN_WRAPPING));
        tempCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.settings.putBoolean(PREFERENCES.SETTINGS_DEBUG_SCREEN_WRAPPING,((CheckBox)actor).isChecked());
            }
        });
        //TODO Proper Design
        settingsTable.add(tempCheckBox).expandX().fill().padBottom(DIM.MAIN_MENU_BUTTONS_DISTANCE);
        settingsTable.row();

        //Debug Menu
        tempCheckBox = new CheckBox(STRINGS.SETTINGS_MENU_DEBUG_MENU, app.skins.qsnake);
        tempCheckBox.setChecked(app.settings.getBoolean(PREFERENCES.SETTINGS_DEBUG_MENU));
        tempCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.settings.putBoolean(PREFERENCES.SETTINGS_DEBUG_MENU,((CheckBox)actor).isChecked());
            }
        });
        //TODO Proper Design
        settingsTable.add(tempCheckBox).expandX().fill().padBottom(DIM.MAIN_MENU_BUTTONS_DISTANCE);
        settingsTable.row();

        //Debug HUD
        tempCheckBox = new CheckBox(STRINGS.SETTINGS_MENU_DEBUG_HUD, app.skins.qsnake);
        tempCheckBox.setChecked(app.settings.getBoolean(PREFERENCES.SETTINGS_DEBUG_HUD));
        tempCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.settings.putBoolean(PREFERENCES.SETTINGS_DEBUG_HUD,((CheckBox)actor).isChecked());
            }
        });
        //TODO Proper Design
        settingsTable.add(tempCheckBox).expandX().fill().padBottom(DIM.MAIN_MENU_BUTTONS_DISTANCE);
        settingsTable.row();

        //Debug Entities
        tempCheckBox = new CheckBox(STRINGS.SETTINGS_MENU_DEBUG_ENTITIES, app.skins.qsnake);
        tempCheckBox.setChecked(app.settings.getBoolean(PREFERENCES.SETTINGS_DEBUG_ENTITIES));
        tempCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                app.settings.putBoolean(PREFERENCES.SETTINGS_DEBUG_ENTITIES,((CheckBox)actor).isChecked());
            }
        });
        //TODO Proper Design
        settingsTable.add(tempCheckBox).expandX().fill().padBottom(DIM.MAIN_MENU_BUTTONS_DISTANCE);
        settingsTable.row();

        // Reset Button
        tempButton = new TextButton(STRINGS.SETTINGS_MENU_RESET, app.skins.qsnake);
        tempButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetPreferences();
                setPage(MenuPage.SETTINGS);
            }
        });
        settingsTable.add(tempButton).expandX().fill().padBottom(DIM.MAIN_MENU_BUTTONS_DISTANCE);
        settingsTable.row();

        // Back Button
        tempButton = new TextButton(STRINGS.SETTINGS_MENU_BACK, app.skins.qsnake);
        tempButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.settings.flush();
                setPage(MenuPage.MAIN);
            }
        });
        settingsTable.add(tempButton).expandX().fill().padBottom(DIM.MAIN_MENU_BUTTONS_DISTANCE);
        settingsTable.row();

    }

    private void resetPreferences() {
        app.settings.clear();
        app.settings.putBoolean(PREFERENCES.SETTINGS_DEBUG_SCREEN_WRAPPING, PREFERENCES.DEFAULT_SETTINGS_DEBUG_SCREEN_WRAPPING);
        app.settings.putBoolean(PREFERENCES.SETTINGS_DEBUG_MENU, PREFERENCES.DEFAULT_SETTINGS_DEBUG_MENU);
        app.settings.putBoolean(PREFERENCES.SETTINGS_DEBUG_HUD, PREFERENCES.DEFAULT_SETTINGS_DEBUG_HUD);
        app.settings.putBoolean(PREFERENCES.SETTINGS_DEBUG_ENTITIES, PREFERENCES.DEFAULT_SETTINGS_DEBUG_ENTITIES);
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
