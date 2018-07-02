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

import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MouseInputHandler {
    private boolean mouseDragging;
    private Point2D startPoint;

    private final StartDragPredicate startDrag;
    private final BiConsumer<Point2D, Point2D> continueDrag;
    private final Runnable endDrag;

    public MouseInputHandler(StartDragPredicate startDrag, BiConsumer<Point2D, Point2D> continueDrag, Runnable endDrag) {
        this.startDrag = startDrag;
        this.continueDrag = continueDrag;
        this.endDrag = endDrag;
    }

    public void handle(Parent diagram, MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.getButton() == MouseButton.PRIMARY) {
            startPoint = new Point2D(event.getScreenX(), event.getScreenY());
        }
        else if (event.getEventType() == MouseEvent.DRAG_DETECTED && event.getButton() == MouseButton.PRIMARY) {
            if (startPoint == null) { // java 9 or 10
                startPoint = new Point2D(event.getScreenX(), event.getScreenY());
            }
            if (startDrag.test(startPoint, event.isShortcutDown())) {
                this.mouseDragging = true;
                diagram.startFullDrag();
            }
        }
        else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (mouseDragging) {
                endDrag.run();
                mouseDragging = false;
            }
        }
        else if (mouseDragging) continueDrag.accept(startPoint, new Point2D(event.getScreenX(), event.getScreenY()));
    }

    public interface StartDragPredicate {
        boolean test(Point2D point, boolean isShortcutDown);
    }
}
