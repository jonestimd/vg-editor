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
package io.github.jonestimd.vgeditor.scene.model;

import io.github.jonestimd.vgeditor.scene.SceneTest;
import io.github.jonestimd.vgeditor.scene.control.selection.HighlightBounds;
import io.github.jonestimd.vgeditor.scene.control.selection.HighlightBounds.Corner;
import javafx.geometry.Point2D;
import org.junit.Test;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;
import static org.assertj.core.api.Java6Assertions.*;

public class PolylineModelTest extends SceneTest {
    private static final int X1 = 20;
    private static final int Y1 = 30;
    private static final int X2 = 40;
    private static final int Y2 = 50;
    private static final Corner[] CORNERS = {Corner.TopLeft, Corner.TopRight, Corner.BottomRight};
    private PolylineModel model;
    private final HighlightBounds bounds = new HighlightBounds(X1, Y1, X2-X1, Y2-Y1);

    @Override
    public void setUpScene() throws Exception {
        super.setUpScene();
        model = new PolylineModel(diagram, X1, Y1, X2, Y1, X2, Y2);
        model.setStrokeWidth(1);
    }

    @Test
    public void isInSelectionRange_ReturnsTrueForPolylineWithinHighlightRange() throws Exception {
        bounds.forEach(bound -> {
            assertThat(model.isInSelectionRange(bound.x, bound.y)).isTrue();
            if (bound.isTop()) {
                assertThat(model.isInSelectionRange(bound.x, bound.y-bound.getInnerY(0))).isTrue();
                assertThat(model.isInSelectionRange(bound.x, bound.y+bound.getInnerY(0))).isTrue();
            }
            if (!bound.isLeft()) {
                assertThat(model.isInSelectionRange(bound.x-bound.getInnerX(0), bound.y)).isTrue();
                assertThat(model.isInSelectionRange(bound.x+bound.getInnerX(0), bound.y)).isTrue();
            }
        }, CORNERS);
    }

    @Test
    public void isInSelectionRange_ReturnsFalseForPolylineOutsideHighlightRange() throws Exception {
        bounds.forEach(bound -> {
            assertThat(model.isInSelectionRange(bound.x-bound.getInnerX(1), bound.y)).isFalse();
            assertThat(model.isInSelectionRange(bound.x, bound.y-bound.getInnerY(1))).isFalse();
            assertThat(model.isInSelectionRange(bound.x+bound.getInnerX(1), bound.y+bound.getInnerY(1))).isFalse();
            if (bound.isLeft()) {
                assertThat(model.isInSelectionRange(bound.x, bound.y+bound.getInnerY(1))).isFalse();
            }
            else {
                assertThat(model.isInSelectionRange(bound.x-bound.getInnerX(1), bound.y)).isFalse();
            }
        }, CORNERS);
        assertThat(model.isInSelectionRange(X1-1, Y1)).isFalse();
        assertThat(model.isInSelectionRange(X2, Y2+1)).isFalse();
    }

    @Test
    public void isInSelectionRange_ReturnsFalseForSinglePoint() throws Exception {
        assertThat(new PolylineModel(diagram, X1, Y1).isInSelectionRange(X1, Y1)).isFalse();
    }

    @Test
    public void isInSelectionRange_HandlesZeroLengthLine() throws Exception {
        PolylineModel model = new PolylineModel(diagram, X1, Y1, X1, Y1);

        assertThat(model.isInSelectionRange(X1, Y1)).isTrue();
        assertThat(model.isInSelectionRange(X1+HIGHLIGHT_OFFSET+1, Y1)).isFalse();
        assertThat(model.isInSelectionRange(X1, Y1+HIGHLIGHT_OFFSET+1)).isFalse();
    }

    @Test
    public void getMarkerPosition_ReturnsEndpointWithinHighlightRange() throws Exception {
        assertThat(model.getMarkerLocation(X1, Y1)).isEqualTo(new Point2D(X1, Y1));
        assertThat(model.getMarkerLocation(X1+HIGHLIGHT_OFFSET, Y1)).isEqualTo(new Point2D(X1, Y1));
        assertThat(model.getMarkerLocation(X2, Y1)).isEqualTo(new Point2D(X2, Y1));
        assertThat(model.getMarkerLocation(X2-HIGHLIGHT_OFFSET, Y1)).isEqualTo(new Point2D(X2, Y1));
    }

    @Test
    public void getMarkerPosition_ReturnsMidpoint() throws Exception {
        Point2D midpoint = new Point2D((X1+X2)/2, Y1);

        assertThat(model.getMarkerLocation(X1+HIGHLIGHT_OFFSET+1, Y1)).isEqualTo(midpoint);
        assertThat(model.getMarkerLocation(X2-HIGHLIGHT_OFFSET-1, Y1)).isEqualTo(midpoint);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMarkerPosition_ThrowsExceptionWhenNotInRange() throws Exception {
        model.getMarkerLocation(X1, Y1-HIGHLIGHT_OFFSET-1);
    }
}