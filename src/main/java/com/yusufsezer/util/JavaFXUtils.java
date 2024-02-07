package com.yusufsezer.util;

import com.yusufsezer.controller.MainController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

public class JavaFXUtils {

    public final static String BUNDLE_NAME = "bundle/text";
    public final static String FXML_PATH = "fxml/";

    public static FXMLLoader loadFXML(String fxml) {
        return loadFXMLFromDirectory(FXML_PATH, fxml);
    }

    public static FXMLLoader loadFXMLFromDirectory(String directory, String fxmlFileName) {
        return loadFXMLFromResource(directory + fxmlFileName + ".fxml");
    }

    public static FXMLLoader loadFXMLFromResource(String fxmlPath) {
        URL url = JavaFXUtils.class.getClassLoader().getResource(fxmlPath);
        ResourceBundle rb = ResourceBundle.getBundle(JavaFXUtils.BUNDLE_NAME);
        return new FXMLLoader(url, rb);
    }

    public static String getBundleMessage(String message) {
        return ResourceBundle
                .getBundle(JavaFXUtils.BUNDLE_NAME)
                .getString(message);
    }

    public static Stage createWindow(FXMLLoader loader) {
        Stage newStage = new Stage();
        try {
            Scene scene = new Scene(loader.load());
            newStage.setScene(scene);
            newStage.getIcons().add(new Image("icon.png"));
            newStage.setTitle("Untitled - YSNotepadFX");
            MainController controller = loader.<MainController>getController();
            newStage.setOnCloseRequest((t) -> {
                controller.closeWindow();
            });
            scene.setOnDragOver((event) -> {
                if (event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            });
            scene.setOnDragDropped((event) -> {
                controller.openFile(event
                        .getDragboard()
                        .getFiles()
                        .get(0));
            });
        } catch (IOException e) {
            System.err.println(e);
        }
        return newStage;
    }

}
