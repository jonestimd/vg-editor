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
package io.github.jonestimd.vgeditor.scene.model.path;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.geometry.Point2D;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

public class PathVisitor {
    private final Path path;
    private final Map<PathElement, PathSegment<?>> segmentMap = new HashMap<>();

    public PathVisitor(Path path) {
        this.path = path;
    }

    /**
     * @return true if at least one element of the path matches the predicate.
     */
    public boolean some(Predicate<PathSegment<?>> predicate) {
        return find(predicate).isPresent();
    }

    public Optional<PathSegment<?>> find(Predicate<PathSegment<?>> predicate) {
        final Iterator<PathElement> iterator = path.getElements().iterator();
        if (iterator.hasNext()) {
            PathElement first = iterator.next();
            if (first instanceof MoveTo) {
                Point2D start = getPoint((MoveTo) first), previous = start;
                while (iterator.hasNext()) {
                    PathElement element = iterator.next();
                    PathSegment<?> segment = segmentMap.get(element);
                    if (segment == null) {
                        segment = PathSegment.of(previous, element, start);
                        segmentMap.put(element, segment);
                    }
                    if (predicate.test(segment)) return Optional.of(segment);
                    previous = segment.getEnd();
                    if (segment instanceof MoveToSegment) start = previous;
                }
            }
            else throw new IllegalArgumentException("Path does not start with MoveTo");
        }
        return Optional.empty();
    }

    private static Point2D getPoint(MoveTo moveTo) {
        return new Point2D(moveTo.getX(), moveTo.getY());
    }
}
