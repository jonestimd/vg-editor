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

import io.github.jonestimd.vgeditor.scene.Nodes;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

public class ToolPaneLoader {
    private final Pane diagram;
    private final FXMLLoader loader = new FXMLLoader();
    private final Stage stage = new Stage(StageStyle.UTILITY);
    private final Scene scene = new Scene(new VBox());
    private String fileName;
    private Pair<NodeController<?>, Pane> controllerPane;
    private final Map<String, Pair<NodeController<?>, Pane>> fileControllers = new HashMap<>();

    public ToolPaneLoader(Pane diagram) {
        this.diagram = diagram;
        scene.getAccelerators().putAll(diagram.getScene().getAccelerators());
        stage.setScene(scene);
        diagram.getScene().addEventHandler(MouseEvent.ANY, event -> {
            if (controllerPane != null && controllerPane.getValue().getScene().getWindow().isShowing()) {
                controllerPane.getKey().handle(event);
            }
        });
    }

    public NodeController<?> show(String fileName) {
        if (!fileName.equals(this.fileName)) {
            this.fileName = fileName;
            controllerPane = fileControllers.computeIfAbsent(fileName, this::load);
            controllerPane.getKey().setPane(diagram);
            scene.setRoot(controllerPane.getValue());
        }
        Nodes.visit(controllerPane.getValue(), TextField.class, (field) -> field.setText(""));
        stage.show();
        stage.requestFocus();
        return controllerPane.getKey();
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
