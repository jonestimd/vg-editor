package io.github.jonestimd.svgeditor;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

public class SelectionController implements EventHandler<MouseEvent> {
    private final Pane diagram;

    private Node highlighted;
    private final Effect highlightEffect = new DropShadow(0, Color.GRAY);
    private final Shape marker = new Circle(3, Paint.valueOf("#00000000"));

    public SelectionController(Pane diagram) {
        this.diagram = diagram;
        marker.setStroke(Paint.valueOf("yellow"));
        marker.setStrokeWidth(1.5);
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getTarget() instanceof Shape || event.getTarget() instanceof ImageView) {
            Node target = (Node) event.getTarget();
            if (event.getEventType() == MouseEvent.MOUSE_ENTERED_TARGET) onMouseEntered(target, event.getScreenX(), event.getScreenY());
            else if (event.getEventType() == MouseEvent.MOUSE_EXITED_TARGET) onMouseExited(target);
        }
    }

    private void onMouseEntered(Node node, double x, double y) {
        if (highlighted != null) highlighted.setEffect(null);
        highlighted = node;
        highlighted.setEffect(highlightEffect);
        setMarker(node, x, y);
    }

    private void onMouseExited(Node shape) {
        if (highlighted == shape) {
            highlighted.setEffect(null);
            highlighted = null;
            removeMarker();
        }
    }

    private void setMarker(Node shape, double screenX, double screenY) {
        removeMarker();
        if (shape instanceof SVGPath) {
            SVGPath path = (SVGPath) shape;
            Point2D point = path.screenToLocal(screenX, screenY);
        }
        else if (shape instanceof Polyline) {
            Polyline line = (Polyline) shape;
            Point2D point = line.screenToLocal(screenX, screenY);
        }
        else {
            Bounds bounds = shape.getBoundsInParent();
            marker.setTranslateX((bounds.getMinX() + bounds.getMaxX()) / 2);
            marker.setTranslateY((bounds.getMinY() + bounds.getMaxY()) / 2);
        }
        if (shape.getParent() instanceof Group) {
            ((Group) shape.getParent()).getChildren().add(marker);
        }
        else {
            diagram.getChildren().add(marker);
        }
    }

    private void removeMarker() {
        if (marker.getParent() instanceof Group) {
            ((Group) marker.getParent()).getChildren().remove(marker);
        }
        else if (marker.getParent() instanceof Pane) {
            ((Pane) marker.getParent()).getChildren().remove(marker);
        }
    }
}
