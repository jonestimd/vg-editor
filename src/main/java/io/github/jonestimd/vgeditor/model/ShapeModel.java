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

import java.util.List;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;

public abstract class ShapeModel<T extends Shape> implements NodeModel {
    protected final transient T shape;

    protected ShapeModel(Group group, T shape) {
        this.shape = shape;
        group.getChildren().add(shape);
        shape.setUserData(this);
    }

    public T getShape() {
        return shape;
    }

    @Override
    public void remove() {
        Parent parent = shape.getParent();
        if (parent instanceof Pane) ((Pane) parent).getChildren().remove(shape);
        else if (parent instanceof Group) ((Group) parent).getChildren().remove(shape);
        else throw new IllegalStateException("Unexpected parent type: "+parent.getClass().getName());
    }

    @Override
    public double getRotate() {
        return shape.getRotate();
    }

    @Override
    public void setRotate(double angle) {
        shape.setRotate(angle);
    }

    @Override
    public List<Transform> getTransforms() {
        return shape.getTransforms();
    }

    public Paint getFill() {
        return shape.getFill();
    }

    public void setFill(Paint paint) {
        shape.setFill(paint);
    }

    public Paint getStroke() {
        return shape.getStroke();
    }

    public void setStroke(Paint paint) {
        shape.setStroke(paint);
    }

    public double getStrokeWidth() {
        return shape.getStrokeWidth();
    }

    public void setStrokeWidth(double strokeWidth) {
        shape.setStrokeWidth(strokeWidth);
    }
}
