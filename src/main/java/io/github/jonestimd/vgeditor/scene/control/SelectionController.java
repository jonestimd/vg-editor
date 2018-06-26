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
package io.github.jonestimd.vgeditor.scene.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

import io.github.jonestimd.vgeditor.collection.IterableUtils;
import io.github.jonestimd.vgeditor.collection.LruCache;
import io.github.jonestimd.vgeditor.scene.Nodes;
import io.github.jonestimd.vgeditor.scene.shape.PolylinePredicate;
import io.github.jonestimd.vgeditor.scene.shape.path.PathSegment;
import io.github.jonestimd.vgeditor.scene.shape.path.PathVisitor;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
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

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_MOVED) onMouseMoved(event.getScreenX(), event.getScreenY());
    }

    private void onMouseMoved(double screenX, double screenY) {
        List<Node> nodes = findNodes(diagram, new HighlightFilter(screenX, screenY));
        List<Node> matches = IterableUtils.minBy(nodes, Nodes::boundingArea); // TODO check path elements
        if (matches.isEmpty()) hideMarker();
        else {
            Node node = matches.get(0);
            if (node != highlighted) showMarker(node, screenX, screenY);
        }
    }

    private void showMarker(Node node, double screenX, double screenY) {
        hideMarker();
        highlighted = node;
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
        highlighted = null;
        marker.setVisible(false);
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

    private static double squareDist(double x1, double y1, double x2, double y2) {
        return square(x1-x2)+square(y1-y2);
    }

    private static double square(double v) {
        return v*v;
    }

    private class HighlightFilter implements Predicate<Node> {
        private final double screenX;
        private final double screenY;
        private final Bounds bounds;

        public HighlightFilter(double screenX, double screenY) {
            this.screenX = screenX;
            this.screenY = screenY;
            this.bounds = new BoundingBox(screenX-HIGHLIGHT_OFFSET, screenY-HIGHLIGHT_OFFSET, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE);
        }

        public boolean test(Node node) {
            if (node instanceof Polyline) return test((Polyline) node);
            if (node instanceof Path) return test((Path) node);
            if (node instanceof Rectangle) return test((Rectangle) node);
            Bounds nodeBounds = node.getBoundsInLocal();
            if (node instanceof Parent || nodeBounds.getWidth() < HIGHLIGHT_SIZE || nodeBounds.getHeight() < HIGHLIGHT_SIZE) {
                return node.screenToLocal(bounds).intersects(nodeBounds);
            }
            return node.contains(node.screenToLocal(screenX, screenY));
        }

        private boolean test(Rectangle rectangle) {
            Point2D localPoint = rectangle.screenToLocal(screenX, screenY);
            if (rectangle.getFill() == null) {
                return localPoint.getX() > rectangle.getX()-HIGHLIGHT_OFFSET && localPoint.getX() < rectangle.getX()+rectangle.getWidth()+HIGHLIGHT_OFFSET
                        && localPoint.getY() > rectangle.getY()-HIGHLIGHT_OFFSET && localPoint.getY() < rectangle.getY()+rectangle.getHeight()+HIGHLIGHT_OFFSET
                        && (localPoint.getX() < rectangle.getX()+HIGHLIGHT_OFFSET || localPoint.getX() > rectangle.getX()+rectangle.getWidth()-HIGHLIGHT_OFFSET
                        || localPoint.getY() < rectangle.getY()+HIGHLIGHT_OFFSET || localPoint.getY() > rectangle.getY()+rectangle.getHeight()-HIGHLIGHT_OFFSET);
            }
            return rectangle.contains(localPoint);
        }

        private boolean test(Polyline polyline) {
            if (polyline.intersects(polyline.screenToLocal(bounds))) {
                return new PolylinePredicate(polyline.screenToLocal(screenX, screenY)).test(polyline);
            }
            return false;
        }

        private boolean test(Path path) {
            if (path.intersects(path.screenToLocal(bounds))) {
                return pathVisitorCache.get(path, PathVisitor::new).some(new HighlightPathPredicate(path.screenToLocal(screenX, screenY)));
            }
            return false;
        }
    }

    private class HighlightPathPredicate implements Predicate<PathSegment<?>> {
        private final Point2D cursor;

        public HighlightPathPredicate(Point2D cursor) {
            this.cursor = cursor;
        }

        @Override
        public boolean test(PathSegment<?> pathSegment) {
            return pathSegment.getDistanceSquared(cursor) <= HIGHLIGHT_SIZE_SQUARED;
        }
    }
}
