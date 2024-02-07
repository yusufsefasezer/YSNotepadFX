package com.yusufsezer.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

public class FindReplaceController implements Initializable {

    @FXML
    private TextField textFieldFind;

    @FXML
    private CheckBox checkBoxMatchCase;

    @FXML
    private CheckBox checkBoxWraparound;

    @FXML
    private ToggleGroup direction;

    private TextArea textArea;

    @FXML
    private RadioButton radioButtonUp;

    @FXML
    private TextField textFieldReplace;

    @FXML
    private TitledPane titledPaneDirection;

    @FXML
    private Button buttonReplace;

    @FXML
    private Button buttonReplaceAll;

    @FXML
    private HBox hboxReplace;

    @FXML
    private Button buttonFindNext;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        buttonFindNext
                .disableProperty()
                .bind(textFieldFind
                        .textProperty()
                        .isEmpty());
        buttonReplace
                .disableProperty()
                .bind(textFieldFind
                        .textProperty()
                        .isEmpty());
        buttonReplaceAll
                .disableProperty()
                .bind(textFieldFind
                        .textProperty()
                        .isEmpty());
    }

    @FXML
    private void onActionFindNext(ActionEvent event) {
        find(false);
    }

    public void find(boolean prev) {
        String searchString = textFieldFind.getText();
        String textString = textArea.getText();

        if (!checkBoxMatchCase.isSelected()) {
            searchString = searchString.toLowerCase();
            textString = textString.toLowerCase();
        }

        int from = textArea.getCaretPosition();
        int fromIndex = radioButtonUp.isSelected() || prev
                ? from - textFieldFind.getLength() - 1
                : from;

        int index = radioButtonUp.isSelected() || prev
                ? textString.lastIndexOf(searchString, fromIndex)
                : textString.indexOf(searchString, fromIndex);

        if (index == -1 && checkBoxWraparound.isSelected()) {
            index = radioButtonUp.isSelected()
                    ? textString.lastIndexOf(searchString)
                    : textString.indexOf(searchString);
        }

        if (index == -1) {
            Alert information = new Alert(Alert.AlertType.INFORMATION);
            information.setTitle("YSNotepadFX");
            information.setHeaderText("");
            information.setContentText(String
                    .format("Cannot find \"%s\"", searchString));
            information.show();
        } else {
            textArea
                    .selectRange(index,
                            index + textFieldFind.getLength());
        }
    }

    @FXML
    private void onActionReplace(ActionEvent event) {
        if (textArea.getSelectedText().equals(textFieldFind.getText())) {
            textArea.replaceSelection(textFieldReplace.getText());
        }
        find(false);
    }

    @FXML
    private void onActionReplaceAll(ActionEvent event) {
        int flags = Pattern.LITERAL | Pattern.UNICODE_CASE;
        if (!checkBoxMatchCase.isSelected()) {
            flags |= Pattern.CASE_INSENSITIVE;
        }
        String replaceAll = Pattern
                .compile(textFieldFind.getText(), flags)
                .matcher(textArea.getText())
                .replaceAll(textFieldReplace.getText());
        textArea.
                textProperty()
                .set(replaceAll);
    }

    @FXML
    private void onActionCancel(ActionEvent event) {
        textFieldFind
                .getScene()
                .getWindow()
                .hide();
    }

    void setTextArea(TextArea textAreaText) {
        textArea = textAreaText;
    }

    void setReplace(boolean show) {
        titledPaneDirection.setVisible(!show);
        titledPaneDirection
                .managedProperty()
                .bind(titledPaneDirection.visibleProperty());
        buttonReplace.setVisible(show);
        buttonReplace
                .managedProperty()
                .bind(buttonReplace.visibleProperty());
        buttonReplaceAll.setVisible(show);
        buttonReplaceAll
                .managedProperty()
                .bind(buttonReplaceAll.visibleProperty());
        hboxReplace.setVisible(show);
        hboxReplace
                .managedProperty()
                .bind(hboxReplace.visibleProperty());
    }

    @FXML
    private void onKeyPressedFind(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            buttonFindNext.fire();
        }
    }

}
