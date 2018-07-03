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

import io.github.jonestimd.vgeditor.scene.shape.path.PathSegment;
import javafx.geometry.Point2D;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.Mockito.*;

public class HighlightPathPredicateTest {
    private final PathSegment<?> pathSegment = mock(PathSegment.class);
    private final Point2D point = new Point2D(4, 5);
    private final HighlightPathPredicate predicate = new HighlightPathPredicate(point);

    @Test
    public void returnsTrueForDistanceEqualToHighlightOffset() throws Exception {
        when(pathSegment.getDistanceSquared(any(Point2D.class))).thenReturn(SelectionController.HIGHLIGHT_OFFSET_SQUARED*1d);

        assertThat(predicate.test(pathSegment)).isTrue();

        verify(pathSegment).getDistanceSquared(point);
    }

    @Test
    public void returnsFalseForDistanceGreaterThanHighlightOffset() throws Exception {
        when(pathSegment.getDistanceSquared(any(Point2D.class))).thenReturn(SelectionController.HIGHLIGHT_OFFSET_SQUARED+1d);

        assertThat(predicate.test(pathSegment)).isFalse();

        verify(pathSegment).getDistanceSquared(point);
    }
}