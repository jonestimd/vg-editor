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
package io.github.jonestimd.vgeditor.shape.path;

import java.util.function.DoubleFunction;

import javafx.geometry.Point2D;
import javafx.scene.shape.CubicCurveTo;

public class CubicCurveToSegment extends BezierPathSegment<CubicCurveTo> {

    public CubicCurveToSegment(Point2D start, CubicCurveTo curveTo) {
        super(start, curveTo, new Point2D(curveTo.getX(), curveTo.getY()), new BezierFunction(start, curveTo));
    }

    private static class BezierFunction implements DoubleFunction<Point2D> {
        private final Point2D start;
        private final CubicCurveTo curveTo;
        private final double c1x, c1y;
        private final double c2x, c2y;

        public BezierFunction(Point2D start, CubicCurveTo curveTo) {
            this.start = start;
            this.curveTo = curveTo;
            if (curveTo.isAbsolute()) {
                c1x = curveTo.getControlX1();
                c1y = curveTo.getControlY1();
                c2x = curveTo.getControlX2();
                c2y = curveTo.getControlY2();
            }
            else {
                c1x = curveTo.getControlX1()+start.getX();
                c1y = curveTo.getControlY1()+start.getY();
                c2x = curveTo.getControlX2()+start.getX();
                c2y = curveTo.getControlY2()+start.getY();
            }
        }

        public Point2D apply(double t) {
            double u = 1-t, u2 = u*u, u3 = u2*u, t2 = t*t, t3 = t2*t;
            double x = u3*start.getX()+3*(t*u2*c1x+t2*u*c2x)+t3*curveTo.getX();
            double y = u3*start.getY()+3*(t*u2*c1y+t2*u*c2y)+t3*curveTo.getY();
            return new Point2D(x, y);
        }
    }
}
