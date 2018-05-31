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

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
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
    private static final double HIGHLIGHT_OFFSET = 5;
    private static final double HIGHLIGHT_SIZE = HIGHLIGHT_OFFSET*2;
    private static final double SQUARE_HIGHLIGHT_OFFSET = HIGHLIGHT_OFFSET*HIGHLIGHT_OFFSET;
    private final Pane diagram;

    private Node highlighted;
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
        // if (shape instanceof Polyline) {
        //     Polyline line = (Polyline) shape;
        //     Point2D point = line.screenToLocal(screenX, screenY);
        // }
        // else {
        Bounds bounds = node.getBoundsInParent();
        marker.setTranslateX((bounds.getMinX()+bounds.getMaxX())/2);
        marker.setTranslateY((bounds.getMinY()+bounds.getMaxY())/2);
        // }
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

        private boolean test(Path path) {
            if (path.intersects(path.screenToLocal(bounds))) {
                Point2D cursor = path.screenToLocal(x, y);
                return new PathVisitor(path).findSegment((p1, p2) -> test(cursor, p1.getX(), p1.getY(), p2.getX(), p2.getY()));
            }
            return false;
        }

        private boolean test(Polyline polyline) {
            ObservableList<Double> points = polyline.getPoints();
            if (points.size() > 3) {
                Point2D cursor = polyline.screenToLocal(x, y);
                double x1 = points.get(0);
                double y1 = points.get(1);
                for (int i = 2; i < points.size(); i += 2) {
                    double x2 = points.get(i);
                    double y2 = points.get(i+1);
                    if (test(cursor, x1, y1, x2, y2)) return true;
                }
            }
            return false;
        }

        private boolean test(Point2D cursor, double x1, double y1, double x2, double y2) {
            double squareLen = squareDist(x1, y1, x2, y2);
            if (squareLen == 0) return squareDist(cursor.getX(), cursor.getY(), x1, y1) < SQUARE_HIGHLIGHT_OFFSET;
            double projection = ((cursor.getX()-x1)*(x2-x1)+(cursor.getY()-y1)*(y2-y1))/squareLen;
            double squareDist = squareDist(cursor.getX(), cursor.getY(), x1+projection*(x2-x1), y1+projection*(y2-y1));
            return projection >= 0 && projection <= 1 && squareDist < SQUARE_HIGHLIGHT_OFFSET;
        }

        private double squareDist(double x1, double y1, double x2, double y2) {
            return square(x1-x2)+square(y1-y2);
        }

        private double square(double v) {
            return v*v;
        }
    }
}
