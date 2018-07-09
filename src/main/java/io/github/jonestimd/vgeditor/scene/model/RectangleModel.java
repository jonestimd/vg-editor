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

import io.github.jonestimd.vgeditor.scene.control.selection.SelectionController;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

public class RectangleModel extends AnchoredShapeModel<Rectangle> implements LocationModel, SizeModel {
    public static final String TOOL_FXML = "RectangleTool.fxml";

    public RectangleModel(Group group) {
        this(group, new Rectangle());
    }

    public RectangleModel(Group group, double x, double y, double width, double height) {
        this(group, new Rectangle(x, y, width, height));
    }

    protected RectangleModel(Group group, Rectangle rectangle) {
        super(group, TOOL_FXML, rectangle);
    }

    @Override
    public double getX() {
        return shape.getX();
    }

    @Override
    public void setX(double x) {
        shape.setX(x);
        getAnchor().translate(shape, shape.getWidth(), shape.getHeight());
    }

    @Override
    public double getY() {
        return shape.getY();
    }

    @Override
    public void setY(double y) {
        shape.setY(y);
        getAnchor().translate(shape, shape.getWidth(), shape.getHeight());
    }

    @Override
    public double getWidth() {
        return shape.getWidth();
    }

    @Override
    public void setWidth(double width) {
        shape.setWidth(width);
        getAnchor().translate(shape, width, shape.getHeight());

    }

    @Override
    public double getHeight() {
        return shape.getHeight();
    }

    @Override
    public void setHeight(double height) {
        shape.setHeight(height);
        getAnchor().translate(shape, shape.getWidth(), height);
    }

    public double getArcWidth() {
        return shape.getArcWidth()/2;
    }

    public void setArcWidth(double value) {
        shape.setArcWidth(value*2);
    }

    public double getArcHeight() {
        return shape.getArcHeight()/2;
    }

    public void setArcHeight(double value) {
        shape.setArcHeight(value*2);
    }

    protected boolean isInSelectionRange(Point2D localPoint) {
        if (shape.getFill() == null) {
            return isInBounds(shape.getX(), shape.getWidth(), localPoint.getX()) && isInBounds(shape.getY(), shape.getHeight(), localPoint.getY()) &&
                    (isNotInside(shape.getX(), shape.getWidth(), localPoint.getX()) || isNotInside(shape.getY(), shape.getHeight(), localPoint.getY()));
        }
        return shape.contains(localPoint);
    }

    public Point2D getMarkerLocation(double screenX, double screenY) {
        double x = shape.getX(), y = shape.getY();
        Point2D cursor = shape.screenToLocal(screenX, screenY).subtract(x, y);
        return new Point2D(x+selectEdge(cursor.getX(), shape.getWidth()), y+selectEdge(cursor.getY(), shape.getHeight()));
    }

    private static double selectEdge(double value, double size) {
        if (value <= SelectionController.HIGHLIGHT_OFFSET) return 0;
        if (value >= size-SelectionController.HIGHLIGHT_OFFSET) return size;
        return size/2;
    }
}
