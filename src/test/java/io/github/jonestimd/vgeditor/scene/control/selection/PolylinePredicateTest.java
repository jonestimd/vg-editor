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
import javafx.geometry.Point2D;
import javafx.scene.shape.Polyline;
import org.junit.Test;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;
import static org.assertj.core.api.Java6Assertions.*;

public class PolylinePredicateTest extends SceneTest {
    private static final int X1 = 20;
    private static final int Y1 = 30;
    private static final int X2 = 40;
    private static final int Y2 = 50;
    private final Polyline polyline = new Polyline(X1, Y1, X2, Y1, X2, Y2);

    @Override
    public void setUpScene() throws Exception {
        super.setUpScene();
        polyline.setStrokeWidth(1);
        diagram.getChildren().add(polyline);
    }

    @Test
    public void testReturnsTrueForPolylineWithinHighlightRange() throws Exception {
        assertThat(new PolylinePredicate(X1, Y1).test(polyline)).isTrue();
        assertThat(new PolylinePredicate(X1, Y1-HIGHLIGHT_OFFSET).test(polyline)).isTrue();
        assertThat(new PolylinePredicate(X1, Y1+HIGHLIGHT_OFFSET).test(polyline)).isTrue();
        assertThat(new PolylinePredicate(X2, Y1-HIGHLIGHT_OFFSET).test(polyline)).isTrue();
        assertThat(new PolylinePredicate(X2-HIGHLIGHT_OFFSET, Y2).test(polyline)).isTrue();
        assertThat(new PolylinePredicate(X2+HIGHLIGHT_OFFSET, Y2).test(polyline)).isTrue();
    }

    @Test
    public void testReturnsFalseForPolylineOutsideHighlightRange() throws Exception {
        assertThat(new PolylinePredicate(X1-1, Y1).test(polyline)).isFalse();
        assertThat(new PolylinePredicate(X1, Y1-HIGHLIGHT_OFFSET-1).test(polyline)).isFalse();
        assertThat(new PolylinePredicate(X1, Y1+HIGHLIGHT_OFFSET+1).test(polyline)).isFalse();
        assertThat(new PolylinePredicate(X2+HIGHLIGHT_OFFSET+1, Y1).test(polyline)).isFalse();
        assertThat(new PolylinePredicate(X2, Y1-HIGHLIGHT_OFFSET-1).test(polyline)).isFalse();
        assertThat(new PolylinePredicate(X2-HIGHLIGHT_OFFSET-1, Y1+HIGHLIGHT_OFFSET+1).test(polyline)).isFalse();
        assertThat(new PolylinePredicate(X2-HIGHLIGHT_OFFSET-1, Y2).test(polyline)).isFalse();
        assertThat(new PolylinePredicate(X2+HIGHLIGHT_OFFSET+1, Y2).test(polyline)).isFalse();
        assertThat(new PolylinePredicate(X2, Y2+1).test(polyline)).isFalse();
    }

    @Test
    public void testReturnsFalseForSinglePoint() throws Exception {
        assertThat(new PolylinePredicate(X1, Y1).test(new Polyline(X1, Y1))).isFalse();
    }

    @Test
    public void getMarkerPositionReturnsEndpointWithinHighlightRange() throws Exception {
        assertThat(new PolylinePredicate(X1, Y1).getMarkerPosition(polyline)).isEqualTo(new Point2D(X1, Y1));
        assertThat(new PolylinePredicate(X1+HIGHLIGHT_OFFSET, Y1).getMarkerPosition(polyline)).isEqualTo(new Point2D(X1, Y1));
        assertThat(new PolylinePredicate(X2, Y1).getMarkerPosition(polyline)).isEqualTo(new Point2D(X2, Y1));
        assertThat(new PolylinePredicate(X2-HIGHLIGHT_OFFSET, Y1).getMarkerPosition(polyline)).isEqualTo(new Point2D(X2, Y1));
    }

    @Test
    public void getMarkerPositionReturnsMidpoint() throws Exception {
        Point2D midpoint = new Point2D((X1+X2)/2, Y1);

        assertThat(new PolylinePredicate(X1+HIGHLIGHT_OFFSET+1, Y1).getMarkerPosition(polyline)).isEqualTo(midpoint);
        assertThat(new PolylinePredicate(X2-HIGHLIGHT_OFFSET-1, Y1).getMarkerPosition(polyline)).isEqualTo(midpoint);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMarkerPositionThrowsExceptionWhenNotInRange() throws Exception {
        new PolylinePredicate(X1, Y1-HIGHLIGHT_OFFSET-1).getMarkerPosition(polyline);
    }
}