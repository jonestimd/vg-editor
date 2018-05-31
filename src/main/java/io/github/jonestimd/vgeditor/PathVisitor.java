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
package io.github.jonestimd.vgeditor;

import java.util.Iterator;

import javafx.geometry.Point2D;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;

public class PathVisitor {
    private final Path path;

    public PathVisitor(Path path) {
        this.path = path;
    }

    public boolean findSegment(SegmentPredicate predicate) {
        final Iterator<Point2D> iterator = new PointIterator();
        if (iterator.hasNext()) {
            Point2D last = iterator.next();
            while (iterator.hasNext()) {
                Point2D next = iterator.next();
                if (predicate.visit(last, next)) return true;
                last = next;
            }
        }
        return false;
    }

    private class PointIterator implements Iterator<Point2D> {
        private final Iterator<PathElement> iterator;
        private MoveTo start;

        public PointIterator() {
            this.iterator = path.getElements().iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Point2D next() {
            PathElement element = iterator.next();
            if (element instanceof MoveTo) return getPoint(start = (MoveTo) element);
            if (element instanceof LineTo) return getPoint((LineTo) element);
            if (element instanceof CubicCurveTo) return getPoint((CubicCurveTo) element);
            if (element instanceof QuadCurveTo) return getPoint((QuadCurveTo) element);
            if (element instanceof ArcTo) return getPoint((ArcTo) element);
            if (element instanceof ClosePath) return getPoint(start);
            throw new IllegalArgumentException("Unsupported path element: "+element.getClass());
        }
    }

    private static Point2D getPoint(MoveTo moveTo) {
        return new Point2D(moveTo.getX(), moveTo.getY());
    }

    private static Point2D getPoint(LineTo lineTo) {
        return new Point2D(lineTo.getX(), lineTo.getY());
    }

    private static Point2D getPoint(CubicCurveTo curveTo) {
        return new Point2D(curveTo.getX(), curveTo.getY());
    }

    private static Point2D getPoint(QuadCurveTo curveTo) {
        return new Point2D(curveTo.getX(), curveTo.getY());
    }

    private static Point2D getPoint(ArcTo arcTo) {
        return new Point2D(arcTo.getX(), arcTo.getY());
    }

    public interface SegmentPredicate {
        boolean visit(Point2D p1, Point2D p2);
    }
}
