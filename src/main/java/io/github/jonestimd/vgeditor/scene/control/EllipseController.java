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

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import io.github.jonestimd.vgeditor.scene.NodeAnchor;
import io.github.jonestimd.vgeditor.scene.model.EllipseModel;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 * Controller for the Ellipse tool window.
 */
public class EllipseController extends ShapeController<EllipseModel> {
    public EllipseController() {
        super(EllipseModel::new);
    }

    @Override
    protected Dimension2D getNewNodeSize(Point2D diagramStart, Point2D diagramEnd) {
        return new Dimension2D(Math.abs(diagramEnd.getX()-diagramStart.getX()), Math.abs(diagramEnd.getY()-diagramStart.getY()));
    }

    @Override
    protected BiConsumer<Point2D, Point2D> getResizeDragHandler(NodeAnchor resizeAnchor) {
        return new ResizeDragHandler(resizeAnchor, getModel());
    }

    private class ResizeDragHandler implements BiConsumer<Point2D, Point2D> {
        private final double startWidth, startHeight;
        private final ResizeCalculator resizeDragCalculator;

        public ResizeDragHandler(NodeAnchor resizeAnchor, EllipseModel model) {
            resizeDragCalculator = new ResizeCalculator(resizeAnchor, model.getRotate());
            this.startWidth = model.getWidth();
            this.startHeight = model.getHeight();
        }

        @Override
        public void accept(Point2D start, Point2D end) {
            Dimension2D adjustment = resizeDragCalculator.apply(start, end);
            setSizeInputs(Math.abs(startWidth+adjustment.getWidth()), Math.abs(startHeight+adjustment.getHeight()));
            setNodeSize();
        }
    }

    private static class ResizeCalculator implements BiFunction<Point2D, Point2D, Dimension2D> {
        private final double widthFactor, heightFactor;
        private final double cos, sin;

        public ResizeCalculator(NodeAnchor resizeAnchor, double rotation) {
            double angle = Math.toRadians(rotation);
            cos = Math.cos(angle);
            sin = Math.sin(angle);
            widthFactor = resizeAnchor.dx == 0 ? 0 : 1/Math.cos(resizeAnchor.angle);
            heightFactor = resizeAnchor.dy == 0 ? 0 : 1/Math.sin(resizeAnchor.angle);
        }

        @Override
        public Dimension2D apply(Point2D start, Point2D end) {
            double dx = end.getX()-start.getX();
            double dy = end.getY()-start.getY();
            double dWidth = (dx*cos+dy*sin)*widthFactor;
            double dHeight = (-dx*sin+dy*cos)*heightFactor;
            return new Dimension2D(dWidth, dHeight);
        }
    }
}