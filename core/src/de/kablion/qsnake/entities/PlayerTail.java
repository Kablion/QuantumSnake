package de.kablion.qsnake.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import de.kablion.qsnake.Application;


//TODO When Particle is collected while pathPoints are not enough the game crashs

public class PlayerTail extends Group {
    private final Application app;
    private final Player player;

    private static final float RECORDS_PER_SECOND = 30;
    private static final float CONTAINER_DISTANCE = 30;
    private static final int SPLINE_SAMPLES = 100;

    private Array<Vector2> pathPoints = new Array<Vector2>();
    private CatmullRomSpline<Vector2> spline = new CatmullRomSpline<Vector2>();
    private float sinceLastRecord = 1/RECORDS_PER_SECOND+1;

    private Array<ParticleContainer> particleContainers = new Array<ParticleContainer>();

    public PlayerTail(Application app, Player player) {
        this.app = app;
        this.player = player;
    }

    public void recordPlayerPosition(float x, float y) {
        pathPoints.add(new Vector2(x,y));
        Vector2[] dataSet = pathPoints.toArray(Vector2.class);
        spline.set(dataSet, false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if(sinceLastRecord >= 1/RECORDS_PER_SECOND) {
            if(pathPoints.size>2 && spline.approxLength(SPLINE_SAMPLES)>=(particleContainers.size+2)*(ParticleContainer.CONTAINER_LENGTH+CONTAINER_DISTANCE)) {
                // reduce lenght of tail
                pathPoints.removeIndex(0);
            }
            recordPlayerPosition(player.getX(), player.getY());
            sinceLastRecord = 0;
        } else {
            sinceLastRecord += delta;
        }

        moveParticleContainers(delta);
    }

    private void moveParticleContainers(float delta) {
        // TODO make smoother
        if(particleContainers.size>0) {
            Vector2 out = new Vector2();
            float splineLength = spline.approxLength(SPLINE_SAMPLES);
            float relativeContainerDistance = (ParticleContainer.CONTAINER_LENGTH+CONTAINER_DISTANCE) / splineLength;
            for (int i = 0; i < particleContainers.size; i++) {
                int positionInQueue = particleContainers.size - i;
                float relativePosition = 1 - positionInQueue * relativeContainerDistance;
                // setPosition
                spline.valueAt(out, relativePosition);
                particleContainers.get(i).setPosition(out.x, out.y);
                // setRotation
                spline.derivativeAt(out, relativePosition);
                particleContainers.get(i).setRotation(out.angle());
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

        // Draw the Path
        if(pathPoints.size > 2) {
            int k = pathPoints.size * 2; //double the precision as the recorded Points
            Vector2[] dataSet = pathPoints.toArray(Vector2.class);
            shapes.set(ShapeRenderer.ShapeType.Line);
            shapes.setColor(1, 1, 1, 1);
            for (int i = 0; i < k - 1; ++i) {
                shapes.line(spline.valueAt(new Vector2(), ((float) i) / ((float) k - 1)), spline.valueAt(new Vector2(), ((float) (i + 1)) / ((float) k - 1)));
            }
        }
    }

    public void addParticleContainer() {
        ParticleContainer particleContainer = new ParticleContainer();
        particleContainers.add(particleContainer);
        addActor(particleContainer);
    }

    public Array<ParticleContainer> getParticleContainers() {
        return particleContainers;
    }
}
