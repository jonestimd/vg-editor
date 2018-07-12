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

import io.github.jonestimd.vgeditor.scene.control.MouseInputHandler.StartDragPredicate;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MouseInputHandlerTest {
    @Mock
    private Parent parent;
    @Mock
    private StartDragPredicate startDrag;
    @Mock
    private BiConsumer<Point2D, Point2D> continueDrag;
    @Mock
    private Runnable endDrag;
    @InjectMocks
    private MouseInputHandler mouseInputHandler;

    private Point2D start = new Point2D(1, 2);
    private Point2D drag = new Point2D(3, 4);
    private Point2D move = new Point2D(5, 6);

    @Test
    public void ignoresSecondaryPress() throws Exception {
        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.MOUSE_PRESSED, start.getX(), start.getY(), MouseButton.SECONDARY, false));
        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.DRAG_DETECTED, drag.getX(), drag.getY(), MouseButton.SECONDARY, false));

        verifyZeroInteractions(startDrag);
        verify(parent, never()).startFullDrag();
    }

    @Test
    public void ignoresDragWhenStarDragReturnsFalse() throws Exception {
        when(startDrag.test(any(Point2D.class), anyBoolean())).thenReturn(false);

        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.MOUSE_PRESSED, start.getX(), start.getY(), MouseButton.PRIMARY, false));
        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.DRAG_DETECTED, drag.getX(), drag.getY(), MouseButton.PRIMARY, false));

        verify(startDrag).test(start, false);
        verify(parent, never()).startFullDrag();
    }

    @Test
    public void capturesStartPoint() throws Exception {
        when(startDrag.test(any(Point2D.class), anyBoolean())).thenReturn(true);

        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.MOUSE_PRESSED, start.getX(), start.getY(), MouseButton.PRIMARY, false));
        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.DRAG_DETECTED, drag.getX(), drag.getY(), MouseButton.PRIMARY, false));
        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.MOUSE_MOVED, move.getX(), move.getY(), MouseButton.PRIMARY, false));

        verify(startDrag).test(start, false);
        verify(continueDrag).accept(start, move);
        verify(parent).startFullDrag();
        verifyZeroInteractions(endDrag);
    }

    @Test
    public void endsDragOnRelease() throws Exception {
        when(startDrag.test(any(Point2D.class), anyBoolean())).thenReturn(true);

        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.MOUSE_PRESSED, start.getX(), start.getY(), MouseButton.PRIMARY, false));
        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.DRAG_DETECTED, drag.getX(), drag.getY(), MouseButton.PRIMARY, false));
        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.MOUSE_MOVED, move.getX(), move.getY(), MouseButton.PRIMARY, false));
        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.MOUSE_RELEASED, move.getX(), move.getY(), MouseButton.PRIMARY, false));

        verify(startDrag).test(start, false);
        verify(continueDrag).accept(start, move);
        verify(parent).startFullDrag();
        verify(endDrag).run();
    }

    @Test
    public void ignoresReleaseWhenNotDragging() throws Exception {
        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.MOUSE_PRESSED, start.getX(), start.getY(), MouseButton.PRIMARY, false));
        mouseInputHandler.handle(parent, getMouseEvent(MouseEvent.MOUSE_RELEASED, move.getX(), move.getY(), MouseButton.PRIMARY, false));

        verifyZeroInteractions(startDrag, continueDrag, endDrag);
    }

    private MouseEvent getMouseEvent(EventType<MouseEvent> eventType, double x, double y, MouseButton button, boolean controlDown) {
        return new MouseEvent(parent, parent, eventType, x, y, x, y, button, 0,
                false, controlDown, false, false, true, false, false, false, false, false, null);
    }
}