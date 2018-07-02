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

import java.util.function.Function;
import java.util.function.Predicate;

import io.github.jonestimd.vgeditor.scene.shape.path.PathVisitor;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;

public class HighlightFilter implements Predicate<Node> {
    private final Function<Path, PathVisitor> pathVisitorFactory;
    private final double screenX;
    private final double screenY;
    private final Bounds bounds;
    private final RectanglePredicate rectanglePredicate;
    private final PolylinePredicate polylinePredicate;

    public HighlightFilter(double screenX, double screenY, Function<Path, PathVisitor> pathVisitorFactory) {
        this.pathVisitorFactory = pathVisitorFactory;
        this.screenX = screenX;
        this.screenY = screenY;
        this.bounds = new BoundingBox(screenX-SelectionController.HIGHLIGHT_OFFSET, screenY-SelectionController.HIGHLIGHT_OFFSET, SelectionController.HIGHLIGHT_SIZE, SelectionController.HIGHLIGHT_SIZE);
        this.rectanglePredicate = new RectanglePredicate(screenX, screenY);
        this.polylinePredicate = new PolylinePredicate(screenX, screenY);
    }

    public boolean test(Node node) {
        if (node instanceof Polyline) return test((Polyline) node);
        if (node instanceof Path) return test((Path) node);
        if (node instanceof Rectangle) return rectanglePredicate.test((Rectangle) node);
        Bounds nodeBounds = node.getBoundsInLocal();
        if (node instanceof Parent || nodeBounds.getWidth() < SelectionController.HIGHLIGHT_SIZE || nodeBounds.getHeight() < SelectionController.HIGHLIGHT_SIZE) {
            return node.screenToLocal(bounds).intersects(nodeBounds);
        }
        return node.contains(node.screenToLocal(screenX, screenY));
    }

    private boolean test(Polyline polyline) {
        return polyline.intersects(polyline.screenToLocal(bounds)) && polylinePredicate.test(polyline);
    }

    private boolean test(Path path) {
        if (path.intersects(path.screenToLocal(bounds))) {
            return pathVisitorFactory.apply(path).some(new HighlightPathPredicate(path.screenToLocal(screenX, screenY)));
        }
        return false;
    }
}
