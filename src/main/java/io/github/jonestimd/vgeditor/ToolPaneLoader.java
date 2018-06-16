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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import io.github.jonestimd.vgeditor.scene.Nodes;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Pair;

public class ToolPaneLoader {
    private static final double MIN_WIDTH = 180;
    private final Pane diagram;
    private final FXMLLoader loader = new FXMLLoader();
    private final Stage stage = new Stage(StageStyle.UTILITY);
    private String fileName;
    private Pair<NodeController<?>, Pane> controllerPane;
    private final Map<String, Pair<NodeController<?>, Pane>> fileControllers = new HashMap<>();

    public ToolPaneLoader(Pane diagram) {
        loader.setResources(ResourceBundle.getBundle("io.github.jonestimd.vgeditor.labels"));
        this.diagram = diagram;
        Scene scene = new Scene(new VBox());
        scene.getAccelerators().putAll(diagram.getScene().getAccelerators());
        stage.setScene(scene);
        diagram.getScene().addEventHandler(MouseEvent.ANY, event -> {
            if (controllerPane != null && controllerPane.getValue().getScene().getWindow().isShowing()) {
                controllerPane.getKey().handle(event);
            }
        });
    }

    public NodeController<?> show(String fileName) {
        if (fileControllers.isEmpty()) locateWindow();
        if (!fileName.equals(this.fileName)) {
            this.fileName = fileName;
            controllerPane = fileControllers.computeIfAbsent(fileName, this::load);
            controllerPane.getKey().setPane(diagram);
            stage.getScene().setRoot(controllerPane.getValue());
        }
        Nodes.visit(controllerPane.getValue(), TextField.class, (field) -> field.setText(""));
        stage.show();
        stage.requestFocus();
        return controllerPane.getKey();
    }

    private void locateWindow() {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        Window mainWindow = diagram.getScene().getWindow();
        stage.setY(mainWindow.getY());
        double x = mainWindow.getX()+mainWindow.getWidth();
        if (x+MIN_WIDTH > bounds.getMaxX()) x = bounds.getMaxX()-MIN_WIDTH;
        stage.setX(x);
    }

    private Pair<NodeController<?>, Pane> load(String fileName) {
        try {
            Pane root = loader.load(getClass().getResourceAsStream(fileName));
            return new Pair<>(loader.getController(), root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
