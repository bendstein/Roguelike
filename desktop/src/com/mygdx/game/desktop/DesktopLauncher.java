package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import game.Main;
import prefabbuilder.PrefabBuilderMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		int choice = 0;
		config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		config.fullscreen = false;

		if(choice == 0) {
			config.title = "Roguelike";
			//System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
			new LwjglApplication(new Main(), config);
		}
		else {
			config.title = "Prefab Builder";
			//System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
			new LwjglApplication(new PrefabBuilderMain(), config);
		}
	}
}
