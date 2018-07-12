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
import java.util.function.BiConsumer;

import io.github.jonestimd.vgeditor.scene.NodeAnchor;
import io.github.jonestimd.vgeditor.scene.Nodes;
import io.github.jonestimd.vgeditor.scene.SceneTest;
import io.github.jonestimd.vgeditor.scene.model.RectangleModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static io.github.jonestimd.vgeditor.scene.control.ShapeController.*;
import static org.assertj.core.api.Java6Assertions.*;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class AnchoredShapeControllerTest extends SceneTest {
    private RectangleController controller;
    private FormController basicShapeController = mock(FormController.class);
    private FillPaneController fillPaneController = mock(FillPaneController.class);
    private StrokePaneController strokePaneController = mock(StrokePaneController.class);
    private GridPane anchorParent;
    private TextField anchorXField;
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
        anchorParent = Nodes.findFirstById(form, "anchorParent", GridPane.class).get();
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
        final double x = 5d, y = 6d, width = 30d, height = 20d;
        RectangleModel model = new RectangleModel(diagram, x, y, width, height);
        model.setAnchor(NodeAnchor.CENTER);

        controller.setModel(model);

        assertThat(controller.getModel()).isSameAs(model);
        verify(basicShapeController).setValue(ID_ANCHOR_X, x);
        verify(basicShapeController).setValue(ID_ANCHOR_Y, y);
        verify(basicShapeController).setValue(ID_WIDTH, width);
        verify(basicShapeController).setValue(ID_HEIGHT, height);
        verify(fillPaneController).editNode(model);
        verify(strokePaneController).editNode(model);
        verify(anchorXField).requestFocus();
        assertThat(model.getAnchor()).isEqualTo(NodeAnchor.CENTER);
    }

    @Test
    public void onAnchorTest_DoesNothingModelIsNull() throws Exception {
        RadioButton button = Nodes.findById(anchorParent, NodeAnchor.CENTER.name(), RadioButton.class);

        controller.onAnchorChange(new ActionEvent(button, button));

        assertThat(getValue(controller, "nodeAnchor", NodeAnchor.class)).isEqualTo(NodeAnchor.CENTER);
    }

    @Test
    public void onAnchorTest_DoesNothingIfAnchorIsSame() throws Exception {
        final double x = 5d, y = 6d, width = 30d, height = 20d;
        RectangleModel model = new RectangleModel(diagram, x, y, width, height);
        model.setAnchor(NodeAnchor.CENTER);
        controller.setModel(model);
        RadioButton button = Nodes.findById(anchorParent, NodeAnchor.CENTER.name(), RadioButton.class);

        controller.onAnchorChange(new ActionEvent(button, button));

        assertThat(controller.getModel().getAnchor()).isEqualTo(NodeAnchor.CENTER);
    }

    @Test
    public void onAnchorTest_UpdatesModel() throws Exception {
        final double x = 5d, y = 6d, width = 30d, height = 20d;
        RectangleModel model = new RectangleModel(diagram, x, y, width, height);
        model.setAnchor(NodeAnchor.CENTER);
        controller.setModel(model);
        RadioButton button = Nodes.findById(anchorParent, NodeAnchor.BOTTOM_LEFT.name(), RadioButton.class);

        controller.onAnchorChange(new ActionEvent(button, button));

        assertThat(controller.getModel().getAnchor()).isEqualTo(NodeAnchor.BOTTOM_LEFT);
    }

    @Test
    public void setsAnchorOnNewRectangle() throws Exception {
        fieldValues.put(ID_ANCHOR_X, Math.random()*100);
        fieldValues.put(ID_ANCHOR_Y, Math.random()*100);
        fieldValues.put(ID_WIDTH, Math.random()*100);
        fieldValues.put(ID_HEIGHT, Math.random()*100);
        when(basicShapeController.validFields()).thenReturn(ShapeController.REQUIRED_FIELDS);
        setValue(controller, "nodeAnchor", NodeAnchor.RIGHT);

        ArgumentCaptor<PropertyChangeListener> listenerCaptor = ArgumentCaptor.forClass(PropertyChangeListener.class);
        verify(basicShapeController).addListener(listenerCaptor.capture());
        listenerCaptor.getValue().propertyChange(new PropertyChangeEvent(basicShapeController, ID_HEIGHT, null, null));

        assertThat(controller.getModel().getAnchor()).isEqualTo(NodeAnchor.RIGHT);
    }

    @Test
    public void getNewNodeSize_NullModel() throws Exception {
        setValue(controller, "nodeAnchor", NodeAnchor.TOP_RIGHT);

        Dimension2D newNodeSize = controller.getNewNodeSize(new Point2D(5, 6), new Point2D(15, 26));

        assertThat(newNodeSize.getWidth()).isEqualTo(10d);
        assertThat(newNodeSize.getHeight()).isEqualTo(20d);
        assertThat(getValue(controller, "nodeAnchor", NodeAnchor.class)).isEqualTo(NodeAnchor.TOP_LEFT);
    }

    @Test
    public void getNewNodeSize_Center() throws Exception {
        setValue(controller, "nodeAnchor", NodeAnchor.CENTER);

        Dimension2D newNodeSize = controller.getNewNodeSize(new Point2D(5, 6), new Point2D(15, 26));

        assertThat(newNodeSize.getWidth()).isEqualTo(20d);
        assertThat(newNodeSize.getHeight()).isEqualTo(40d);
    }

    @Test
    public void getNewNodeSize_Corner() throws Exception {
        setValue(controller, "nodeAnchor", NodeAnchor.TOP_LEFT);

        Dimension2D newNodeSize = controller.getNewNodeSize(new Point2D(5, 6), new Point2D(15, 26));

        assertThat(newNodeSize.getWidth()).isEqualTo(10d);
        assertThat(newNodeSize.getHeight()).isEqualTo(20d);
    }

    @Test
    public void getNewNodeSize_VerticalCenter() throws Exception {
        setValue(controller, "nodeAnchor", NodeAnchor.TOP);

        Dimension2D newNodeSize = controller.getNewNodeSize(new Point2D(5, 6), new Point2D(15, 26));

        assertThat(newNodeSize.getWidth()).isEqualTo(20d);
        assertThat(newNodeSize.getHeight()).isEqualTo(20d);
    }

    @Test
    public void getNewNodeSize_HorizontalCenter() throws Exception {
        setValue(controller, "nodeAnchor", NodeAnchor.LEFT);

        Dimension2D newNodeSize = controller.getNewNodeSize(new Point2D(5, 6), new Point2D(15, 26));

        assertThat(newNodeSize.getWidth()).isEqualTo(10d);
        assertThat(newNodeSize.getHeight()).isEqualTo(40d);
    }

    @Test
    public void resizingAdjustsAnchorHorizontally() throws Exception {
        final double x = 5d, y = 6d, width = 30d, height = 20d;
        RectangleModel model = new RectangleModel(diagram, x, y, width, height);
        model.setAnchor(NodeAnchor.TOP_LEFT);
        controller.setModel(model);
        BiConsumer<Point2D, Point2D> resizeHandler = controller.getResizeDragHandler(NodeAnchor.BOTTOM_RIGHT);

        resizeHandler.accept(new Point2D(x+width, y+height), new Point2D(x-5, y+height));
        assertThat(controller.getModel().getAnchor()).isEqualTo(NodeAnchor.TOP_RIGHT);
        assertThat(controller.getModel().getWidth()).isEqualTo(5);

        resizeHandler.accept(new Point2D(x+width, y+height), new Point2D(x+5, y+height));
        assertThat(controller.getModel().getAnchor()).isEqualTo(NodeAnchor.TOP_LEFT);
        assertThat(controller.getModel().getWidth()).isEqualTo(5);
    }

    @Test
    public void resizingAdjustsAnchorVertically() throws Exception {
        final double x = 5d, y = 6d, width = 30d, height = 20d;
        RectangleModel model = new RectangleModel(diagram, x, y, width, height);
        model.setAnchor(NodeAnchor.TOP_LEFT);
        controller.setModel(model);
        BiConsumer<Point2D, Point2D> resizeHandler = controller.getResizeDragHandler(NodeAnchor.BOTTOM_RIGHT);

        resizeHandler.accept(new Point2D(x+width, y+height), new Point2D(x+width, y-5));
        assertThat(controller.getModel().getAnchor()).isEqualTo(NodeAnchor.BOTTOM_LEFT);
        assertThat(controller.getModel().getHeight()).isEqualTo(5);

        resizeHandler.accept(new Point2D(x+width, y+height), new Point2D(x+width, y+5));
        assertThat(controller.getModel().getAnchor()).isEqualTo(NodeAnchor.TOP_LEFT);
        assertThat(controller.getModel().getHeight()).isEqualTo(5);
    }
}