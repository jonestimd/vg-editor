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

import java.util.function.Consumer;

import javafx.geometry.Point2D;

import static io.github.jonestimd.vgeditor.scene.control.selection.SelectionController.*;

public class HighlightBounds {
    private final int minX;
    private final int minY;
    private final int width;
    private final int height;

    public HighlightBounds(int minX, int minY, int width, int height) {
        this.minX = minX;
        this.minY = minY;
        this.width = width;
        this.height = height;
    }

    public void forEach(Consumer<TestCase> testCase, Corner ... corners) {
        for (Corner corner : corners.length > 0 ? corners : Corner.values()) {
            testCase.accept(new TestCase(corner));
        }
    }

    public class TestCase {
        private final Corner corner;
        public final int x;
        public final int y;

        private TestCase(Corner corner) {
            this.corner = corner;
            this.x = corner.isLeft() ? minX : minX+width;
            this.y = corner.isTop() ? minY : minY+height;
        }

        public boolean isLeft() {
            return corner.isLeft();
        }

        public boolean isTop() {
            return corner.isTop();
        }

        public double getInnerX(int adjustment) {
            int direction = corner.isLeft() ? 1 : -1;
            return direction*(HIGHLIGHT_OFFSET+adjustment);
        }

        public double getInnerY(int adjustment) {
            int direction = corner.isTop() ? 1 : -1;
            return direction*(HIGHLIGHT_OFFSET+adjustment);
        }

        public Point2D getMidpoint(boolean topBottom) {
            int mx = topBottom ? minX + width/2 : x;
            int my = topBottom ? y : minY + height/2;
            return new Point2D(mx, my);
        }
    }

    public enum Corner {
        TopLeft,
        TopRight,
        BottomRight,
        BottomLeft;

        public boolean isLeft() {
            return name().endsWith("Left");
        }

        public boolean isTop() {
            return name().startsWith("Top");
        }
    }
}
