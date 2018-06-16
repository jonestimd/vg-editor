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
package io.github.jonestimd.vgeditor;

import java.util.Optional;

import io.github.jonestimd.vgeditor.scene.Nodes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class RectangleController implements NodeController<Rectangle> {
    private Pane diagram;
    private Rectangle node;
    private NodeAnchor nodeAnchor = NodeAnchor.TOP_LEFT;
    private boolean mouseDragging;
    @FXML
    private Pane root;
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

    private Point2D startDrag;

    @Override
    public void setPane(Pane diagram) {
        this.diagram = diagram;
    }

    @Override
    public Rectangle getNode() {
        return node;
    }

    @Override
    public boolean newNode() {
        if (this.node == null || this.node.getWidth() > 0 && this.node.getHeight() > 0) {
            this.node = new Rectangle();
            return true;
        }
        return false;
    }

    public void selectAnchor(ActionEvent event) {
        setAnchor(NodeAnchor.valueOf(((RadioButton) event.getSource()).getId()));
    }

    private void setAnchor(NodeAnchor nodeAnchor) {
        this.nodeAnchor = nodeAnchor;
        if (this.nodeAnchor.isLeft()) node.setTranslateX(0);
        else if (this.nodeAnchor.isRight()) node.setTranslateX(-node.getWidth());
        else node.setTranslateX(-node.getWidth()/2);
        if (this.nodeAnchor.isTop()) node.setTranslateY(0);
        else if (this.nodeAnchor.isBottom()) node.setTranslateY(-node.getHeight());
        else node.setTranslateY(-node.getHeight()/2);
    }

    private double parseInput(KeyEvent event) {
        String text = ((TextInputControl) event.getSource()).getText();
        return text.length() > 0 ? Double.parseDouble(text) : 0d;
    }

    public void setNodeX(KeyEvent event) {
        node.setX(parseInput(event));
    }

    public void setNodeY(KeyEvent event) {
        node.setY(parseInput(event));
    }

    public void setNodeWidth(KeyEvent event) {
        setNodeWidth(parseInput(event));
    }

    public void setNodeHeight(KeyEvent event) {
        setNodeHeight(parseInput(event));
    }

    private void setNodeWidth(double width) {
        node.setWidth(width);
        if (nodeAnchor.isRight()) node.setTranslateX(-node.getWidth());
        else if (!nodeAnchor.isLeft()) node.setTranslateX(-node.getWidth()/2);
    }

    private void setNodeHeight(double height) {
        node.setHeight(height);
        if (nodeAnchor.isBottom()) node.setTranslateY(-node.getHeight());
        else if (!nodeAnchor.isTop()) node.setTranslateY(-node.getHeight()/2);
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.getButton() == MouseButton.PRIMARY) {
            startDrag = diagram.screenToLocal(event.getScreenX(), event.getScreenY());
            setAnchorX(startDrag.getX());
            setAnchorY(startDrag.getY());
        }
        else if (event.getEventType() == MouseEvent.DRAG_DETECTED && event.getButton() == MouseButton.PRIMARY) {
            this.mouseDragging = true;
            diagram.startFullDrag();
        }
        else if (event.getEventType() == MouseEvent.MOUSE_RELEASED && mouseDragging) {
            this.mouseDragging = false;
        }
        else if (mouseDragging) {
            Point2D point = diagram.screenToLocal(event.getScreenX(), event.getScreenY());
            final double height = Math.abs(point.getY()-startDrag.getY());
            final double width = Math.abs(point.getX()-startDrag.getX());
            if (nodeAnchor == NodeAnchor.CENTER) {
                setWidth(width*2);
                setHeight(height*2);
            }
            else if (nodeAnchor.dx == 0 && nodeAnchor.dy != 0) {
                selectAnchor(point.getY() < startDrag.getY() ? NodeAnchor.BOTTOM_CENTER : NodeAnchor.TOP_CENTER);
                setWidth(width*2);
                setHeight(height);
            }
            else if (nodeAnchor.dx != 0 && nodeAnchor.dy == 0) {
                selectAnchor(point.getX() < startDrag.getX() ? NodeAnchor.RIGHT : NodeAnchor.LEFT);
                setWidth(width);
                setHeight(height*2);
            }
            else { // adjust corner anchor
                if (point.getX() < startDrag.getX()) {
                    selectAnchor(point.getY() < startDrag.getY() ? NodeAnchor.BOTTOM_RIGHT : NodeAnchor.TOP_RIGHT);
                }
                else {
                    selectAnchor(point.getY() < startDrag.getY() ? NodeAnchor.BOTTOM_LEFT : NodeAnchor.TOP_LEFT);
                }
                setWidth(width);
                setHeight(height);
            }
        }
    }

    private void selectAnchor(NodeAnchor anchor) {
        setAnchor(anchor);
        Optional<Node> button = anchorParent.getChildren().stream().filter(node -> anchor.name().equals(node.getId())).findFirst();
        button.ifPresent(node -> ((RadioButton) node).setSelected(true));
    }

    private void setAnchorX(double x) {
        node.setX(x);
        anchorX.setText(Double.toString(x));
    }

    private void setAnchorY(double y) {
        node.setY(y);
        anchorY.setText(Double.toString(y));
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
