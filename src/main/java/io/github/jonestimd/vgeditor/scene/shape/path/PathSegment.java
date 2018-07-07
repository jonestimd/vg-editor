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
package io.github.jonestimd.vgeditor.scene.shape.path;

import io.github.jonestimd.vgeditor.scene.control.selection.SelectionController;
import javafx.geometry.Point2D;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;

/**
 * Helper class for elements of a {@link Path}.  Provides methods for getting points on the element and checking its distance to
 * a point.
 */
public abstract class PathSegment<T extends PathElement> {
    protected final Point2D start;
    protected final Point2D end;
    protected final T element;

    protected PathSegment(Point2D start, T element, Point2D end) {
        this.start = start;
        this.element = element;
        this.end = element.isAbsolute() ? end : end.add(start);
    }

    public Point2D getStart() {
        return start;
    }

    public T getElement() {
        return element;
    }

    public Point2D getEnd() {
        return end;
    }

    /**
     * Get the midpoint of the path element.  Used for placing the highlight cursor.
     */
    public abstract Point2D getMidpoint();

    /**
     * Get the squared distance from a point to the path element.  Used to check if the path is within the highlighting range of the cursor.
     */
    public abstract double getDistanceSquared(Point2D point);

    public boolean isInSelectionRange(Point2D cursor) {
        return getDistanceSquared(cursor) <= SelectionController.HIGHLIGHT_OFFSET_SQUARED;
    }

    /**
     * Get an instance of {@code PathSegment} for a path element.
     * @param start the start point of the path element
     * @param element the path element
     * @param end the end point of the path element
     */
    @SuppressWarnings("unchecked")
    public static <T extends PathElement> PathSegment<T> of(Point2D start, T element, Point2D end) {
        if (element instanceof MoveTo) return (PathSegment<T>) new MoveToSegment(start, (MoveTo) element);
        if (element instanceof LineTo) return (PathSegment<T>) new LineToSegment(start, (LineTo) element);
        if (element instanceof ClosePath) return (PathSegment<T>) new ClosePathSegment(start, (ClosePath) element, end);
        if (element instanceof CubicCurveTo) return (PathSegment<T>) new CubicCurveToSegment(start, (CubicCurveTo) element);
        if (element instanceof QuadCurveTo) return (PathSegment<T>) new QuadCurveToSegment(start, (QuadCurveTo) element);
        if (element instanceof ArcTo) return (PathSegment<T>) new ArcToSegment(start, (ArcTo) element);
        throw new IllegalArgumentException("Unsupported path element");
    }

    protected static double squaredDistance(Point2D p1, Point2D p2) {
        return squaredDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    protected static double squaredDistance(double x1, double y1, double x2, double y2) {
        double dx = x1-x2, dy = y1-y2;
        return dx*dx+dy*dy;
    }
}
