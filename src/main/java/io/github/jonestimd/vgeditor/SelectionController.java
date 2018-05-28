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

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

public class SelectionController implements EventHandler<MouseEvent> {
    private final Pane diagram;

    private Node highlighted;
    private final Effect highlightEffect = new DropShadow(0, Color.GRAY);
    private final Shape marker = new Circle(5, new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
            new Stop(0.5, Color.valueOf("#00000000")), new Stop(0.75, Color.YELLOW), new Stop(1, Color.BLACK)));

    public SelectionController(Pane diagram) {
        this.diagram = diagram;
        marker.setEffect(new Blend(BlendMode.MULTIPLY));
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
