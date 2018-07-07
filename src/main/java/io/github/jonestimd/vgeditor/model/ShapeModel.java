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
package io.github.jonestimd.vgeditor.model;

import io.github.jonestimd.vgeditor.scene.NodeAnchor;
import javafx.geometry.Point2D;
import javafx.scene.paint.Paint;

public interface ShapeModel {
    void remove();

    double getX();
    double getY();

    void setX(double x);
    void setY(double y);

    double getWidth();
    double getHeight();

    void setWidth(double width);
    void setHeight(double height);

    double getRotate();
    void setRotate(double angle);

    NodeAnchor getAnchor();
    void setAnchor(NodeAnchor anchor);

    Paint getFill();
    void setFill(Paint paint);

    Paint getStroke();
    void setStroke(Paint paint);

    double getStrokeWidth();
    void setStrokeWidth(double strokeWidth);

    NodeAnchor getResizeAnchor(Point2D screenPoint);

    boolean isStartDrag(Point2D screenPoint);
}
