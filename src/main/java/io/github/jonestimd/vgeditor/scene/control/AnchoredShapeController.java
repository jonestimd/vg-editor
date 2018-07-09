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
package io.github.jonestimd.vgeditor.scene.control;

import java.util.function.BiConsumer;
import java.util.function.Function;

import io.github.jonestimd.vgeditor.scene.NodeAnchor;
import io.github.jonestimd.vgeditor.scene.model.AnchoredShapeModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;

public class AnchoredShapeController<T extends AnchoredShapeModel> extends ShapeController<T> {
    private NodeAnchor nodeAnchor = NodeAnchor.TOP_LEFT;
    @FXML
    private GridPane anchorParent;

    protected AnchoredShapeController(Function<Group, T> modelFactory) {
        super(modelFactory);
    }

    public void initialize() {
        selectAnchor();
        super.initialize();
    }

    @Override
    public void setModel(T model) {
        super.setModel(model);
        selectAnchor(model.getAnchor());
    }

    public void onAnchorChange(ActionEvent event) {
        NodeAnchor nodeAnchor = NodeAnchor.valueOf(((RadioButton) event.getSource()).getId());
        if (this.nodeAnchor != nodeAnchor) {
            this.nodeAnchor = nodeAnchor;
            if (getModel() != null) getModel().setAnchor(nodeAnchor);
        }
    }

    protected void createNode() {
        super.createNode();
        getModel().setAnchor(nodeAnchor);
    }

    private void selectAnchor(NodeAnchor anchor) {
        if (anchor != this.nodeAnchor) {
            this.nodeAnchor = anchor;
            selectAnchor();
            if (getModel() != null) getModel().setAnchor(anchor);
        }
    }

    private void selectAnchor() {
        anchorParent.getChildren().stream().filter(node1 -> nodeAnchor.name().equals(node1.getId())).findFirst()
                .ifPresent(button -> ((RadioButton) button).setSelected(true));
    }

    @Override
    protected Dimension2D getNewNodeSize(Point2D diagramStart, Point2D diagramEnd) {
        selectAnchor(nodeAnchor.adjust(diagramStart, diagramEnd));
        return nodeAnchor.getSize(diagramStart, diagramEnd);
    }

    @Override
    protected BiConsumer<Point2D, Point2D> getResizeDragHandler(NodeAnchor resizeAnchor) {
        return new ResizeDragHandler(resizeAnchor, getModel());
    }

    private class ResizeDragHandler implements BiConsumer<Point2D, Point2D> {
        private final double startX, startY;
        private final double startWidth, startHeight;
        private final ResizeDragCalculator resizeDragCalculator;
        private int swapX = 1, swapY = 1;

        public ResizeDragHandler(NodeAnchor resizeAnchor, T model) {
            resizeDragCalculator = new ResizeDragCalculator(resizeAnchor, nodeAnchor, model.getRotate(), 2);
            this.startX = model.getX();
            this.startY = model.getY();
            this.startWidth = model.getWidth();
            this.startHeight = model.getHeight();
        }

        @Override
        public void accept(Point2D start, Point2D end) {
            Offset2D adjustment = resizeDragCalculator.apply(start, end);
            setLocationInputs(startX+adjustment.dx, startY+adjustment.dy);
            setNodeLocation();
            double width = swapX*(startWidth+adjustment.dWidth);
            double height = swapY*(startHeight+adjustment.dHeight);
            if (width < 0) {
                selectAnchor(nodeAnchor.swapX());
                swapX = -swapX;
            }
            if (height < 0) {
                selectAnchor(nodeAnchor.swapY());
                swapY = -swapY;
            }
            setSizeInputs(Math.abs(width), Math.abs(height));
            setNodeSize();
        }
    }
}