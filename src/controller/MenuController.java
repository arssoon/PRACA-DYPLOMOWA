package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MenuController {

    @FXML
    private MainController mainController;

    @FXML
    public void openVisualisation () {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/resources/VisualisationWindow.fxml"));
         Pane pane = null;

        try {
            pane = loader.load();
        } catch (IOException e ) {
            e.printStackTrace();
        }
        VisualisationController visualisationController = loader.getController();
        visualisationController.setMainController(mainController);
        mainController.setWindow(pane);
    }


    @FXML
    public void openSettings () {

    }

    @FXML
    public void exit() {

        Platform.exit();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


}
