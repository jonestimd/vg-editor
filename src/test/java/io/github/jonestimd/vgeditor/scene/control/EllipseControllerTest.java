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
import java.util.ResourceBundle;

import io.github.jonestimd.vgeditor.scene.SceneTest;
import io.github.jonestimd.vgeditor.scene.model.EllipseModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.junit.Test;

import static io.github.jonestimd.vgeditor.scene.control.ShapeController.*;
import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class EllipseControllerTest extends SceneTest {
    private EllipseController controller;
    private FormController basicShapeController = mock(FormController.class);
    private FillPaneController fillPaneController = mock(FillPaneController.class);
    private StrokePaneController strokePaneController = mock(StrokePaneController.class);
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
        createScene();
        TextField anchorXField = new TextField();
        createScene().getChildren().add(anchorXField);
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
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(new ResourceBundleWrapper(ResourceBundle.getBundle("io.github.jonestimd.vgeditor.labels")));
        loader.setLocation(getClass().getResource(EllipseModel.TOOL_FXML));
        loader.setControllerFactory(this::getController);
        loader.load();
        controller = loader.getController();
        controller.setDiagram(diagram);
    }

    @Test
    public void mouseHandler_ResizesShapeDiagonally() throws Exception {
        testResize(Math.cos(Math.PI/4), Math.sin(Math.PI/4), 0);
    }

    @Test
    public void mouseHandler_ResizesShapeVertically() throws Exception {
        testResize(0, 1, 0);
    }

    @Test
    public void mouseHandler_ResizesShapeHorizontally() throws Exception {
        testResize(1, 0, 0);
    }

    @Test
    public void mouseHandler_ResizesRotatedShapeHorizontally() throws Exception {
        testResize(1, 0, 30);
    }

    private void testResize(double cos, double sin, double rotation) throws Exception {
        final double rotateRadians = Math.toRadians(rotation);
        final double rotateCos = Math.cos(rotateRadians), rotateSin = Math.sin(rotateRadians);
        final int dx = 5, dy = 10;
        final double cx = 5d, cy = 10d, rx = 50d, ry = 40d;
        final double startX = cx+rx*cos*rotateCos-ry*sin*rotateSin;
        final double startY = cy+ry*sin*rotateCos+rx*cos*rotateSin;
        Group diagram = mock(Group.class);
        when(diagram.getChildren()).thenReturn(FXCollections.observableArrayList());
        EllipseModel model = new EllipseModel(diagram, cx, cy, rx, ry);
        model.setRotate(rotation);
        setScene(model.getShape());
        setValue(controller, "model", model);

        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_PRESSED, startX, startY, true));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.DRAG_DETECTED, startX, startY, true));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_MOVED, startX+dx, startY+dy, true));
        controller.getMouseHandler().handle(diagram, getMouseEvent(MouseEvent.MOUSE_RELEASED, startX+dx, startY+dy, true));

        double dWidth = cos == 0 ? 0 : (dx*rotateCos+dy*rotateSin)/cos;
        double dHeight = sin == 0 ? 0 : (dy*rotateCos-dx*rotateSin)/sin;
        verify(basicShapeController).setValue(ID_WIDTH, rx+dWidth);
        verify(basicShapeController).setValue(ID_HEIGHT, ry+dHeight);
        assertThat(controller.getModel().getX()).isEqualTo(cx);
        assertThat(controller.getModel().getY()).isEqualTo(cy);
        assertThat(controller.getModel().getWidth()).isEqualTo(rx+dWidth);
        assertThat(controller.getModel().getHeight()).isEqualTo(ry+dHeight);
    }
}