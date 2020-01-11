package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class HelpController {

    private MainController mainController;

    @FXML
    private Label helpLabel;

    @FXML
    private Label closeLabelHelp;

    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        helpLabel.setText("Welcome of a system for capture data sent in network LAN.\n\n\n" +
                          "capture = \n" +
                          "capture = \n" +
                          "capture = \n" +
                          "capture = \n" +
                          "capture = \n" +
                          "capture = \n" +
                          "capture = \n" +
                          "capture = \n" +
                          "capture = \n" +
                          "capture = \n" +
                          "capture = \n" +
                          "capture = \n"
                );
    }

    @FXML
    public void handleClose(MouseEvent dragEvent) {
        System.exit(0);
    }

    @FXML
    public void backToMenu(ActionEvent actionEvent) throws IOException {
        mainController.loadMenuWindow();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
