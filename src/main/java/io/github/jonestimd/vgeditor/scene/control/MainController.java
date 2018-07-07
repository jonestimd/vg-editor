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

import java.io.File;

import io.github.jonestimd.vgeditor.model.RectangleModel;
import io.github.jonestimd.vgeditor.scene.control.selection.SelectionController;
import io.github.jonestimd.vgeditor.svg.SvgParser;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.WindowEvent;

public class MainController {
    public static final double PADDING = 10;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Group diagram;
    @FXML
    private Line xAxis = new Line();
    @FXML
    private Line yAxis = new Line();
    @FXML
    private Circle marker;

    private ToolPaneLoader toolPaneLoader;

    private SelectionController selectionController;

    public void initialize() {
        scrollPane.setPrefSize(600, 500);
        selectionController = new SelectionController(diagram, marker);
        selectionController.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getUserData() instanceof RectangleModel) editRectangle((RectangleModel) newValue.getUserData());
        });
        diagram.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                diagram.sceneProperty().removeListener(this);
                diagram.getScene().addEventFilter(MouseEvent.ANY, selectionController);
                toolPaneLoader = new ToolPaneLoader(diagram);
                diagram.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, MainController.this::onClose);
            }
        });
        scrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            adjustAxes(diagram.getBoundsInLocal(), newValue);
        });
        diagram.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> {
            adjustAxes(newValue, scrollPane.getViewportBounds());
        });
    }

    private void adjustAxes(Bounds diagramBounds, Bounds viewportBounds) {
        double minX = Math.min(0, diagramBounds.getMinX())-PADDING;
        double minY = Math.min(0, diagramBounds.getMinY())-PADDING;
        xAxis.setStartX(minX);
        xAxis.setEndX(Math.max(diagram.getBoundsInLocal().getMaxX()+PADDING, viewportBounds.getMaxX()+minX-1));
        yAxis.setStartY(minY);
        yAxis.setEndY(Math.max(diagram.getBoundsInLocal().getMaxY()+PADDING, viewportBounds.getMaxY()+minY-1));
    }

    public void createFile(ActionEvent event) {
        System.out.println("new file");
    }

    public void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("SVG files", "*.svg"),
                new ExtensionFilter("All files", "*.*"));
        File file = fileChooser.showOpenDialog(null);  // workaround for JavaFX bug that disables resizing parent window
        if (file != null) {
            try {
                diagram.getChildren().clear();
                diagram.getChildren().addAll(new SvgParser().parse(file));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void saveFile(ActionEvent event) {
        System.out.println("save file");
    }

    public void saveFileAs(ActionEvent event) {
        System.out.println("save file as");
    }

    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    public void addRectangle() {
        toolPaneLoader.show("RectangleTool.fxml");
    }

    public void editRectangle(RectangleModel model) {
        NodeController<RectangleModel> controller = toolPaneLoader.show("RectangleTool.fxml");
        controller.setModel(model);
    }

    private void onClose(WindowEvent event) {
        System.exit(0);
    }
}
