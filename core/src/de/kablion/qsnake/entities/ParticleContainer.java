package de.kablion.qsnake.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import de.kablion.qsnake.Application;
import de.kablion.qsnake.constants.DIM;
import de.kablion.qsnake.constants.PATHS;

public class ParticleContainer extends Actor {

    private final Application app;

    private float probability = 0;

    private TextureRegion containerTextureRegion;
    private TextureRegion chargeTextureRegion;

    public ParticleContainer(Application app) {
        this.app = app;

        setSize(DIM.PARTICLE_CONTAINER_WIDTH,DIM.PARTICLE_CONTAINER_LENGTH);
        setOrigin(Align.center);
        this.containerTextureRegion = app.assets.get(PATHS.ENTITY_SPRITES, TextureAtlas.class).findRegion("particle_container");
        this.chargeTextureRegion = app.assets.get(PATHS.ENTITY_SPRITES, TextureAtlas.class).findRegion("particle_container_charge");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(chargeTextureRegion,getX()-getOriginX(),getY()-getOriginY(),getOriginX(),getOriginY(),getWidth(),getHeight(),getScaleX(),getScaleY(),getRotation());
        batch.draw(containerTextureRegion,getX()-getOriginX(),getY()-getOriginY(),getOriginX(),getOriginY(),getWidth(),getHeight(),getScaleX(),getScaleY(),getRotation());

    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        if (!getDebug()) return;
        // draw bounds
        /*shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(getStage().getDebugColor());
        shapes.rect(getX()-getOriginX(), getY()-getOriginY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());*/
        // draw Hitbox
        shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(getStage().getDebugColor());
        shapes.polygon(getHitbox().getTransformedVertices());
        // draw position
        shapes.set(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(getStage().getDebugColor());
        shapes.circle(getX(),getY(),1);

    }

    public Polygon getHitbox() {
        Polygon containerHitbox = new Polygon();
        float bottom = -getHeight()/2;
        float top = -bottom;
        float left = -getWidth()/2;
        float right = -left;
        float[] vertices = {left,bottom,right,bottom,right,top,left,top};
        containerHitbox.setVertices(vertices);
        containerHitbox.setPosition(getX(), getY());
        containerHitbox.setRotation(getRotation());
        return containerHitbox;
    }

}
