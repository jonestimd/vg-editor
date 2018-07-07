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

import io.github.jonestimd.vgeditor.scene.SceneTest;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.junit.Test;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;
import static org.assertj.core.api.Assertions.*;

public class SelectionControllerTest extends SceneTest {
    private Circle marker = new Circle();
    private Rectangle shape = new Rectangle(10, 10);
    private SelectionController controller;

    @Override
    public void setUpScene() throws Exception {
        super.setUpScene();
        marker.setVisible(false);
        controller = new SelectionController(diagram, marker);
        diagram.getChildren().add(shape);
    }

    @Test
    public void highlightsPathLineSegments() throws Exception {
        final int x1 = 30, y1 = 20, x2 = 50, y2 = 40;
        final int mx = (x1+x2)/2, my = (y1+y2)/2;
        final Point2D[] points = {new Point2D(x1, y1), new Point2D(x2, y1), new Point2D(x2, y2)};
        Path path = new Path(new MoveTo(x1, y1), new LineTo(x2, y1), new LineTo(x2, y2));
        path.setStrokeWidth(1);
        diagram.getChildren().add(path);

        checkHighlight(x1-HIGHLIGHT_OFFSET-2, y1-HIGHLIGHT_OFFSET-2, null, 0, 0);

        for (int i = 0; i < points.length; i++) {
            Point2D point = points[i];
            checkHighlight(point.getX()-HIGHLIGHT_OFFSET, point.getY(), path, point.getX(), point.getY());
            checkHighlight(point.getX()+HIGHLIGHT_OFFSET, point.getY(), path, point.getX(), point.getY());
            checkHighlight(point.getX(), point.getY()-HIGHLIGHT_OFFSET, path, point.getX(), point.getY());
            checkHighlight(point.getX(), point.getY()+HIGHLIGHT_OFFSET, path, point.getX(), point.getY());

            if (i != 1) {
                checkHighlight(point.getX()-HIGHLIGHT_OFFSET-1, point.getY(), null, 0, 0);
                checkHighlight(point.getX(), point.getY()+HIGHLIGHT_OFFSET+1, null, 0, 0);
            }
            if (i != 2) checkHighlight(point.getX(), point.getY()-HIGHLIGHT_OFFSET-1, null, 0, 0);
            if (i != 0) checkHighlight(point.getX()+HIGHLIGHT_OFFSET+1, point.getY(), null, 0, 0);
        }

        checkHighlight(points[0].getX()+HIGHLIGHT_OFFSET+1, points[1].getY()-HIGHLIGHT_OFFSET, path, mx, y1);
        checkHighlight(points[0].getX()+HIGHLIGHT_OFFSET+1, points[1].getY()+HIGHLIGHT_OFFSET, path, mx, y1);
        checkHighlight(points[1].getX()-HIGHLIGHT_OFFSET-1, points[1].getY()-HIGHLIGHT_OFFSET, path, mx, y1);
        checkHighlight(points[1].getX()-HIGHLIGHT_OFFSET-1, points[1].getY()+HIGHLIGHT_OFFSET, path, mx, y1);

        checkHighlight(points[1].getX()-HIGHLIGHT_OFFSET, points[1].getY()+HIGHLIGHT_OFFSET+1, path, x2, my);
        checkHighlight(points[1].getX()+HIGHLIGHT_OFFSET, points[1].getY()+HIGHLIGHT_OFFSET+1, path, x2, my);
        checkHighlight(points[1].getX()-HIGHLIGHT_OFFSET, points[2].getY()-HIGHLIGHT_OFFSET-1, path, x2, my);
        checkHighlight(points[1].getX()+HIGHLIGHT_OFFSET, points[2].getY()-HIGHLIGHT_OFFSET-1, path, x2, my);
    }

    @Test
    public void highlightsPolylineSegments() throws Exception {
        final int x1 = 30, y1 = 20, x2 = 50, y2 = 40;
        final Point2D[] points = {new Point2D(x1, y1), new Point2D(x2, y1), new Point2D(x2, y2)};
        Polyline polyline = new Polyline(x1, y1, x2, y1, x2, y2);
        polyline.setStrokeWidth(1);
        diagram.getChildren().add(polyline);

        for (int i = 1; i < points.length; i++) {
            Point2D start = points[i-1];
            Point2D end = points[i];
            double mx = (start.getX()+end.getX())/2;
            double my = (start.getY()+end.getY())/2;
            checkHighlight(mx, my, polyline, mx, my);
        }
    }

    @Test
    public void highlightsHollowRectangle() throws Exception {
        final int x = 20, y = 30;
        final int width = 40, height = 50;
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setStroke(Color.BLACK);
        rectangle.setFill(null);
        diagram.getChildren().add(rectangle);
        final Point2D[] corners = {new Point2D(x, y), new Point2D(x+width, y), new Point2D(x+width, y+height), new Point2D(x, y+height)};

        for (int i = 0; i < corners.length; i++) {
            Point2D corner = corners[i];
            checkHighlight(corner.getX()-HIGHLIGHT_OFFSET, corner.getY()-HIGHLIGHT_OFFSET, null, 0, 0);
            checkHighlight(corner.getX()+HIGHLIGHT_OFFSET, corner.getY()-HIGHLIGHT_OFFSET, null, 0, 0);
            checkHighlight(corner.getX()+HIGHLIGHT_OFFSET, corner.getY()+HIGHLIGHT_OFFSET, null, 0, 0);
            checkHighlight(corner.getX()-HIGHLIGHT_OFFSET, corner.getY()+HIGHLIGHT_OFFSET, null, 0, 0);

            checkHighlight(corner.getX()-HIGHLIGHT_OFFSET+1, corner.getY(), rectangle, corner.getX(), corner.getY());
            checkHighlight(corner.getX()+HIGHLIGHT_OFFSET-1, corner.getY(), rectangle, corner.getX(), corner.getY());
            checkHighlight(corner.getX(), corner.getY()-HIGHLIGHT_OFFSET+1, rectangle, corner.getX(), corner.getY());
            checkHighlight(corner.getX(), corner.getY()+HIGHLIGHT_OFFSET-1, rectangle, corner.getX(), corner.getY());

            Point2D next = corners[(i+1) & 3];
            double xOffset = Math.signum(next.getX()-corner.getX())*(HIGHLIGHT_OFFSET+1);
            double yOffset = Math.signum(next.getY()-corner.getY())*(HIGHLIGHT_OFFSET+1);
            double mx = (corner.getX()+next.getX())/2;
            double my = (corner.getY()+next.getY())/2;
            checkHighlight(corner.getX()+xOffset, corner.getY()+yOffset, rectangle, mx, my);
            checkHighlight(next.getX()-xOffset, next.getY()-yOffset, rectangle, mx, my);
        }
    }

    @Test
    public void highlightsFilledRectangle() throws Exception {
        final int x = 20, y = 30;
        final int width = 40, height = 50;
        Rectangle rectangle = new Rectangle(x, y, width, height);
        rectangle.setStroke(Color.BLACK);
        diagram.getChildren().add(rectangle);
        final Point2D[] corners = {new Point2D(x, y), new Point2D(x+width, y), new Point2D(x+width, y+height), new Point2D(x, y+height)};

        for (int i = 0; i < corners.length; i++) {
            Point2D corner = corners[i];
            Point2D next = corners[(i+1) & 3];
            double xOffset = Math.signum(next.getX()-corner.getX());
            double yOffset = Math.signum(next.getY()-corner.getY());

            checkHighlight(corner.getX(), corner.getY(), rectangle, corner.getX(), corner.getY());
            checkHighlight(corner.getX(), corner.getY(), rectangle, corner.getX(), corner.getY());
            checkHighlight(corner.getX()+xOffset, corner.getY()+yOffset, rectangle, corner.getX(), corner.getY());
            checkHighlight(corner.getX()+xOffset, corner.getY()+yOffset, rectangle, corner.getX(), corner.getY());

            checkHighlight(corner.getX()-xOffset, corner.getY()-yOffset, null, 0, 0);
            checkHighlight(next.getX()+xOffset, next.getY()+yOffset, null, 0, 0);

            double mx = (corner.getX()+next.getX())/2;
            double my = (corner.getY()+next.getY())/2;
            checkHighlight(corner.getX()+xOffset*(HIGHLIGHT_OFFSET+1), corner.getY()+yOffset*(HIGHLIGHT_OFFSET+1), rectangle, mx, my);
            checkHighlight(next.getX()-xOffset*(HIGHLIGHT_OFFSET+1), next.getY()-yOffset*(HIGHLIGHT_OFFSET+1), rectangle, mx, my);
        }
    }

    @Test
    public void highlightsText() throws Exception {
        Text text = new Text(30, 20, "the text");
        Bounds bounds = text.getBoundsInLocal();
        Point2D centroid = getCentroid(bounds);
        diagram.getChildren().add(text);

        checkHighlight(bounds.getMinX()-1, bounds.getMinY(), null, 0, 0);
        checkHighlight(bounds.getMinX(), bounds.getMinY()-1, null, 0, 0);
        checkHighlight(bounds.getMaxX()+1, bounds.getMaxY(), null, 0, 0);
        checkHighlight(bounds.getMaxX(), bounds.getMaxY()+1, null, 0, 0);
        checkHighlight(bounds.getMinX(), bounds.getMinY(), text, centroid.getX(), centroid.getY());
        checkHighlight(bounds.getMaxX(), bounds.getMaxY(), text, centroid.getX(), centroid.getY());
    }

    @Test
    public void highlightsTextInGroup() throws Exception {
        Text text = new Text(30, 20, "the text");
        diagram.getChildren().add(new Group(text, new Text(30, 50, "other text")));
        Bounds bounds = text.getBoundsInParent();
        Point2D centroid = getCentroid(bounds);

        checkHighlight(bounds.getMinX()-1, bounds.getMinY(), null, 0, 0);
        checkHighlight(bounds.getMinX(), bounds.getMinY()-1, null, 0, 0);
        checkHighlight(bounds.getMaxX()+1, bounds.getMaxY(), null, 0, 0);
        checkHighlight(bounds.getMaxX(), bounds.getMaxY()+1, null, 0, 0);
        checkHighlight(bounds.getMinX(), bounds.getMinY(), text, centroid.getX(), centroid.getY());
        checkHighlight(bounds.getMaxX(), bounds.getMaxY(), text, centroid.getX(), centroid.getY());
    }

    private Point2D getCentroid(Bounds bounds) {
        return new Point2D((bounds.getMinX()+bounds.getMaxX())/2, (bounds.getMinY()+bounds.getMaxY())/2);
    }

    private void checkHighlight(double x, double y, Node expectedHighlight, double markerX, double markerY) throws Exception {
        setValue(controller, "highlighted", shape);
        shape.setEffect(new ColorAdjust(-.25, 0.2, 0.5, 0));

        controller.handle(getEvent(MouseEvent.MOUSE_MOVED, x, y, null));

        assertThat(shape.getEffect()).isNull();
        assertThat(controller.getHighlighted()).isSameAs(expectedHighlight);
        if (expectedHighlight != null) {
            assertThat(marker.isVisible()).isTrue();
            assertThat(marker.getLayoutX()).isEqualTo(markerX);
            assertThat(marker.getLayoutY()).isEqualTo(markerY);
        }
        else assertThat(marker.isVisible()).isFalse();
    }

    @Test
    public void selectsRectangleOnPrimaryPress() throws Exception {
        final int x = 20, y = 30;
        final int width = 40, height = 50;
        Rectangle rectangle = new Rectangle(x, y, width, height);
        diagram.getChildren().add(rectangle);

        checkSelection(x, y, rectangle);
        checkSelection(x+width-1, y+height-1, rectangle);

        checkSelection(x-1, y, null);
        checkSelection(x, y-1, null);
        checkSelection(x+width, y+height, null);
        checkSelection(x+width, y+height, null);
    }

    private void checkSelection(int x, int y, Node expectedSelection) {
        controller.handle(getEvent(MouseEvent.MOUSE_MOVED, x, y, null));
        controller.handle(getEvent(MouseEvent.MOUSE_PRESSED, x, y, MouseButton.PRIMARY));
        assertThat(controller.selectedProperty().getValue()).isSameAs(expectedSelection);
    }

    @Test
    public void ignoresSecondaryPress() throws Exception {
        final int x = 20, y = 30;
        Rectangle rectangle = new Rectangle(x, y, 40, 50);
        diagram.getChildren().add(rectangle);
        controller.handle(getEvent(MouseEvent.MOUSE_MOVED, x, y, null));

        controller.handle(getEvent(MouseEvent.MOUSE_PRESSED, x, y, MouseButton.SECONDARY));

        assertThat(controller.selectedProperty().getValue()).isNull();
    }

    @Test
    public void ignoresMouseDrag() throws Exception {
        final int x = 20, y = 30;
        Rectangle rectangle = new Rectangle(x, y, 40, 50);
        diagram.getChildren().add(rectangle);
        controller.handle(getEvent(MouseEvent.MOUSE_MOVED, x, y, null));

        controller.handle(getEvent(MouseEvent.MOUSE_DRAGGED, x, y, MouseButton.PRIMARY));

        assertThat(controller.selectedProperty().getValue()).isNull();
    }

    private MouseEvent getEvent(EventType<MouseEvent> eventType, double x, double y, MouseButton button) {
        return new MouseEvent(null, diagram, eventType, x, y, x, y, button, 0, false, false, false, false, false, false, false, false, false, false, null);
    }
}