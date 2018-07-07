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
package io.github.jonestimd.vgeditor.model;

import io.github.jonestimd.vgeditor.scene.NodeAnchor;
import io.github.jonestimd.vgeditor.scene.control.selection.RectanglePredicate;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class RectangleModel implements ShapeModel {
    private final transient Rectangle rectangle;
    private NodeAnchor anchor;

    public RectangleModel(Group group) {
        rectangle = new Rectangle();
        rectangle.setUserData(this);
        group.getChildren().add(rectangle);
    }

    public Rectangle getNode() {
        return rectangle;
    }

    @Override
    public void remove() {
        Parent parent = rectangle.getParent();
        if (parent instanceof Pane) ((Pane) parent).getChildren().remove(rectangle);
        else if (parent instanceof Group) ((Group) parent).getChildren().remove(rectangle);
        else throw new IllegalStateException("Unexpected parent type: "+parent.getClass().getName());
    }

    @Override
    public double getX() {
        return rectangle.getX();
    }

    @Override
    public void setX(double x) {
        rectangle.setX(x);
        anchor.translate(rectangle, rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public double getY() {
        return rectangle.getY();
    }

    @Override
    public void setY(double y) {
        rectangle.setY(y);
        anchor.translate(rectangle, rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public double getWidth() {
        return rectangle.getWidth();
    }

    @Override
    public void setWidth(double width) {
        rectangle.setWidth(width);
        anchor.translate(rectangle, width, rectangle.getHeight());

    }

    @Override
    public double getHeight() {
        return rectangle.getHeight();
    }

    @Override
    public void setHeight(double height) {
        rectangle.setHeight(height);
        anchor.translate(rectangle, rectangle.getWidth(), height);
    }

    @Override
    public double getRotate() {
        return rectangle.getRotate();
    }

    @Override
    public void setRotate(double angle) {
        rectangle.setRotate(angle);
        anchor.translate(rectangle, rectangle.getWidth(), rectangle.getHeight());
    }

    public double getArcWidth() {
        return rectangle.getArcWidth()/2;
    }

    public void setArcWidth(double value) {
        rectangle.setArcWidth(value*2);
    }

    public double getArcHeight() {
        return rectangle.getArcHeight()/2;
    }

    public void setArcHeight(double value) {
        rectangle.setArcHeight(value*2);
    }

    @Override
    public NodeAnchor getAnchor() {
        return anchor;
    }

    @Override
    public void setAnchor(NodeAnchor anchor) {
        this.anchor = anchor;
        anchor.translate(rectangle, rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public Paint getFill() {
        return rectangle.getFill();
    }

    @Override
    public void setFill(Paint paint) {
        rectangle.setFill(paint);
    }

    @Override
    public Paint getStroke() {
        return rectangle.getStroke();
    }

    @Override
    public void setStroke(Paint paint) {
        rectangle.setStroke(paint);
    }

    @Override
    public double getStrokeWidth() {
        return rectangle.getStrokeWidth();
    }

    @Override
    public void setStrokeWidth(double strokeWidth) {
        rectangle.setStrokeWidth(strokeWidth);
    }

    @Override
    public NodeAnchor getResizeAnchor(Point2D screenPoint) {
        return NodeAnchor.forResize(rectangle.screenToLocal(screenPoint), rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    @Override
    public boolean isStartDrag(Point2D screenPoint) {
        return new RectanglePredicate(screenPoint.getX(), screenPoint.getY()).test(rectangle);
    }
}
