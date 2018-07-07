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

import java.util.Map;
import java.util.function.DoubleConsumer;

import com.google.common.collect.ImmutableMap;
import io.github.jonestimd.vgeditor.model.RectangleModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;

public class RectangleController extends ShapeController<RectangleModel> {
    public static final String ID_ARC_WIDTH = "arcWidth";
    public static final String ID_ARC_HEIGHT = "arcHeight";

    private final Map<String, DoubleConsumer> fieldHandlers = ImmutableMap.of(
            ID_ARC_WIDTH, this::setNodeArcWidth,
            ID_ARC_HEIGHT, this::setNodeArcHeight);

    @FXML
    private TextField arcWidth;
    @FXML
    private TextField arcHeight;

    public RectangleController() {
        super(RectangleModel::new);
    }

    public void onKeyEvent(KeyEvent event) {
        TextInputControl field = (TextInputControl) event.getSource();
        fieldHandlers.get(field.getId()).accept(TextFields.parseDouble(field).orElse(0));
    }

    @Override
    public void setModel(RectangleModel model) {
        super.setModel(model);
        arcWidth.setText(Preferences.numberFormat().format(model.getArcWidth()));
        arcHeight.setText(Preferences.numberFormat().format(model.getArcHeight()));
    }

    private void setNodeArcWidth(double width) {
        if (getModel() != null) getModel().setArcWidth(width);
    }

    private void setNodeArcHeight(double height) {
        if (getModel() != null) getModel().setArcHeight(height);
    }

    @Override
    protected void createNode() {
        super.createNode();
        getModel().setArcWidth(TextFields.parseDouble(arcWidth).orElse(0));
        getModel().setArcHeight(TextFields.parseDouble(arcHeight).orElse(0));
    }
}