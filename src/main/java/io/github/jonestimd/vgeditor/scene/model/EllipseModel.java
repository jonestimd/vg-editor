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

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Ellipse;

public class EllipseModel extends AnchoredShapeModel<Ellipse> {
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
        return shape.getRadiusX()*2;
    }

    @Override
    public void setWidth(double width) {
        shape.setRadiusX(width/2);
    }

    @Override
    public double getHeight() {
        return shape.getRadiusY()*2;
    }

    @Override
    public void setHeight(double height) {
        shape.setRadiusY(height/2);
    }

    @Override
    public boolean isInSelectionRange(double screenX, double screenY) {
        return shape.contains(shape.screenToLocal(screenX, screenY));
    }

    @Override
    public Point2D getMarkerLocation(double screenX, double screenY) {
        throw new UnsupportedOperationException();
    }
}
