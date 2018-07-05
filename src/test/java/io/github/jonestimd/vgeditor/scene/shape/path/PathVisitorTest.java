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
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class PathVisitorTest {
    @Test
    public void findReturnsEmptyForEmptyPath() throws Exception {
        PathVisitor visitor = new PathVisitor(new Path());

        assertThat(visitor.find(x -> true)).isEmpty();
    }

    @Test
    public void findReturnsEmptyForNoSegments() throws Exception {
        PathVisitor visitor = new PathVisitor(new Path(new MoveTo()));

        assertThat(visitor.find(x -> true)).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void findThrowsExceptionForNotStartingWithMoveTo() throws Exception {
        new PathVisitor(new Path(new LineTo())).find(x -> true);
    }

    @Test
    public void findReturnsFirstMatch() throws Exception {
        MoveTo moveTo = new MoveTo();
        LineTo lineTo = new LineTo(10, 20);
        PathVisitor visitor = new PathVisitor(new Path(moveTo, lineTo));

        PathSegment<?> actual = visitor.find(x -> true).get();

        assertThat(actual.getStart()).isEqualTo(new Point2D(moveTo.getX(), moveTo.getY()));
        assertThat(actual.getElement()).isEqualTo(lineTo);
    }

    @Test
    public void findUsesPreviousMoveToForStart() throws Exception {
        MoveTo moveTo = new MoveTo(5, 6);
        LineTo lineTo = new LineTo(10, 20);
        PathVisitor visitor = new PathVisitor(new Path(new MoveTo(), new ClosePath(), moveTo, lineTo));

        PathSegment<?> actual = visitor.find(x -> x.getElement() instanceof LineTo).get();

        assertThat(actual.getStart()).isEqualTo(new Point2D(moveTo.getX(), moveTo.getY()));
        assertThat(actual.getElement()).isEqualTo(lineTo);
    }

    @Test
    public void cachesSegments() throws Exception {
        MoveTo moveTo = new MoveTo();
        LineTo lineTo = new LineTo(10, 20);
        PathVisitor visitor = new PathVisitor(new Path(moveTo, lineTo));
        PathSegment<?> first = visitor.find(x -> true).get();

        assertThat(visitor.find(x -> true).get()).isSameAs(first);
    }

    @Test
    public void someReturnsTrueForMatch() throws Exception {
        MoveTo moveTo = new MoveTo(5, 6);
        LineTo lineTo = new LineTo(10, 20);
        PathVisitor visitor = new PathVisitor(new Path(new MoveTo(), new ClosePath(), moveTo, lineTo));

        assertThat(visitor.some(x -> x.getElement() instanceof LineTo)).isTrue();
    }

    @Test
    public void someReturnsFalseForNoMatch() throws Exception {
        MoveTo moveTo = new MoveTo(5, 6);
        LineTo lineTo = new LineTo(10, 20);
        PathVisitor visitor = new PathVisitor(new Path(new MoveTo(), new ClosePath(), moveTo, lineTo));

        assertThat(visitor.some(x -> x.getElement() instanceof QuadCurveTo)).isFalse();
    }
}