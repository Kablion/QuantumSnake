package de.kablion.qsnake.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import de.kablion.qsnake.Application;
import de.kablion.qsnake.constants.DIM;
import de.kablion.qsnake.constants.STRINGS;
import de.kablion.qsnake.constants.Skins;
import de.kablion.qsnake.utils.UtilCalc;

public class HUDStage extends Stage {

    /**
     * Stage that handles and represents the the HUD/Interface while playing
     * It also handles all the Input
     */

    private final Application app;
    private final WorldStage worldStage;

    private Table rootTable;
    private Table statsTable;
    private Table pauseButtonTable;

    // Ingame Labels
    private Label collectedLabel;
    private Label scoreLabel;
    private Label fpsLabel;

    // Game Over Labels
    private Label gameOverCollectedLabel;
    private Label gameOverScoreLabel;
    private Label gameOverTimeLabel;

    private Button steerLeftButton;
    private Button steerRightButton;
    private Button pauseButton;
    private Button resumeButton;

    private Window pauseWindow;
    private Window gameOverWindow;

    // FPS which is currently shown
    private float fpsCurrent;
    // When the fpsShown was last updated
    private long fpsUpdated;
    // How long will the fps Stay on the Screen
    private static final long fpsRestTime = 1;

    public HUDStage(Application app, WorldStage worldStage) {
        super(new ExtendViewport(DIM.SCREEN_WIDTH,DIM.SCREEN_HEIGHT), app.batch);
        this.app = app;
        this.worldStage = worldStage;

        setDebugAll(false);
    }

    public void reset() {
        clear();
        initialize();
    }

    private void initialize() {
        rootTable = new Table();
        rootTable.setFillParent(true);
        addActor(rootTable);

        initStats();
        initButtons();
        initPauseWindow();
        initGameOverWindow();
    }

    private void initStats() {
        statsTable = new Table();
        rootTable.add(statsTable).left().top().pad(DIM.PAD_INGAME_STATS);

        collectedLabel = new Label(STRINGS.HUD_STATS_COLLECTED,app.skins.qsnake);
        collectedLabel.setFontScale(DIM.FONT_INGAME_STATS);
        statsTable.add(collectedLabel).align(Align.left);
        statsTable.row();

        scoreLabel = new Label(STRINGS.HUD_STATS_SCORE, app.skins.qsnake);
        scoreLabel.setFontScale(DIM.FONT_INGAME_STATS);
        statsTable.add(scoreLabel).align(Align.left);
        statsTable.row();

        fpsLabel = new Label(STRINGS.HUD_STATS_FPS, app.skins.qsnake);
        fpsLabel.setFontScale(DIM.FONT_INGAME_STATS);
        fpsLabel.setColor(app.skins.qsnake.getColor("grey"));
        statsTable.add(fpsLabel).align(Align.left);
        statsTable.row();
    }

    private void initButtons() {
        String alpha = "";
        if(Skins.alpha_buttons) alpha = "_alpha";

        pauseButtonTable = new Table();
        rootTable.add(pauseButtonTable).right().top().pad(DIM.PAD_PAUSE_BUTTON).expand().align(Align.topRight);
        rootTable.row();

        pauseButton = new Button(app.skins.qsnake,"pause"+alpha);
        pauseButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.gameScreen.pause();
            }
        });
        pauseButtonTable.add(pauseButton);

        resumeButton = new Button(app.skins.qsnake, "resume"+alpha);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.gameScreen.resume();
            }
        });

        steerLeftButton = new Button(app.skins.qsnake,"left"+alpha);
        rootTable.add(steerLeftButton).left().bottom().pad(DIM.PAD_STEER_LEFT_BUTTON).expand();

        steerRightButton = new Button(app.skins.qsnake,"right"+alpha);
        rootTable.add(steerRightButton).right().bottom().pad(DIM.PAD_STEER_RIGHT_BUTTON).expand();
    }

    private void initPauseWindow() {
        Table pauseTable = new Table();
        pauseTable.setFillParent(true);
        addActor(pauseTable);

        pauseWindow = new Window(STRINGS.PAUSE_WINDOW_TITLE, app.skins.qsnake);
        pauseWindow.setMovable(false);
        pauseWindow.setResizable(false);
        pauseWindow.setVisible(false);
        pauseWindow.getTitleLabel().setFontScale(DIM.FONT_DIALOG_TITLE);
        pauseWindow.getTitleLabel().setAlignment(Align.center);

        pauseTable.add(pauseWindow).align(Align.center).width(DIM.PAUSE_WINDOW_WIDTH).height(DIM.PAUSE_WINDOW_HEIGHT);

        TextButton resumeButton = new TextButton(STRINGS.PAUSE_WINDOW_RESUME, app.skins.qsnake);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.gameScreen.resume();
            }
        });
        pauseWindow.add(resumeButton).width(DIM.PAUSE_WINDOW_RESUME_WIDTH).height(DIM.PAUSE_WINDOW_BUTTON_HEIGHT).align(Align.center).expand();
        pauseWindow.row();

        TextButton mainMenuButton = new TextButton(STRINGS.PAUSE_WINDOW_MAIN_MENU,app.skins.qsnake);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.menuScreen);
            }
        });
        pauseWindow.add(mainMenuButton).width(DIM.PAUSE_WINDOW_MAIN_MENU_WIDTH).height(DIM.PAUSE_WINDOW_BUTTON_HEIGHT).align(Align.center).expand();
    }

    private void initGameOverWindow() {
        Table gameOverTable = new Table();
        gameOverTable.setFillParent(true);
        addActor(gameOverTable);

        gameOverWindow = new Window(STRINGS.GAMEOVER_WINDOW_TITLE, app.skins.qsnake);
        gameOverWindow.setMovable(false);
        gameOverWindow.setResizable(false);
        gameOverWindow.setVisible(false);
        gameOverWindow.getTitleLabel().setFontScale(DIM.FONT_DIALOG_TITLE);
        gameOverWindow.getTitleLabel().setAlignment(Align.center);

        gameOverTable.add(gameOverWindow).align(Align.center).width(DIM.GAMEOVER_WINDOW_WIDTH).height(DIM.GAMEOVER_WINDOW_HEIGHT);

        gameOverCollectedLabel = new Label("", app.skins.qsnake);
        gameOverCollectedLabel.setFontScale(DIM.FONT_GAMEOVER_STATS);
        gameOverWindow.add(gameOverCollectedLabel).align(Align.center).expand();
        gameOverWindow.row();
        gameOverScoreLabel = new Label("", app.skins.qsnake);
        gameOverScoreLabel.setFontScale(DIM.FONT_GAMEOVER_STATS);
        gameOverWindow.add(gameOverScoreLabel).align(Align.center).expand();
        gameOverWindow.row();
        gameOverTimeLabel = new Label("", app.skins.qsnake);
        gameOverTimeLabel.setFontScale(DIM.FONT_GAMEOVER_STATS);
        gameOverWindow.add(gameOverTimeLabel).align(Align.center).expand();
        gameOverWindow.row();


        TextButton againButton = new TextButton(STRINGS.GAMEOVER_WINDOW_AGAIN, app.skins.qsnake);
        againButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.gameScreen.reset();
            }
        });
        gameOverWindow.add(againButton).width(DIM.GAMEOVER_WINDOW_AGAIN_WIDTH).height(DIM.GAMEOVER_WINDOW_BUTTON_HEIGHT).align(Align.center).expand();
        gameOverWindow.row();

        TextButton mainMenuButton = new TextButton(STRINGS.GAMEOVER_WINDOW_MAIN_MENU,app.skins.qsnake);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                app.setScreen(app.menuScreen);
            }
        });
        gameOverWindow.add(mainMenuButton).width(DIM.GAMEOVER_WINDOW_MAIN_MENU_WIDTH).height(DIM.GAMEOVER_WINDOW_BUTTON_HEIGHT).align(Align.center).expand();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        updateStats(delta);
        handleSteerButtons(delta);
        handleKeyboardInput(delta);
    }

    private void handleSteerButtons(float delta) {
        if(steerLeftButton.isPressed()) {
            worldStage.getPlayer().steerLeft(delta);
        }
        if(steerRightButton.isPressed()) {
            worldStage.getPlayer().steerRight(delta);
        }
    }

    private void handleKeyboardInput(float delta) {
        // Steering
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            worldStage.getPlayer().steerLeft(delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            worldStage.getPlayer().steerRight(delta);
        }
    }

    private void calculateFPS(float delta) {
        float fps = 1 / delta;
        if (fpsUpdated + fpsRestTime * 500 < System.currentTimeMillis()) {
            fpsCurrent = fps;
            fpsUpdated = System.currentTimeMillis();
            fpsLabel.setText(STRINGS.HUD_STATS_FPS + MathUtils.round(fpsCurrent));
        }
    }

    private void updateStats(float delta) {
        calculateFPS(delta);
        collectedLabel.setText(STRINGS.HUD_STATS_COLLECTED+worldStage.getPlayer().countCollectedParticles());
        scoreLabel.setText(STRINGS.HUD_STATS_SCORE+worldStage.getPlayer().countCollectedParticles());
    }

    public void setPaused(boolean pause) {
        pauseWindow.setVisible(pause);
        steerLeftButton.setVisible(!pause);
        steerRightButton.setVisible(!pause);

        pauseButtonTable.clearChildren();
        if(pause) {
            pauseButtonTable.add(resumeButton);
        } else {
            pauseButtonTable.add(pauseButton);
        }
    }

    public void gameOver() {
        int collectedParticles = worldStage.getPlayer().countCollectedParticles();
        String timePlayed = UtilCalc.secondsToString(worldStage.getSecondsPlayed(), "dhMs", false);

        gameOverCollectedLabel.setText(STRINGS.GAMEOVER_WINDOW_COLLECTED + collectedParticles);
        gameOverScoreLabel.setText(STRINGS.GAMEOVER_WINDOW_SCORE + collectedParticles);
        gameOverTimeLabel.setText(STRINGS.GAMEOVER_WINDOW_TIME + timePlayed);

        gameOverWindow.setVisible(true);
        steerLeftButton.setVisible(false);
        steerRightButton.setVisible(false);
        pauseButton.setVisible(false);
        statsTable.setVisible(false);
    }

    @Override
    public void draw() {
        super.draw();
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
