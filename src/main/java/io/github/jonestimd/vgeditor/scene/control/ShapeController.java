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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;

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
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public abstract class ShapeController<T extends Shape> implements NodeController<T> {
    private static final String ID_ANCHOR_X = "anchorX";
    private static final String ID_ANCHOR_Y = "anchorY";
    private static final String ID_WIDTH = "width";
    private static final String ID_HEIGHT = "height";

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
    @FXML
    private Button newButton;

    private final MouseInputHandler mouseInputHandler = new MouseInputHandler(this::startDrag, this::continueDrag);
    private final Supplier<T> nodeFactory;
    private final Map<String, KeyHandler<T>> keyHandlers = ImmutableMap.of(
            ID_ANCHOR_X, this::setX,
            ID_ANCHOR_Y, this::setY,
            ID_WIDTH, this::setWidth,
            ID_HEIGHT, this::setHeight);

    private Pane diagram;
    private T node;

    private final Map<String, Double> values = new HashMap<>();

    protected ShapeController(Supplier<T> nodeFactory) {
        this.nodeFactory = nodeFactory;
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
        anchorX.setText("");
        anchorY.setText("");
        width.setText("");
        height.setText("");
        values.clear();
        anchorX.requestFocus();
    }

    private void clearNode() {
        node = null;
        fillPaneController.setNode(null);
        strokePaneController.setNode(null);
    }

    protected boolean isValid() {
        return values.size() == 4 && values.get(ID_WIDTH) > 0 && values.get(ID_HEIGHT) > 0;
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
        if (node != null) this.nodeAnchor.translate(node, values.get(ID_WIDTH), values.get(ID_HEIGHT));
    }

    public void onKeyEvent(KeyEvent event) {
        TextInputControl source = (TextInputControl) event.getSource();
        OptionalDouble optionalValue = TextFields.parseDouble(source);
        if (optionalValue.isPresent()) {
            values.put(source.getId(), optionalValue.getAsDouble());
            if (isValid()) {
                if (node == null) createNode();
                keyHandlers.get(((Node) event.getTarget()).getId()).accept(node, values.get(source.getId()));
            }
        }
        else values.remove(source.getId());
        newButton.setDisable(!isValid());
    }

    protected abstract void setX(T node, double x);
    protected abstract void setY(T node, double y);
    protected abstract void setWidth(T node, double width);
    protected abstract void setHeight(T node, double height);

    private void setNodeSize() {
        setWidth(node, values.get(ID_WIDTH));
        setHeight(node, values.get(ID_HEIGHT));
    }

    private void startDrag(Point2D point) {
        newNode();
        setFieldValue(anchorX, point.getX(), ID_ANCHOR_X);
        setFieldValue(anchorY, point.getY(), ID_ANCHOR_Y);
        if (isValid()) {
            setX(node, point.getX());
            setY(node, point.getY());
        }
    }

    private void setFieldValue(TextField field, double value, String id) {
        field.setText(Double.toString(value));
        values.put(id, value);
    }

    private void continueDrag(Point2D start, Point2D end) {
        selectAnchor(nodeAnchor.adjust(start, end));
        setSize(nodeAnchor.getSize(start, end));
        if (isValid()) {
            if (node == null) createNode();
            nodeAnchor.translate(node, values.get(ID_WIDTH), values.get(ID_HEIGHT));
            setNodeSize();
        }
        else if (node != null) {
            diagram.getChildren().remove(node);
            clearNode();
        }
        newButton.setDisable(!isValid());
    }

    private void createNode() {
        node = nodeFactory.get();
        setX(node, values.get(ID_ANCHOR_X));
        setY(node, values.get(ID_ANCHOR_Y));
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
        setFieldValue(width, size.getWidth(), ID_WIDTH);
        setFieldValue(height, size.getHeight(), ID_HEIGHT);
    }

    private interface KeyHandler<T> {
        void accept(T node, double value);
    }
}