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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

import io.github.jonestimd.vgeditor.collection.IterableUtils;
import io.github.jonestimd.vgeditor.path.PathSegment;
import io.github.jonestimd.vgeditor.path.PathVisitor;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

public class SelectionController implements EventHandler<MouseEvent> {
    public static final double HIGHLIGHT_OFFSET = 5;
    public static final double HIGHLIGHT_SIZE = HIGHLIGHT_OFFSET*2;
    public static final double HIGHLIGHT_SIZE_SQUARED = HIGHLIGHT_SIZE*HIGHLIGHT_SIZE;
    private final Pane diagram;

    private Node highlighted;
    private PathSegment<?> pathHighlight;
    private int polylineHighlight = -1;

    private final Effect highlightEffect = new DropShadow(0, Color.GRAY);
    private final Shape marker = new Circle(5, new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
            new Stop(0.5, Color.valueOf("#00000000")), new Stop(0.75, Color.YELLOW), new Stop(1, Color.BLACK)));

    public SelectionController(Pane diagram) {
        this.diagram = diagram;
        marker.setEffect(new Blend(BlendMode.MULTIPLY));
    }

    public Node getHighlighted() {
        return highlighted;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_MOVED) onMouseMoved(event.getScreenX(), event.getScreenY());
    }

    private void onMouseMoved(double x, double y) {
        Point2D point = diagram.screenToLocal(x, y);
        List<Node> nodes = findNodes(diagram, new HighlightFilter(x, y));
        List<Node> matches = IterableUtils.minBy(nodes, Nodes::boundingArea); // TODO check path elements
        if (matches.isEmpty()) removeMarker();
        else {
            Node node = matches.get(0);
            if (node != highlighted) setMarker(node, x, y);
        }
    }

    private void setMarker(Node node, double screenX, double screenY) {
        removeMarker();
        highlighted = node;
        if (node instanceof Path) {
            Path path = (Path) node;
            PathSegment<?> segment = new PathVisitor(path).find(new HighlightPathPredicate(path.screenToLocal(screenX, screenY))).get();
            Point2D midpoint = segment.getMidpoint();
            marker.setTranslateX(midpoint.getX());
            marker.setTranslateY(midpoint.getY());
        }
        else if (node instanceof Polyline) {
            Polyline line = (Polyline) node;
            Point2D point = line.screenToLocal(screenX, screenY);
        }
        else {
            Bounds bounds = node.getBoundsInParent();
            marker.setTranslateX((bounds.getMinX()+bounds.getMaxX())/2);
            marker.setTranslateY((bounds.getMinY()+bounds.getMaxY())/2);
        }
        if (node.getParent() instanceof Group) {
            ((Group) node.getParent()).getChildren().add(marker);
        }
        else {
            diagram.getChildren().add(marker);
        }
    }

    private void removeMarker() {
        highlighted = null;
        if (marker.getParent() instanceof Group) {
            ((Group) marker.getParent()).getChildren().remove(marker);
        }
        else if (marker.getParent() instanceof Pane) {
            ((Pane) marker.getParent()).getChildren().remove(marker);
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

    private static double squareDist(double x1, double y1, double x2, double y2) {
        return square(x1-x2)+square(y1-y2);
    }

    private static double square(double v) {
        return v*v;
    }

    private class HighlightFilter implements Predicate<Node> {
        private final double x;
        private final double y;
        private final Bounds bounds;

        public HighlightFilter(double x, double y) {
            this.x = x;
            this.y = y;
            this.bounds = new BoundingBox(x-HIGHLIGHT_OFFSET, y-HIGHLIGHT_OFFSET, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE);
        }

        public boolean test(Node node) {
            if (node == marker) return false;
            if (node instanceof Polyline) return test((Polyline) node);
            if (node instanceof Path) return test((Path) node);
            Bounds nodeBounds = node.getBoundsInLocal();
            if (node instanceof Parent || nodeBounds.getWidth() < HIGHLIGHT_SIZE || nodeBounds.getHeight() < HIGHLIGHT_SIZE) {
                return node.screenToLocal(bounds).intersects(nodeBounds);
            }
            return node.contains(node.screenToLocal(x, y));
        }

        private boolean test(Polyline polyline) {
            if (polyline.intersects(polyline.screenToLocal(bounds))) {
                return new PolylinePredicate(polyline.screenToLocal(x, y)).test(polyline);
            }
            return false;
        }

        private boolean test(Path path) {
            if (path.intersects(path.screenToLocal(bounds))) {
                return new PathVisitor(path).some(new HighlightPathPredicate(path.screenToLocal(x, y)));
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
