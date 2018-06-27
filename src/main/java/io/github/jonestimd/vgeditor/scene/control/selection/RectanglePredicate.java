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

import java.util.function.Predicate;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;

public class RectanglePredicate implements Predicate<Rectangle> {
    private final double screenX, screenY;

    public RectanglePredicate(double screenX, double screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
    }

    @Override
    public boolean test(Rectangle rectangle) {
        Point2D localPoint = rectangle.screenToLocal(screenX, screenY);
        if (rectangle.getFill() == null) {
            return isInBounds(rectangle.getX(), rectangle.getWidth(), localPoint.getX()) && isInBounds(rectangle.getY(), rectangle.getHeight(), localPoint.getY()) &&
                    (isNotInside(rectangle.getX(), rectangle.getWidth(), localPoint.getX()) || isNotInside(rectangle.getY(), rectangle.getHeight(), localPoint.getY()));
        }
        return rectangle.contains(localPoint);
    }

    private static boolean isInBounds(double min, double size, double value) {
        return value > min-HIGHLIGHT_OFFSET && value < min+size+HIGHLIGHT_OFFSET;
    }

    private static boolean isNotInside(double min, double size, double value) {
        return value < min+HIGHLIGHT_OFFSET || value > min+size-HIGHLIGHT_OFFSET;
    }

    public static Point2D getMarkerLocation(double screenX, double screenY, Rectangle rectangle) {
        double x = rectangle.getX(), y = rectangle.getY();
        Point2D cursor = rectangle.screenToLocal(screenX, screenY).subtract(x, y);
        return new Point2D(x+selectEdge(cursor.getX(), rectangle.getWidth()), y+selectEdge(cursor.getY(), rectangle.getHeight()));
    }

    private static double selectEdge(double value, double size) {
        if (value > size*0.75) return size;
        if (value > size*0.25) return size/2;
        return 0;
    }
}