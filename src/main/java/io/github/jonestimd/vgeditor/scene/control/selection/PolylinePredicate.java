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

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polyline;

public class PolylinePredicate extends LineSegmentPredicate implements Predicate<Polyline> {
    public PolylinePredicate(double screenX, double screenY) {
        super(screenX, screenY);
    }

    public boolean test(Polyline polyline) {
        return findSegment(polyline).isPresent();
    }

    public Point2D getMarkerPosition(Polyline polyline) {
        return findSegment(polyline).map(i -> {
            ObservableList<Double> points = polyline.getPoints();
            return new Point2D((points.get(i-2)+points.get(i))/2, (points.get(i-1)+points.get(i+1))/2);
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
