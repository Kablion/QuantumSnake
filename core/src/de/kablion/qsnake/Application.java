package de.kablion.qsnake;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.kablion.qsnake.constants.SKINS;
import de.kablion.qsnake.screens.GameScreen;
import de.kablion.qsnake.screens.LoadingScreen;

import java.util.HashMap;

public class Application extends Game {
	public SpriteBatch batch;
	public ShapeRenderer shapeRenderer;
	public AssetManager assets;
    public HashMap<SKINS, Skin> skins;

	//Screens
	public LoadingScreen loadingScreen;
	//public MainMenuScreen mainMenuScreen;
	public GameScreen gameScreen;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		assets = new AssetManager();
		skins = new HashMap<SKINS, Skin>();

		loadingScreen = new LoadingScreen(this);
		//mainMenuScreen = new MainMenuScreen(this);
		gameScreen = new GameScreen(this);

        // the Game Starts with the LoadingScreen
		this.setScreen(loadingScreen);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
	    super.dispose();
		batch.dispose();
		shapeRenderer.dispose();
		assets.dispose();
		skins.clear();

		loadingScreen.dispose();
		//mainMenuScreen.dispose();
        gameScreen.dispose();
	}
}
