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
import javafx.scene.shape.QuadCurveTo;

public class QuadCurveToSegment extends BezierPathSegment<QuadCurveTo> {
    public QuadCurveToSegment(Point2D start, QuadCurveTo curveTo) {
        super(start, curveTo, new Point2D(curveTo.getX(), curveTo.getY()), new BezierFunction(start, curveTo));
    }

    private static class BezierFunction implements DoubleFunction<Point2D> {
        private final Point2D start;
        private final QuadCurveTo curveTo;
        private final double cx, cy;

        private BezierFunction(Point2D start, QuadCurveTo curveTo) {
            this.start = start;
            this.curveTo = curveTo;
            if (curveTo.isAbsolute()) {
                cx = curveTo.getControlX();
                cy = curveTo.getControlY();
            }
            else {
                cx = curveTo.getControlX() + start.getX();
                cy = curveTo.getControlY() + start.getY();
            }
        }

        public Point2D apply(double t) {
            double u = 1-t, u2 = u*u, t2 = t*t;
            double x = u2*start.getX()+2*u*t*cx+t2*curveTo.getX();
            double y = u2*start.getY()+2*u*t*cy+t2*curveTo.getY();
            return new Point2D(x, y);
        }
    }
}
