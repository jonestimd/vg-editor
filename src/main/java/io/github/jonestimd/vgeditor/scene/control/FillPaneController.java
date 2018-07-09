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

import io.github.jonestimd.vgeditor.scene.model.ShapeModel;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class FillPaneController {
    @FXML
    private CheckBox fill;
    @FXML
    private ColorPicker fillColor;

    private ShapeModel model;

    public void initialize() {
        fillColor.setValue(Color.BLACK);
    }

    public void editNode(ShapeModel model) {
        this.model = model;
        Paint nodeFill = model.getFill();
        if (nodeFill != null) {
            fill.setSelected(true);
            fillColor.setValue((Color) nodeFill);
            fillColor.setDisable(false);
        }
        else {
            fill.setSelected(false);
            fillColor.setDisable(true);
        }
    }

    public void newNode(ShapeModel model) {
        this.model = model;
        setNodeFill();
    }

    private void setNodeFill() {
        if (model != null) {
            if (fill.isSelected()) model.setFill(fillColor.getValue());
            else model.setFill(null);
        }
    }

    public void onFillColorChange() {
        if (model != null) model.setFill(fillColor.getValue());
    }

    public void onFillChange() {
        fillColor.setDisable(!fill.isSelected());
        setNodeFill();
    }
}
