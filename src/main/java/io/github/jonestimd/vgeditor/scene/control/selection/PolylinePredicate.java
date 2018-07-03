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
package io.github.jonestimd.vgeditor.scene.control.selection;

import java.util.Optional;
import java.util.function.Predicate;

import io.github.jonestimd.vgeditor.scene.Geometry;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polyline;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;

/**
 * Predicate to check if a point is within the highlight range of segment of a {@link Polyline}.
 */
public class PolylinePredicate extends LineSegmentPredicate implements Predicate<Polyline> {
    public PolylinePredicate(double screenX, double screenY) {
        super(screenX, screenY);
    }

    /**
     * @return if the point is within range of any segment of the {@link Polyline}
     */
    public boolean test(Polyline polyline) {
        return findSegment(polyline).isPresent();
    }

    /**
     * @return the point to highlight (an endpoint of midpoint of the nearest line segment)
     * @throws IllegalArgumentException the {@link Polyline} is not within highlight range of the point
     */
    public Point2D getMarkerPosition(Polyline polyline) {
        return findSegment(polyline).map(i -> {
            ObservableList<Double> points = polyline.getPoints();
            Point2D cursor = polyline.screenToLocal(screenX, screenY);
            Point2D start = new Point2D(points.get(i-2), points.get(i-1));
            if (Geometry.distanceSquared(start, cursor) <= HIGHLIGHT_OFFSET_SQUARED) return start;
            Point2D end = new Point2D(points.get(i), points.get(i+1));
            if (Geometry.distanceSquared(end, cursor) <= HIGHLIGHT_OFFSET_SQUARED) return end;
            return start.midpoint(end);
        }).orElseThrow(() -> new IllegalArgumentException("Cursor not in range"));
    }

    private Optional<Integer> findSegment(Polyline polyline) {
        Point2D cursor = polyline.screenToLocal(screenX, screenY);
        ObservableList<Double> points = polyline.getPoints();
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
}
