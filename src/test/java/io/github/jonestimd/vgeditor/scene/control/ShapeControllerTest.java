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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import io.github.jonestimd.vgeditor.scene.Nodes;
import io.github.jonestimd.vgeditor.scene.SceneTest;
import io.github.jonestimd.vgeditor.scene.model.EllipseModel;
import javafx.collections.FXCollections;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static io.github.jonestimd.vgeditor.scene.control.ShapeController.*;
import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.Mockito.*;

public class ShapeControllerTest extends SceneTest {
    private EllipseController controller;
    private FormController basicShapeController = mock(FormController.class);
    private FillPaneController fillPaneController = mock(FillPaneController.class);
    private StrokePaneController strokePaneController = mock(StrokePaneController.class);
    private Button newButton;
    private TextField anchorXField;
    private final Map<String, Double> fieldValues = new HashMap<>();

    private Object getController(Class<?> type) {
        if (FormController.class.equals(type)) return basicShapeController;
        if (FillPaneController.class.equals(type)) return fillPaneController;
        if (StrokePaneController.class.equals(type)) return strokePaneController;
        if (EllipseController.class.equals(type)) return new EllipseController();
        return null;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void setUpScene() throws Exception {
        super.setUpScene();
        scene.setRoot(diagram);
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(new ResourceBundleWrapper(ResourceBundle.getBundle("io.github.jonestimd.vgeditor.labels")));
        loader.setLocation(getClass().getResource("EllipseTool.fxml"));
        loader.setControllerFactory(this::getController);
        Pane form = loader.load();
        controller = loader.getController();
        controller.setDiagram(diagram);
        newButton = Nodes.findFirstById(form, "newButton", Button.class).get();
        anchorXField = mock(TextField.class);
        when(basicShapeController.getField(ID_ANCHOR_X)).thenReturn(anchorXField);
        doAnswer(invocation -> {
            String key = (String) invocation.getArguments()[0];
            Double value = (Double) invocation.getArguments()[1];
            fieldValues.put(key, value);
            return null;
        }).when(basicShapeController).setValue(anyString(), anyDouble());
        when(basicShapeController.getValue(anyString(), anyDouble())).thenAnswer(invocation -> {
            return fieldValues.getOrDefault(invocation.getArguments()[0], (Double) invocation.getArguments()[1]);
        });
    }

    @Test
    public void initialize() throws Exception {
        verify(basicShapeController).addListener(any(PropertyChangeListener.class));
        assertThat(controller.getModel()).isNull();
    }

    @Test
    public void setModel() throws Exception {
        final double cx = 5d, cy = 6d, rx = 30d, ry = 20d, rotation = 15d;
        EllipseModel model = new EllipseModel(diagram, cx, cy, rx, ry);
        model.setRotate(rotation);

        controller.setModel(model);

        assertThat(controller.getModel()).isSameAs(model);
        verify(basicShapeController).setValue(ID_ANCHOR_X, cx);
        verify(basicShapeController).setValue(ID_ANCHOR_Y, cy);
        verify(basicShapeController).setValue(ID_WIDTH, rx);
        verify(basicShapeController).setValue(ID_HEIGHT, ry);
        verify(basicShapeController).setValue(ID_ROTATION, rotation);
        verify(fillPaneController).editNode(model);
        verify(strokePaneController).editNode(model);
        verify(anchorXField).requestFocus();
    }

    @Test
    public void onNewNode() throws Exception {
        final double cx = 5d, cy = 6d, rx = 30d, ry = 20d;
        EllipseModel model = new EllipseModel(diagram, cx, cy, rx, ry);
        setValue(controller, "model", model);

        controller.onNewNode();

        assertThat(controller.getModel()).isNull();
        verify(fillPaneController).newNode(null);
        verify(strokePaneController).newNode(null);
        verify(basicShapeController).clear();
        verify(anchorXField).requestFocus();
    }

    @Test
    public void onDeleteNode_IgnoresNoSelection() throws Exception {
        controller.onDeleteNode();

        assertThat(controller.getModel()).isNull();
        verify(fillPaneController).newNode(null);
        verify(strokePaneController).newNode(null);
        verify(basicShapeController).clear();
        verify(anchorXField).requestFocus();
    }

    @Test
    public void onDeleteNode_RemovesSelectedNode() throws Exception {
        final double cx = 5d, cy = 6d, rx = 30d, ry = 20d;
        EllipseModel model = new EllipseModel(diagram, cx, cy, rx, ry);
        setValue(controller, "model", model);

        controller.onDeleteNode();

        assertThat(controller.getModel()).isNull();
        assertThat(model.getShape().getParent()).isNull();
        verify(fillPaneController).newNode(null);
        verify(strokePaneController).newNode(null);
        verify(basicShapeController).clear();
        verify(anchorXField).requestFocus();
    }

    @Test
    public void createsShapeWhenRequiredFieldsArePopulated() throws Exception {
        final double cx = 5d, cy = 6d, rx = 30d, ry = 20d;
        when(basicShapeController.getValue(ID_ANCHOR_X, null)).thenReturn(cx);
        when(basicShapeController.getValue(ID_ANCHOR_Y, null)).thenReturn(cy);
        when(basicShapeController.getValue(ID_WIDTH, null)).thenReturn(rx);
        when(basicShapeController.getValue(ID_HEIGHT, null)).thenReturn(ry);
        when(basicShapeController.validFields()).thenReturn(Arrays.asList(ID_ANCHOR_X, ID_ANCHOR_Y, ID_WIDTH, ID_HEIGHT));
        ArgumentCaptor<PropertyChangeListener> listenerCaptor = ArgumentCaptor.forClass(PropertyChangeListener.class);
        verify(basicShapeController).addListener(listenerCaptor.capture());

        listenerCaptor.getValue().propertyChange(new PropertyChangeEvent(basicShapeController, ID_HEIGHT, null, null));

        assertThat(controller.getModel()).isNotNull();
        assertThat(newButton.isDisabled()).isFalse();
        verifyModel(cx, cy, rx, ry);
    }

    @Test
    public void updatesModelWhenFieldsChange() throws Exception {
        final double cx = 5d, cy = 6d, rx = 30d, ry = 20d, rotation = 15d;
        EllipseModel model = new EllipseModel(diagram, cx*2, cy*2, rx*2, ry*2);
        setValue(controller, "model", model);
        when(basicShapeController.getValue(ID_ANCHOR_X, null)).thenReturn(cx);
        when(basicShapeController.getValue(ID_ANCHOR_Y, null)).thenReturn(cy);
        when(basicShapeController.getValue(ID_WIDTH, null)).thenReturn(rx);
        when(basicShapeController.getValue(ID_HEIGHT, null)).thenReturn(ry);
        when(basicShapeController.getValue(ID_ROTATION, 0d)).thenReturn(rotation);
        when(basicShapeController.validFields()).thenReturn(Arrays.asList(ID_ANCHOR_X, ID_ANCHOR_Y, ID_WIDTH, ID_HEIGHT));
        ArgumentCaptor<PropertyChangeListener> listenerCaptor = ArgumentCaptor.forClass(PropertyChangeListener.class);
        verify(basicShapeController).addListener(listenerCaptor.capture());

        listenerCaptor.getValue().propertyChange(new PropertyChangeEvent(basicShapeController, ID_ANCHOR_X, null, null));
        listenerCaptor.getValue().propertyChange(new PropertyChangeEvent(basicShapeController, ID_ANCHOR_Y, null, null));
        listenerCaptor.getValue().propertyChange(new PropertyChangeEvent(basicShapeController, ID_WIDTH, null, null));
        listenerCaptor.getValue().propertyChange(new PropertyChangeEvent(basicShapeController, ID_HEIGHT, null, null));
        listenerCaptor.getValue().propertyChange(new PropertyChangeEvent(basicShapeController, ID_ROTATION, null, null));

        assertThat(controller.getModel()).isSameAs(model);
        assertThat(newButton.isDisabled()).isFalse();
        assertThat(model.getX()).isEqualTo(cx);
        assertThat(model.getY()).isEqualTo(cy);
        assertThat(model.getWidth()).isEqualTo(rx);
        assertThat(model.getHeight()).isEqualTo(ry);
        assertThat(model.getRotate()).isEqualTo(rotation);
    }

    @Test
    public void disablesNewButtonWhenRequiredFieldsAreNotPopulated() throws Exception {
        when(basicShapeController.validFields()).thenReturn(Arrays.asList(ID_ANCHOR_X, ID_ANCHOR_Y, ID_WIDTH));
        ArgumentCaptor<PropertyChangeListener> listenerCaptor = ArgumentCaptor.forClass(PropertyChangeListener.class);
        verify(basicShapeController).addListener(listenerCaptor.capture());

        listenerCaptor.getValue().propertyChange(new PropertyChangeEvent(basicShapeController, ID_HEIGHT, null, null));

        assertThat(controller.getModel()).isNull();
        assertThat(newButton.isDisabled()).isTrue();
    }

    @Test
    public void removesShapeWhenRequiredFieldsAreNotPopulated() throws Exception {
        EllipseModel model = new EllipseModel(diagram, 5d, 6d, 30d, 20d);
        setValue(controller, "model", model);
        when(basicShapeController.validFields()).thenReturn(ShapeController.REQUIRED_FIELDS.subList(0, 3));
        ArgumentCaptor<PropertyChangeListener> listenerCaptor = ArgumentCaptor.forClass(PropertyChangeListener.class);
        verify(basicShapeController).addListener(listenerCaptor.capture());

        listenerCaptor.getValue().propertyChange(new PropertyChangeEvent(basicShapeController, ID_HEIGHT, null, null));

        assertThat(controller.getModel()).isNull();
        assertThat(model.getShape().getParent()).isNull();
        assertThat(newButton.isDisabled()).isTrue();
    }

    @Test
    public void mouseHandler_SetsLocationInputs() throws Exception {
        final double x = 5, y = 6;
        Group diagram = mock(Group.class);

        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_PRESSED, x, y, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.DRAG_DETECTED, x, y, false));

        verify(diagram).startFullDrag();
        verify(basicShapeController).setValue(ID_ANCHOR_X, x);
        verify(basicShapeController).setValue(ID_ANCHOR_Y, y);
        assertThat(controller.getModel()).isNull();
    }

    @Test
    public void mouseHandler_SetsSizeInputs() throws Exception {
        final double startX = 5, startY = 6, endX = 35, endY = 36;
        Group diagram = mock(Group.class);
        when(basicShapeController.validFields()).thenReturn(ShapeController.REQUIRED_FIELDS);

        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_PRESSED, startX, startY, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.DRAG_DETECTED, startX, startY, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_MOVED, endX, endY, false));

        verify(diagram).startFullDrag();
        verify(basicShapeController).setValue(ID_ANCHOR_X, startX);
        verify(basicShapeController).setValue(ID_ANCHOR_Y, startY);
        verify(basicShapeController).setValue(ID_WIDTH, endX-startX);
        verify(basicShapeController).setValue(ID_HEIGHT, endY-startY);
        verifyModel(startX, startY, endX-startX, endY-startY);
    }

    @Test
    public void mouseHandler_CreatesShape() throws Exception {
        final double startX = 5, startY = 6, endX = 35, endY = 36;
        Group diagram = mock(Group.class);
        when(diagram.getChildren()).thenReturn(FXCollections.observableArrayList());
        EllipseModel model = new EllipseModel(diagram, 50, 50, 5, 5);
        setScene(model.getShape());
        setValue(controller, "model", model);
        when(basicShapeController.validFields()).thenReturn(ShapeController.REQUIRED_FIELDS);

        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_PRESSED, startX, startY, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.DRAG_DETECTED, startX, startY, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_MOVED, endX/2, endY/2, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_MOVED, endX, endY, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_RELEASED, endX, endY, false));

        verify(diagram).startFullDrag();
        verify(basicShapeController).setValue(ID_ANCHOR_X, startX);
        verify(basicShapeController).setValue(ID_ANCHOR_Y, startY);
        verify(basicShapeController).setValue(ID_WIDTH, endX/2-startX);
        verify(basicShapeController).setValue(ID_HEIGHT, endY/2-startY);
        verify(basicShapeController).setValue(ID_WIDTH, endX-startX);
        verify(basicShapeController).setValue(ID_HEIGHT, endY-startY);
        verifyModel(startX, startY, endX-startX, endY-startY);
    }

    @Test
    public void mouseHandler_RemovesInvalidShape() throws Exception {
        final double startX = 5, startY = 6, endX = 35, endY = 36;
        Group diagram = mock(Group.class);
        when(basicShapeController.validFields()).thenReturn(ShapeController.REQUIRED_FIELDS);

        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_PRESSED, startX, startY, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.DRAG_DETECTED, startX, startY, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_MOVED, endX, endY, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_MOVED, startX, startY, false));

        verify(diagram).startFullDrag();
        verify(basicShapeController).setValue(ID_ANCHOR_X, startX);
        verify(basicShapeController).setValue(ID_ANCHOR_Y, startY);
        verify(basicShapeController).setValue(ID_WIDTH, endX-startX);
        verify(basicShapeController).setValue(ID_HEIGHT, endY-startY);
        verify(basicShapeController).setValue(ID_WIDTH, 0d);
        verify(basicShapeController).setValue(ID_HEIGHT, 0d);
        assertThat(controller.getModel()).isNull();
    }

    @Test
    public void mouseHandler_IgnoresDragWithCtrl() throws Exception {
        final double x = 5, y = 6;
        Group diagram = mock(Group.class);

        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_PRESSED, x, y, true));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.DRAG_DETECTED, x, y, true));

        verify(diagram, never()).startFullDrag();
        verify(basicShapeController, never()).setValue(ID_ANCHOR_X, x);
        verify(basicShapeController, never()).setValue(ID_ANCHOR_Y, y);
        assertThat(controller.getModel()).isNull();
    }

    @Test
    public void mouseHandler_MovesShape() throws Exception {
        final double cx = 5d, cy = 6d, rx = 30d, ry = 20d;
        int dx = 5, dy = 10;
        Group diagram = mock(Group.class);
        when(diagram.getChildren()).thenReturn(FXCollections.observableArrayList());
        EllipseModel model = new EllipseModel(diagram, cx, cy, rx, ry);
        setScene(model.getShape());
        setValue(controller, "model", model);

        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_PRESSED, cx, cy+ry, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.DRAG_DETECTED, cx, cy+ry, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_MOVED, cx+dx, cy+ry+dy, false));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_RELEASED, cx+dx, cy+ry+dy, false));

        verify(basicShapeController).setValue(ID_ANCHOR_X, cx+dx);
        verify(basicShapeController).setValue(ID_ANCHOR_Y, cy+dy);
        assertThat(controller.getModel().getX()).isEqualTo(cx+dx);
        assertThat(controller.getModel().getY()).isEqualTo(cy+dy);
    }

    @Test
    public void mouseHandler_ResizesShape() throws Exception {
        final double cx = 5d, cy = 6d, rx = 30d, ry = 20d;
        int dx = 5, dy = 10;
        Group diagram = mock(Group.class);
        when(diagram.getChildren()).thenReturn(FXCollections.observableArrayList());
        EllipseModel model = new EllipseModel(diagram, cx, cy, rx, ry);
        setScene(model.getShape());
        setValue(controller, "model", model);

        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_PRESSED, cx, cy+ry, true));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.DRAG_DETECTED, cx, cy+ry, true));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_MOVED, cx+dx, cy+ry+dy, true));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_RELEASED, cx+dx, cy+ry+dy, true));

        verify(basicShapeController).setValue(ID_WIDTH, rx);
        verify(basicShapeController).setValue(ID_HEIGHT, ry+dy);
        assertThat(controller.getModel().getX()).isEqualTo(cx);
        assertThat(controller.getModel().getY()).isEqualTo(cy);
        assertThat(controller.getModel().getWidth()).isEqualTo(rx);
        assertThat(controller.getModel().getHeight()).isEqualTo(ry+dy);
    }

    private void verifyModel(double startX, double startY, double width, double height) {
        assertThat(controller.getModel().getX()).isEqualTo(startX);
        assertThat(controller.getModel().getY()).isEqualTo(startY);
        assertThat(controller.getModel().getWidth()).isEqualTo(width);
        assertThat(controller.getModel().getHeight()).isEqualTo(height);
    }

    private MouseEvent getMouseEvent(EventType<MouseEvent> eventType, double x, double y, boolean controlDown) {
        return new MouseEvent(diagram, diagram, eventType, x, y, x, y, MouseButton.PRIMARY, 0,
                false, controlDown, false, false, true, false, false, false, false, false, null);
    }
}