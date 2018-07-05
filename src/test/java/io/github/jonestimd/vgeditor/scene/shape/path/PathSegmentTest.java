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
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PathSegmentTest {
    private static final Point2D START = new Point2D(0, 0);

    @Test
    public void ofMoveTo() throws Exception {
        MoveTo moveTo = new MoveTo(20, 30);

        PathSegment<MoveTo> segment = PathSegment.of(START, moveTo, null);

        assertThat(segment.getElement()).isSameAs(moveTo);
        assertThat(segment.getStart()).isSameAs(START);
        assertThat(segment.getEnd()).isEqualTo(new Point2D(moveTo.getX(), moveTo.getY()));
    }

    @Test
    public void ofLineTo() throws Exception {
        LineTo lineTo = new LineTo(20, 30);

        PathSegment<LineTo> segment = PathSegment.of(START, lineTo, null);

        assertThat(segment.getElement()).isSameAs(lineTo);
        assertThat(segment.getStart()).isSameAs(START);
        assertThat(segment.getEnd()).isEqualTo(new Point2D(lineTo.getX(), lineTo.getY()));
    }

    @Test
    public void ofClosePath() throws Exception {
        ClosePath closePath = new ClosePath();
        Point2D end = new Point2D(20, 30);

        PathSegment<ClosePath> segment = PathSegment.of(START, closePath, end);

        assertThat(segment.getElement()).isSameAs(closePath);
        assertThat(segment.getStart()).isSameAs(START);
        assertThat(segment.getEnd()).isSameAs(end);
    }

    @Test
    public void ofCubicCurveTo() throws Exception {
        CubicCurveTo curveTo = new CubicCurveTo(20, 30, 21, 31, 22, 32);

        PathSegment<CubicCurveTo> segment = PathSegment.of(START, curveTo, null);

        assertThat(segment.getElement()).isSameAs(curveTo);
        assertThat(segment.getStart()).isSameAs(START);
        assertThat(segment.getEnd()).isEqualTo(new Point2D(curveTo.getX(), curveTo.getY()));
    }

    @Test
    public void ofQuadCurveTo() throws Exception {
        QuadCurveTo curveTo = new QuadCurveTo(20, 30, 21, 31);

        PathSegment<QuadCurveTo> segment = PathSegment.of(START, curveTo, null);

        assertThat(segment.getElement()).isSameAs(curveTo);
        assertThat(segment.getStart()).isSameAs(START);
        assertThat(segment.getEnd()).isEqualTo(new Point2D(curveTo.getX(), curveTo.getY()));
    }

    @Test
    public void ofArcTo() throws Exception {
        ArcTo arcTo = new ArcTo(20, 30, 0, 0, -30, false, false);

        PathSegment<ArcTo> segment = PathSegment.of(START, arcTo, null);

        assertThat(segment.getElement()).isSameAs(arcTo);
        assertThat(segment.getStart()).isSameAs(START);
        assertThat(segment.getEnd()).isEqualTo(new Point2D(arcTo.getX(), arcTo.getY()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ofUnknown() throws Exception {
        PathSegment.of(null, mock(PathElement.class), null);
    }
}