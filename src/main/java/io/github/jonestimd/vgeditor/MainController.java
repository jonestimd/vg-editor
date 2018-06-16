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

import java.io.File;

import io.github.jonestimd.vgeditor.svg.SvgParser;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController {
    @FXML
    private MenuBar menuBar;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Pane diagram;

    private ToolPaneLoader toolPaneLoader;

    private SelectionController selectionController;

    // TODO close tool windows and exit when scene is closed
    public void initialize() {
        scrollPane.setPrefSize(600, 500);
        selectionController = new SelectionController(diagram);
        diagram.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                diagram.getScene().addEventHandler(MouseEvent.ANY, selectionController);
                toolPaneLoader = new ToolPaneLoader(diagram);
                diagram.sceneProperty().removeListener(this);
            }
        });
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

    public void addRectangle(ActionEvent event) {
        System.out.println("addRectangle");
        NodeController<?> controller = toolPaneLoader.show("RectangleTool.fxml");
        if (controller.newNode()) diagram.getChildren().add(controller.getNode());
    }
}
