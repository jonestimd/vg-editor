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

import io.github.jonestimd.vgeditor.scene.NodeAnchor;
import io.github.jonestimd.vgeditor.scene.control.ResizeDragCalculator.Offset2D;
import javafx.geometry.Point2D;
import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class ResizeDragCalculatorTest {
    private static final Offset<Double> DOUBLE_OFFSET = Offset.offset(0.0000001);
    private static final int WIDTH = 10;
    private static final int HEIGHT = 20;
    private static final Point2D POINT1 = new Point2D(0, 0);
    private static final Point2D POINT2 = new Point2D(WIDTH, HEIGHT);

    @Test
    public void topLeft_0() throws Exception {
        final double angle = 0;
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP_LEFT, angle, WIDTH, HEIGHT, -WIDTH, -HEIGHT);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP, angle, 0, HEIGHT, 0, -HEIGHT);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP_RIGHT, angle, 0, HEIGHT, WIDTH, -HEIGHT);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.LEFT, angle, WIDTH, 0, -WIDTH, 0);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.RIGHT, angle, 0, 0, WIDTH, 0);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM_LEFT, angle, WIDTH, 0, -WIDTH, HEIGHT);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM, angle, 0, 0, 0, HEIGHT);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM_RIGHT, angle, 0, 0, WIDTH, HEIGHT);
    }

    @Test
    public void topLeft_30() throws Exception {
        final double angle = 30;
        final double cos = Math.cos(Math.toRadians(angle));
        final double sin = Math.sin(Math.toRadians(angle));
        double dWidth = cos*WIDTH+sin*HEIGHT, dHeight = -sin*WIDTH+cos*HEIGHT;
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP_LEFT, angle, WIDTH, HEIGHT, -dWidth, -dHeight);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP, angle, -sin*dHeight, cos*dHeight, 0, -dHeight);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP_RIGHT, angle, -sin*dHeight, cos*dHeight, dWidth, -dHeight);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.LEFT, angle, cos*dWidth, sin*dWidth, -dWidth, 0);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.RIGHT, angle, 0, 0, dWidth, 0);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM_LEFT, angle, cos*dWidth, sin*dWidth, -dWidth, dHeight);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM, angle, 0, 0, 0, dHeight);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM_RIGHT, angle, 0, 0, dWidth, dHeight);
    }

    @Test
    public void topRight_30() throws Exception {
        final double angle = 30;
        final double cos = Math.cos(Math.toRadians(angle));
        final double sin = Math.sin(Math.toRadians(angle));
        double dWidth = cos*WIDTH+sin*HEIGHT, dHeight = -sin*WIDTH+cos*HEIGHT;
        testResize(NodeAnchor.TOP_RIGHT, NodeAnchor.TOP_LEFT, angle, -sin*dHeight, cos*dHeight, -dWidth, -dHeight);
        testResize(NodeAnchor.TOP_RIGHT, NodeAnchor.TOP, angle, -sin*dHeight, cos*dHeight, 0, -dHeight);
        testResize(NodeAnchor.TOP_RIGHT, NodeAnchor.TOP_RIGHT, angle, WIDTH, HEIGHT, dWidth, -dHeight);
        testResize(NodeAnchor.TOP_RIGHT, NodeAnchor.LEFT, angle, 0, 0, -dWidth, 0);
        testResize(NodeAnchor.TOP_RIGHT, NodeAnchor.RIGHT, angle, cos*dWidth, sin*dWidth, dWidth, 0);
        testResize(NodeAnchor.TOP_RIGHT, NodeAnchor.BOTTOM_LEFT, angle, 0, 0, -dWidth, dHeight);
        testResize(NodeAnchor.TOP_RIGHT, NodeAnchor.BOTTOM, angle, 0, 0, 0, dHeight);
        testResize(NodeAnchor.TOP_RIGHT, NodeAnchor.BOTTOM_RIGHT, angle, cos*dWidth, sin*dWidth, dWidth, dHeight);
    }

    @Test
    public void bottomRight_30() throws Exception {
        final double angle = 30;
        final double cos = Math.cos(Math.toRadians(angle));
        final double sin = Math.sin(Math.toRadians(angle));
        double dWidth = cos*WIDTH+sin*HEIGHT, dHeight = -sin*WIDTH+cos*HEIGHT;
        testResize(NodeAnchor.BOTTOM_RIGHT, NodeAnchor.TOP_LEFT, angle, 0, 0, -dWidth, -dHeight);
        testResize(NodeAnchor.BOTTOM_RIGHT, NodeAnchor.TOP, angle, 0, 0, 0, -dHeight);
        testResize(NodeAnchor.BOTTOM_RIGHT, NodeAnchor.TOP_RIGHT, angle, cos*dWidth, sin*dWidth, dWidth, -dHeight);
        testResize(NodeAnchor.BOTTOM_RIGHT, NodeAnchor.LEFT, angle, 0, 0, -dWidth, 0);
        testResize(NodeAnchor.BOTTOM_RIGHT, NodeAnchor.RIGHT, angle, cos*dWidth, sin*dWidth, dWidth, 0);
        testResize(NodeAnchor.BOTTOM_RIGHT, NodeAnchor.BOTTOM_LEFT, angle, -sin*dHeight, cos*dHeight, -dWidth, dHeight);
        // testResize(NodeAnchor.BOTTOM_RIGHT, NodeAnchor.BOTTOM, angle, 0, 0, 0, dHeight);
        testResize(NodeAnchor.BOTTOM_RIGHT, NodeAnchor.BOTTOM_RIGHT, angle, WIDTH, HEIGHT, dWidth, dHeight);
    }

    @Test
    public void topLeft_180() throws Exception {
        final double angle = 180;
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP_LEFT, angle, WIDTH, HEIGHT, WIDTH, HEIGHT);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP, angle, 0, HEIGHT, 0, HEIGHT);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP_RIGHT, angle, 0, HEIGHT, -WIDTH, HEIGHT);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.LEFT, angle, WIDTH, 0, WIDTH, 0);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.RIGHT, angle, 0, 0, -WIDTH, 0);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM_LEFT, angle, WIDTH, 0, WIDTH, -HEIGHT);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM, angle, 0, 0, 0, -HEIGHT);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM_RIGHT, angle, 0, 0, -WIDTH, -HEIGHT);
    }

    @Test
    @SuppressWarnings("SuspiciousNameCombination")
    public void topLeft_90() throws Exception {
        final double angle = 90;
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP_LEFT, angle, WIDTH, HEIGHT, -HEIGHT, WIDTH);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP, angle, WIDTH, 0, 0, WIDTH);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP_RIGHT, angle, WIDTH, 0, HEIGHT, WIDTH);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.LEFT, angle, 0, HEIGHT, -HEIGHT, 0);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.RIGHT, angle, 0, 0, HEIGHT, 0);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM_LEFT, angle, 0, HEIGHT, -HEIGHT, -WIDTH);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM, angle, 0, 0, 0, -WIDTH);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM_RIGHT, angle, 0, 0, HEIGHT, -WIDTH);
    }

    @Test
    @SuppressWarnings("SuspiciousNameCombination")
    public void topLeft_minus90() throws Exception {
        final double angle = -90;
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP_LEFT, angle, WIDTH, HEIGHT, HEIGHT, -WIDTH);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP, angle, WIDTH, 0, 0, -WIDTH);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.TOP_RIGHT, angle, WIDTH, 0, -HEIGHT, -WIDTH);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.LEFT, angle, 0, HEIGHT, HEIGHT, 0);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.RIGHT, angle, 0, 0, -HEIGHT, 0);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM_LEFT, angle, 0, HEIGHT, HEIGHT, WIDTH);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM, angle, 0, 0, 0, WIDTH);
        testResize(NodeAnchor.TOP_LEFT, NodeAnchor.BOTTOM_RIGHT, angle, 0, 0, -HEIGHT, WIDTH);
    }

    @Test
    public void center_0() throws Exception {
        final double angle = 0;
        testResize(NodeAnchor.CENTER, NodeAnchor.TOP_LEFT, angle, 0, 0, -WIDTH*2, -HEIGHT*2);
        testResize(NodeAnchor.CENTER, NodeAnchor.TOP, angle, 0, 0, 0, -HEIGHT*2);
        testResize(NodeAnchor.CENTER, NodeAnchor.TOP_RIGHT, angle, 0, 0, WIDTH*2, -HEIGHT*2);
        testResize(NodeAnchor.CENTER, NodeAnchor.LEFT, angle, 0, 0, -WIDTH*2, 0);
        testResize(NodeAnchor.CENTER, NodeAnchor.RIGHT, angle, 0, 0, WIDTH*2, 0);
        testResize(NodeAnchor.CENTER, NodeAnchor.BOTTOM_LEFT, angle, 0, 0, -WIDTH*2, HEIGHT*2);
        testResize(NodeAnchor.CENTER, NodeAnchor.BOTTOM, angle, 0, 0, 0, HEIGHT*2);
        testResize(NodeAnchor.CENTER, NodeAnchor.BOTTOM_RIGHT, angle, 0, 0, WIDTH*2, HEIGHT*2);
    }

    @Test
    @SuppressWarnings("SuspiciousNameCombination")
    public void center_90() throws Exception {
        final double angle = 90;
        testResize(NodeAnchor.CENTER, NodeAnchor.TOP_LEFT, angle, 0, 0, -HEIGHT*2, WIDTH*2);
        testResize(NodeAnchor.CENTER, NodeAnchor.TOP, angle, 0, 0, 0, WIDTH*2);
        testResize(NodeAnchor.CENTER, NodeAnchor.TOP_RIGHT, angle, 0, 0, HEIGHT*2, WIDTH*2);
        testResize(NodeAnchor.CENTER, NodeAnchor.LEFT, angle, 0, 0, -HEIGHT*2, 0);
        testResize(NodeAnchor.CENTER, NodeAnchor.RIGHT, angle, 0, 0, HEIGHT*2, 0);
        testResize(NodeAnchor.CENTER, NodeAnchor.BOTTOM_LEFT, angle, 0, 0, -HEIGHT*2, -WIDTH*2);
        testResize(NodeAnchor.CENTER, NodeAnchor.BOTTOM, angle, 0, 0, 0, -WIDTH*2);
        testResize(NodeAnchor.CENTER, NodeAnchor.BOTTOM_RIGHT, angle, 0, 0, HEIGHT*2, -WIDTH*2);
    }

    public void testResize(NodeAnchor nodeAnchor, NodeAnchor resizeAnchor, double rotation, double dx, double dy, double dWidth, double dHeight) {
        ResizeDragCalculator resizeDragCalculator = new ResizeDragCalculator(resizeAnchor, nodeAnchor, rotation, 2);

        Offset2D adjustment = resizeDragCalculator.apply(POINT1, POINT2);

        assertThat(adjustment.dx).as("delta x").isEqualTo(dx, DOUBLE_OFFSET);
        assertThat(adjustment.dy).as("delta y").isEqualTo(dy, DOUBLE_OFFSET);
        assertThat(adjustment.dWidth).as("delta width").isEqualTo(dWidth, DOUBLE_OFFSET);
        assertThat(adjustment.dHeight).as("delta height").isEqualTo(dHeight, DOUBLE_OFFSET);
    }
}