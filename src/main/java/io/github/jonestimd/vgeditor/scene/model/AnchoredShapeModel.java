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
import javafx.scene.shape.Shape;

public abstract class AnchoredShapeModel<T extends Shape> extends ShapeModel<T> implements AnchoredModel {
    private NodeAnchor anchor = NodeAnchor.TOP_LEFT;

    protected AnchoredShapeModel(Group group, String toolFxml, T shape) {
        super(group, toolFxml, shape);
    }

    public NodeAnchor getAnchor() {
        return anchor;
    }

    public void setAnchor(NodeAnchor anchor) {
        this.anchor = anchor;
        anchor.translate(shape, getWidth(), getHeight());
    }

    public NodeAnchor getResizeAnchor(Point2D screenPoint) {
        return NodeAnchor.forResize(shape.screenToLocal(screenPoint), getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void setRotate(double angle) {
        super.setRotate(angle);
        anchor.translate(shape, getWidth(), getHeight());
    }
}
