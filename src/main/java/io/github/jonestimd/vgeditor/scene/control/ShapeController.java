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
import java.util.function.BiConsumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.jonestimd.vgeditor.scene.NodeAnchor;
import io.github.jonestimd.vgeditor.scene.model.LocationModel;
import io.github.jonestimd.vgeditor.scene.model.ShapeModel;
import io.github.jonestimd.vgeditor.scene.model.SizeModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;

public abstract class ShapeController<T extends ShapeModel<?> & LocationModel & SizeModel> implements NodeController<T> {
    protected static final String ID_NAME = "name";
    protected static final String ID_ANCHOR_X = "anchorX";
    protected static final String ID_ANCHOR_Y = "anchorY";
    protected static final String ID_WIDTH = "width";
    protected static final String ID_HEIGHT = "height";
    protected static final String ID_ROTATION = "rotation";
    protected static final List<String> REQUIRED_FIELDS = ImmutableList.of(ID_ANCHOR_X, ID_ANCHOR_Y, ID_WIDTH, ID_HEIGHT);
    protected static final Map<String, Double> DEFAULT_VALUES = ImmutableMap.of(ID_ROTATION, 0d);

    @FXML
    private FormController basicShapeController;
    @FXML
    private FillPaneController fillPaneController;
    @FXML
    private StrokePaneController strokePaneController;
    @FXML
    private Button newButton;

    private Group diagram;
    private T model;

    private BiConsumer<Point2D, Point2D> drag;

    private final MouseInputHandler mouseInputHandler = new MouseInputHandler(this::startDrag, this::continueDrag, this::endDrag);
    private final Function<Group, T> modelFactory;
    private final Map<String, DoubleConsumer> fieldHandlers = ImmutableMap.of(
            ID_ANCHOR_X, (x) -> model.setX(x),
            ID_ANCHOR_Y, (y) -> model.setY(y),
            ID_WIDTH, (width) -> model.setWidth(width),
            ID_HEIGHT, (height) -> model.setHeight(height),
            ID_ROTATION, (rotate) -> model.setRotate(rotate));

    protected ShapeController(Function<Group, T> modelFactory) {
        this.modelFactory = modelFactory;
    }

    public void initialize() {
        basicShapeController.addListener(change -> {
            if (isValid()) {
                if (model == null) createNode();
                String fieldId = change.getPropertyName();
                if (ID_NAME.equals(fieldId)) model.setId(basicShapeController.getText(fieldId));
                else fieldHandlers.get(fieldId).accept(getFieldValue(fieldId));
            }
            else if (model != null) {
                model.remove();
                clearModel();
            }
            newButton.setDisable(!isValid());
        });
        TextInputControl anchorXField = basicShapeController.getField(ID_ANCHOR_X);
        anchorXField.getParent().sceneProperty().addListener((observable, oldValue, newValue) -> anchorXField.requestFocus());
    }

    private Double getFieldValue(String fieldId) {
        return basicShapeController.getValue(fieldId, DEFAULT_VALUES.get(fieldId));
    }

    @Override
    public void setDiagram(Group diagram) {
        this.diagram = diagram;
    }

    @Override
    public MouseInputHandler getMouseHandler() {
        return mouseInputHandler;
    }

    @Override
    public T getModel() {
        return model;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setModel(T model) {
        this.model = model;
        basicShapeController.setText(ID_NAME, model.getId());
        setLocationInputs(model.getX(), model.getY());
        setSizeInputs(model.getWidth(), model.getHeight());
        basicShapeController.setValue(ID_ROTATION, model.getRotate());
        fillPaneController.editNode(model);
        strokePaneController.editNode(model);
        basicShapeController.getField(ID_ANCHOR_X).requestFocus();
    }

    public void onNewNode() {
        newButton.setDisable(true);
        clearModel();
        basicShapeController.clear();
        Platform.runLater(() -> basicShapeController.getField(ID_ANCHOR_X).requestFocus());
    }

    private void clearModel() {
        model = null;
        fillPaneController.newNode(null);
        strokePaneController.newNode(null);
    }

    protected boolean isValid() {
        return basicShapeController.validFields().containsAll(REQUIRED_FIELDS)
                && getFieldValue(ID_WIDTH) > 0 && getFieldValue(ID_HEIGHT) > 0;
    }

    public void onDeleteNode() {
        if (model != null) {
            model.remove();
        }
        onNewNode();
    }

    protected void setNodeLocation() {
        model.setX(getFieldValue(ID_ANCHOR_X));
        model.setY(getFieldValue(ID_ANCHOR_Y));
    }

    protected void setNodeSize() {
        model.setWidth(getFieldValue(ID_WIDTH));
        model.setHeight(getFieldValue(ID_HEIGHT));
    }

    protected boolean startDrag(Point2D screenPoint, boolean isShortcutDown) {
        if (model != null && model.isInSelectionRange(screenPoint.getX(), screenPoint.getY())) {
            if (isShortcutDown) {
                NodeAnchor resizeAnchor = model.getResizeAnchor(screenPoint);
                if (resizeAnchor != null) drag = getResizeDragHandler(resizeAnchor);
            }
            else drag = new MoveDrag();
        }
        else if (!isShortcutDown) {
            onNewNode();
            Point2D point = diagram.screenToLocal(screenPoint);
            setLocationInputs(point.getX(), point.getY());
            drag = new NewNodeDrag();
        }
        return drag != null;
    }

    protected abstract BiConsumer<Point2D, Point2D> getResizeDragHandler(NodeAnchor resizeAnchor);

    protected void setLocationInputs(double x, double y) {
        basicShapeController.setValue(ID_ANCHOR_X, x);
        basicShapeController.setValue(ID_ANCHOR_Y, y);
    }

    protected void continueDrag(Point2D screenStart, Point2D screenEnd) {
        drag.accept(screenStart, screenEnd);
    }

    protected void endDrag() {
        drag = null;
    }

    protected void createNode() {
        model = modelFactory.apply(diagram);
        // model.setAnchor(nodeAnchor);
        setNodeLocation();
        model.setRotate(getFieldValue(ID_ROTATION));
        setNodeSize();
        fillPaneController.newNode(model);
        strokePaneController.newNode(model);
    }

    protected void setSizeInputs(Dimension2D size) {
        setSizeInputs(size.getWidth(), size.getHeight());
    }

    protected void setSizeInputs(double width, double height) {
        basicShapeController.setValue(ID_WIDTH, width);
        basicShapeController.setValue(ID_HEIGHT, height);
    }

    protected abstract Dimension2D getNewNodeSize(Point2D diagramStart, Point2D diagramEnd);

    private class NewNodeDrag implements BiConsumer<Point2D, Point2D> {
        @Override
        public void accept(Point2D screenStart, Point2D screenEnd) {
            Point2D start = diagram.screenToLocal(screenStart);
            Point2D end = diagram.screenToLocal(screenEnd);
            setSizeInputs(getNewNodeSize(start, end));
            if (isValid()) {
                if (model == null) createNode();
                else setNodeSize();
            }
            else if (model != null) {
                model.remove();
                clearModel();
            }
            newButton.setDisable(!isValid());
        }
    }

    private class MoveDrag implements BiConsumer<Point2D, Point2D> {
        private final double startX, startY;

        public MoveDrag() {
            this.startX = model.getX();
            this.startY = model.getY();
        }

        @Override
        public void accept(Point2D start, Point2D end) { // TODO compensate for axis adjustment at top and left screen border
            setLocationInputs(startX+end.getX()-start.getX(), startY+end.getY()-start.getY());
            setNodeLocation();
        }
    }
}