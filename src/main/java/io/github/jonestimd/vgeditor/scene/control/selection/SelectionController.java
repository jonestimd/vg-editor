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

import java.util.List;

import io.github.jonestimd.vgeditor.collection.IterableUtils;
import io.github.jonestimd.vgeditor.collection.LruCache;
import io.github.jonestimd.vgeditor.scene.Geometry;
import io.github.jonestimd.vgeditor.scene.Nodes;
import io.github.jonestimd.vgeditor.scene.model.NodeModel;
import io.github.jonestimd.vgeditor.scene.shape.path.PathSegment;
import io.github.jonestimd.vgeditor.scene.shape.path.PathVisitor;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import static io.github.jonestimd.vgeditor.scene.Nodes.*;

public class SelectionController implements EventHandler<MouseEvent> {
    public static final int HIGHLIGHT_OFFSET = 5;
    public static final int HIGHLIGHT_SIZE = HIGHLIGHT_OFFSET*2;
    public static final int HIGHLIGHT_OFFSET_SQUARED = HIGHLIGHT_OFFSET*HIGHLIGHT_OFFSET;
    public static final int PATH_CACHE_SIZE = 30;
    private final Group diagram;

    private final LruCache<Path, PathVisitor> pathVisitorCache = new LruCache<>(PATH_CACHE_SIZE);

    private Node highlighted;
    private final Property<Node> selected = new SimpleObjectProperty<>(this, "selected");
    private int polylineHighlight = -1;

    private final Effect highlightEffect = new ColorAdjust(-.25, 0.2, 0.5, 0);
    private final Shape marker;

    public SelectionController(Group diagram, Shape marker) {
        this.diagram = diagram;
        this.marker = marker;
    }

    public Node getHighlighted() {
        return highlighted;
    }

    public Property<Node> selectedProperty() {
        return selected;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getEventType() == MouseEvent.MOUSE_MOVED) onMouseMoved(event.getScreenX(), event.getScreenY());
        else if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.getButton() == MouseButton.PRIMARY) {
            selected.setValue(highlighted);
        }
    }

    private void onMouseMoved(double screenX, double screenY) {
        List<Node> nodes = findNodes(diagram, new HighlightFilter(screenX, screenY, path -> pathVisitorCache.get(path, PathVisitor::new)));
        List<Node> matches = IterableUtils.minBy(nodes, Nodes::boundingArea); // TODO check path elements
        if (matches.isEmpty()) hideMarker();
        else showMarker(matches.get(0), screenX, screenY);
    }

    private void showMarker(Node node, double screenX, double screenY) {
        if (highlighted != null && highlighted != node) highlighted.setEffect(null);
        highlighted = node;
        highlighted.setEffect(highlightEffect);
        if (node instanceof Path) {
            Path path = (Path) node;
            Point2D cursor = path.screenToLocal(screenX, screenY);
            PathSegment<?> segment = pathVisitorCache.get(path, PathVisitor::new)
                    .find(new HighlightPathPredicate(cursor))
                    .orElseThrow(IllegalStateException::new);
            Point2D midpoint = segment.getMidpoint();
            if (Geometry.distanceSquared(cursor, segment.getStart()) <= HIGHLIGHT_OFFSET_SQUARED) midpoint = segment.getStart();
            else if (Geometry.distanceSquared(cursor, segment.getEnd()) <= HIGHLIGHT_OFFSET_SQUARED) midpoint = segment.getEnd();
            setMarker(node, midpoint.getX(), midpoint.getY());
        }
        else if (node instanceof Polyline) {
            Point2D cursor = new PolylinePredicate(screenX, screenY).getMarkerPosition((Polyline) node);
            setMarker(node, cursor.getX(), cursor.getY());
        }
        else if (node instanceof Rectangle) {
            Point2D location = ((NodeModel) node.getUserData()).getMarkerLocation(screenX, screenY);
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

}
