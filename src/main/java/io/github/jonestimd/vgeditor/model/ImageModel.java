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

import io.github.jonestimd.vgeditor.scene.NodeAnchor;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;

public class ImageModel implements NodeModel, AnchoredModel {
    private ImageView imageView;
    private NodeAnchor anchor = NodeAnchor.TOP_LEFT;

    public ImageModel(Group group, Image image) {
        this.imageView = new ImageView(image);
        imageView.setUserData(this);
        group.getChildren().add(imageView);
    }

    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public void remove() {
        Parent parent = imageView.getParent();
        if (parent instanceof Pane) ((Pane) parent).getChildren().remove(imageView);
        else if (parent instanceof Group) ((Group) parent).getChildren().remove(imageView);
        else throw new IllegalStateException("Unexpected parent type: "+parent.getClass().getName());
    }

    @Override
    public NodeAnchor getAnchor() {
        return anchor;
    }

    @Override
    public void setAnchor(NodeAnchor anchor) {
        this.anchor = anchor;
    }

    @Override
    public NodeAnchor getResizeAnchor(Point2D screenPoint) {
        return NodeAnchor.forResize(imageView.screenToLocal(screenPoint), getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public double getX() {
        return imageView.getX();
    }

    @Override
    public void setX(double x) {
        imageView.setX(x);
    }

    @Override
    public double getY() {
        return imageView.getY();
    }

    @Override
    public void setY(double y) {
        imageView.setY(y);
    }

    @Override
    public double getWidth() {
        return imageView.getFitWidth();
    }

    @Override
    public void setWidth(double width) {
        imageView.setFitWidth(width);
    }

    @Override
    public double getHeight() {
        return imageView.getFitHeight();
    }

    @Override
    public void setHeight(double height) {
        imageView.setFitHeight(height);
    }

    @Override
    public double getRotate() {
        return imageView.getRotate();
    }

    @Override
    public void setRotate(double angle) {
        imageView.setRotate(angle);
    }

    @Override
    public List<Transform> getTransforms() {
        return imageView.getTransforms();
    }

    @Override
    public boolean isInSelectionRange(Point2D screenPoint) {
        return false;
    }
}
