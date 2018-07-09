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
package io.github.jonestimd.vgeditor.scene.control;

import java.util.function.BiFunction;

import io.github.jonestimd.vgeditor.scene.NodeAnchor;
import io.github.jonestimd.vgeditor.scene.control.ResizeDragCalculator.Offset2D;
import javafx.geometry.Point2D;

public class ResizeDragCalculator implements BiFunction<Point2D, Point2D, Offset2D> {
    private final double xxFactor, xyFactor, yxFactor, yyFactor;
    private final int widthFactor, heightFactor;
    private final double cos, sin;

    public ResizeDragCalculator(NodeAnchor resizeAnchor, NodeAnchor nodeAnchor, double rotation, int scale) {
        double angle = Math.toRadians(rotation);
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        int xFactor = resizeAnchor.dx == -nodeAnchor.dx || nodeAnchor.dx == 0 ? 0 : resizeAnchor.dx;
        int yFactor = resizeAnchor.dy == -nodeAnchor.dy || nodeAnchor.dy == 0 ? 0 : resizeAnchor.dy;
        xxFactor = xFactor*cos;
        xyFactor = xFactor*sin;
        yxFactor = yFactor*sin;
        yyFactor = yFactor*cos;
        widthFactor = nodeAnchor.dx == 0 ? scale*resizeAnchor.dx : resizeAnchor.dx;
        heightFactor = nodeAnchor.dy == 0 ? scale*resizeAnchor.dy : resizeAnchor.dy;
    }

    @Override
    public Offset2D apply(Point2D start, Point2D end) {
        double dx = end.getX()-start.getX();
        double dy = end.getY()-start.getY();
        double dWidth = (dx*cos+dy*sin)*widthFactor;
        double dHeight = (-dx*sin+dy*cos)*heightFactor;
        return new Offset2D(dWidth*xxFactor-dHeight*yxFactor, dWidth*xyFactor+dHeight*yyFactor, dWidth, dHeight);
    }

    public static class Offset2D {
        public final double dx, dy;
        public final double dWidth, dHeight;

        public Offset2D(double dx, double dy, double dWidth, double dHeight) {
            this.dx = dx;
            this.dy = dy;
            this.dWidth = dWidth;
            this.dHeight = dHeight;
        }
    }
}