// The MIT License (MIT)
//
// Copyright (c) 2018 Tim Jones
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
package io.github.jonestimd.vgeditor.scene.model;

import java.util.Optional;

import io.github.jonestimd.vgeditor.scene.Geometry;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Polyline;

import static io.github.jonestimd.vgeditor.scene.Geometry.*;
import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;

public class PolylineModel extends ShapeModel<Polyline> {
    public PolylineModel(Group group, double... points) {
        this(group, new Polyline(points));
    }

    protected PolylineModel(Group group, Polyline polyline) {
        super(group, "", polyline);
    }

    @Override
    public boolean isInSelectionRange(double screenX, double screenY) {
        Point2D cursor = shape.screenToLocal(screenX, screenY);
        Bounds bounds = shape.getBoundsInLocal();
        return isInBounds(bounds.getMinX(), bounds.getWidth(), cursor.getX())
                && isInBounds(bounds.getMinY(), bounds.getHeight(), cursor.getY())
                && findSegment(cursor).isPresent();
    }

    @Override
    public Point2D getMarkerLocation(double screenX, double screenY) {
        Point2D cursor = shape.screenToLocal(screenX, screenY);
        return findSegment(cursor).map(i -> {
            ObservableList<Double> points = shape.getPoints();
            Point2D start = new Point2D(points.get(i-2), points.get(i-1));
            if (Geometry.distanceSquared(start, cursor) <= HIGHLIGHT_OFFSET_SQUARED) return start;
            Point2D end = new Point2D(points.get(i), points.get(i+1));
            if (Geometry.distanceSquared(end, cursor) <= HIGHLIGHT_OFFSET_SQUARED) return end;
            return start.midpoint(end);
        }).orElseThrow(() -> new IllegalArgumentException("Cursor not in range"));
    }

    private Optional<Integer> findSegment(Point2D cursor) {
        ObservableList<Double> points = shape.getPoints();
        if (points.size() > 2) {
            double x1 = points.get(0);
            double y1 = points.get(1);
            for (int i = 2; i < points.size(); i += 2) {
                double x2 = points.get(i);
                double y2 = points.get(i+1);
                if (isInHighlightRange(cursor, x1, y1, x2, y2)) return Optional.of(i);
                x1 = x2;
                y1 = y2;
            }
        }
        return Optional.empty();
    }

    /**
     * @param cursor the point to check (in the shape's local coordinate space)
     * @param x1 the x coordinate of the starting point of the line segment
     * @param y1 the y coordinate of the starting point of the line segment
     * @param x2 the x coordinate of the ending point of the line segment
     * @param y2 the y coordinate of the ending point of the line segment
     * @return true if the line segment defined by the 2 points is within the highlight range of the {@code cursor}.
     */
    private static boolean isInHighlightRange(Point2D cursor, double x1, double y1, double x2, double y2) {
        double squareLen = distanceSquared(x1, y1, x2, y2);
        if (squareLen == 0) return distanceSquared(cursor.getX(), cursor.getY(), x1, y1) <= HIGHLIGHT_OFFSET_SQUARED;
        double projection = ((cursor.getX()-x1)*(x2-x1)+(cursor.getY()-y1)*(y2-y1))/squareLen;
        double squareDist = distanceSquared(cursor.getX(), cursor.getY(), x1+projection*(x2-x1), y1+projection*(y2-y1));
        return projection >= 0 && projection <= 1 && squareDist <= HIGHLIGHT_OFFSET_SQUARED;
    }
}
