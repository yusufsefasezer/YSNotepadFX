package com.yusufsezer;

import com.yusufsezer.util.JavaFXUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        JavaFXUtils
                .createWindow(JavaFXUtils.loadFXML("main"))
                .show();
    }

    public static void main(String[] args) {
        launch();
    }

}
