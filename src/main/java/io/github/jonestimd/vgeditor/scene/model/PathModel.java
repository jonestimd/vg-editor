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

import java.util.function.Predicate;

import io.github.jonestimd.vgeditor.scene.Geometry;
import io.github.jonestimd.vgeditor.scene.model.path.PathSegment;
import io.github.jonestimd.vgeditor.scene.model.path.PathVisitor;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;

public class PathModel extends ShapeModel<Path> {
    private final PathVisitor pathVisitor;

    public PathModel(Group group, PathElement... elements) {
        this(group, new Path(elements));
    }

    public PathModel(Group group, Path shape) {
        super(group, shape);
        pathVisitor = new PathVisitor(shape);
    }

    @Override
    public boolean isInSelectionRange(double screenX, double screenY) {
        Point2D cursor = shape.screenToLocal(screenX, screenY);
        Bounds bounds = shape.getBoundsInLocal();
        return isInBounds(bounds.getMinX(), bounds.getWidth(), cursor.getX()) &&
                isInBounds(bounds.getMinY(), bounds.getHeight(), cursor.getY()) &&
                pathVisitor.some(cursorPredicate(cursor));
    }

    @Override
    public Point2D getMarkerLocation(double screenX, double screenY) {
        Point2D cursor = shape.screenToLocal(screenX, screenY);
        PathSegment<?> segment = pathVisitor.find(cursorPredicate(cursor)).orElseThrow(IllegalStateException::new);
        if (Geometry.distanceSquared(cursor, segment.getStart()) <= HIGHLIGHT_OFFSET_SQUARED) return segment.getStart();
        if (Geometry.distanceSquared(cursor, segment.getEnd()) <= HIGHLIGHT_OFFSET_SQUARED) return segment.getEnd();
        return segment.getMidpoint();
    }

    private Predicate<PathSegment<?>> cursorPredicate(Point2D cursor) {
        return segment -> segment.isInSelectionRange(cursor);
    }
}
