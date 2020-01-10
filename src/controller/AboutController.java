package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class AboutController {

    @FXML
    private MenuController menuController;

    @FXML
    private Label aboutLabel;

    @FXML
    private Label closeLabelAbout;

    @FXML
    private Button backButton;

    public void handleClose(MouseEvent dragEvent) {
        System.exit(0);
    }

    @FXML
    public void backToMenu(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/resources/MenuWindow.fxml"));
        Pane pane = null;

        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        menuController.mainController.mainStack.getChildren().clear();

    }
}
