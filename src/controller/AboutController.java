package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class AboutController {

    private MainController mainController;

    @FXML
    private Label aboutLabel;

    @FXML
    private Label closeLabelAbout;

    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        aboutLabel.setText("\t  PRACA DYPLOMOWA\n\n" +
                "Nazwa aplikacji -> Capture Packets\n" +
                "Wykonał -> Arkadiusz Sobol\n" +
                "Promotor -> Jarosław Wikarek\n\n"

        );
    }

    public void handleClose(MouseEvent dragEvent) {
        System.exit(0);
    }

    @FXML
    public void action_backToMenu(ActionEvent actionEvent) throws IOException {
        mainController.loadMenuWindow();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
