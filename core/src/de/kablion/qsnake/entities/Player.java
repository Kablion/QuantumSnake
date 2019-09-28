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
    private final WorldStage worldStage;

    private float speed = DIM.PLAYER_SPEED;
    private float rotationSpeed = DIM.PLAYER_ROTATION_SPEED;
    private Vector2 velocity = new Vector2(0,1);

    private TextureRegion headTextureRegion;

    private PlayerTail tail;

    public Player(Application app, WorldStage worldStage) {
        this.app = app;
        this.worldStage = worldStage;

        this.setPosition(DIM.WORLD_WIDTH/2f, DIM.WORLD_HEIGHT/2f);
        this.setRotation(0);
        this.setSize(DIM.PLAYER_HEAD_WIDTH,DIM.PLAYER_HEAD_HEIGHT);
        this.setOrigin(Align.center);
        this.speedChanged();
        this.headTextureRegion = app.assets.get(PATHS.ENTITY_SPRITES, TextureAtlas.class).findRegion("player_head");

        this.tail = new PlayerTail(this.app, this);
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
        checkCollisionWithBorder();
    }

    private void checkCollisionWithParticle(Particle particle) {
        Circle headHitbox = getHitbox();
        Circle particleHitbox = particle.getHibox();

        if(Intersector.overlaps(headHitbox,particleHitbox)) {
            absorbParticle(particle);
        }
    }

    private void checkCollisionWithTail() {
        Circle headHitbox = getHitbox();
        Array<ParticleContainer> particleContainers = tail.getParticleContainers();
        for(int i = 0; i<particleContainers.size; i++) {
            ParticleContainer container = particleContainers.get(i);
            Polygon containerHitbox = container.getHitbox();
            if(IntersectorExtension.overlaps(headHitbox,containerHitbox)) {
                handleContainerHit(container);
            }
        }
    }

    private void checkCollisionWithBorder() {
        if(Intersector.overlaps(this.getHitbox(), worldStage.getBorder()) && !worldStage.getBorder().contains(this.getHitbox())) {
            // TODO: Either die or come out of the other side of the screen
            app.gameScreen.gameOver();
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
        if(!worldStage.isPaused()) rotateBy(rotationSpeed*delta);
    }
    public void steerRight(float delta) {
        if(!worldStage.isPaused()) rotateBy(-rotationSpeed*delta);
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
        Circle hitbox = getHitbox();
        shapes.circle(hitbox.x,hitbox.y,hitbox.radius);
    }

    public PlayerTail getTail() {
        return tail;
    }

    public int countCollectedParticles() {
        return tail.getParticleContainers().size;
    }

    public Circle getHitbox() {
        Vector2 hitbox_offset = new Vector2(velocity);
        hitbox_offset.setLength(1);
        hitbox_offset.scl(DIM.PLAYER_HEAD_HITBOX_OFFSET);
        return new Circle(getX() + hitbox_offset.x,
                getY() + hitbox_offset.y,
                DIM.PLAYER_HEAD_HITBOX_RADIUS);
    }
}
