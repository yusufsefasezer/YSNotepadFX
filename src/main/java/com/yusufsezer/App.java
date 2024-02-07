package com.yusufsezer;

import com.yusufsezer.util.JavaFXUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = JavaFXUtils.loadFXML("main");
        JavaFXUtils.createWindow(loader).show();
    }

    public static void main(String[] args) {
        launch();
    }

}
