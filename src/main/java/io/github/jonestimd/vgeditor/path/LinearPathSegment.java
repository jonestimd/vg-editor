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
package io.github.jonestimd.vgeditor.path;

import javafx.geometry.Point2D;
import javafx.scene.shape.PathElement;

public abstract class LinearPathSegment<T extends PathElement> extends PathSegment<T> {
    private final Point2D midpoint;
    private final double squareLen;
    private final double dx, dy;

    protected LinearPathSegment(Point2D start, T element, Point2D end) {
        super(start, element, end);
        midpoint = new Point2D((start.getX()+end.getX())/2, (start.getY()+end.getY())/2);
        squareLen = squaredDistance(start, end);
        dx = end.getX()-start.getX();
        dy = end.getY()-start.getY();
    }

    public Point2D getMidpoint() {
        return midpoint;
    }

    @Override
    public double getDistanceSquared(Point2D cursor) {
        if (squareLen == 0) return squaredDistance(cursor, start);
        double projection = ((cursor.getX()-start.getX())*dx+(cursor.getY()-start.getY())*dy)/squareLen;
        if (projection < 0) return squaredDistance(cursor, start);
        if (projection > 1) return squaredDistance(cursor, end);
        return squaredDistance(cursor.getX(), cursor.getY(), start.getX()+projection*dx, start.getY()+projection*dy);
    }
}
