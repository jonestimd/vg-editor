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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

import io.github.jonestimd.vgeditor.collection.IterableUtils;
import io.github.jonestimd.vgeditor.collection.LruCache;
import io.github.jonestimd.vgeditor.scene.Nodes;
import io.github.jonestimd.vgeditor.scene.shape.path.PathSegment;
import io.github.jonestimd.vgeditor.scene.shape.path.PathVisitor;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class SelectionController implements EventHandler<MouseEvent> {
    public static final double HIGHLIGHT_OFFSET = 5;
    public static final double HIGHLIGHT_SIZE = HIGHLIGHT_OFFSET*2;
    public static final double HIGHLIGHT_SIZE_SQUARED = HIGHLIGHT_SIZE*HIGHLIGHT_SIZE;
    public static final int PATH_CACHE_SIZE = 30;
    private final Group diagram;

    private final LruCache<Path, PathVisitor> pathVisitorCache = new LruCache<>(PATH_CACHE_SIZE);

    private Node highlighted;
    private final Property<Node> selected = new SimpleObjectProperty<>(this, "selected");
    private PathSegment<?> pathHighlight;
    private int polylineHighlight = -1;

    private final Effect highlightEffect = new DropShadow(0, Color.GRAY);
    private final Shape marker;

    public SelectionController(Group diagram, Shape marker) {
        this.diagram = diagram;
        this.marker = marker;
    }

    public Node getHighlighted() {
        return highlighted;
    }

    public Node getSelected() {
        return selected.getValue();
    }

    public Property<Node> selectedProperty() {
        return selected;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_MOVED) onMouseMoved(event.getScreenX(), event.getScreenY());
        else if (event.getEventType() == MouseEvent.MOUSE_CLICKED && event.getButton() == MouseButton.PRIMARY) {
            selected.setValue(highlighted);
        }
    }

    private void onMouseMoved(double screenX, double screenY) {
        List<Node> nodes = findNodes(diagram, new HighlightFilter(screenX, screenY));
        List<Node> matches = IterableUtils.minBy(nodes, Nodes::boundingArea); // TODO check path elements
        if (matches.isEmpty()) hideMarker();
        else showMarker(matches.get(0), screenX, screenY);
    }

    private void showMarker(Node node, double screenX, double screenY) {
        highlighted = node;
        highlighted.setEffect(highlightEffect);
        if (node instanceof Path) {
            Path path = (Path) node;
            PathSegment<?> segment = pathVisitorCache.get(path, PathVisitor::new)
                    .find(new HighlightPathPredicate(path.screenToLocal(screenX, screenY)))
                    .orElseThrow(IllegalStateException::new);
            Point2D midpoint = segment.getMidpoint();
            setMarker(node, midpoint.getX(), midpoint.getY());
        }
        else if (node instanceof Polyline) {
            Polyline line = (Polyline) node;
            Point2D point = line.screenToLocal(screenX, screenY);
        }
        else if (node instanceof Rectangle) {
            Point2D location = RectanglePredicate.getMarkerLocation(screenX, screenY, (Rectangle) node);
            setMarker(node, location.getX(), location.getY());
        }
        else {
            Bounds bounds = node.getBoundsInLocal();
            setMarker(node, (bounds.getMinX()+bounds.getMaxX())/2, (bounds.getMinY()+bounds.getMaxY())/2);
        }
        marker.setVisible(true);
    }

    private void setMarker(Node node, double localX, double localY) {
        Point2D onDiagram = diagram.sceneToLocal(node.localToScene(localX, localY));
        marker.setLayoutX(onDiagram.getX());
        marker.setLayoutY(onDiagram.getY());
    }

    private void hideMarker() {
        if (highlighted != null) {
            highlighted.setEffect(null);
            highlighted = null;
            selected.setValue(null);
            marker.setVisible(false);
        }
    }

    private List<Node> findNodes(Parent root, Predicate<Node> filter) {
        List<Node> matches = new ArrayList<>();
        Stack<Parent> stack = new Stack<>();
        stack.add(root);
        while (!stack.isEmpty()) {
            Parent parent = stack.pop();
            for (Node node : parent.getChildrenUnmodifiable()) {
                if (node instanceof Parent) {
                    if (filter.test(node)) stack.push((Parent) node);
                }
                else if (filter.test(node)) matches.add(node);
            }
        }
        return matches;
    }

    private class HighlightFilter implements Predicate<Node> {
        private final double screenX;
        private final double screenY;
        private final Bounds bounds;
        private final RectanglePredicate rectanglePredicate;
        private final PolylinePredicate polylinePredicate;

        public HighlightFilter(double screenX, double screenY) {
            this.screenX = screenX;
            this.screenY = screenY;
            this.bounds = new BoundingBox(screenX-HIGHLIGHT_OFFSET, screenY-HIGHLIGHT_OFFSET, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE);
            this.rectanglePredicate = new RectanglePredicate(screenX, screenY);
            this.polylinePredicate = new PolylinePredicate(screenX, screenY);
        }

        public boolean test(Node node) {
            if (node instanceof Polyline) return test((Polyline) node);
            if (node instanceof Path) return test((Path) node);
            if (node instanceof Rectangle) return rectanglePredicate.test((Rectangle) node);
            Bounds nodeBounds = node.getBoundsInLocal();
            if (node instanceof Parent || nodeBounds.getWidth() < HIGHLIGHT_SIZE || nodeBounds.getHeight() < HIGHLIGHT_SIZE) {
                return node.screenToLocal(bounds).intersects(nodeBounds);
            }
            return node.contains(node.screenToLocal(screenX, screenY));
        }

        private boolean test(Polyline polyline) {
            return polyline.intersects(polyline.screenToLocal(bounds)) && polylinePredicate.test(polyline);
        }

        private boolean test(Path path) {
            if (path.intersects(path.screenToLocal(bounds))) {
                return pathVisitorCache.get(path, PathVisitor::new).some(new HighlightPathPredicate(path.screenToLocal(screenX, screenY)));
            }
            return false;
        }
    }
}
