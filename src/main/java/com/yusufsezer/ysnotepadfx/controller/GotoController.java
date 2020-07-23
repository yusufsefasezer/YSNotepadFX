package com.yusufsezer.ysnotepadfx.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

public class GotoController implements Initializable {

    @FXML
    private TextField textFieldLineNumber;

    private TextArea textArea;

    @FXML
    private Button buttonGoto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textFieldLineNumber
                .textProperty()
                .addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends String> observable,
                            String oldValue,
                            String newValue) {
                        if (!newValue.matches("\\d*")) {
                            textFieldLineNumber
                                    .setText(newValue.replaceAll("[^\\d]", ""));
                        }
                    }
                });
    }

    @FXML
    private void onActionGoto(ActionEvent event) {
        try {
            int position = -1;
            int lineNumber = Integer
                    .valueOf(textFieldLineNumber
                            .getText());
            String[] lines = textArea
                    .getText()
                    .lines()
                    .toArray(String[]::new);

            if (lineNumber == 0 || lineNumber > lines.length) {
                Alert alert = new Alert(Alert.AlertType.NONE,
                        "The line number is beyond the total number of lines",
                        ButtonType.OK);
                alert.initOwner(textFieldLineNumber.getScene().getWindow());
                alert.initModality(Modality.WINDOW_MODAL);
                alert.setTitle("YSNotepadFX - Goto Line");
                alert.setHeaderText("");
                alert.show();
            } else {
                for (int i = 0; i < lineNumber - 1; i++) {
                    position += lines[i].length();
                }
                textArea.positionCaret(position + lineNumber);
                onActionCancel(event);
            }
        } catch (NumberFormatException e) {
        }
    }

    @FXML
    private void onActionCancel(ActionEvent event) {
        textFieldLineNumber
                .getScene()
                .getWindow()
                .hide();
    }

    void setTextArea(TextArea textAreaText) {
        textArea = textAreaText;
    }

    void setLineNumber(String value) {
        textFieldLineNumber.setText(value);
    }

    @FXML
    private void onKeyPressedLineNumber(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            buttonGoto.fire();
        }
    }

}
