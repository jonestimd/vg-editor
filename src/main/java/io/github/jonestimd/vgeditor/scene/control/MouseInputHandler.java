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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MouseInputHandler {
    private boolean mouseDragging;
    private Point2D startPoint;

    private final Consumer<Point2D> startDrag;
    private final BiConsumer<Point2D, Point2D> continueDrag;

    public MouseInputHandler(Consumer<Point2D> startDrag, BiConsumer<Point2D, Point2D> continueDrag) {
        this.startDrag = startDrag;
        this.continueDrag = continueDrag;
    }

    public void handle(Parent diagram, MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.getButton() == MouseButton.PRIMARY) {
            startPoint = diagram.screenToLocal(event.getScreenX(), event.getScreenY());
            startDrag.accept(startPoint);
        }
        else if (event.getEventType() == MouseEvent.DRAG_DETECTED && event.getButton() == MouseButton.PRIMARY) {
            this.mouseDragging = true;
            diagram.startFullDrag();
        }
        else if (event.getEventType() == MouseEvent.MOUSE_RELEASED && mouseDragging) {
            this.mouseDragging = false;
        }
        else if (mouseDragging) {
            Point2D point = diagram.screenToLocal(event.getScreenX(), event.getScreenY());
            continueDrag.accept(startPoint, point);
        }
    }
}
