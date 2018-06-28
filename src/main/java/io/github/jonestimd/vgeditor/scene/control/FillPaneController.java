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

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;

public class FillPaneController {
    @FXML
    private CheckBox fill;
    @FXML
    private ColorPicker fillColor;

    private Shape node;

    public void initialize() {
        fillColor.setValue(Color.BLACK);
    }

    public void editNode(Shape node) {
        this.node = node;
        Paint nodeFill = node.getFill();
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

    public void newNode(Shape node) {
        this.node = node;
        setNodeFill();
    }

    private void setNodeFill() {
        if (node != null) {
            if (fill.isSelected()) node.setFill(fillColor.getValue());
            else node.setFill(null);
        }
    }

    public void onFillColorChange() {
        if (node != null) node.setFill(fillColor.getValue());
    }

    public void onFillChange() {
        fillColor.setDisable(!fill.isSelected());
        setNodeFill();
    }
}
