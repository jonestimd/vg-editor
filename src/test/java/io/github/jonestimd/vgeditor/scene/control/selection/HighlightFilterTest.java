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
package io.github.jonestimd.vgeditor.scene.control.selection;

import io.github.jonestimd.vgeditor.scene.SceneTest;
import io.github.jonestimd.vgeditor.scene.model.RectangleModel;
import io.github.jonestimd.vgeditor.scene.shape.path.PathVisitor;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HighlightFilterTest extends SceneTest {
    private static final int SCREEN_X = 5;
    private static final int SCREEN_Y = 15;

    @Test
    public void checksPolylineBounds() throws Exception {
        Bounds bounds = new BoundingBox(0, 0, 20, 30);
        Polyline polyline = mock(Polyline.class);
        when(polyline.screenToLocal(any(Bounds.class))).thenReturn(bounds);
        when(polyline.intersects(any(Bounds.class))).thenReturn(false);
        HighlightFilter highlightFilter = new HighlightFilter(SCREEN_X, SCREEN_Y, null);

        assertThat(highlightFilter.test(polyline)).isFalse();

        ArgumentCaptor<Bounds> boundsCaptor = ArgumentCaptor.forClass(Bounds.class);
        verify(polyline).screenToLocal(boundsCaptor.capture());
        verify(polyline).intersects(same(bounds));
        verifyNoMoreInteractions(polyline);
        assertThat(boundsCaptor.getValue().getMinX()).isEqualTo(SCREEN_X-HIGHLIGHT_OFFSET);
        assertThat(boundsCaptor.getValue().getMinY()).isEqualTo(SCREEN_Y-HIGHLIGHT_OFFSET);
        assertThat(boundsCaptor.getValue().getMaxX()).isEqualTo(SCREEN_X+HIGHLIGHT_OFFSET);
        assertThat(boundsCaptor.getValue().getMaxY()).isEqualTo(SCREEN_Y+HIGHLIGHT_OFFSET);
    }

    @Test
    public void checksPolylineSegments() throws Exception {
        Polyline polyline = new Polyline(SCREEN_X, SCREEN_Y-10, SCREEN_X, SCREEN_Y+20, SCREEN_X+30, SCREEN_Y+20);
        polyline.setStrokeWidth(1);
        diagram.getChildren().add(polyline);

        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y, null).test(polyline)).isTrue();
        assertThat(new HighlightFilter(SCREEN_X+HIGHLIGHT_OFFSET+1, SCREEN_Y, null).test(polyline)).isFalse();
    }

    @Test
    public void checksPathBounds() throws Exception {
        Bounds bounds = new BoundingBox(0, 0, 20, 30);
        Path path = mock(Path.class);
        when(path.screenToLocal(any(Bounds.class))).thenReturn(bounds);
        when(path.intersects(any(Bounds.class))).thenReturn(false);
        HighlightFilter highlightFilter = new HighlightFilter(SCREEN_X, SCREEN_Y, null);

        assertThat(highlightFilter.test(path)).isFalse();

        ArgumentCaptor<Bounds> boundsCaptor = ArgumentCaptor.forClass(Bounds.class);
        verify(path).screenToLocal(boundsCaptor.capture());
        verify(path).intersects(same(bounds));
        verifyNoMoreInteractions(path);
        assertThat(boundsCaptor.getValue().getMinX()).isEqualTo(SCREEN_X-HIGHLIGHT_OFFSET);
        assertThat(boundsCaptor.getValue().getMinY()).isEqualTo(SCREEN_Y-HIGHLIGHT_OFFSET);
        assertThat(boundsCaptor.getValue().getMaxX()).isEqualTo(SCREEN_X+HIGHLIGHT_OFFSET);
        assertThat(boundsCaptor.getValue().getMaxY()).isEqualTo(SCREEN_Y+HIGHLIGHT_OFFSET);
    }

    @Test
    public void checksPathSegments() throws Exception {
        Path path = new Path(new MoveTo(SCREEN_X, SCREEN_Y-10), new LineTo(SCREEN_X, SCREEN_Y+20), new LineTo(SCREEN_X+30, SCREEN_Y+20));
        path.setStrokeWidth(1);
        diagram.getChildren().add(path);

        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y, PathVisitor::new).test(path)).isTrue();
        assertThat(new HighlightFilter(SCREEN_X+HIGHLIGHT_OFFSET+1, SCREEN_Y, PathVisitor::new).test(path)).isFalse();
    }

    @Test
    public void checksRectangle() throws Exception {
        RectangleModel rectangle = new RectangleModel(diagram, SCREEN_X-10, SCREEN_Y, 20, 20);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1);

        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y, null).test(rectangle.getShape())).isTrue();
        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y-HIGHLIGHT_OFFSET, null).test(rectangle.getShape())).isFalse();
    }

    @Test
    public void checksParent() throws Exception {
        Rectangle rectangle = new Rectangle(SCREEN_X-10, SCREEN_Y, 20, 20);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1);
        Group group = new Group(rectangle);
        diagram.getChildren().add(group);

        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y, null).test(group)).isTrue();
        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y-HIGHLIGHT_OFFSET-1, null).test(group)).isFalse();
    }

    @Test
    public void checksCursorWithinRangeOfHorizontalLine() throws Exception {
        int startX = SCREEN_X-10;
        int endX = SCREEN_X+10;
        Line line = new Line(startX, SCREEN_Y, endX, SCREEN_Y);
        diagram.getChildren().add(line);

        assertThat(new HighlightFilter(startX-HIGHLIGHT_OFFSET, SCREEN_Y-HIGHLIGHT_OFFSET, null).test(line)).isTrue();
        assertThat(new HighlightFilter(startX-HIGHLIGHT_OFFSET, SCREEN_Y+HIGHLIGHT_OFFSET, null).test(line)).isTrue();
        assertThat(new HighlightFilter(endX-HIGHLIGHT_OFFSET, SCREEN_Y-HIGHLIGHT_OFFSET, null).test(line)).isTrue();
        assertThat(new HighlightFilter(endX-HIGHLIGHT_OFFSET, SCREEN_Y+HIGHLIGHT_OFFSET, null).test(line)).isTrue();

        assertThat(new HighlightFilter(startX-HIGHLIGHT_OFFSET-1, SCREEN_Y, null).test(line)).isFalse();
        assertThat(new HighlightFilter(endX+HIGHLIGHT_OFFSET+1, SCREEN_Y, null).test(line)).isFalse();
        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y-HIGHLIGHT_OFFSET-1, null).test(line)).isFalse();
        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y+HIGHLIGHT_OFFSET+1, null).test(line)).isFalse();
    }

    @Test
    public void checksCursorWithinRangeOfVerticalLine() throws Exception {
        int startY = SCREEN_Y-10;
        int endY = SCREEN_Y+10;
        Line line = new Line(SCREEN_X, startY, SCREEN_X, endY);
        line.setStrokeWidth(1);
        diagram.getChildren().add(line);

        assertThat(new HighlightFilter(SCREEN_X-HIGHLIGHT_OFFSET, startY-HIGHLIGHT_OFFSET, null).test(line)).isTrue();
        assertThat(new HighlightFilter(SCREEN_X+HIGHLIGHT_OFFSET, startY-HIGHLIGHT_OFFSET, null).test(line)).isTrue();
        assertThat(new HighlightFilter(SCREEN_X-HIGHLIGHT_OFFSET, endY+HIGHLIGHT_OFFSET, null).test(line)).isTrue();
        assertThat(new HighlightFilter(SCREEN_X+HIGHLIGHT_OFFSET, endY+HIGHLIGHT_OFFSET, null).test(line)).isTrue();

        assertThat(new HighlightFilter(SCREEN_X-HIGHLIGHT_OFFSET-1, SCREEN_Y, null).test(line)).isFalse();
        assertThat(new HighlightFilter(SCREEN_X+HIGHLIGHT_OFFSET+1, SCREEN_Y, null).test(line)).isFalse();
        assertThat(new HighlightFilter(SCREEN_X, startY-HIGHLIGHT_OFFSET-1, null).test(line)).isFalse();
        assertThat(new HighlightFilter(SCREEN_X, endY+HIGHLIGHT_OFFSET+1, null).test(line)).isFalse();
    }

    @Test
    public void checksCursorWithinRangeOfDiagonalLine() throws Exception {
        int startX = SCREEN_X-15;
        int startY = SCREEN_Y-10;
        int endX = SCREEN_X+15;
        int endY = SCREEN_Y+10;
        Line line = new Line(startX, startY, endX, endY);
        line.setStrokeWidth(1);
        diagram.getChildren().add(line);

        assertThat(new HighlightFilter(startX, startY, null).test(line)).isTrue();
        assertThat(new HighlightFilter(endX, endY, null).test(line)).isTrue();

        assertThat(new HighlightFilter(startX-1, startY, null).test(line)).isFalse();
        assertThat(new HighlightFilter(startX, startY-1, null).test(line)).isFalse();
        assertThat(new HighlightFilter(endX+1, endY, null).test(line)).isFalse();
        assertThat(new HighlightFilter(endX, endY+1, null).test(line)).isFalse();
    }
}