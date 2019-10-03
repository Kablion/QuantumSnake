package de.kablion.qsnake.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import de.kablion.qsnake.Application;
import de.kablion.qsnake.constants.DIM;
import de.kablion.qsnake.constants.PATHS;

// TODO Particle is not visible because it is behind the HUD

public class Particle extends Actor {

    private final Application app;


    private float radius = DIM.PARTICLE_RADIUS;

    private TextureRegion textureRegion;

    public Particle(Application app) {
        this.app = app;

        float x = MathUtils.random(radius, DIM.WORLD_WIDTH-radius);
        float y = MathUtils.random(radius, DIM.WORLD_HEIGHT-radius);
        this.setPosition(x,y);

        this.textureRegion = app.assets.get(PATHS.ENTITY_SPRITES, TextureAtlas.class).findRegion("particle");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(textureRegion,getX()-getRadius(),getY()-getRadius(),getOriginX(),getOriginY(),getRadius()*2,getRadius()*2,getScaleX(),getScaleY(),getRotation());
    }


    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);

        // draw Hitbox
        shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(getStage().getDebugColor());
        Circle hitbox = getHibox();
        shapes.circle(hitbox.x,hitbox.y,hitbox.radius);
    }

    public float getRadius() {
        return radius;
    }

    public Circle getHibox() {
        return new Circle(getX(),getY(),getRadius());
    }
}
