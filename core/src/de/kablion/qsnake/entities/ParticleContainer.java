package de.kablion.qsnake.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

public class ParticleContainer extends Actor {

    public static final float CONTAINER_WIDTH = 40;
    public static final float CONTAINER_LENGTH = 70;

    private float probability = 0;

    public ParticleContainer() {
        setSize(CONTAINER_LENGTH,CONTAINER_WIDTH);
        setOrigin(Align.center);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        if (!getDebug()) return;
        // draw bounds
        shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(getStage().getDebugColor());
        shapes.rect(getX()-getOriginX(), getY()-getOriginY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        // draw position
        shapes.set(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(getStage().getDebugColor());
        shapes.circle(getX(),getY(),1);
    }

}
