package com.yusufsezer;

import com.yusufsezer.ysnotepadfx.util.JavaFXUtil;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        JavaFXUtil
                .createWindow(JavaFXUtil.loadFXML("main"))
                .show();
    }

    public static void main(String[] args) {
        launch();
    }

}
