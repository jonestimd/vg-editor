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
package io.github.jonestimd.vgeditor;

import io.github.jonestimd.vgeditor.PathVisitor.SegmentPredicate;
import javafx.geometry.Point2D;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.QuadCurveTo;

class PathElementPredicate extends LineSegmentPredicate implements SegmentPredicate {
    public PathElementPredicate(Point2D cursor) {
        super(cursor);
    }

    @Override
    public boolean test(Point2D p1, MoveTo moveTo) {
        return false;
    }

    @Override
    public boolean test(Point2D p1, LineTo lineTo) {
        return isInHighlightRange(p1.getX(), p1.getY(), lineTo.getX(), lineTo.getY());
    }

    @Override
    public boolean test(Point2D p1, CubicCurveTo cubicCurveTo) {
        return false;
    }

    @Override
    public boolean test(Point2D p1, QuadCurveTo quadCurveTo) {
        return false;
    }

    @Override
    public boolean test(Point2D p1, ArcTo arcTo) {
        return false;
    }

    @Override
    public boolean test(Point2D p1, ClosePath closePath, Point2D p2) {
        return isInHighlightRange(p1.getX(), p1.getY(), p1.getX(), p2.getY());
    }
}
