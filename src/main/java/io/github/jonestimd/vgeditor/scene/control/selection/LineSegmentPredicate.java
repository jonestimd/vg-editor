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

import javafx.geometry.Point2D;

import static io.github.jonestimd.vgeditor.scene.Geometry.*;
import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;

/**
 * Base class for predicates that check if a point is within the highlight range of an line segment.
 */
public abstract class LineSegmentPredicate {
    protected final double screenX, screenY;

    protected LineSegmentPredicate(double screenX, double screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
    }

    /**
     * @param cursor the point to check (in the shape's local coordinate space)
     * @param x1 the x coordinate of the starting point of the line segment
     * @param y1 the y coordinate of the starting point of the line segment
     * @param x2 the x coordinate of the ending point of the line segment
     * @param y2 the y coordinate of the ending point of the line segment
     * @return true if the line segment defined by the 2 points is within the highlight range of the {@code cursor}.
     */
    protected boolean isInHighlightRange(Point2D cursor, double x1, double y1, double x2, double y2) {
        double squareLen = distanceSquared(x1, y1, x2, y2);
        if (squareLen == 0) return distanceSquared(cursor.getX(), cursor.getY(), x1, y1) <= HIGHLIGHT_OFFSET_SQUARED;
        double projection = ((cursor.getX()-x1)*(x2-x1)+(cursor.getY()-y1)*(y2-y1))/squareLen;
        double squareDist = distanceSquared(cursor.getX(), cursor.getY(), x1+projection*(x2-x1), y1+projection*(y2-y1));
        return projection >= 0 && projection <= 1 && squareDist <= HIGHLIGHT_OFFSET_SQUARED;
    }
}
