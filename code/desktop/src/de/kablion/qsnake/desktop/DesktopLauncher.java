package de.kablion.qsnake.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.kablion.qsnake.Application;
import de.kablion.qsnake.constants.DIM;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "QuantumSnake";
		config.width = DIM.SCREEN_WIDTH;
		config.height = DIM.SCREEN_HEIGHT;
		config.samples = 8;
		new LwjglApplication(new Application(), config);
	}
}
