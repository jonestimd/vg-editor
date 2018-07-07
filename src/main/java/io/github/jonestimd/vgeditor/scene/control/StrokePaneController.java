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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import io.github.jonestimd.vgeditor.scene.model.AnchoredShapeModel;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class StrokePaneController {
    private static final double DEFAULT_STROKE_WIDTH = 1;
    private NumberFormat numberFormat = new DecimalFormat("#0.#");
    @FXML
    private CheckBox stroke;
    @FXML
    private ColorPicker strokeColor;
    @FXML
    private TextField strokeWidthInput;

    private double strokeWidth = DEFAULT_STROKE_WIDTH;
    private AnchoredShapeModel model;

    public void initialize() {
        strokeColor.setValue(Color.BLACK);
    }

    public void editNode(AnchoredShapeModel model) {
        this.model = model;
        if (model.getStroke() != null) {
            setEnabled(true);
            strokeColor.setValue((Color) model.getStroke());
            strokeWidth = model.getStrokeWidth();
            strokeWidthInput.setText(numberFormat.format(model.getStrokeWidth()));
        }
        else setEnabled(false);
    }

    private void setEnabled(boolean enabled) {
        stroke.setSelected(enabled);
        strokeColor.setDisable(!enabled);
        strokeWidthInput.setDisable(!enabled);
    }

    public void newNode(AnchoredShapeModel model) {
        this.model = model;
        setNodeStroke();
    }

    private void setNodeStroke() {
        if (model != null) {
            if (stroke.isSelected()) {
                model.setStroke(strokeColor.getValue());
                model.setStrokeWidth(strokeWidth);
            }
            else model.setStroke(null);
        }
    }

    public void onStrokeColorChange() {
        if (model != null) model.setStroke(strokeColor.getValue());
    }

    public void onStrokeWidthChange() {
        strokeWidth = TextFields.parseDouble(strokeWidthInput).orElse(DEFAULT_STROKE_WIDTH);
        if (model != null) model.setStrokeWidth(strokeWidth);
    }

    public void onStrokeChange() {
        strokeColor.setDisable(!stroke.isSelected());
        strokeWidthInput.setDisable(!stroke.isSelected());
        setNodeStroke();
    }
}
