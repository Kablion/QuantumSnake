package de.kablion.qsnake.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import de.kablion.qsnake.Application;
import de.kablion.qsnake.constants.DIM;
import de.kablion.qsnake.constants.STRINGS;

public class HUDStage extends Stage {

    /**
     * Stage that handles and represents the the HUD/Interface while playing
     * It also handles all the Input
     */

    private final Application app;
    private final WorldStage worldStage;

    private Table rootTable = new Table();
    private Table statsTable = new Table();

    private Label collectedLabel;
    private Label scoreLabel;
    private Label fpsLabel;

    private Button steerLeftButton;
    private Button steerRightButton;
    private Button pauseButton;

    // FPS which is currently shown
    private float fpsCurrent;
    // When the fpsShown was last updated
    private long fpsUpdated;
    // How long will the fps Stay on the Screen
    private static final long fpsRestTime = 1;

    public HUDStage(Application app) {
        super(new ExtendViewport(DIM.SCREEN_WIDTH,DIM.SCREEN_HEIGHT), app.batch);
        this.app = app;
        this.worldStage = this.app.gameScreen.worldStage;

        setDebugAll(true);

        initialize();
    }

    private void initialize() {
        rootTable.setFillParent(true);
        addActor(rootTable);

        initStats();
        initButtons();
    }

    private void initStats() {
        rootTable.add(statsTable).left().top().pad(DIM.PAD_INGAME_STATS);
        statsTable.clearChildren();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = app.skins.qsnake.getFont("default");

        collectedLabel = new Label(STRINGS.HUD_STATS_COLLECTED,labelStyle);
        collectedLabel.setFontScale(DIM.FONT_INGAME_STATS);
        statsTable.add(collectedLabel);
        statsTable.row();

        scoreLabel = new Label(STRINGS.HUD_STATS_SCORE, app.skins.qsnake);
        scoreLabel.setFontScale(DIM.FONT_INGAME_STATS);
        statsTable.add(scoreLabel);
        statsTable.row();

        fpsLabel = new Label(STRINGS.HUD_STATS_FPS, app.skins.qsnake);
        fpsLabel.setFontScale(DIM.FONT_INGAME_STATS);
        fpsLabel.setColor(app.skins.qsnake.getColor("grey"));
        statsTable.add(fpsLabel);
        statsTable.row();
    }

    private void initButtons() {
        pauseButton = new Button(app.skins.qsnake,"pause");
        rootTable.add(pauseButton).right().top().pad(DIM.PAD_PAUSE_BUTTON);
        rootTable.row();

        steerLeftButton = new Button(app.skins.qsnake,"left");
        rootTable.add(steerLeftButton).left().bottom().pad(DIM.PAD_STEER_LEFT_BUTTON).expand();

        steerRightButton = new Button(app.skins.qsnake,"right");
        rootTable.add(steerRightButton).right().bottom().pad(DIM.PAD_STEER_RIGHT_BUTTON).expand();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        calculateFPS(delta);
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

    @Override
    public void draw() {
        super.draw();
    }

    public void resize(int width, int height) {
        getViewport().update(width, height, false);
    }

    @Override
    public void dispose() {
        super.dispose();
    }


}
