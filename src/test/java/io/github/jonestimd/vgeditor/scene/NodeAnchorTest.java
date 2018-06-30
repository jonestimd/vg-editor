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
package io.github.jonestimd.vgeditor.scene;

import io.github.jonestimd.vgeditor.JavaFxThreadingRule;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.data.Offset.offset;

public class NodeAnchorTest {
    public static final int WIDTH = 100;
    public static final int HEIGHT = 80;

    @Rule
    public JavaFxThreadingRule rule = new JavaFxThreadingRule();
    private Rectangle rectangle;

    @Before
    public void setUp() throws Exception {
        rectangle = new Rectangle(100, 80);
    }

    @Test
    public void translateTopLeft() throws Exception {
        testTranslate(NodeAnchor.TOP_LEFT, 0, 0, 0);
        testTranslate(NodeAnchor.TOP_LEFT, 90, -WIDTH/2-HEIGHT/2, WIDTH/2-HEIGHT/2);
        testTranslate(NodeAnchor.TOP_LEFT, -90, -WIDTH/2+HEIGHT/2, -WIDTH/2-HEIGHT/2);
        testTranslate(NodeAnchor.TOP_LEFT, 180, -WIDTH, -HEIGHT);
    }

    @Test
    public void translateTopCenter() throws Exception {
        testTranslate(NodeAnchor.TOP, 0, -WIDTH/2, 0);
        testTranslate(NodeAnchor.TOP, 90, -WIDTH/2-HEIGHT/2, -HEIGHT/2);
        testTranslate(NodeAnchor.TOP, -90, -WIDTH/2+HEIGHT/2, -HEIGHT/2);
        testTranslate(NodeAnchor.TOP, 180, -WIDTH/2, -HEIGHT);
    }

    @Test
    public void translateTopRight() throws Exception {
        testTranslate(NodeAnchor.TOP_RIGHT, 0, -WIDTH, 0);
        testTranslate(NodeAnchor.TOP_RIGHT, 90, -WIDTH/2-HEIGHT/2, -HEIGHT/2-WIDTH/2);
        testTranslate(NodeAnchor.TOP_RIGHT, -90, -WIDTH/2+HEIGHT/2, WIDTH/2-HEIGHT/2);
        testTranslate(NodeAnchor.TOP_RIGHT, 180, 0, -HEIGHT);
    }

    @Test
    public void translateLeft() throws Exception {
        testTranslate(NodeAnchor.LEFT, 0, 0, -HEIGHT/2);
        testTranslate(NodeAnchor.LEFT, 90, -WIDTH/2, WIDTH/2-HEIGHT/2);
        testTranslate(NodeAnchor.LEFT, -90, -WIDTH/2, -HEIGHT-WIDTH/2+HEIGHT/2);
        testTranslate(NodeAnchor.LEFT, 180, -WIDTH, -HEIGHT/2);
    }

    @Test
    public void translateCenter() throws Exception {
        testTranslate(NodeAnchor.CENTER, 0, -WIDTH/2, -HEIGHT/2);
        testTranslate(NodeAnchor.CENTER, 90, -WIDTH/2, -HEIGHT/2);
        testTranslate(NodeAnchor.CENTER, -90, -WIDTH/2, -HEIGHT/2);
        testTranslate(NodeAnchor.CENTER, 180, -WIDTH/2, -HEIGHT/2);
    }

    @Test
    public void translateRight() throws Exception {
        testTranslate(NodeAnchor.RIGHT, 0, -WIDTH, -HEIGHT/2);
        testTranslate(NodeAnchor.RIGHT, 90, -WIDTH/2, -HEIGHT/2-WIDTH/2);
        testTranslate(NodeAnchor.RIGHT, -90, -WIDTH/2, -HEIGHT/2+WIDTH/2);
        testTranslate(NodeAnchor.RIGHT, 180, 0, -HEIGHT/2);
    }

    @Test
    public void translateBottomLeft() throws Exception {
        testTranslate(NodeAnchor.BOTTOM_LEFT, 0, 0, -HEIGHT);
        testTranslate(NodeAnchor.BOTTOM_LEFT, 90, -WIDTH/2+HEIGHT/2, WIDTH/2-HEIGHT/2);
        testTranslate(NodeAnchor.BOTTOM_LEFT, -90, -WIDTH/2-HEIGHT/2, -WIDTH/2-HEIGHT/2);
        testTranslate(NodeAnchor.BOTTOM_LEFT, 180, -WIDTH, 0);
    }

    @Test
    public void translateBottomCenter() throws Exception {
        testTranslate(NodeAnchor.BOTTOM, 0, -WIDTH/2, -HEIGHT);
        testTranslate(NodeAnchor.BOTTOM, 90, -WIDTH/2+HEIGHT/2, -HEIGHT/2);
        testTranslate(NodeAnchor.BOTTOM, -90, -WIDTH/2-HEIGHT/2, -HEIGHT/2);
        testTranslate(NodeAnchor.BOTTOM, 180, -WIDTH/2, 0);
    }

    @Test
    public void translateBottomRight() throws Exception {
        testTranslate(NodeAnchor.BOTTOM_RIGHT, 0, -WIDTH, -HEIGHT);
        testTranslate(NodeAnchor.BOTTOM_RIGHT, 90, -WIDTH/2+HEIGHT/2, -HEIGHT/2-WIDTH/2);
        testTranslate(NodeAnchor.BOTTOM_RIGHT, -90, -WIDTH/2-HEIGHT/2, -HEIGHT/2+WIDTH/2);
        testTranslate(NodeAnchor.BOTTOM_RIGHT, 180, 0, 0);
    }

    private void testTranslate(NodeAnchor anchor, double angle, double expectedX, double expectedY) {
        rectangle.setRotate(angle);

        anchor.translate(rectangle, WIDTH, HEIGHT);

        assertThat(rectangle.getLayoutX()).as("x offset").isEqualTo(expectedX, offset(0.0000001));
        assertThat(rectangle.getLayoutY()).as("y offset").isEqualTo(expectedY, offset(0.0000001));
    }

    @Test
    public void valueOfTopLeft() throws Exception {
        testValueOf(NodeAnchor.TOP_LEFT, 0);
        testValueOf(NodeAnchor.TOP_LEFT, 90);
        testValueOf(NodeAnchor.TOP_LEFT, -90);
        testValueOf(NodeAnchor.TOP_LEFT, 180);
    }

    @Test
    public void valueOfTopCenter() throws Exception {
        testValueOf(NodeAnchor.TOP, 0);
        testValueOf(NodeAnchor.TOP, 90);
        testValueOf(NodeAnchor.TOP, -90);
        testValueOf(NodeAnchor.TOP, 180);
    }

    @Test
    public void valueOfTopRight() throws Exception {
        testValueOf(NodeAnchor.TOP_RIGHT, 0);
        testValueOf(NodeAnchor.TOP_RIGHT, 90);
        testValueOf(NodeAnchor.TOP_RIGHT, -90);
        testValueOf(NodeAnchor.TOP_RIGHT, 180);
    }

    @Test
    public void valueOfLeft() throws Exception {
        testValueOf(NodeAnchor.LEFT, 0);
        testValueOf(NodeAnchor.LEFT, 90);
        testValueOf(NodeAnchor.LEFT, -90);
        testValueOf(NodeAnchor.LEFT, 180);
    }

    @Test
    public void valueOfCenter() throws Exception {
        testValueOf(NodeAnchor.CENTER, 0);
        testValueOf(NodeAnchor.CENTER, 90);
        testValueOf(NodeAnchor.CENTER, -90);
        testValueOf(NodeAnchor.CENTER, 180);
    }

    @Test
    public void valueOfRight() throws Exception {
        testValueOf(NodeAnchor.RIGHT, 0);
        testValueOf(NodeAnchor.RIGHT, 90);
        testValueOf(NodeAnchor.RIGHT, -90);
        testValueOf(NodeAnchor.RIGHT, 180);
    }

    @Test
    public void valueOfBottomLeft() throws Exception {
        testValueOf(NodeAnchor.BOTTOM_LEFT, 0);
        testValueOf(NodeAnchor.BOTTOM_LEFT, 90);
        testValueOf(NodeAnchor.BOTTOM_LEFT, -90);
        testValueOf(NodeAnchor.BOTTOM_LEFT, 180);
    }

    @Test
    public void valueOfBottomCenter() throws Exception {
        testValueOf(NodeAnchor.BOTTOM, 0);
        testValueOf(NodeAnchor.BOTTOM, 90);
        testValueOf(NodeAnchor.BOTTOM, -90);
        testValueOf(NodeAnchor.BOTTOM, 180);
    }

    @Test
    public void valueOfBottomRight() throws Exception {
        testValueOf(NodeAnchor.BOTTOM_RIGHT, 0);
        testValueOf(NodeAnchor.BOTTOM_RIGHT, 90);
        testValueOf(NodeAnchor.BOTTOM_RIGHT, -90);
        testValueOf(NodeAnchor.BOTTOM_RIGHT, 180);
    }

    private void testValueOf(NodeAnchor anchor, double angle) {
        rectangle.setRotate(angle);
        anchor.translate(rectangle, WIDTH, HEIGHT);

        assertThat(NodeAnchor.valueOf(rectangle, rectangle.getWidth(), rectangle.getHeight())).isEqualTo(anchor);
    }

    @Test
    public void forResize() throws Exception {
        testForResize(-HIGHLIGHT_OFFSET, HIGHLIGHT_OFFSET, -HIGHLIGHT_OFFSET, HIGHLIGHT_OFFSET, NodeAnchor.TOP_LEFT);
        testForResize(HIGHLIGHT_OFFSET+1, WIDTH-HIGHLIGHT_OFFSET-1, -HIGHLIGHT_OFFSET, HIGHLIGHT_OFFSET, NodeAnchor.TOP);
        testForResize(WIDTH-HIGHLIGHT_OFFSET, WIDTH+HIGHLIGHT_OFFSET, -HIGHLIGHT_OFFSET, HIGHLIGHT_OFFSET, NodeAnchor.TOP_RIGHT);
        testForResize(-HIGHLIGHT_OFFSET, HIGHLIGHT_OFFSET, HIGHLIGHT_OFFSET+1, HEIGHT-HIGHLIGHT_OFFSET-1, NodeAnchor.LEFT);
        testForResize(HIGHLIGHT_OFFSET+1, WIDTH-HIGHLIGHT_OFFSET-1, HIGHLIGHT_OFFSET+1, HEIGHT-HIGHLIGHT_OFFSET-1, null);
        testForResize(WIDTH-HIGHLIGHT_OFFSET, WIDTH+HIGHLIGHT_OFFSET, HIGHLIGHT_OFFSET+1, HEIGHT-HIGHLIGHT_OFFSET-1, NodeAnchor.RIGHT);
        testForResize(-HIGHLIGHT_OFFSET, HIGHLIGHT_OFFSET, HEIGHT-HIGHLIGHT_OFFSET, HEIGHT+HIGHLIGHT_OFFSET, NodeAnchor.BOTTOM_LEFT);
        testForResize(HIGHLIGHT_OFFSET+1, WIDTH-HIGHLIGHT_OFFSET-1, HEIGHT-HIGHLIGHT_OFFSET, HEIGHT+HIGHLIGHT_OFFSET, NodeAnchor.BOTTOM);
        testForResize(WIDTH-HIGHLIGHT_OFFSET, WIDTH+HIGHLIGHT_OFFSET, HEIGHT-HIGHLIGHT_OFFSET, HEIGHT+HIGHLIGHT_OFFSET, NodeAnchor.BOTTOM_RIGHT);
    }

    private void testForResize(double left, double right, double top, double bottom, NodeAnchor anchor) {
        assertThat(NodeAnchor.forResize(new Point2D(left, top), 0, 0, WIDTH, HEIGHT)).isEqualTo(anchor);
        assertThat(NodeAnchor.forResize(new Point2D(right, top), 0, 0, WIDTH, HEIGHT)).isEqualTo(anchor);
        assertThat(NodeAnchor.forResize(new Point2D(right, bottom), 0, 0, WIDTH, HEIGHT)).isEqualTo(anchor);
        assertThat(NodeAnchor.forResize(new Point2D(left, bottom), 0, 0, WIDTH, HEIGHT)).isEqualTo(anchor);
    }
}