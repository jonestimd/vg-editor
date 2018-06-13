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
package io.github.jonestimd.vgeditor.scene.shape;

import javafx.geometry.Point2D;

import static io.github.jonestimd.vgeditor.SelectionController.*;

public abstract class LineSegmentPredicate {
    protected static final double SQUARE_HIGHLIGHT_OFFSET = HIGHLIGHT_OFFSET*HIGHLIGHT_OFFSET;
    protected final Point2D cursor;

    protected LineSegmentPredicate(Point2D cursor) {
        this.cursor = cursor;
    }

    /**
     * @return true if the line segment defined by the 2 points is within the highlight range of the {@code cursor}.
     */
    protected boolean isInHighlightRange(double x1, double y1, double x2, double y2) {
        double squareLen = squareDist(x1, y1, x2, y2);
        if (squareLen == 0) return squareDist(cursor.getX(), cursor.getY(), x1, y1) < SQUARE_HIGHLIGHT_OFFSET;
        double projection = ((cursor.getX()-x1)*(x2-x1)+(cursor.getY()-y1)*(y2-y1))/squareLen;
        double squareDist = squareDist(cursor.getX(), cursor.getY(), x1+projection*(x2-x1), y1+projection*(y2-y1));
        return projection >= 0 && projection <= 1 && squareDist < SQUARE_HIGHLIGHT_OFFSET;
    }

    private static double squareDist(double x1, double y1, double x2, double y2) {
        return square(x1-x2)+square(y1-y2);
    }

    private static double square(double v) {
        return v*v;
    }
}
