package de.kablion.qsnake.constants;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Skins {
    public Skin loading;
    public Skin qsnake;

    public void dispose() {
        loading = null;
        qsnake = null;
    }
}
