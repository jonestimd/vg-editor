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
package io.github.jonestimd.vgeditor.scene;

import io.github.jonestimd.vgeditor.scene.control.selection.SelectionController;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;

import static java.lang.Math.*;

public enum NodeAnchor {
    TOP_LEFT(-1, -1),
    TOP(0, -1),
    TOP_RIGHT(1, -1),
    LEFT(-1, 0),
    CENTER(0, 0),
    RIGHT(1, 0),
    BOTTOM_LEFT(-1, 1),
    BOTTOM(0, 1),
    BOTTOM_RIGHT(1, 1);

    private static double alignLeftX(double halfWidth, double angle) {
        return halfWidth*cos(angle);
    }

    private static double alignLeftY(double halfWidth, double angle) {
        return halfWidth*sin(angle);
    }

    private static double alignTopX(double halfHeight, double angle) {
        return -halfHeight*sin(angle);
    }

    private static double alignTopY(double halfHeight, double angle) {
        return halfHeight*cos(angle);
    }

    // normalized anchor offset from center
    public final int dy;
    public final int dx;

    NodeAnchor(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Get the appropriate anchor for the start point based on the end point.
     */
    public NodeAnchor adjust(Point2D start, Point2D end) {
        if (this == CENTER) return this;
        boolean above = end.getY() < start.getY();
        if (dx == 0) return above ? BOTTOM : TOP;
        boolean left = end.getX() < start.getX();
        if (dy == 0) return left ? RIGHT : LEFT;
        if (left) return above ? BOTTOM_RIGHT : TOP_RIGHT;
        return above ? BOTTOM_LEFT : TOP_LEFT;
    }

    /**
     * Set the node's translation based on the anchor and its rotation.
     * @param node the node to translate
     * @param width the node width
     * @param height the node height
     */
    public void translate(Node node, double width, double height) {
        translate(node, Math.toRadians(node.getRotate()), width/2, height/2);
    }

    private void translate(Node node, double angle, double halfWidth, double halfHeight) {
        node.setLayoutX(-dx*alignLeftX(halfWidth, angle)-dy*alignTopX(halfHeight, angle)-halfWidth);
        node.setLayoutY(-dx*alignLeftY(halfWidth, angle)-dy*alignTopY(halfHeight, angle)-halfHeight);
    }

    public Dimension2D getSize(Point2D start, Point2D end) {
        double width = Math.abs(start.getX()-end.getX());
        double height = Math.abs(start.getY()-end.getY());
        return new Dimension2D(width * (dx == 0 ? 2 : 1), height * (dy == 0 ? 2 : 1));
    }

    public static NodeAnchor valueOf(Node node, double width, double height) {
        double angle = Math.toRadians(node.getRotate());
        double halfWidth = width/2, halfHeight = height/2;
        double layoutX = node.getLayoutX(), layoutY = node.getLayoutY();
        double alignLeftX = alignLeftX(halfWidth, angle);
        double alignTopX = alignTopX(halfHeight, angle);
        double alignLeftY = alignLeftY(halfWidth, angle);
        double alignTopY = alignTopY(halfHeight, angle);
        for (NodeAnchor anchor : values()) {
            if (layoutX == -anchor.dx*alignLeftX-anchor.dy*alignTopX-halfWidth && layoutY == -anchor.dx*alignLeftY-anchor.dy*alignTopY-halfHeight) {
                return anchor;
            }
        }
        throw new IllegalArgumentException();
    }

    public static NodeAnchor forResize(Point2D cursor, double x, double y, double width, double height) {
        boolean top = Math.abs(y-cursor.getY()) <= SelectionController.HIGHLIGHT_OFFSET;
        boolean bottom = Math.abs(y+height-cursor.getY()) <= SelectionController.HIGHLIGHT_OFFSET;
        if (Math.abs(x-cursor.getX()) <= SelectionController.HIGHLIGHT_OFFSET) {
            if (top) return TOP_LEFT;
            return bottom ? BOTTOM_LEFT : LEFT;
        }
        if (Math.abs(x+width-cursor.getX()) <= SelectionController.HIGHLIGHT_OFFSET) {
            if (top) return TOP_RIGHT;
            return bottom ? BOTTOM_RIGHT : RIGHT;
        }
        if (top) return TOP;
        return bottom ? BOTTOM : null;
    }
}
