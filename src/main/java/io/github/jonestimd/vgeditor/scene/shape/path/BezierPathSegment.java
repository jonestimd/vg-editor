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

import java.util.function.DoubleFunction;

import javafx.geometry.Point2D;
import javafx.scene.shape.PathElement;

public abstract class BezierPathSegment<T extends PathElement> extends PathSegment<T> {
    private static final int SCANS = 25;
    private static final double ERROR = 1;
    private final DoubleFunction<Point2D> bezierPoint;
    private final Point2D[] points;
    private final Point2D midpoint;

    protected BezierPathSegment(Point2D start, T element, Point2D end, DoubleFunction<Point2D> bezierPoint) {
        super(start, element, end);
        this.bezierPoint = bezierPoint;
        this.midpoint = bezierPoint.apply(0.5);
        points = new Point2D[SCANS+1];
        for (int i = 0; i <= SCANS ; i++) {
            points[i] = bezierPoint.apply((0d+i)/SCANS);
        }
    }

    @Override
    public Point2D getMidpoint() {
        return midpoint;
    }

    @Override
    public double getDistanceSquared(Point2D point) {
        return squaredDistance(point, closestPoint(point));
    }

    private Point2D closestPoint(Point2D cursor) {
        int mIndex = 0;
        double min = Double.POSITIVE_INFINITY;
        for (int i = SCANS; i >= 0; i--) {
            double d2 = squaredDistance(cursor, points[i]);
            if (d2 < min) {
                min = d2;
                mIndex = i;
            }
        }
        double t0 = Math.max((mIndex-1d)/SCANS, 0d);
        double t1 = Math.min((mIndex+1d)/SCANS, 1d);
        DoubleFunction<Double> getDistance = t -> squaredDistance(cursor, bezierPoint.apply(t));
        return bezierPoint.apply(localMinimum(t0, t1, getDistance, ERROR));
    }

    private double localMinimum(double minX, double maxX, DoubleFunction<Double> func, double error) {
        double m = minX, n = maxX, k = (n+m)/2;
        while ((n-m) > error) {
            k = (n+m)/2;
            if (func.apply(k-error) < func.apply(k+error)) n = k;
            else m = k;
        }
        return k;
    }

    private Point2D interpolate(Point2D p1, Point2D p2, double t) {
        return new Point2D(p1.getX()+t*(p2.getX()-p1.getX()), p1.getY()+t*(p2.getY()-p1.getY()));
    }
}
