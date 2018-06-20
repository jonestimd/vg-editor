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

import java.util.Optional;
import java.util.function.Supplier;

import io.github.jonestimd.vgeditor.scene.NodeAnchor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public abstract class ShapeController<T extends Shape> implements NodeController<T> {
    private NodeAnchor nodeAnchor = NodeAnchor.TOP_LEFT;
    @FXML
    private TextField anchorX;
    @FXML
    private TextField anchorY;
    @FXML
    private TextField width;
    @FXML
    private TextField height;
    @FXML
    private GridPane anchorParent;
    @FXML
    private FillPaneController fillPaneController;
    @FXML
    private StrokePaneController strokePaneController;

    private final MouseInputHandler mouseInputHandler = new MouseInputHandler(this::startDrag, this::continueDrag);
    private final Supplier<T> nodeFactory;

    private T node;

    protected ShapeController(Supplier<T> nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    @Override
    public MouseInputHandler getMouseHandler() {
        return mouseInputHandler;
    }

    @Override
    public T getNode() {
        return node;
    }

    @Override
    public boolean newNode() {
        if (node == null || isValid(node)) {
            this.node = nodeFactory.get();
            anchorX.setText("");
            anchorY.setText("");
            width.setText("");
            height.setText("");
            fillPaneController.setNode(node);
            strokePaneController.setNode(node);
            anchorX.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    public void cancelCreate() {
        if (node != null && !isValid(node)) {
            Parent parent = node.getParent();
            if (parent instanceof Pane) ((Pane) parent).getChildren().remove(node);
            else if (parent instanceof Group) ((Group) parent).getChildren().remove(node);
            else throw new IllegalStateException("Unexpected parent type: " + parent.getClass().getName());
            node = null;
        }
    }

    protected abstract boolean isValid(T node);

    public void selectAnchor(ActionEvent event) {
        setAnchor(NodeAnchor.valueOf(((RadioButton) event.getSource()).getId()));
    }

    private void setAnchor(NodeAnchor nodeAnchor) {
        this.nodeAnchor = nodeAnchor;
        nodeAnchor.translateX(node, getWidth(node));
        nodeAnchor.translateY(node, getHeight(node));
    }

    protected abstract double getWidth(T node);

    protected abstract double getHeight(T node);

    public void setNodeX(KeyEvent event) {
        setX(node, TextFields.parseDouble(event, 0));
    }

    protected abstract void setX(T node, double x);

    public void setNodeY(KeyEvent event) {
        setY(node, TextFields.parseDouble(event, 0d));
    }

    protected abstract void setY(T node, double y);

    public void setNodeWidth(KeyEvent event) {
        setWidth(node, TextFields.parseDouble(event, 0d));
    }

    private void setNodeWidth(double width) {
        setWidth(node, width);
        nodeAnchor.translateX(node, width);
    }

    protected abstract void setWidth(T node, double width);

    public void setNodeHeight(KeyEvent event) {
        setHeight(node, TextFields.parseDouble(event, 0d));
    }

    private void setNodeHeight(double height) {
        setHeight(node, height);
        nodeAnchor.translateY(node, height);
    }

    protected abstract void setHeight(T node, double height);

    private void startDrag(Point2D point) {
        setX(node, point.getX());
        setY(node, point.getY());
        anchorX.setText(Double.toString(point.getX()));
        anchorY.setText(Double.toString(point.getY()));
    }

    private void continueDrag(Point2D start, Point2D end) {
        selectAnchor(nodeAnchor.adjust(start, end));
        setSize(nodeAnchor.getSize(start, end));
    }

    private void selectAnchor(NodeAnchor anchor) {
        if (anchor != this.nodeAnchor) {
            setAnchor(anchor);
            Optional<Node> button = anchorParent.getChildren().stream().filter(node -> anchor.name().equals(node.getId())).findFirst();
            button.ifPresent(node -> ((RadioButton) node).setSelected(true));
        }
    }

    private void setSize(Dimension2D size) {
        setWidth(size.getWidth());
        setHeight(size.getHeight());
    }

    private void setWidth(double width) {
        setNodeWidth(width);
        this.width.setText(Double.toString(width));
    }

    private void setHeight(double height) {
        setNodeHeight(height);
        this.height.setText(Double.toString(height));
    }
}