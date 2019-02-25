package de.kablion.qsnake.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import de.kablion.qsnake.Application;
import de.kablion.qsnake.collision.IntersectorExtension;
import de.kablion.qsnake.constants.DIM;
import de.kablion.qsnake.constants.PATHS;
import de.kablion.qsnake.stages.WorldStage;

public class Player extends Actor {

    private final Application app;

    private static final float DEFAULT_SPEED = 100; // Units per second
    private static final float DEFAULT_ROTATION_SPEED = 120; // Degrees per second
    private static final float HEAD_WIDTH = 50;
    private static final float HEAD_HEIGHT = 40;
    private static final float HEAD_HITBOX_RADIUS = 30;
    private static final float HEAD_HITBOX_OFFSET_X = 0;
    private static final float HEAD_HITBOX_OFFSET_Y = -5;

    private float speed = DEFAULT_SPEED;
    private float rotationSpeed = DEFAULT_ROTATION_SPEED;
    private Vector2 velocity = new Vector2(0,1);

    private TextureRegion headTextureRegion;

    private PlayerTail tail;

    public Player(Application app) {
        this.app = app;

        this.tail = new PlayerTail(this.app, this);

        this.setPosition(DIM.WORLD_WIDTH/2f, DIM.WORLD_HEIGHT/2f);
        this.setRotation(0);
        this.setSize(HEAD_WIDTH,HEAD_HEIGHT);
        this.setOrigin(Align.center);
        this.speedChanged();
        this.headTextureRegion = app.assets.get(PATHS.ENTITY_SPRITES, TextureAtlas.class).findRegion("player_head");
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        tail.act(delta);
        move(delta);
        checkCollisions(delta);
    }

    private void checkCollisions(float delta) {
        checkCollisionWithParticle(app.gameScreen.worldStage.getParticle());
        checkCollisionWithTail();
    }

    private void checkCollisionWithParticle(Particle particle) {
        Circle headHitbox = new Circle(getX(),getY(),HEAD_HITBOX_RADIUS);
        Circle particleHitbox = new Circle(particle.getX(),particle.getY(),particle.getRadius());

        if(Intersector.overlaps(headHitbox,particleHitbox)) {
            absorbParticle(particle);
        }
    }

    private void checkCollisionWithTail() {
        Circle headHitbox = new Circle(getX(),getY(),HEAD_HITBOX_RADIUS);
        Array<ParticleContainer> particleContainers = tail.getParticleContainers();
        for(int i = 0; i<particleContainers.size; i++) {
            ParticleContainer container = particleContainers.get(i);
            Polygon containerHitbox = new Polygon();
            float bottom = -container.getHeight();
            float top = -bottom;
            float left = -container.getWidth();
            float right = -left;
            float[] vertices = {left,bottom,right,bottom,right,top,left,top};
            containerHitbox.setVertices(vertices);
            containerHitbox.setPosition(container.getX(), container.getY());
            containerHitbox.setRotation(container.getRotation());
            if(IntersectorExtension.overlaps(headHitbox,containerHitbox)) {
                handleContainerHit(container);
            }
        }
    }

    private void handleContainerHit(ParticleContainer container) {
        app.gameScreen.gameOver();
    }

    private void absorbParticle(Particle particle) {
        tail.addParticleContainer();
        ((WorldStage)getStage()).createParticle();
    }

    private void move(float delta) {
        moveBy(velocity.x*delta, velocity.y*delta);
    }

    public void steerLeft(float delta) {
        rotateBy(rotationSpeed*delta);
    }
    public void steerRight(float delta) {
        rotateBy(-rotationSpeed*delta);
    }

    protected void speedChanged() {
        velocity.setLength(speed);
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();
    }

    @Override
    protected void rotationChanged() {
        super.rotationChanged();
        velocity.setAngle(90+getRotation());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(headTextureRegion,getX()-getOriginX(),getY()-getOriginY(),getOriginX(),getOriginY(),getWidth(),getHeight(),getScaleX(),getScaleY(),getRotation());

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
        shapes.circle(getX(),getY(),5);
        // draw hitbox
        shapes.set(ShapeRenderer.ShapeType.Line);
        shapes.setColor(getStage().getDebugColor());
        shapes.circle(getX(),getY(),HEAD_HITBOX_RADIUS);
    }

    public PlayerTail getTail() {
        return tail;
    }
}
