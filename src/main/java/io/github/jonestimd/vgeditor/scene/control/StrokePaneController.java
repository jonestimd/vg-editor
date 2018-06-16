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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class StrokePaneController {
    private static final double DEFAULT_STROKE_WIDTH = 1;
    @FXML
    private ColorPicker strokeColor;
    @FXML
    private TextField strokeWidthInput;

    private boolean stroke = true;
    private double strokeWidth = DEFAULT_STROKE_WIDTH;
    private Shape node;

    public void initialize() {
        strokeColor.setValue(Color.BLACK);
    }

    public void setNode(Shape node) {
        this.node = node;
        setStroke();
    }

    private void setStroke() {
        if (!stroke) node.setStroke(null);
        else {
            node.setStroke(strokeColor.getValue());
            node.setStrokeWidth(strokeWidth);
        }
    }

    public void setStrokeColor(ActionEvent event) {
        node.setStroke(strokeColor.getValue());
    }

    public void setStrokeWidth(KeyEvent event) {
        strokeWidth = TextFields.parseDouble(event, DEFAULT_STROKE_WIDTH);
        node.setStrokeWidth(strokeWidth);
    }

    public void setStroke(ActionEvent event) {
        CheckBox source = (CheckBox) event.getSource();
        stroke = source.isSelected();
        strokeColor.setDisable(!stroke);
        strokeWidthInput.setDisable(!stroke);
        setStroke();
    }
}
