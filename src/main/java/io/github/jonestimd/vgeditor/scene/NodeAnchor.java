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

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;

public enum NodeAnchor {
    TOP_LEFT(-1, -1),
    TOP_CENTER(0, -1),
    TOP_RIGHT(1, -1),
    LEFT(-1, 0),
    CENTER(0, 0),
    RIGHT(1, 0),
    BOTTOM_LEFT(-1, 1),
    BOTTOM_CENTER(0, 1),
    BOTTOM_RIGHT(1, 1);

    public final int dx;
    public final int dy;

    NodeAnchor(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public boolean isLeft() {
        return dx < 0;
    }

    public boolean isRight() {
        return dx > 0;
    }

    public boolean isTop() {
        return dy < 0;
    }

    public boolean isBottom() {
        return dy > 0;
    }

    /**
     * Get the appropriate anchor for the start point based on the end point.
     */
    public NodeAnchor adjust(Point2D start, Point2D end) {
        if (this == CENTER) return this;
        boolean above = end.getY() < start.getY();
        if (dx == 0) return above ? BOTTOM_CENTER : TOP_CENTER;
        boolean left = end.getX() < start.getX();
        if (dy == 0) return left ? RIGHT : LEFT;
        if (left) return above ? BOTTOM_RIGHT : TOP_RIGHT;
        return above ? BOTTOM_LEFT : TOP_LEFT;
    }

    public void translateX(Node node, double width) {
        if (isLeft()) node.setTranslateX(0);
        else if (isRight()) node.setTranslateX(-width);
        else node.setTranslateX(-width/2);
    }

    public void translateY(Node node, double height) {
        if (isTop()) node.setTranslateY(0);
        else if (isBottom()) node.setTranslateY(-height);
        else node.setTranslateY(-height/2);
    }

    public Dimension2D getSize(Point2D start, Point2D end) {
        double width = Math.abs(start.getX()-end.getX());
        double height = Math.abs(start.getY()-end.getY());
        return new Dimension2D(width * (dx == 0 ? 2 : 1), height * (dy == 0 ? 2 : 1));
    }
}
