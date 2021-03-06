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
package io.github.jonestimd.vgeditor.scene.model;

import io.github.jonestimd.vgeditor.scene.NodeAnchor;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Circle;

public class CircleModel extends ShapeModel<Circle> implements LocationModel, SizeModel {
    public CircleModel(Group group) {
        this(group, new Circle());
    }

    public CircleModel(Group group, double cx, double cy, double radius) {
        this(group, new Circle(cx, cy, radius));
    }

    protected CircleModel(Group group, Circle circle) {
        super(group, "", circle);
    }

    @Override
    public double getX() {
        return shape.getCenterX();
    }

    @Override
    public void setX(double x) {
        shape.setCenterX(x);
    }

    @Override
    public double getY() {
        return shape.getCenterY();
    }

    @Override
    public void setY(double y) {
        shape.setCenterY(y);
    }

    @Override
    public double getWidth() {
        return shape.getRadius();
    }

    @Override
    public void setWidth(double width) {
        shape.setRadius(width);
    }

    @Override
    public double getHeight() {
        return shape.getRadius();
    }

    @Override
    public void setHeight(double height) {
        shape.setRadius(height);
    }

    @Override
    public NodeAnchor getResizeAnchor(Point2D screenPoint) {
        return null; // TODO
    }

    @Override
    protected boolean isInSelectionRange(Point2D localCursor) {
        return shape.contains(localCursor);
    }

    @Override
    public Point2D getMarkerLocation(double screenX, double screenY) {
        throw new UnsupportedOperationException();
    }
}
