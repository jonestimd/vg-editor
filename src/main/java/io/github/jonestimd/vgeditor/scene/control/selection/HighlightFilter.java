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

import io.github.jonestimd.vgeditor.scene.model.NodeModel;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;

/**
 * Checks if a shape is within highlight range of a point on the screen.
 */
public class HighlightFilter implements Predicate<Node> {
    private final double screenX;
    private final double screenY;
    private final Bounds bounds;

    public HighlightFilter(double screenX, double screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.bounds = new BoundingBox(screenX-HIGHLIGHT_OFFSET, screenY-HIGHLIGHT_OFFSET, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE);
    }

    public boolean test(Node node) {
        if (node instanceof Polyline) return ((NodeModel) node.getUserData()).isInSelectionRange(screenX, screenY);
        if (node instanceof Path) return ((NodeModel) node.getUserData()).isInSelectionRange(screenX, screenY);
        if (node instanceof Rectangle) return ((NodeModel) node.getUserData()).isInSelectionRange(screenX, screenY);
        Bounds nodeBounds = node.getBoundsInLocal();
        if (node instanceof Parent || nodeBounds.getWidth() < HIGHLIGHT_SIZE || nodeBounds.getHeight() < HIGHLIGHT_SIZE) {
            return node.screenToLocal(bounds).intersects(nodeBounds);
        }
        return node.contains(node.screenToLocal(screenX, screenY));
    }
}
