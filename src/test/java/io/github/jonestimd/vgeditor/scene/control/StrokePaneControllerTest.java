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

import java.util.ResourceBundle;

import io.github.jonestimd.vgeditor.JavafxTest;
import io.github.jonestimd.vgeditor.model.AnchoredShapeModel;
import io.github.jonestimd.vgeditor.scene.Nodes;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StrokePaneControllerTest extends JavafxTest {
    private StrokePaneController controller;
    private CheckBox stroke;
    private ColorPicker strokeColor;
    private TextField strokeWidth;
    private AnchoredShapeModel model = mock(AnchoredShapeModel.class);

    @Before
    public void loadForm() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("io.github.jonestimd.vgeditor.labels"));
        loader.setLocation(getClass().getResource("StrokePane.fxml"));
        Pane form = loader.load();
        controller = loader.getController();
        stroke = Nodes.findFirstById(form, "stroke", CheckBox.class).get();
        strokeColor = Nodes.findFirstById(form, "strokeColor", ColorPicker.class).get();
        strokeWidth = Nodes.findFirstById(form, "strokeWidthInput", TextField.class).get();
    }

    @Test
    public void initialize() throws Exception {
        assertThat(controller).isNotNull();
        assertThat(stroke.isSelected()).isTrue();
        assertThat(strokeColor.getValue()).isEqualTo(Color.BLACK);
        assertThat(strokeWidth.getText()).isEqualTo("1");
    }

    @Test
    public void editNodeClearsStroke() throws Exception {
        stroke.setSelected(true);
        when(model.getStroke()).thenReturn(null);

        controller.editNode(model);

        assertThat(stroke.isSelected()).isFalse();
        assertThat(getControllerValue(controller, "model", AnchoredShapeModel.class)).isEqualTo(model);
    }

    @Test
    public void editNodeUpdatesStrokeAndColor() throws Exception {
        when(model.getStroke()).thenReturn(Color.ALICEBLUE);
        when(model.getStrokeWidth()).thenReturn(3d);

        controller.editNode(model);

        assertThat(stroke.isSelected()).isTrue();
        assertThat(strokeColor.getValue()).isEqualTo(Color.ALICEBLUE);
        assertThat(strokeWidth.getText()).isEqualTo("3");
        assertThat(getControllerValue(controller, "model", AnchoredShapeModel.class)).isEqualTo(model);
    }

    @Test
    public void newNodeClearsControllerNode() throws Exception {
        controller.newNode(null);

        assertThat(getControllerValue(controller, "model", AnchoredShapeModel.class)).isEqualTo(null);
    }

    @Test
    public void newNodeSetsStrokeOnShape() throws Exception {
        stroke.setSelected(true);
        strokeColor.setValue(Color.ALICEBLUE);
        strokeWidth.setText("2");
        controller.onStrokeWidthChange();

        controller.newNode(model);

        verify(model).setStroke(Color.ALICEBLUE);
        verify(model).setStrokeWidth(2d);
        assertThat(getControllerValue(controller, "model", AnchoredShapeModel.class)).isEqualTo(model);
    }

    @Test
    public void newNodeClearsStrokeOnShape() throws Exception {
        stroke.setSelected(false);

        controller.newNode(model);

        verify(model).setStroke(null);
        assertThat(getControllerValue(controller, "model", AnchoredShapeModel.class)).isEqualTo(model);
    }

    @Test
    public void onStrokeColorChangeHandlesNullNode() throws Exception {
        controller.onStrokeColorChange();
    }

    @Test
    public void onStrokeColorChangeUpdatesNode() throws Exception {
        controller.editNode(model);
        strokeColor.setValue(Color.ALICEBLUE);

        controller.onStrokeColorChange();

        verify(model).setStroke(Color.ALICEBLUE);
    }

    @Test
    public void onStrokeChangeHandlesNullNode() throws Exception {
        controller.onStrokeChange();
    }

    @Test
    public void onStrokeChangeSetsStrokeColor() throws Exception {
        controller.editNode(model);
        stroke.setSelected(true);
        strokeColor.setValue(Color.ALICEBLUE);
        strokeWidth.setText("2");
        controller.onStrokeWidthChange();

        controller.onStrokeChange();

        verify(model).setStroke(Color.ALICEBLUE);
        verify(model, times(2)).setStrokeWidth(2d);
    }

    @Test
    public void onStrokeChangeClearsStrokeColor() throws Exception {
        controller.editNode(model);
        stroke.setSelected(false);

        controller.onStrokeChange();

        verify(model).setStroke(null);
    }
}