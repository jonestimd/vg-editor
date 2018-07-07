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
package io.github.jonestimd.vgeditor.scene.model;

import io.github.jonestimd.vgeditor.scene.SceneTest;
import io.github.jonestimd.vgeditor.scene.control.selection.HighlightBounds;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RectangleModelTest extends SceneTest {
    private static final int X = 20;
    private static final int Y = 30;
    private static final int WIDTH = 40;
    private static final int HEIGHT = 50;
    private final HighlightBounds bounds = new HighlightBounds(X, Y, WIDTH, HEIGHT);
    private RectangleModel model;

    @Override
    public void setUpScene() throws Exception {
        super.setUpScene();
        model = new RectangleModel(diagram, X, Y, WIDTH, HEIGHT);
        model.setStroke(Color.BLACK);
        model.setStrokeWidth(1);
        model.setFill(null);
    }

    @Test
    public void isInSelectionRange_UsesContainsForFilledRectangle() throws Exception {
        final int screenX = 1, screenY = 2;
        Rectangle rectangle = spy(new Rectangle(10, 20, Color.BLACK));
        model.remove();
        model = new RectangleModel(diagram, rectangle);

        Assertions.assertThat(model.isInSelectionRange(screenX, screenY)).isTrue();

        ArgumentCaptor<Point2D> cursorCaptor = ArgumentCaptor.forClass(Point2D.class);
        verify(rectangle).screenToLocal(screenX, screenY);
        verify(rectangle).contains(cursorCaptor.capture());
        assertThat(cursorCaptor.getValue().getX()).isEqualTo(screenX);
        assertThat(cursorCaptor.getValue().getY()).isEqualTo(screenY);
    }

    @Test
    public void isInSelectionRange_ReturnsTrueWhenWithinRangeOfUnfilledRectangle() throws Exception {
        bounds.forEach(bound -> {
            assertThat(model.isInSelectionRange(bound.x+bound.getInnerX(1), bound.y+bound.getInnerY(-1))).isTrue();
            assertThat(model.isInSelectionRange(bound.x+bound.getInnerX(-1), bound.y+bound.getInnerY(1))).isTrue();
            assertThat(model.isInSelectionRange(bound.x-bound.getInnerX(-1), bound.y-bound.getInnerY(-1))).isTrue();
        });
    }

    @Test
    public void isInSelectionRange_ReturnsFalseWhenInsideUnfilledRectangle() throws Exception {
        bounds.forEach(bound -> {
            assertThat(model.isInSelectionRange(bound.x+bound.getInnerX(1), bound.y+bound.getInnerY(1))).isFalse();
        });
    }

    @Test
    public void isInSelectionRange_ReturnsFalseWhenOutsideRangeOfUnfilledRectangle() throws Exception {
        bounds.forEach(bound -> {
            assertThat(model.isInSelectionRange(bound.x+bound.getInnerX(0), bound.y-bound.getInnerY(1))).isFalse();
            assertThat(model.isInSelectionRange(bound.x-bound.getInnerX(1), bound.y+bound.getInnerY(0))).isFalse();
        });
    }

    @Test
    public void getMarkerLocation_ReturnsCorner() throws Exception {
        bounds.forEach(bound -> {
            Point2D corner = new Point2D(bound.x, bound.y);
            assertThat(model.getMarkerLocation(bound.x+bound.getInnerX(0), bound.y)).isEqualTo(corner);
            assertThat(model.getMarkerLocation(bound.x, bound.y+bound.getInnerY(0))).isEqualTo(corner);
        });
    }

    @Test
    public void getMarkerLocation_ReturnsMidpointOfSide() throws Exception {
        bounds.forEach(bound -> {
            assertThat(model.getMarkerLocation(bound.x+bound.getInnerX(1), bound.y)).isEqualTo(bound.getMidpoint(true));
            assertThat(model.getMarkerLocation(bound.x, bound.y+bound.getInnerY(1))).isEqualTo(bound.getMidpoint(false));
        });
    }

    @Test
    public void getMarkerLocation_ReturnsCenterOfRectangle() throws Exception {
        Point2D point = model.getMarkerLocation(X+HIGHLIGHT_OFFSET+2, Y+HIGHLIGHT_OFFSET+2);

        assertThat(point.getX()).isEqualTo(X+WIDTH/2);
        assertThat(point.getY()).isEqualTo(Y+HEIGHT/2);
    }
}