package de.kablion.qsnake.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Collections;

import de.kablion.qsnake.Application;
import de.kablion.qsnake.constants.DIM;

public class PlayerTail extends Group {
    private final Application app;
    private final Player player;

    private static final float RECORDS_PER_SECOND = 100000;
    private static final int SPLINE_SAMPLES = 100;

    private Array<Vector2> pathPoints = new Array<Vector2>();
    private CatmullRomSpline<Vector2> spline = new CatmullRomSpline<Vector2>();
    private float sinceLastRecord = 0;

    private Array<ParticleContainer> particleContainers = new Array<ParticleContainer>();

    public PlayerTail(Application app, Player player) {
        this.app = app;
        this.player = player;

        initPathPoints();
    }

    private void initPathPoints() {
        //there have to be at least 4 pathPoints
        for(int i=0;i<4;i++) {
            pathPoints.add(new Vector2(player.getX(),player.getY()));
        }
        updateSpline();
    }

    public void updateSpline() {
        Vector2[] dataSet = pathPoints.toArray(Vector2.class);
        for(int i = 0; i<dataSet.length; i++) {
            dataSet[i] = new Vector2(dataSet[i]);
        }
        spline.set(dataSet, false);
    }

    private void recordPlayerPosition() {
        Vector2 lastPosition = new Vector2(pathPoints.peek());
        Vector2 velocity = player.getVelocity();
        lastPosition.add(velocity.scl(sinceLastRecord));
        pathPoints.add(lastPosition);
        updateSpline();
    }

    private boolean isSplineLongEnough() {
        float splineLength = spline.approxLength(SPLINE_SAMPLES);
        float minLength = (particleContainers.size+1)*(DIM.PARTICLE_CONTAINER_LENGTH+ DIM.PARTICLE_CONTAINER_DISTANCE);
        return splineLength >= minLength;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        sinceLastRecord += delta;

        if(sinceLastRecord >= 1/RECORDS_PER_SECOND) {
            if(isSplineLongEnough()) {
                // reduce lenght of tail
                pathPoints.removeIndex(0);
            }
            recordPlayerPosition();
            sinceLastRecord = 0;
        }

        handleScreenWrapping();

        if(!particleContainers.isEmpty()) {
            //while container creation the other containers don't move
            if (!particleContainers.peek().isCreated())
                handleContainerCreation();
            else
                moveParticleContainers(delta);
        }
    }

    private void handleScreenWrapping() {
        /*
         * Screen Wrapping Concept:
         * change recordPlayerPosition to calcPlayerPosition using the velocity because of player teleportation
         * for drawing and collision detection check for each container on which screens he currently is and virtually change the position accordingly
         * if the whole player tail is on one screen (maybe another condition) teleport it back to the (0,0) screen
         * E.g. the tail could range over multiple screens (0,0),(0,1),(0,2) but for every container the visible part is correctly calculated
         */
        ArrayList<Vector2> screens = this.overlapsWhichScreens();

        if(!screens.contains(new Vector2(0,0))) {
            // teleport because tail is not on main screen
            for (Vector2 point:pathPoints) {
                point.add(-screens.get(0).x * DIM.WORLD_WIDTH, -screens.get(0).y * DIM.WORLD_HEIGHT);
            }
        }
    }

    /**
     * Determines which Screen the Container is seen on. For Screen Wrapping
     * @return List of those Screens, indicated by two integers
     * where 0/0 is the screen in the middle and -1/-1 is the screen above and left
     * search all 8 screens around the screen on which it's position is on
     */
    private ArrayList<Vector2> overlapsWhichScreens() {

        Rectangle screenHitbox = new Rectangle(0,0, DIM.WORLD_WIDTH, DIM.WORLD_HEIGHT);

        Rectangle tailHitbox = getHitbox();

        ArrayList<Vector2> screens = new ArrayList<Vector2>();

        //Find main screen (center of the Tail)
        int mainScreenX = (int)(tailHitbox.getCenter(new Vector2()).x/screenHitbox.width);
        int mainScreenY = (int)(tailHitbox.getCenter(new Vector2()).y/screenHitbox.height);

        //How many +- screens to search
        int offsetXOfScreens = (int)(tailHitbox.width/screenHitbox.width) + 1;
        int offsetYOfScreens = (int)(tailHitbox.height/screenHitbox.height) + 1;

        for(int x = mainScreenX-offsetXOfScreens; x<=mainScreenX+offsetXOfScreens; x++) {
            for(int y = mainScreenY-offsetYOfScreens; y<=mainScreenY+offsetYOfScreens; y++) {
                Rectangle tempHitbox = new Rectangle(screenHitbox);
                tempHitbox.x += x * tempHitbox.width;
                tempHitbox.y += y * tempHitbox.height;
                if(Intersector.overlaps(tempHitbox, tailHitbox)) {
                    screens.add(new Vector2(x,y));
                }
            }
        }

        return screens;
    }

    private void handleContainerCreation() {
        // checks whether a particle container has just finished being created and starts the creation for the next one.
        ParticleContainer firstWaitingContainer = null;
        for(int i = particleContainers.size-1; i >= 0; i--) {
            if(particleContainers.get(i).getCreationStatus() == ParticleContainer.CreationStatus.WAITING) {
                firstWaitingContainer = particleContainers.get(i);
            } else if (particleContainers.get(i).getCreationStatus() == ParticleContainer.CreationStatus.CREATING) {
                float splineLength = spline.approxLength(SPLINE_SAMPLES);
                Vector2 containerPosition = new Vector2(particleContainers.get(i).getX(),particleContainers.get(i).getY());
                float relativeContainerPosition = spline.approximate(containerPosition);
                float relativeContainerDistance = (DIM.PARTICLE_CONTAINER_LENGTH+DIM.PARTICLE_CONTAINER_DISTANCE) / splineLength;
                if(relativeContainerPosition <= 1 - relativeContainerDistance) {
                    particleContainers.get(i).finishCreation();
                }
                return;
            } else {
                break;
            }
        }
        if (firstWaitingContainer != null) {
            firstWaitingContainer.startCreation();
            firstWaitingContainer.setPosition(player.getX(),player.getY());
            firstWaitingContainer.setRotation(player.getRotation());
            addActor(firstWaitingContainer);
        }
    }

    private void moveParticleContainers(float delta) {
        // TODO make smoother/ better performance
        if(particleContainers.size>0) {
            Vector2 out = new Vector2();
            float splineLength = spline.approxLength(SPLINE_SAMPLES);
            float relativeContainerDistance = (DIM.PARTICLE_CONTAINER_LENGTH+DIM.PARTICLE_CONTAINER_DISTANCE) / splineLength;
            for (int i = 0; i < particleContainers.size; i++) {
                int positionInQueue = particleContainers.size - i;
                float relativePosition = 1 - positionInQueue * relativeContainerDistance;
                // setPosition
                spline.valueAt(out, relativePosition);
                particleContainers.get(i).setPosition(out.x, out.y);
                // setRotation
                spline.derivativeAt(out, relativePosition);
                particleContainers.get(i).setRotation(out.angle()+90);
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void drawDebug(ShapeRenderer shapes) {
        super.drawDebug(shapes);

        //draw the path points
        shapes.set(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.RED);
        for (Vector2 pathPoint:pathPoints) {
            shapes.circle(pathPoint.x,pathPoint.y,2);
        }

        // Draw the Path
        if(pathPoints.size > 2) {
            int k = pathPoints.size;
            Vector2[] dataSet = pathPoints.toArray(Vector2.class);
            shapes.set(ShapeRenderer.ShapeType.Line);
            shapes.setColor(Color.WHITE);
            Vector2 a = new Vector2();
            Vector2 b = new Vector2();
            for (int i = 0; i < k - 1; ++i) {
                spline.valueAt(a, ((float) i) / ((float) k - 1));
                spline.valueAt(b, ((float) (i + 1)) / ((float) k - 1));
                shapes.setColor(Color.WHITE);
                //Line
                shapes.line(a,b);
                //Line Vertices
                shapes.setColor(Color.RED);
                //shapes.circle(a.x,a.y,1);
            }
        }
    }

    public void addParticleContainer() {
        //TODO Make more gradually (not just appearing instantly)
        ParticleContainer particleContainer = new ParticleContainer(app);
        particleContainers.add(particleContainer);
        //addActor(particleContainer);
    }

    public Array<ParticleContainer> getParticleContainers() {
        return particleContainers;
    }

    private Rectangle getHitbox() {
        ArrayList<Float> pathPointsX = new ArrayList<Float>();
        ArrayList<Float> pathPointsY = new ArrayList<Float>();
        for (Vector2 point: pathPoints) {
            pathPointsX.add(point.x);
            pathPointsY.add(point.y);
        }

        float minX = Collections.min(pathPointsX);
        float maxX = Collections.max(pathPointsX);
        float minY = Collections.min(pathPointsY);
        float maxY = Collections.max(pathPointsY);

        return new Rectangle(minX,minY,maxX-minX,maxY-minY);
    }
}
