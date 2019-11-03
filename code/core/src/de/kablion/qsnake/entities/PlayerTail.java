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


//TODO When Particle is collected while pathPoints are not enough the game crashs

public class PlayerTail extends Group {
    private final Application app;
    private final Player player;

    private static final float RECORDS_PER_SECOND = 1;
    private static final float RECORDS_PER_CONTAINER = 5;
    private static final int SPLINE_LENGTH_SAMPLES = 100;

    private static final float SPLINE_LENGTH_PER_PATHPOINT = (DIM.PARTICLE_CONTAINER_LENGTH+ DIM.PARTICLE_CONTAINER_DISTANCE)/RECORDS_PER_CONTAINER;

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
        //there have to be at least 4 points in pathPoints (see the handlePathPoints concept)
        for(int i = 0; i<4; i++) {
            pathPoints.add(new Vector2(player.getX(),player.getY()));
        }

        updateSpline();

        // Make an initial Path behind the player for quickly collected particles (kinda a hack)
        /*
        for(int i = 3; i>=0; i--) {
            pathPoints.add(new Vector2(player.getX(), player.getY() - i * (DIM.PARTICLE_CONTAINER_LENGTH + DIM.PARTICLE_CONTAINER_DISTANCE)));
        }
        updateSpline();
         */
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        handlePathPoints(delta);

        handleScreenWrapping();

        if(!particleContainers.isEmpty()) {
            //while container creation the other containers don't move
            if (!particleContainers.peek().isCreated())
                handleContainerCreation();
            else
                moveParticleContainers(delta);
        }
    }

    private boolean isSplineLongEnough() {
        float splineLength = spline.approxLength(SPLINE_LENGTH_SAMPLES);
        float minLength = (particleContainers.size+1)*(DIM.PARTICLE_CONTAINER_LENGTH+ DIM.PARTICLE_CONTAINER_DISTANCE);
        return splineLength >= minLength;
    }

    private void handlePathPoints(float delta) {
        /**
         * New Concept:
         * There are 2 phases: 1) lengthening and 2) moving
         * 1:
         * If the Path is not long enough to contain all containers lengthen it
         * Do this by moving the last point along with the player and fixing it according to RECORDS_PER_SECOND
         * fix it by cloning the last point
         * 2:
         * if the path has not to be lengthened
         * move the last point with the player
         * and move every other point along the spline with the same velocity as the player
         *
         * Side Note:
         * In a CatmullRomSpline the first and last points only affect the direction
         * This means the first 2 and last 2 points of pathPoints have to be the same, so that every recorded point is in the spline
         */


        Vector2 playerVelocityScaled = player.getVelocity().scl(delta);

        //move the last point along with the player
        pathPoints.peek().add(playerVelocityScaled);
        //the second last point has to be the same (see the side note)
        pathPoints.get(pathPoints.size-2).set(pathPoints.peek());

        if(!isSplineLongEnough()) {
            //Phase 1 Lengthening
            float splineLength = spline.approxLength(SPLINE_LENGTH_SAMPLES);

            if(splineLength/(pathPoints.size-3) >= SPLINE_LENGTH_PER_PATHPOINT) {
                //clone the last point to save it
                pathPoints.add(new Vector2(pathPoints.get(pathPoints.size-2)));
                sinceLastRecord = 0;
            }
        } else {
            //Phase 2 Moving
            // move every point (except the last) along the spline
            Vector2 newPosition = new Vector2();
            float playerVelocityScaledLen = playerVelocityScaled.len();
            float splineLength = spline.approxLength(SPLINE_LENGTH_SAMPLES);
            float relativeMove = playerVelocityScaledLen/splineLength;
            //the first one extra
            spline.valueAt(newPosition, relativeMove);
            pathPoints.get(0).set(newPosition);
            pathPoints.get(1).set(newPosition);
            for (int i = 2; i < pathPoints.size-2; i++) {
                // locate fails for the first point because it is doubled
                float relativePosition = ((float)(i-1))/((float)(pathPoints.size-3));
                spline.valueAt(newPosition, relativePosition+relativeMove);
                pathPoints.get(i).set(newPosition);
            }
            // the first point has to be equal to the first one (see the side note)
            pathPoints.first().set(pathPoints.get(1));
        }

        updateSpline();

        /**
         * Old Concept:
         * Record the Player Position and build a smooth path from this.
         * Record it every frame but only save the last recorded point according to RECORDS_PER_SECOND
         * so move the current point to the player
         * if RECORDS_PER_SECOND clone the last Path Point
         * so that the last path point from now on stays at its position
         * the path has to stay a certain length
         * so move the first point along the path until it is after the second point then delete the first point
         *
         * Side Note:
         * In a CatmullRomSpline the first and last points only affect the direction
         * This means the first 2 and last 2 points of pathPoints have to be the same, so that every recorded point is in the spline
         *
         * This is the old concept. The problem:
         * In a CatmullRomSpline the values between 0-1 are not distributed evenly along the path
         * Rather the values are evenly distributed between each controllpoint
         * E.g with 3 controllpoints (a,b,c) 0-0.5 is distributed between a and b
         * and 0.5-1 is distributed between b and c
         * but with this concept the points are fixed so the containers only move when a new point is added or removed

        sinceLastRecord += delta;

        Vector2 playerVelocityPerFrame = player.getVelocity().scl(delta);

        //record Player Position (move last PathPoint)
        pathPoints.peek().add(playerVelocityPerFrame);
        //the second point has to be the same (see the side note)
        pathPoints.get(pathPoints.size-2).set(pathPoints.peek());

        if(spline.approxLength(SPLINE_SAMPLES)>=(particleContainers.size+2)*(DIM.PARTICLE_CONTAINER_LENGTH+ DIM.PARTICLE_CONTAINER_DISTANCE)) {
            //move second point to the third point
            //the first point is only a copy of the second point (see the side note)
            Vector2 velocityFirstPoint = new Vector2();
            spline.derivativeAt(velocityFirstPoint, 0);
            velocityFirstPoint.setLength(playerVelocityPerFrame.len());
            pathPoints.get(1).add(velocityFirstPoint);
            if (pathPoints.get(1).dst(pathPoints.get(2)) <= playerVelocityPerFrame.len()) {
                pathPoints.removeIndex(2);
            }
            pathPoints.first().set(pathPoints.get(1));
        }

        if(sinceLastRecord >= 1/RECORDS_PER_SECOND) {
            //clone the last point to save it
            pathPoints.add(new Vector2(pathPoints.get(pathPoints.size-2)));
            sinceLastRecord = 0;
        }

        updateSpline();
        */
    }

    public void updateSpline() {
        Vector2[] dataSet = pathPoints.toArray(Vector2.class);
        for(int i=0;i<dataSet.length;i++) {
            dataSet[i] = new Vector2(dataSet[i]);
        }
        spline.set(dataSet, false);
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
            updateSpline();
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
                float splineLength = spline.approxLength(SPLINE_LENGTH_SAMPLES);
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
        /**
         * Concept:
         * First Index is the last in queue
         * 1:
         * Just divide the spline in particleContainers.size parts and put the container at its position
         * 2:
         * set a const PARTICLE_CONTAINER_DISTANCE and place each container PARTICLE_CONTAINER_DISTANCE appart from each other
         */
        Vector2 out = new Vector2();
        for (int i = 0; i < particleContainers.size; i++) {
            // setPosition
            spline.valueAt(out, ((float)i)/((float)(particleContainers.size-1)));
            particleContainers.get(i).setPosition(out.x, out.y);
            // setRotation
            spline.derivativeAt(out, ((float)i)/((float)(particleContainers.size-1)));
            particleContainers.get(i).setRotation(out.angle()+90);
        }
        // TODO make smoother
        /* This is the 2nd Concept but I couldn't get it to work
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
         */
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
            int k = pathPoints.size * 10; //double the precision as the recorded Points
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
