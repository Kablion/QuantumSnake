package de.kablion.qsnake.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import de.kablion.qsnake.Application;

public class HUDStage extends Stage {

    /**
     * Stage that handles and represents the the HUD/Interface while playing
     * It also handles all the Input
     */

    private final Application app;
    private final WorldStage worldStage;

    private Table rootTable = new Table();

    public HUDStage(Application app) {
        super(new ExtendViewport(1,1), app.batch);
        this.app = app;
        this.worldStage = this.app.gameScreen.worldStage;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        handleKeyboardInput(delta);
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
