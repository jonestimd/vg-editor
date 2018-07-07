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

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Line;

public class LineModel extends ShapeModel<Line> {
    public LineModel(Group group) {
        super(group, new Line());
    }

    public LineModel(Group group, double x1, double y1, double x2, double y2) {
        super(group, new Line(x1, y1, x2, y2));
    }

    public double getStartX() {
        return shape.getStartX();
    }

    public void setStartX(double value) {
        shape.setStartX(value);
    }

    public double getStartY() {
        return shape.getStartY();
    }

    public void setStartY(double value) {
        shape.setStartY(value);
    }

    public double getEndX() {
        return shape.getEndX();
    }

    public void setEndX(double value) {
        shape.setEndX(value);
    }

    public double getEndY() {
        return shape.getEndY();
    }

    public void setEndY(double value) {
        shape.setEndY(value);
    }

    @Override
    public boolean isInSelectionRange(Point2D screenPoint) {
        return false;
    }
}
