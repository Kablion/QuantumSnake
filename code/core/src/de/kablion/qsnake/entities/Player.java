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

import java.util.ArrayList;

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
        move(delta);
        handleScreenWrapping();
        checkCollisions(delta);
    }

    private void handleScreenWrapping() {
        /*
         * Screen Wrapping Concept:
         * Assume 9 playing screens side by side with the actual playing screens in the middle.
         * Once the player leaves the middle screens teleport it back to the middle.
         * While drawing check if the player overlaps with on of the surrounding screens and draw the player of the opposite screen as well.
         * E.g: The player is on the top left corner of the screen.
         *      now 3 additional players have to be drawn: bottom, right, bottom right
        **/

        ArrayList<Vector2> screens = overlapsWhichScreens();

        if(!screens.contains(new Vector2(0,0))) {
            // teleport because mainship is not on main screen
            setPosition(getX() - screens.get(0).x * DIM.WORLD_WIDTH, getY() - screens.get(0).y * DIM.WORLD_HEIGHT);
        }

        //TODO: Screen wrapping collisions
    }

    private void checkCollisions(float delta) {
        if (worldStage.isBorderDeadly()) {
            checkCollisionWithBorder();
        }

        //check all other collisions with each player instance due to screen wrapping by temporarily changing the position
        ArrayList<Vector2> screens = this.overlapsWhichScreens();
        float actualX = getX();
        float actualY = getY();
        for(Vector2 screen:screens) {
            setX(actualX - screen.x*DIM.WORLD_WIDTH);
            setY(actualY - screen.y*DIM.WORLD_HEIGHT);
            checkCollisionWithParticle(app.gameScreen.worldStage.getParticle());
            checkCollisionWithTail();
        }
        setPosition(actualX, actualY);
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
            ArrayList<Polygon> containerHitboxes = container.getAllVisibleHitboxes();
            for(Polygon containerHitbox:containerHitboxes) {
                if (IntersectorExtension.overlaps(headHitbox, containerHitbox)) {
                    handleContainerHit(container);
                }
            }
        }
    }

    private void checkCollisionWithBorder() {
        if(Intersector.overlaps(this.getHitbox(), worldStage.getBorder()) && !worldStage.getBorder().contains(this.getHitbox())) {
            app.gameScreen.gameOver();
        }
    }

    /**
     * Determines which of the 9 Screens the player is seen on. For Screen Wrapping
     * @return List of those Screens, indicated by two integers between -1 and 1 where 0/0 is the screen in the middle.
     */
    private ArrayList<Vector2> overlapsWhichScreens() {
        Rectangle screenHitbox = new Rectangle(0,0, DIM.WORLD_WIDTH, DIM.WORLD_HEIGHT);
        Circle playerHitbox = getHitbox();

        ArrayList<Vector2> screens = new ArrayList<Vector2>();

        for(int x = -1; x<=1; x++) {
            for(int y = -1; y<=1; y++) {
                Rectangle tempHitbox = new Rectangle(screenHitbox);
                tempHitbox.x += x * tempHitbox.width;
                tempHitbox.y += y * tempHitbox.height;
                if(Intersector.overlaps(playerHitbox, tempHitbox)) {
                    screens.add(new Vector2(x,y));
                }
            }
        }

        return screens;
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
        float x = getX()-getOriginX();
        float y = getY()-getOriginY();
        ArrayList<Vector2> screens = this.overlapsWhichScreens();
        for (Vector2 screen: screens) {
            batch.draw(headTextureRegion,
                    x - screen.x * DIM.WORLD_WIDTH,
                    y - screen.y * DIM.WORLD_HEIGHT,
                    getOriginX(),getOriginY(),getWidth(),getHeight(),getScaleX(),getScaleY(),getRotation());
        }
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

    public Vector2 getVelocity() {
        return new Vector2(velocity);
    }
}
