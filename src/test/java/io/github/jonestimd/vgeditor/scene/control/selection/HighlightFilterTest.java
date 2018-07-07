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
import io.github.jonestimd.vgeditor.scene.model.PathModel;
import io.github.jonestimd.vgeditor.scene.model.PolylineModel;
import io.github.jonestimd.vgeditor.scene.model.RectangleModel;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Rectangle;
import org.junit.Test;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;
import static org.assertj.core.api.Assertions.*;

public class HighlightFilterTest extends SceneTest {
    private static final int SCREEN_X = 5;
    private static final int SCREEN_Y = 15;

    @Test
    public void checksPolylineSegments() throws Exception {
        PolylineModel model = new PolylineModel(diagram, SCREEN_X, SCREEN_Y-10, SCREEN_X, SCREEN_Y+20, SCREEN_X+30, SCREEN_Y+20);
        model.setStrokeWidth(1);

        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y).test(model.getShape())).isTrue();
        assertThat(new HighlightFilter(SCREEN_X+HIGHLIGHT_OFFSET+1, SCREEN_Y).test(model.getShape())).isFalse();
    }

    @Test
    public void checksPathSegments() throws Exception {
        PathModel path = new PathModel(diagram, new MoveTo(SCREEN_X, SCREEN_Y-10), new LineTo(SCREEN_X, SCREEN_Y+20), new LineTo(SCREEN_X+30, SCREEN_Y+20));
        path.setStrokeWidth(1);

        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y).test(path.getShape())).isTrue();
        assertThat(new HighlightFilter(SCREEN_X+HIGHLIGHT_OFFSET+1, SCREEN_Y).test(path.getShape())).isFalse();
    }

    @Test
    public void checksRectangle() throws Exception {
        RectangleModel rectangle = new RectangleModel(diagram, SCREEN_X-10, SCREEN_Y, 20, 20);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1);

        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y).test(rectangle.getShape())).isTrue();
        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y-HIGHLIGHT_OFFSET).test(rectangle.getShape())).isFalse();
    }

    @Test
    public void checksParent() throws Exception {
        Rectangle rectangle = new Rectangle(SCREEN_X-10, SCREEN_Y, 20, 20);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1);
        Group group = new Group(rectangle);
        diagram.getChildren().add(group);

        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y).test(group)).isTrue();
        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y-HIGHLIGHT_OFFSET-1).test(group)).isFalse();
    }

    @Test
    public void checksCursorWithinRangeOfHorizontalLine() throws Exception {
        int startX = SCREEN_X-10;
        int endX = SCREEN_X+10;
        Line line = new Line(startX, SCREEN_Y, endX, SCREEN_Y);
        diagram.getChildren().add(line);

        assertThat(new HighlightFilter(startX-HIGHLIGHT_OFFSET, SCREEN_Y-HIGHLIGHT_OFFSET).test(line)).isTrue();
        assertThat(new HighlightFilter(startX-HIGHLIGHT_OFFSET, SCREEN_Y+HIGHLIGHT_OFFSET).test(line)).isTrue();
        assertThat(new HighlightFilter(endX-HIGHLIGHT_OFFSET, SCREEN_Y-HIGHLIGHT_OFFSET).test(line)).isTrue();
        assertThat(new HighlightFilter(endX-HIGHLIGHT_OFFSET, SCREEN_Y+HIGHLIGHT_OFFSET).test(line)).isTrue();

        assertThat(new HighlightFilter(startX-HIGHLIGHT_OFFSET-1, SCREEN_Y).test(line)).isFalse();
        assertThat(new HighlightFilter(endX+HIGHLIGHT_OFFSET+1, SCREEN_Y).test(line)).isFalse();
        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y-HIGHLIGHT_OFFSET-1).test(line)).isFalse();
        assertThat(new HighlightFilter(SCREEN_X, SCREEN_Y+HIGHLIGHT_OFFSET+1).test(line)).isFalse();
    }

    @Test
    public void checksCursorWithinRangeOfVerticalLine() throws Exception {
        int startY = SCREEN_Y-10;
        int endY = SCREEN_Y+10;
        Line line = new Line(SCREEN_X, startY, SCREEN_X, endY);
        line.setStrokeWidth(1);
        diagram.getChildren().add(line);

        assertThat(new HighlightFilter(SCREEN_X-HIGHLIGHT_OFFSET, startY-HIGHLIGHT_OFFSET).test(line)).isTrue();
        assertThat(new HighlightFilter(SCREEN_X+HIGHLIGHT_OFFSET, startY-HIGHLIGHT_OFFSET).test(line)).isTrue();
        assertThat(new HighlightFilter(SCREEN_X-HIGHLIGHT_OFFSET, endY+HIGHLIGHT_OFFSET).test(line)).isTrue();
        assertThat(new HighlightFilter(SCREEN_X+HIGHLIGHT_OFFSET, endY+HIGHLIGHT_OFFSET).test(line)).isTrue();

        assertThat(new HighlightFilter(SCREEN_X-HIGHLIGHT_OFFSET-1, SCREEN_Y).test(line)).isFalse();
        assertThat(new HighlightFilter(SCREEN_X+HIGHLIGHT_OFFSET+1, SCREEN_Y).test(line)).isFalse();
        assertThat(new HighlightFilter(SCREEN_X, startY-HIGHLIGHT_OFFSET-1).test(line)).isFalse();
        assertThat(new HighlightFilter(SCREEN_X, endY+HIGHLIGHT_OFFSET+1).test(line)).isFalse();
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

        assertThat(new HighlightFilter(startX, startY).test(line)).isTrue();
        assertThat(new HighlightFilter(endX, endY).test(line)).isTrue();

        assertThat(new HighlightFilter(startX-1, startY).test(line)).isFalse();
        assertThat(new HighlightFilter(startX, startY-1).test(line)).isFalse();
        assertThat(new HighlightFilter(endX+1, endY).test(line)).isFalse();
        assertThat(new HighlightFilter(endX, endY+1).test(line)).isFalse();
    }
}