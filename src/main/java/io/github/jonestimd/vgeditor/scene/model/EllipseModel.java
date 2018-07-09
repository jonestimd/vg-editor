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
import javafx.scene.shape.Ellipse;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;

public class EllipseModel extends ShapeModel<Ellipse> implements LocationModel, SizeModel {
    public static final String TOOL_FXML = "EllipseTool.fxml";

    public EllipseModel(Group group) {
        this(group, new Ellipse());
    }

    public EllipseModel(Group group, double cx, double cy, double rx, double ry) {
        this(group, new Ellipse(cx, cy, rx, ry));
    }

    protected EllipseModel(Group group, Ellipse ellipse) {
        super(group, TOOL_FXML, ellipse);
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
        return shape.getRadiusX();
    }

    @Override
    public void setWidth(double width) {
        shape.setRadiusX(width);
    }

    @Override
    public double getHeight() {
        return shape.getRadiusY();
    }

    @Override
    public void setHeight(double height) {
        shape.setRadiusY(height);
    }

    @Override
    public NodeAnchor getResizeAnchor(Point2D screenPoint) {
        Point2D delta = shape.screenToLocal(screenPoint).subtract(shape.getCenterX(), shape.getCenterY());
        if (Math.abs(delta.getY()) < shape.getRadiusY()/4 && Math.abs(delta.getX()) > shape.getRadiusX()*3/4) {
            return delta.getX() > 0 ? NodeAnchor.RIGHT : NodeAnchor.LEFT;
        }
        if (Math.abs(delta.getY()) > shape.getRadiusY()*3/4 && Math.abs(delta.getX()) < shape.getRadiusX()*4) {
            return delta.getY() > 0 ? NodeAnchor.BOTTOM : NodeAnchor.TOP;
        }
        if (delta.getY() > 0) {
            return delta.getX() > 0 ? NodeAnchor.BOTTOM_RIGHT : NodeAnchor.BOTTOM_LEFT;
        }
        return delta.getX() > 0 ? NodeAnchor.TOP_RIGHT : NodeAnchor.TOP_LEFT;
    }

    @Override
    protected boolean isInSelectionRange(Point2D localCursor) {
        Point2D delta = localCursor.subtract(shape.getCenterX(), shape.getCenterY());
        double dxSquared = getSquare(delta.getX()), dySquared = getSquare(delta.getY());
        if (shape.getFill() != null) return isInside(dxSquared, dySquared, 0);
        return isInside(dxSquared, dySquared, HIGHLIGHT_OFFSET) && !isInside(dxSquared, dySquared, -HIGHLIGHT_OFFSET);
    }

    private boolean isInside(double dxSquared, double dySquared, int offset) {
        return dxSquared/getSquare(shape.getRadiusX()+offset)+dySquared/getSquare(shape.getRadiusY()+offset) <= 1;
    }

    private static double getSquare(double value) {
        return value*value;
    }

    @Override
    public Point2D getMarkerLocation(double screenX, double screenY) {
        double angle = getMarkerAngle(shape.screenToLocal(screenX, screenY));
        return new Point2D(getX(angle), getY(angle));
    }

    private double getMarkerAngle(Point2D cursor) {
        Point2D delta = cursor.subtract(shape.getCenterX(), shape.getCenterY());
        if (Math.abs(delta.getY()) < shape.getRadiusY()/4 && Math.abs(delta.getX()) > shape.getRadiusX()*3/4) {
            return delta.getX() > 0 ? 0 : Math.PI;
        }
        if (Math.abs(delta.getY()) > shape.getRadiusY()*3/4 && Math.abs(delta.getX()) < shape.getRadiusX()*4) {
            return Math.signum(delta.getY())*Math.PI/2;
        }
        return Math.signum(delta.getY())*(Math.PI/4 + (delta.getX() > 0 ? 0 : Math.PI/2));
    }

    private double getX(double angle) {
        return shape.getCenterX()+shape.getRadiusX()*Math.cos(angle);
    }

    private double getY(double angle) {
        return shape.getCenterY()+shape.getRadiusY()*Math.sin(angle);
    }
}
