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
import io.github.jonestimd.vgeditor.model.ShapeModel;
import io.github.jonestimd.vgeditor.scene.Nodes;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FillPaneControllerTest extends JavafxTest {
    private FillPaneController controller;
    private CheckBox fill;
    private ColorPicker fillColor;
    private ShapeModel model = mock(ShapeModel.class);

    @Before
    public void loadForm() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("io.github.jonestimd.vgeditor.labels"));
        loader.setLocation(getClass().getResource("FillPane.fxml"));
        Pane form = loader.load();
        controller = loader.getController();
        fill = Nodes.findFirstById(form, "fill", CheckBox.class).get();
        fillColor = Nodes.findFirstById(form, "fillColor", ColorPicker.class).get();
    }

    @Test
    public void initialize() throws Exception {
        assertThat(controller).isNotNull();
        assertThat(fill.isSelected()).isFalse();
        assertThat(fillColor.getValue()).isEqualTo(Color.BLACK);
    }

    @Test
    public void editNodeClearsFill() throws Exception {
        fill.setSelected(true);
        when(model.getFill()).thenReturn(null);

        controller.editNode(model);

        assertThat(fill.isSelected()).isFalse();
        assertThat(getControllerValue(controller, "model", ShapeModel.class)).isEqualTo(model);
    }

    @Test
    public void editNodeUpdatesFillAndColor() throws Exception {
        when(model.getFill()).thenReturn(Color.ALICEBLUE);

        controller.editNode(model);

        assertThat(fill.isSelected()).isTrue();
        assertThat(fillColor.getValue()).isEqualTo(Color.ALICEBLUE);
        assertThat(getControllerValue(controller, "model", ShapeModel.class)).isEqualTo(model);
    }

    @Test
    public void newNodeClearsControllerNode() throws Exception {
        controller.newNode(null);

        assertThat(getControllerValue(controller, "model", ShapeModel.class)).isEqualTo(null);
    }

    @Test
    public void newNodeSetsFillOnShape() throws Exception {
        fill.setSelected(true);
        fillColor.setValue(Color.ALICEBLUE);

        controller.newNode(model);

        verify(model).setFill(Color.ALICEBLUE);
        assertThat(getControllerValue(controller, "model", ShapeModel.class)).isEqualTo(model);
    }

    @Test
    public void newNodeClearsFillOnShape() throws Exception {
        fill.setSelected(false);

        controller.newNode(model);

        verify(model).setFill(null);
        assertThat(getControllerValue(controller, "model", ShapeModel.class)).isEqualTo(model);
    }

    @Test
    public void onFillColorChangeHandlesNullNode() throws Exception {
        controller.onFillColorChange();
    }

    @Test
    public void onFillColorChangeUpdatesNode() throws Exception {
        controller.editNode(model);
        fillColor.setValue(Color.ALICEBLUE);

        controller.onFillColorChange();

        verify(model).setFill(Color.ALICEBLUE);
    }

    @Test
    public void onFillChangeHandlesNullNode() throws Exception {
        controller.onFillChange();
    }

    @Test
    public void onFillChangeSetsNodeColor() throws Exception {
        controller.editNode(model);
        fill.setSelected(true);
        fillColor.setValue(Color.ALICEBLUE);

        controller.onFillChange();

        verify(model).setFill(Color.ALICEBLUE);
    }

    @Test
    public void onFillChangeClearsNodeColor() throws Exception {
        controller.editNode(model);
        fill.setSelected(false);

        controller.onFillChange();

        verify(model).setFill(null);
    }
}