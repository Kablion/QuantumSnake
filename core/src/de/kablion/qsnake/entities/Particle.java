package de.kablion.qsnake.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import de.kablion.qsnake.Application;
import de.kablion.qsnake.constants.DIMENSIONS;

public class Particle extends Actor {

    private final Application app;

    private static final float DEFAULT_RADIUS = 20;

    private float radius = DEFAULT_RADIUS;

    public Particle(Application app) {
        this.app = app;

        float x = MathUtils.random(radius,DIMENSIONS.WORLD_WIDTH-radius);
        float y = MathUtils.random(radius,DIMENSIONS.WORLD_HEIGHT-radius);
        this.setPosition(x,y);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);
        shapes.set(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(1,0,0,1);
        shapes.circle(getX(),getY(),getRadius());
    }

    public float getRadius() {
        return radius;
    }
}
