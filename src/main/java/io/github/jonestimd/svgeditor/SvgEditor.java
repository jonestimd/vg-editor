package io.github.jonestimd.svgeditor;

import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SvgEditor extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(ResourceBundle.getBundle("io.github.jonestimd.svgeditor.labels"));
        primaryStage.setScene(new Scene(loader.load(getClass().getResourceAsStream("Main.fxml"))));
        primaryStage.show();
    }
}
