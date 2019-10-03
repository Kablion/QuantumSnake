package de.kablion.qsnake.collision;

import com.badlogic.gdx.math.*;

import java.lang.reflect.Array;
import java.util.Arrays;

public final class IntersectorExtension {

    public static boolean overlaps(Circle circle, Polygon polygon) {
        // TODO Test
        float []vertices=polygon.getTransformedVertices();
        Vector2 center = new Vector2(circle.x, circle.y);
        Vector2 vert1 = new Vector2();
        Vector2 vert2 = new Vector2();
        float squareRadius=circle.radius*circle.radius;
        for (int i=0;i<vertices.length;i+=2){
            if (i==0){
                vert1.set(vertices[vertices.length - 2], vertices[vertices.length - 1]);
            } else {
                vert1.set(vertices[i-2], vertices[i-1]);
            }
            vert2.set(vertices[i], vertices[i+1]);
            if (Intersector.intersectSegmentCircle(vert1, vert2, center, squareRadius)) {
                return true;
            }
        }
        return polygon.contains(circle.x, circle.y);
    }

    public static boolean overlaps(Rectangle rectangle, Polygon polygon) {
        // TODO Test
        Polygon rectPolygon = new Polygon();
        rectPolygon.setPosition(rectangle.getX(),rectangle.getY());
        float[] vertices = {0,0,
                rectangle.getWidth(),0,
                rectangle.getWidth(),rectangle.getHeight(),
                0,rectangle.getHeight()};
        rectPolygon.setVertices(vertices);
        return Intersector.overlapConvexPolygons(rectPolygon,polygon);
    }

    // only works for convex polygons
    public static boolean overlaps(Polygon polygon1, Polygon polygon2) {
        // TODO Test
        float[][] vertices = {polygon1.getTransformedVertices(),polygon2.getTransformedVertices()};
        // check clockwise or counter-clockwise for both polygons
        for(int p = 0; p < 2; p++) {
            float sum = 0;
            for (int i = 0; i < vertices[p].length; i += 2) {
                if (i + 2 >= vertices[p].length) {
                    sum += (vertices[p][0] - vertices[p][i]) * (vertices[p][1] + vertices[p][i + 1]);
                } else {
                    sum += (vertices[p][i + 2] - vertices[p][i]) * (vertices[p][i + 3] + vertices[p][i + 1]);
                }
            }
            boolean clockwise = (sum >= 0);
            if (clockwise) {
                // reverse vertices
                float[] tempVerts = new float[vertices[p].length];
                for(int i = 0; i<vertices[p].length-1; i+=2) {
                    tempVerts[vertices[p].length-i-2] = vertices[p][i];
                    tempVerts[vertices[p].length-i-1] = vertices[p][i+1];
                }
                vertices[p] = tempVerts;
            }
        }
        return Intersector.overlapConvexPolygons(vertices[0],vertices[1],null);
    }

}
