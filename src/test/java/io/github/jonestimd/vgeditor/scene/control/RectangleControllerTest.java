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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import io.github.jonestimd.vgeditor.scene.Nodes;
import io.github.jonestimd.vgeditor.scene.SceneTest;
import io.github.jonestimd.vgeditor.scene.model.RectangleModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static io.github.jonestimd.vgeditor.scene.control.RectangleController.*;
import static io.github.jonestimd.vgeditor.scene.control.ShapeController.ID_ANCHOR_X;
import static io.github.jonestimd.vgeditor.scene.control.ShapeController.ID_ANCHOR_Y;
import static io.github.jonestimd.vgeditor.scene.control.ShapeController.ID_HEIGHT;
import static io.github.jonestimd.vgeditor.scene.control.ShapeController.ID_ROTATION;
import static io.github.jonestimd.vgeditor.scene.control.ShapeController.ID_WIDTH;
import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class RectangleControllerTest extends SceneTest {
    private RectangleController controller;
    private FormController basicShapeController = mock(FormController.class);
    private FillPaneController fillPaneController = mock(FillPaneController.class);
    private StrokePaneController strokePaneController = mock(StrokePaneController.class);
    private TextField anchorXField;
    private TextField arcWidth;
    private TextField arcHeight;
    private final Map<String, Double> fieldValues = new HashMap<>();

    private Object getController(Class<?> type) {
        if (FormController.class.equals(type)) return basicShapeController;
        if (FillPaneController.class.equals(type)) return fillPaneController;
        if (StrokePaneController.class.equals(type)) return strokePaneController;
        if (RectangleController.class.equals(type)) return new RectangleController();
        return null;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void setUpScene() throws Exception {
        super.setUpScene();
        scene.setRoot(diagram);
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(new ResourceBundleWrapper(ResourceBundle.getBundle("io.github.jonestimd.vgeditor.labels")));
        loader.setLocation(getClass().getResource(RectangleModel.TOOL_FXML));
        loader.setControllerFactory(this::getController);
        Pane form = loader.load();
        controller = loader.getController();
        controller.setDiagram(diagram);
        arcWidth = Nodes.findFirstById(form, ID_ARC_WIDTH, TextField.class).get();
        arcHeight = Nodes.findFirstById(form, ID_ARC_HEIGHT, TextField.class).get();
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
    public void setModel() throws Exception {
        final double x = 5d, y = 6d, width = 30d, height = 20d, rotation = 15d;
        RectangleModel model = new RectangleModel(diagram, x, y, width, height);
        model.setRotate(rotation);
        model.setArcWidth(7);
        model.setArcHeight(8);

        controller.setModel(model);

        assertThat(controller.getModel()).isSameAs(model);
        verify(basicShapeController).setValue(ID_ANCHOR_X, x);
        verify(basicShapeController).setValue(ID_ANCHOR_Y, y);
        verify(basicShapeController).setValue(ID_WIDTH, width);
        verify(basicShapeController).setValue(ID_HEIGHT, height);
        verify(basicShapeController).setValue(ID_ROTATION, rotation);
        verify(fillPaneController).editNode(model);
        verify(strokePaneController).editNode(model);
        verify(anchorXField).requestFocus();
        assertThat(arcWidth.getText()).isEqualTo("7");
        assertThat(arcHeight.getText()).isEqualTo("8");
    }

    @Test
    public void onKeyEvent_IgnoredForNoModel() throws Exception {
        controller.onKeyEvent(getKeyEvent(arcWidth, KeyEvent.KEY_PRESSED, ""));
        controller.onKeyEvent(getKeyEvent(arcHeight, KeyEvent.KEY_PRESSED, ""));
    }

    @Test
    public void onKeyEvent_UpdatesArchWidth() throws Exception {
        final double x = 5d, y = 6d, width = 30d, height = 20d;
        RectangleModel model = new RectangleModel(diagram, x, y, width, height);
        setValue(controller, "model", model);
        arcWidth.setText("5");

        controller.onKeyEvent(getKeyEvent(arcWidth, KeyEvent.KEY_PRESSED, ""));

        assertThat(model.getArcWidth()).isEqualTo(5d);
    }

    @Test
    public void onKeyEvent_UpdatesArchHeight() throws Exception {
        final double x = 5d, y = 6d, width = 30d, height = 20d;
        RectangleModel model = new RectangleModel(diagram, x, y, width, height);
        setValue(controller, "model", model);
        arcHeight.setText("5");

        controller.onKeyEvent(getKeyEvent(arcHeight, KeyEvent.KEY_PRESSED, ""));

        assertThat(model.getArcHeight()).isEqualTo(5d);
    }

    @Test
    public void setsCornerSizeOnNewRectangle() throws Exception {
        fieldValues.put(ID_ANCHOR_X, Math.random()*100);
        fieldValues.put(ID_ANCHOR_Y, Math.random()*100);
        fieldValues.put(ID_WIDTH, Math.random()*100);
        fieldValues.put(ID_HEIGHT, Math.random()*100);
        when(basicShapeController.validFields()).thenReturn(ShapeController.REQUIRED_FIELDS);
        arcWidth.setText("6");
        arcHeight.setText("7");

        ArgumentCaptor<PropertyChangeListener> listenerCaptor = ArgumentCaptor.forClass(PropertyChangeListener.class);
        verify(basicShapeController).addListener(listenerCaptor.capture());
        listenerCaptor.getValue().propertyChange(new PropertyChangeEvent(basicShapeController, ID_HEIGHT, null, null));

        assertThat(controller.getModel().getArcWidth()).isEqualTo(6d);
        assertThat(controller.getModel().getArcHeight()).isEqualTo(7d);
    }
}