package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class HelpController {

    @FXML
    private Label helpLabel;

    @FXML
    private Label closeLabelHelp;

    @FXML
    private void initialize() {
        helpLabel.setText("Welcome of a system for capture data in network LAN");
    }

    @FXML
    public void handleClose(MouseEvent dragEvent) {
        System.exit(0);
    }


}
