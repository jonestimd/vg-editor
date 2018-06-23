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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.DoubleConsumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.jonestimd.vgeditor.scene.NodeAnchor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public class ShapeController<T extends Shape> implements NodeController<T> {
    private static final String ID_ANCHOR_X = "anchorX";
    private static final String ID_ANCHOR_Y = "anchorY";
    private static final String ID_WIDTH = "width";
    private static final String ID_HEIGHT = "height";
    private static final String ID_ROTATION = "rotation";
    private static final List<String> REQUIRED_FIELDS = ImmutableList.of(ID_ANCHOR_X, ID_ANCHOR_Y, ID_WIDTH, ID_HEIGHT);
    private static final Map<String, Double> DEFAULT_VALUES = ImmutableMap.of(ID_ROTATION, 0d);

    private NodeAnchor nodeAnchor = NodeAnchor.TOP_LEFT;
    @FXML
    private GridPane anchorParent;
    @FXML
    private FormController basicShapeController;
    @FXML
    private FillPaneController fillPaneController;
    @FXML
    private StrokePaneController strokePaneController;
    @FXML
    private Button newButton;

    private final MouseInputHandler mouseInputHandler = new MouseInputHandler(this::startDrag, this::continueDrag);
    private final Supplier<T> nodeFactory;
    private final ShapeAdapter<T> adapter;
    private final Map<String, DoubleConsumer> fieldHandlers = ImmutableMap.of(
            ID_ANCHOR_X, this::setNodeX,
            ID_ANCHOR_Y, this::setNodeY,
            ID_WIDTH, this::setNodeWidth,
            ID_HEIGHT, this::setNodeHeight,
            ID_ROTATION, this::setRotation);

    private Pane diagram;
    private T node;

    protected ShapeController(Supplier<T> nodeFactory, ShapeAdapter<T> adapter) {
        this.nodeFactory = nodeFactory;
        this.adapter = adapter;
    }

    public void initialize() {
        basicShapeController.addListener(change -> {
            if (isValid()) {
                if (node == null) createNode();
                String fieldId = change.getKey();
                fieldHandlers.get(fieldId).accept(getFieldValue(fieldId));
            }
            else if (node != null) {
                diagram.getChildren().remove(node);
                clearNode();
            }
            newButton.setDisable(!isValid());
        });
    }

    private Double getFieldValue(String fieldId) {
        return basicShapeController.getValue(fieldId, DEFAULT_VALUES.get(fieldId));
    }

    @Override
    public void setDiagram(Pane diagram) {
        this.diagram = diagram;
    }

    @Override
    public MouseInputHandler getMouseHandler() {
        return mouseInputHandler;
    }

    @Override
    public T getNode() {
        return node;
    }

    public void newNode() {
        clearNode();
        basicShapeController.clear();
        basicShapeController.getField(ID_ANCHOR_X).requestFocus();
    }

    private void clearNode() {
        node = null;
        fillPaneController.setNode(null);
        strokePaneController.setNode(null);
    }

    protected boolean isValid() {
        return basicShapeController.validFields().containsAll(REQUIRED_FIELDS)
                && getFieldValue(ID_WIDTH) > 0 && getFieldValue(ID_HEIGHT) > 0;
    }

    public void deleteNode() {
        if (node != null) {
            Parent parent = node.getParent();
            if (parent instanceof Pane) ((Pane) parent).getChildren().remove(node);
            else if (parent instanceof Group) ((Group) parent).getChildren().remove(node);
            else throw new IllegalStateException("Unexpected parent type: " + parent.getClass().getName());
        }
        newNode();
    }

    public void selectAnchor(ActionEvent event) {
        setAnchor(NodeAnchor.valueOf(((RadioButton) event.getSource()).getId()));
    }

    private void setAnchor(NodeAnchor nodeAnchor) {
        this.nodeAnchor = nodeAnchor;
        if (node != null) this.nodeAnchor.translate(node, getFieldValue(ID_WIDTH), getFieldValue(ID_HEIGHT));
    }

    private void setNodeX(double x) {
        adapter.setX(node, x);
    }

    private void setNodeY(double y) {
        adapter.setY(node, y);
    }

    private void setNodeWidth(double width) {
        adapter.setWidth(node, width);
        this.nodeAnchor.translate(node, width, getFieldValue(ID_HEIGHT));
    }

    private void setNodeHeight(double height) {
        adapter.setHeight(node, height);
        this.nodeAnchor.translate(node, getFieldValue(ID_WIDTH), height);
    }

    private void setRotation(double rotation) {
        node.setRotate(rotation);
        this.nodeAnchor.translate(node, getFieldValue(ID_WIDTH), getFieldValue(ID_HEIGHT));
    }

    private void setNodeSize() {
        setNodeWidth(getFieldValue(ID_WIDTH));
        setNodeHeight(getFieldValue(ID_HEIGHT));
        nodeAnchor.translate(node, adapter.getWidth(node), adapter.getHeight(node));
    }

    private void startDrag(Point2D point) {
        newNode();
        basicShapeController.setValue(ID_ANCHOR_X, point.getX());
        basicShapeController.setValue(ID_ANCHOR_Y, point.getY());
        if (isValid()) {
            setNodeX(point.getX());
            setNodeY(point.getY());
            nodeAnchor.translate(node, adapter.getWidth(node), adapter.getHeight(node));
        }
    }

    private void continueDrag(Point2D start, Point2D end) {
        selectAnchor(nodeAnchor.adjust(start, end));
        setSize(nodeAnchor.getSize(start, end));
        if (isValid()) {
            if (node == null) createNode();
            else setNodeSize();
        }
        else if (node != null) {
            diagram.getChildren().remove(node);
            clearNode();
        }
        newButton.setDisable(!isValid());
    }

    protected void createNode() {
        node = nodeFactory.get();
        setNodeX(getFieldValue(ID_ANCHOR_X));
        setNodeY(getFieldValue(ID_ANCHOR_Y));
        node.setRotate(getFieldValue(ID_ROTATION));
        setNodeSize();
        fillPaneController.setNode(node);
        strokePaneController.setNode(node);
        diagram.getChildren().add(node);
    }

    private void selectAnchor(NodeAnchor anchor) {
        if (anchor != this.nodeAnchor) {
            this.nodeAnchor = anchor;
            Optional<Node> button = anchorParent.getChildren().stream().filter(node -> anchor.name().equals(node.getId())).findFirst();
            button.ifPresent(node -> ((RadioButton) node).setSelected(true));
        }
    }

    private void setSize(Dimension2D size) {
        basicShapeController.setValue(ID_WIDTH, size.getWidth());
        basicShapeController.setValue(ID_HEIGHT, size.getHeight());
    }

    protected interface ShapeAdapter<T> {
        void setX(T node, double x);
        void setY(T node, double y);
        double getWidth(T node);
        double getHeight(T node);
        void setWidth(T node, double width);
        void setHeight(T node, double height);
    }
}