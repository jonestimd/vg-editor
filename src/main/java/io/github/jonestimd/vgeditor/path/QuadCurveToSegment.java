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
import javafx.scene.shape.QuadCurveTo;

public class QuadCurveToSegment extends BezierPathSegment<QuadCurveTo> {
    private final double cx, cy;

    public QuadCurveToSegment(Point2D start, QuadCurveTo curveTo) {
        super(start, curveTo, new Point2D(curveTo.getX(), curveTo.getY()));
        if (element.isAbsolute()) {
            cx = element.getControlX();
            cy = element.getControlY();
        }
        else {
            cx = element.getControlX() + start.getX();
            cy = element.getControlY() + start.getY();
        }
    }

    protected Point2D bezierPoint(double t) {
        double u = 1-t, u2 = u*u, t2 = t*t;
        double x = u2*start.getX()+2*u*t*cx+t2*end.getX();
        double y = u2*start.getY()+2*u*t*cy+t2*end.getY();
        return new Point2D(x, y);
    }
}
