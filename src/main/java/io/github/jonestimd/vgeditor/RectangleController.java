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
package io.github.jonestimd.vgeditor;

import java.util.Map;
import java.util.function.DoubleConsumer;

import com.google.common.collect.ImmutableMap;
import javafx.event.ActionEvent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Rectangle;

public class RectangleController implements NodeController<Rectangle> {
    private final Map<String, DoubleConsumer> VALUE_SETTERS = ImmutableMap.of(
        "x", this::setX,
        "y", this::setY,
        "width", this::setWidth,
        "height", this::setHeight
    );

    private Rectangle node;
    private NodeAnchor nodeAnchor = NodeAnchor.TOP_LEFT;

    @Override
    public Rectangle getNode() {
        return node;
    }

    @Override
    public void newNode() {
        this.node = new Rectangle();
    }

    public void selectAnchor(ActionEvent event) {
        RadioButton source = (RadioButton) event.getSource();
        this.nodeAnchor = NodeAnchor.decode(source.getId());
        if (nodeAnchor.isLeft()) node.setTranslateX(0);
        else if (nodeAnchor.isRight()) node.setTranslateX(-node.getWidth());
        else node.setTranslateX(-node.getWidth()/2);
        if (nodeAnchor.isTop()) node.setTranslateY(0);
        else if (nodeAnchor.isBottom()) node.setTranslateY(-node.getHeight());
        else node.setTranslateY(-node.getHeight()/2);
    }

    public void inputChange(KeyEvent event) {
        TextInputControl input = (TextInputControl) event.getSource();
        DoubleConsumer setter = VALUE_SETTERS.get(input.getId());
        try {
            setter.accept(Double.parseDouble(input.getText()));
        } catch (NumberFormatException ex) {
            setter.accept(0d);
        }
    }

    public void setX(double value) {
        node.setX(value);
    }

    public void setY(double value) {
        node.setY(value);
    }

    public void setWidth(double value) {
        node.setWidth(value);
        if (nodeAnchor.isRight()) node.setTranslateX(-node.getWidth());
        else if (!nodeAnchor.isLeft()) node.setTranslateX(-node.getWidth()/2);
    }

    public void setHeight(double value) {
        node.setHeight(value);
        if (nodeAnchor.isBottom()) node.setTranslateY(-node.getHeight());
        else if (!nodeAnchor.isTop()) node.setTranslateY(-node.getHeight()/2);
    }
}
