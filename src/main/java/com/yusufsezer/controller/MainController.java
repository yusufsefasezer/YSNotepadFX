package com.yusufsezer.controller;

import com.yusufsezer.App;
import com.yusufsezer.enumration.EOL;
import com.yusufsezer.enumration.SaveDialogResult;
import com.yusufsezer.util.CharsetUtils;
import com.yusufsezer.util.JavaFXUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.JobSettings;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.dialog.FontSelectorDialog;

public class MainController implements Initializable {

    @FXML
    private MenuItem menuItemUndo;

    @FXML
    private MenuItem menuItemRedo;

    @FXML
    private MenuItem menuItemCut;

    @FXML
    private MenuItem menuItemCopy;

    @FXML
    private MenuItem menuItemDelete;

    @FXML
    private MenuItem menuItemFind;

    @FXML
    private MenuItem menuItemFindNext;

    @FXML
    private MenuItem menuItemFindPrevious;

    @FXML
    private MenuItem menuItemReplace;

    @FXML
    private MenuItem menuItemGoto;

    @FXML
    private CheckMenuItem checkMenuItemWordWrap;

    @FXML
    private CheckMenuItem checkMenuItemStatusBar;

    @FXML
    private TextArea textAreaText;

    @FXML
    private HBox hBoxStatus;

    @FXML
    private Label labelPosition;

    @FXML
    private Label labelZoom;

    @FXML
    private Label labelEOL;

    @FXML
    private Label labelCharset;

    private final BooleanProperty changedProperty;

    private final ObjectProperty<Font> fontProperty;

    private final ObjectProperty<Charset> charsetProperty;

    private final ObjectProperty<EOL> eolProperty;

    private final ObjectProperty<File> fileProperty;

    JobSettings jobSettings = null;

    Stage findReplaceWindow = null;

    Stage gotoWindow = null;

    FindReplaceController findReplaceController = null;

    GotoController gotoController = null;

    public MainController() {
        this.changedProperty = new SimpleBooleanProperty(false);
        this.fontProperty = new SimpleObjectProperty<>(Font.getDefault());
        this.charsetProperty = new SimpleObjectProperty<>(StandardCharsets.UTF_8);
        this.eolProperty = new SimpleObjectProperty<>(EOL.getEOLFromString(System.getProperty("line.separator")));
        this.fileProperty = new SimpleObjectProperty<>();
    }

    @FXML
    void onActionCopy(ActionEvent event) {
        textAreaText.copy();
    }

    @FXML
    void onActionCut(ActionEvent event) {
        textAreaText.cut();
    }

    @FXML
    void onActionDelete(ActionEvent event) {
        textAreaText.deleteText(textAreaText.getSelection());
    }

    @FXML
    void onActionExit(ActionEvent event) {
        closeWindow();
    }

    public void closeWindow() {
        if (changedProperty.get()
                && cancelClicked()) {
            return;
        }

        getWindow().close();
    }

    @FXML
    void onActionFind(ActionEvent event) {
        if (findReplaceWindow == null) {
            createFindReplaceWindow();
        }
        findReplaceWindow.setTitle("Find");
        findReplaceController.setReplace(false);
        findReplaceWindow.show();
    }

    private void createFindReplaceWindow() {
        try {
            findReplaceWindow = new Stage();
            FXMLLoader loadFXML = JavaFXUtils.loadFXML("dialog/findReplace");
            Parent load = loadFXML.load();
            load.setOnKeyPressed((event) -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    findReplaceWindow.close();
                }
            });
            findReplaceController = loadFXML
                    .<FindReplaceController>getController();
            findReplaceController.setTextArea(textAreaText);
            findReplaceWindow.setScene(new Scene(load));
            findReplaceWindow.getIcons().add(new Image("icon.png"));
            findReplaceWindow.initOwner(textAreaText.getScene().getWindow());
            findReplaceWindow.initModality(Modality.NONE);
            findReplaceWindow.setResizable(false);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void createGotoWindow() {
        try {
            gotoWindow = new Stage();
            FXMLLoader loadFXML = JavaFXUtils.loadFXML("dialog/goto");
            Parent load = loadFXML.load();
            load.setOnKeyPressed((event) -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    gotoWindow.close();
                }
            });
            gotoController = loadFXML
                    .<GotoController>getController();
            gotoController.setTextArea(textAreaText);
            gotoWindow.setScene(new Scene(load));
            gotoWindow.getIcons().add(new Image("icon.png"));
            gotoWindow.initOwner(textAreaText.getScene().getWindow());
            gotoWindow.initModality(Modality.WINDOW_MODAL);
            gotoWindow.setResizable(false);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @FXML
    void onActionFindNext(ActionEvent event) {
        if (findReplaceController == null) {
            onActionFind(event);
        }
        findReplaceController.find(false);
    }

    @FXML
    void onActionFont(ActionEvent event) {
        FontSelectorDialog dialog = new FontSelectorDialog(fontProperty.get());
        dialog.showAndWait()
                .ifPresent((t) -> {
                    fontProperty.set(t);
                });
    }

    @FXML
    void onActionGoTo(ActionEvent event) {
        if (gotoWindow == null) {
            createGotoWindow();
        }
        long count = textAreaText
                .getText(0, textAreaText.getCaretPosition())
                .lines()
                .count() + 1L;
        gotoController.setLineNumber(String.valueOf(count));
        gotoWindow.setTitle("Go To Line");
        gotoWindow.show();
    }

    @FXML
    void onActionNew(ActionEvent event) {
        if (changedProperty.get()
                && cancelClicked()) {
            return;
        }

        resetWindow();
    }

    private boolean cancelClicked() {
        String title = "YSNotepadFX";
        String contentText = String
                .format("Do you want to save changes to %s?",
                        getFileName());
        SaveDialogResult result = createDialog(title, contentText);
        if (result.equals(SaveDialogResult.SAVE)) {
            writeFile();
        }
        return result.equals(SaveDialogResult.CANCEL);
    }

    private SaveDialogResult createDialog(String title, String contentText) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("");
        dialog.setContentText(contentText);
        ButtonType saveButtonType = new ButtonType("Save",
                ButtonBar.ButtonData.YES);
        ButtonType dontSaveButtonType = new ButtonType("Don't Save",
                ButtonBar.ButtonData.NO);
        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, dontSaveButtonType, ButtonType.CANCEL);
        dialog.showAndWait();
        ButtonType result = dialog.getResult();
        if (result.equals(saveButtonType)) {
            return SaveDialogResult.SAVE;
        } else if (result.equals(dontSaveButtonType)) {
            return SaveDialogResult.DONTSAVE;
        }
        return SaveDialogResult.CANCEL;
    }

    @FXML
    void onActionNewWindow(ActionEvent event) {
        JavaFXUtils
                .createWindow(JavaFXUtils.loadFXML("main"))
                .show();
    }

    @FXML
    void onActionOpen(ActionEvent event) {

        if (changedProperty.get()) {
            if (cancelClicked()) {
                return;
            }

            readFile();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Documents", "*.txt"),
                new ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(textAreaText
                .getScene()
                .getWindow());
        fileProperty.set(selectedFile);
        readFile();
    }

    public void openFile(File file) {
        if (changedProperty.get()) {
            String title = "YSNotepadFX";
            String contentText = String
                    .format("Do you want to save changes to %s?",
                            getFileName());
            SaveDialogResult result = createDialog(title, contentText);
            if (result.equals(SaveDialogResult.SAVE)) {
                if (fileProperty.get() == null) {
                    saveFileDialog();
                }
                writeFile();
            } else if (result.equals(SaveDialogResult.CANCEL)) {
                return;
            }

        }
        fileProperty.set(file);
        readFile();
    }

    public void openFile() {
        if (changedProperty.get()) {
            if (cancelClicked()) {
                return;
            }

            readFile();
        }
    }

    private void readFile() {
        if (fileProperty.isNotNull().get()) {
            try {
                byte[] readAllBytes = Files
                        .readAllBytes(fileProperty
                                .get()
                                .toPath());
                charsetProperty
                        .set(CharsetUtils
                                .determineCharset(readAllBytes));
                String text = new String(readAllBytes, charsetProperty.get());
                eolProperty
                        .set(EOL.getEOLFromString(text));
                textAreaText
                        .textProperty()
                        .set(text);
                changedProperty.set(false);
                getWindow()
                        .setTitle(fileProperty
                                .get()
                                .getName() + " - " + "YSNotepadFX");
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    @FXML
    void onActionPageSetup(ActionEvent event) {
        PrinterJob printerJob = createPrinterJob();
        printerJob.showPageSetupDialog(textAreaText
                .getScene()
                .getWindow());
        jobSettings = printerJob.getJobSettings();
    }

    private PrinterJob createPrinterJob() {
        PrinterJob printerJob = PrinterJob
                .createPrinterJob();
        if (jobSettings == null) {
            jobSettings = printerJob.getJobSettings();
        } else {
            setPrinterJobSettings(printerJob
                    .getJobSettings(),
                    jobSettings);
        }
        return printerJob;
    }

    private void setPrinterJobSettings(JobSettings from, JobSettings to) {
        to.setCollation(from.getCollation());
        to.setCopies(from.getCopies());
        to.setJobName(from.getJobName());
        to.setPageLayout(from.getPageLayout());
        to.setPageRanges(from.getPageRanges());
        to.setPaperSource(from.getPaperSource());
        to.setPrintColor(from.getPrintColor());
        to.setPrintQuality(from.getPrintQuality());
        to.setPrintResolution(from.getPrintResolution());
        to.setPrintSides(from.getPrintSides());
    }

    @FXML
    void onActionPaste(ActionEvent event) {
        textAreaText.paste();
    }

    @FXML
    void onActionPrevious(ActionEvent event) {
        if (findReplaceController == null) {
            onActionFind(event);
        }
        findReplaceController.find(true);
    }

    @FXML
    void onActionPrint(ActionEvent event) {
        PrinterJob printerJob = createPrinterJob();

        boolean result = printerJob
                .showPrintDialog(textAreaText
                        .getScene()
                        .getWindow());
        if (result) {
            Text text = new Text(textAreaText.getText());
            text.setFont(textAreaText.getFont());
            TextFlow textFlow = new TextFlow(text);
            boolean printed = printerJob
                    .printPage(textFlow);
            if (printed) {
                printerJob.endJob();
            }
        }
    }

    @FXML
    void onActionReplace(ActionEvent event) {
        openReplaceWindow();
    }

    private void openReplaceWindow() {
        if (findReplaceWindow == null) {
            createFindReplaceWindow();
        }
        findReplaceWindow.setTitle("Replace");
        findReplaceController.setReplace(true);
        findReplaceWindow.show();
    }

    @FXML
    void onActionRestoreDefaultZoom(ActionEvent event) {
        Font currentFont = fontProperty.get();
        Font newFont = Font.font(currentFont.getName(), 12d);
        fontProperty.set(newFont);
    }

    @FXML
    void onActionSave(ActionEvent event) {
        if (fileProperty.get() == null) {
            saveFileDialog();
        }
        writeFile();
        readFile();
    }

    private void writeFile() {
        if (fileProperty.isNotNull().get()) {
            StringBuilder stringBuilder = new StringBuilder();
            textAreaText
                    .getText()
                    .lines()
                    .forEach((line) -> {
                        stringBuilder
                                .append(line)
                                .append(eolProperty
                                        .get()
                                        .getSeperator());
                    });

            try {
                Files.writeString(fileProperty
                        .get()
                        .toPath(),
                        stringBuilder,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE);
                String newTitle = getWindow().getTitle().replace("*", "");
                getWindow().setTitle(newTitle);
                changedProperty.set(false);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    @FXML
    void onActionSaveAs(ActionEvent event) {
        saveFileDialog();
        readFile();
    }

    private void saveFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Documents", "*.txt"),
                new ExtensionFilter("All Files", "*.*"));
        fileChooser.setInitialFileName("*.txt");
        File selectedFile = fileChooser.showSaveDialog(textAreaText
                .getScene()
                .getWindow());
        fileProperty.set(selectedFile);
        writeFile();
    }

    @FXML
    void onActionSelectAll(ActionEvent event) {
        textAreaText.selectAll();
    }

    @FXML
    void onActionShowAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(textAreaText.getScene().getWindow());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle("YSNotepadFX");
        alert.setHeaderText("");
        alert.setContentText("Created by Yusuf SEZER (www.yusufsezer.com)");
        alert.show();
    }

    @FXML
    void onActionTimeDate(ActionEvent event) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String format = localDateTime
                .format(DateTimeFormatter.ofPattern("H:mm MM/dd/u"));
        if (textAreaText.getSelectedText() != null
                && !textAreaText.getSelectedText().equals("")) {
            textAreaText.replaceSelection(format);
        } else {
            textAreaText.appendText(format);
        }
    }

    @FXML
    void onActionUndo(ActionEvent event) {
        textAreaText.undo();
    }

    @FXML
    void onActionViewHelp(ActionEvent event) {
        new App.YSNotepadFXApp().getHostServices().showDocument("www.yusufsezer.com");
    }

    @FXML
    void onActionZoomIn(ActionEvent event) {
        Font currentFont = fontProperty.get();
        double newSize = currentFont.getSize() + 1d;
        Font newFont = Font.font(currentFont.getName(), newSize);
        fontProperty.set(newFont);
    }

    @FXML
    void onActionZoomOut(ActionEvent event) {
        Font currentFont = fontProperty.get();
        double newSize = currentFont.getSize() - 1d;
        Font newFont = Font.font(currentFont.getName(), newSize);
        fontProperty.set(newFont);
    }

    @FXML
    void onActionRedo(ActionEvent event) {
        textAreaText.redo();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        if (eolProperty.get() == null) {
            throw new IllegalStateException("Could not determine system default end-of-line marker");
        }

        textAreaText.wrapTextProperty()
                .bind(checkMenuItemWordWrap
                        .selectedProperty());
        textAreaText.textProperty()
                .addListener((o) -> {
                    if (!changedProperty.get()) {
                        changedProperty.set(true);
                    }
                    if (fileProperty.get() == null
                            && textAreaText.getText().isEmpty()) {
                        changedProperty.set(false);
                    }
                });
        changedProperty.addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                Stage currentWindow = getWindow();
                String newTitle = newValue
                        ? "*".concat(currentWindow.getTitle())
                        : currentWindow.getTitle().replace("*", "");
                currentWindow.setTitle(newTitle);
            }
        });

        textAreaText.fontProperty()
                .bind(fontProperty);
        textAreaText.caretPositionProperty()
                .addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                        int to = newValue.intValue() + 1;
                        if (to > textAreaText.getLength()) {
                            to = textAreaText.getLength();
                        }

                        String[] lines = textAreaText
                                .getText(0, to)
                                .lines()
                                .toArray(String[]::new);
                        int position = newValue.intValue() + 1;

                        if (lines.length > 1) {
                            for (int i = 0; i < lines.length - 1; i++) {
                                position -= lines[i].length() + eolProperty
                                        .get()
                                        .getSeperator()
                                        .length();
                            }
                        }

                        labelPosition
                                .textProperty()
                                .set(String
                                        .format("Ln %s, Col %s",
                                                lines.length,
                                                position));

                    }
                });
        hBoxStatus.visibleProperty()
                .bind(checkMenuItemStatusBar
                        .selectedProperty());
        hBoxStatus.managedProperty()
                .bind(hBoxStatus.visibleProperty());
        menuItemUndo.disableProperty()
                .bind(textAreaText
                        .undoableProperty()
                        .not());
        menuItemRedo.disableProperty()
                .bind(textAreaText
                        .redoableProperty()
                        .not());
        menuItemCopy.disableProperty()
                .bind(textAreaText
                        .selectedTextProperty()
                        .isEmpty());
        menuItemCut.disableProperty()
                .bind(textAreaText
                        .selectedTextProperty()
                        .isEmpty());
        menuItemDelete.disableProperty()
                .bind(textAreaText
                        .selectedTextProperty()
                        .isEmpty());
        menuItemFind.disableProperty()
                .bind(textAreaText
                        .textProperty()
                        .isEmpty());
        menuItemFindNext.disableProperty()
                .bind(textAreaText
                        .textProperty()
                        .isEmpty());
        menuItemFindPrevious.disableProperty()
                .bind(textAreaText
                        .textProperty()
                        .isEmpty());
        menuItemReplace.disableProperty()
                .bind(textAreaText
                        .textProperty()
                        .isEmpty());
        menuItemGoto.disableProperty()
                .bind(textAreaText
                        .wrapTextProperty());
        textAreaText.addEventFilter(KeyEvent.KEY_PRESSED,
                (event) -> {
                    if (event.getCode() == KeyCode.H && event.isControlDown()) {
                        openReplaceWindow();
                        event.consume();
                    }
                }
        );
        charsetProperty
                .addListener((ov, oldValue, newValue) -> {
                    labelCharset.setText(newValue.displayName());
                });
        eolProperty
                .addListener((ov, oldValue, newValue) -> {
                    setEOL(newValue);
                });
        setEOL(eolProperty.get());
    }

    private void setEOL(EOL eol) {
        String text = String.format("%s (%s)",
                eol.getStyle(),
                eol);
        labelEOL.setText(text);
    }

    private String getFileName() {
        return fileProperty
                .isNull()
                .get()
                        ? "Untitled"
                        : fileProperty
                                .get()
                                .getAbsolutePath();
    }

    private Stage getWindow() {
        return (Stage) textAreaText
                .getScene()
                .getWindow();
    }

    private void resetWindow() {
        getWindow()
                .setTitle("Untitled - YSNotepadFX");
        textAreaText.clear();
        changedProperty.set(false);
        charsetProperty.set(StandardCharsets.UTF_8);
        eolProperty.set(EOL
                .getEOLFromString(System
                        .getProperty("line.separator")));
        fileProperty.set(null);
    }

}
