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

import javafx.geometry.Point2D;
import javafx.scene.shape.CubicCurveTo;
import org.assertj.core.data.Offset;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class CubicCurveToSegmentTest {
    private static final int START_X = 0;
    private static final int START_Y = 20;
    private static final int END_X = 30;
    private static final int END_Y = 0;
    private static final Point2D START = new Point2D(START_X, START_Y);

    @Test
    public void getMidPoint() throws Exception {
        CubicCurveTo curveTo = new CubicCurveTo(START_X+5, START_Y, END_X, END_Y+5, END_X, END_Y);
        CubicCurveToSegment segment = new CubicCurveToSegment(START, curveTo);

        Point2D midpoint = segment.getMidpoint();

        assertThat(midpoint.getX()).isEqualTo(16.875);
        assertThat(midpoint.getY()).isEqualTo(11.875);
    }

    @Test
    public void getMidPoint_Relative() throws Exception {
        CubicCurveTo curveTo = new CubicCurveTo(5, 0, END_X-START_X, END_Y-START_Y+5, END_X-START_X, END_Y-START_Y);
        curveTo.setAbsolute(false);
        CubicCurveToSegment segment = new CubicCurveToSegment(START, curveTo);

        Point2D midpoint = segment.getMidpoint();

        assertThat(midpoint.getX()).isEqualTo(16.875);
        assertThat(midpoint.getY()).isEqualTo(11.875);
    }

    @Test
    public void getDistanceSquared() throws Exception {
        CubicCurveTo curveTo = new CubicCurveTo(START_X+5, START_Y, END_X, END_Y+5, END_X, END_Y);
        CubicCurveToSegment segment = new CubicCurveToSegment(START, curveTo);

        assertThat(segment.getDistanceSquared(START)).isEqualTo(0);
        assertThat(segment.getDistanceSquared(new Point2D(END_X, END_Y))).isEqualTo(0);
        assertThat(segment.getDistanceSquared(new Point2D(START_X, START_Y+5))).isEqualTo(25, Offset.offset(BezierPathSegment.ERROR));
        assertThat(segment.getDistanceSquared(new Point2D(END_X+5, END_Y))).isEqualTo(25, Offset.offset(BezierPathSegment.ERROR));
    }
}