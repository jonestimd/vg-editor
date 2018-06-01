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
import java.util.Optional;
import java.util.function.BiPredicate;

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

    /**
     * @return true if at least one element of the path matches the predicate.
     */
    public boolean some(SegmentPredicate predicate) {
        return find(predicate).isPresent();
    }

    public Optional<PathElement> find(SegmentPredicate predicate) {
        final Iterator<PathElement> iterator = path.getElements().iterator();
        if (iterator.hasNext()) {
            ElementHandler handler = new ElementHandler(predicate, iterator.next());
            while (iterator.hasNext()) {
                PathElement element = iterator.next();
                if (dispatch(element, handler)) return Optional.of(element);
            }
        }
        return Optional.empty();
    }

    private class ElementHandler {
        private final SegmentPredicate predicate;
        private Point2D start;
        private Point2D previous;

        public ElementHandler(SegmentPredicate predicate, PathElement first) {
            this.predicate = predicate;
            if (first instanceof MoveTo) start = previous = getPoint((MoveTo) first);
            else throw new IllegalArgumentException("Path does not start with MoveTo");
        }

        private <T> boolean test(T element, BiPredicate<Point2D, T> predicate, double nextX, double nextY) {
            if (predicate.test(previous, element)) return true;
            previous = new Point2D(nextX, nextY);
            return false;
        }

        public Boolean moveTo(MoveTo moveTo) {
            start = new Point2D(moveTo.getX(), moveTo.getY());
            return test(moveTo, predicate::test, moveTo.getX(), moveTo.getY());
        }

        public Boolean lineTo(LineTo lineTo) {
            return test(lineTo, predicate::test, lineTo.getX(), lineTo.getY());
        }

        public Boolean cubicCurveTo(CubicCurveTo cubicCurveTo) {
            return test(cubicCurveTo, predicate::test, cubicCurveTo.getX(), cubicCurveTo.getY());
        }

        public Boolean quadCurveTo(QuadCurveTo quadCurveTo) {
            return test(quadCurveTo, predicate::test, quadCurveTo.getX(), quadCurveTo.getY());
        }

        public Boolean arcTo(ArcTo arcTo) {
            return test(arcTo, predicate::test, arcTo.getX(), arcTo.getY());
        }

        public Boolean closePath(ClosePath closePath) {
            if (predicate.test(previous, closePath, start)) return true;
            previous = start;
            return false;
        }
    }

    private static Point2D getPoint(MoveTo moveTo) {
        return new Point2D(moveTo.getX(), moveTo.getY());
    }

    private boolean dispatch(PathElement element, ElementHandler handler) {
        if (element instanceof MoveTo) return handler.moveTo((MoveTo) element);
        if (element instanceof LineTo) return handler.lineTo((LineTo) element);
        if (element instanceof CubicCurveTo) return handler.cubicCurveTo((CubicCurveTo) element);
        if (element instanceof QuadCurveTo) return handler.quadCurveTo((QuadCurveTo) element);
        if (element instanceof ArcTo) return handler.arcTo((ArcTo) element);
        if (element instanceof ClosePath) return handler.closePath((ClosePath) element);
        throw new IllegalArgumentException("Unsupported path element: "+element.getClass());
    }

    public interface SegmentPredicate {
        boolean test(Point2D p1, MoveTo moveTo);
        boolean test(Point2D p1, LineTo lineTo);
        boolean test(Point2D p1, CubicCurveTo cubicCurveTo);
        boolean test(Point2D p1, QuadCurveTo quadCurveTo);
        boolean test(Point2D p1, ArcTo arcTo);
        boolean test(Point2D p1, ClosePath closePath, Point2D p2);
    }
}
