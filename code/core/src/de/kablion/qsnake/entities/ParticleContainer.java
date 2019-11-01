package de.kablion.qsnake.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

import de.kablion.qsnake.Application;
import de.kablion.qsnake.collision.IntersectorExtension;
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

    /**
     * Determines which Screen the Container is seen on. For Screen Wrapping
     * @return List of those Screens, indicated by two integers
     * where 0/0 is the screen in the middle and -1/-1 is the screen above and left
     * search all 8 screens around the screen on which it's position is on
     */
    private ArrayList<Vector2> overlapsWhichScreens() {
        Rectangle screenHitbox = new Rectangle(0,0, DIM.WORLD_WIDTH, DIM.WORLD_HEIGHT);

        Polygon containerHitbox = getHitbox();

        ArrayList<Vector2> screens = new ArrayList<Vector2>();

        //Find main screen
        float DmainScreenX = (getX()/screenHitbox.width);
        int mainScreenX = (int)(getX()/screenHitbox.width);
        int mainScreenY = (int)(getY()/screenHitbox.height);

        for(int x = mainScreenX-1; x<=mainScreenX+1; x++) {
            for(int y = mainScreenY-1; y<=mainScreenY+1; y++) {
                Rectangle tempHitbox = new Rectangle(screenHitbox);
                tempHitbox.x += x * tempHitbox.width;
                tempHitbox.y += y * tempHitbox.height;
                if(IntersectorExtension.overlaps(tempHitbox, containerHitbox)) {
                    screens.add(new Vector2(x,y));
                }
            }
        }

        return screens;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        float x = getX()-getOriginX();
        float y = getY()-getOriginY();
        ArrayList<Vector2> screens = this.overlapsWhichScreens();

        for (Vector2 screen: screens) {
            batch.draw(chargeTextureRegion,
                    x - screen.x * DIM.WORLD_WIDTH,
                    y - screen.y * DIM.WORLD_HEIGHT,
                    getOriginX(),getOriginY(),getWidth(),getHeight(),getScaleX(),getScaleY(),getRotation());
            batch.draw(containerTextureRegion,
                    x - screen.x * DIM.WORLD_WIDTH,
                    y - screen.y * DIM.WORLD_HEIGHT,
                    getOriginX(),getOriginY(),getWidth(),getHeight(),getScaleX(),getScaleY(),getRotation());
        }
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

    public ArrayList<Polygon> getAllVisibleHitboxes() {
        ArrayList<Polygon> hitboxes = new ArrayList<Polygon>();

        ArrayList<Vector2> screens = this.overlapsWhichScreens();
        float actualX = getX();
        float actualY = getY();
        for(Vector2 screen:screens) {
            setX(actualX - screen.x*DIM.WORLD_WIDTH);
            setY(actualY - screen.y*DIM.WORLD_HEIGHT);
            hitboxes.add(getHitbox());
        }
        setPosition(actualX, actualY);
        return hitboxes;
    }

}
