package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.io.IOException;

public class NumberPacketsController extends Component {

    private MainController mainController;
    VisualisationController visualisationController;
    @FXML
    public TextField amountPacket;
    @FXML
    private Label aboutLabel;

    @FXML
    private Label closeLabelAbout;

    @FXML
    private Button backButton;
int numbers;
    public NumberPacketsController() {
    }
    public void handleClose(MouseEvent dragEvent) {
        System.exit(0);
    }

    @FXML
    public void backToMenu(ActionEvent actionEvent) throws IOException {
        mainController.loadSelectDeviceWindow();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void action_nextWindow(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/resources/VisualisationWindow.fxml"));
        Pane pane = null;

        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        visualisationController = loader.getController();
        visualisationController.setMainController(mainController);
        mainController.setWindow(pane);

    }

    public void action_captureAtt(ActionEvent actionEvent) {
        amountPacket.setText("-1");
    }

    public void action_numberPacket(ActionEvent actionEvent) {
        numbers = Integer.parseInt(amountPacket.getText());

    }


}
