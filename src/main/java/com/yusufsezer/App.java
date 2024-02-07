package com.yusufsezer;

import com.yusufsezer.util.JavaFXUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class App {

    public static void main(String[] args) {
        YSNotepadFXApp.main(args);
    }

    public static class YSNotepadFXApp extends Application {

        public static void main(String[] args) {
            launch();
        }

        @Override
        public void start(Stage stage) throws Exception {
            FXMLLoader loader = JavaFXUtils.loadFXML("main");
            JavaFXUtils.createWindow(loader).show();
        }
    }

}
