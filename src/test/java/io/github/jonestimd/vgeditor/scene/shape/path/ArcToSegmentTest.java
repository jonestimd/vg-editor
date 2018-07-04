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
package io.github.jonestimd.vgeditor.scene.shape.path;

import io.github.jonestimd.vgeditor.scene.SceneTest;
import javafx.geometry.Point2D;
import javafx.scene.shape.ArcTo;
import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class ArcToSegmentTest extends SceneTest {
    private static final Offset<Double> DOUBLE_OFFSET = Offset.offset(1e-8);
    private static final int RADIUS_X = 30;
    private static final int RADIUS_Y = 20;

    private double cos(int angle) {
        return Math.cos(Math.toRadians(angle));
    }

    private double sin(int angle) {
        return Math.sin(Math.toRadians(angle));
    }

    @Test
    public void getMidPoint() throws Exception {
        ArcTo arcTo = new ArcTo(RADIUS_X, RADIUS_Y, 0, 0, -RADIUS_Y, false, false);
        ArcToSegment segment = new ArcToSegment(new Point2D(RADIUS_X, 0), arcTo);

        Point2D midpoint = segment.getMidpoint();

        assertThat(midpoint.getX()).isEqualTo(RADIUS_X*cos(-45));
        assertThat(midpoint.getY()).isEqualTo(RADIUS_Y*sin(-45));
    }

    @Test
    public void getMidPoint_Rotate90() throws Exception {
        ArcTo arcTo = new ArcTo(RADIUS_X, RADIUS_Y, 90, 0, -RADIUS_Y, false, false);
        ArcToSegment segment = new ArcToSegment(new Point2D(RADIUS_X, 0), arcTo);

        Point2D midpoint = segment.getMidpoint();

        assertThat(midpoint.getX()).isEqualTo(18.482194249070385);
        assertThat(midpoint.getY()).isEqualTo(-21.75240559061254);
    }

    @Test
    public void getMidPoint_Sweep() throws Exception {
        ArcTo arcTo = new ArcTo(RADIUS_X, RADIUS_Y, 0, 0, -RADIUS_Y, false, true);
        ArcToSegment segment = new ArcToSegment(new Point2D(RADIUS_X, 0), arcTo);

        Point2D midpoint = segment.getMidpoint();

        assertThat(midpoint.getX()).isEqualTo(RADIUS_X*(1-Math.cos(-Math.PI/4)), DOUBLE_OFFSET);
        assertThat(midpoint.getY()).isEqualTo(RADIUS_Y*(-1-Math.sin(-Math.PI/4)), DOUBLE_OFFSET);
    }

    @Test
    public void getMidPoint_LargeArc() throws Exception {
        ArcTo arcTo = new ArcTo(RADIUS_X, RADIUS_Y, 0, 0, -RADIUS_Y, true, false);
        ArcToSegment segment = new ArcToSegment(new Point2D(RADIUS_X, 0), arcTo);

        Point2D midpoint = segment.getMidpoint();

        assertThat(midpoint.getX()).isEqualTo(RADIUS_X*(1+Math.cos(-Math.PI/4)), DOUBLE_OFFSET);
        assertThat(midpoint.getY()).isEqualTo(RADIUS_Y*(-1+Math.sin(-Math.PI/4)), DOUBLE_OFFSET);
    }

    @Test
    public void getMidPoint_SweepLargeArc() throws Exception {
        ArcTo arcTo = new ArcTo(RADIUS_X, RADIUS_Y, 0, 0, -RADIUS_Y, true, true);
        ArcToSegment segment = new ArcToSegment(new Point2D(RADIUS_X, 0), arcTo);

        Point2D midpoint = segment.getMidpoint();

        assertThat(midpoint.getX()).isEqualTo(RADIUS_X*(-Math.cos(-Math.PI/4)), DOUBLE_OFFSET);
        assertThat(midpoint.getY()).isEqualTo(RADIUS_Y*(-Math.sin(-Math.PI/4)), DOUBLE_OFFSET);
    }

    @Test
    public void getDistanceSquared() throws Exception {
        ArcTo arcTo = new ArcTo(RADIUS_X, RADIUS_Y, 0, 0, -RADIUS_Y, false, false);
        ArcToSegment segment = new ArcToSegment(new Point2D(RADIUS_X, 0), arcTo);

        assertThat(segment.getDistanceSquared(new Point2D(0, 0))).isEqualTo(RADIUS_Y*RADIUS_Y);
        assertThat(segment.getDistanceSquared(new Point2D(-1, -1))).isEqualTo(Double.MAX_VALUE);
        assertThat(segment.getDistanceSquared(new Point2D(RADIUS_X+1, 0))).isEqualTo(1);
        assertThat(segment.getDistanceSquared(new Point2D(0, -RADIUS_Y-1))).isEqualTo(1);
        assertThat(segment.getDistanceSquared(new Point2D(RADIUS_X/2, -RADIUS_Y/2))).isEqualTo(100.4285027596952);
    }
}