package controller;

import javafx.fxml.FXML;

public class VisualisationController {

    private MainController mainController;

    @FXML
    public void backToMenu () {
        mainController.loadMenuWindow();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }



}
