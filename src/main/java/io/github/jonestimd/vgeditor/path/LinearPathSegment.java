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
    protected LinearPathSegment(Point2D start, T element, Point2D end) {
        super(start, element, end);
    }

    public Point2D getMidpoint() {
        return new Point2D((start.getX()+end.getX())/2, (start.getY()+end.getY())/2);
    }

    @Override
    public double getDistanceSquared(Point2D cursor) {
        double cx = cursor.getX(), cy = cursor.getY();
        double x1 = start.getX(), y1 = start.getY();
        double x2 = end.getX(), y2 = end.getY();
        double dx = x2-x1, dy = y2-y1;

        double squareLen = squaredDistance(x1, y1, x2, y2);
        if (squareLen == 0) return squaredDistance(cx, cy, x1, y1);
        double projection = ((cx-x1)*dx+(cy-y1)*dy)/squareLen;
        if (projection < 0) return squaredDistance(cx, cy, x1, y1);
        if (projection > 1) return squaredDistance(cx, cy, x2, y2);
        return squaredDistance(cx, cy, x1+projection*dx, y1+projection*dy);
    }
}
