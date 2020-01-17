package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    public StackPane mainStack;

    @FXML
    public void initialize() {
        loadMenuWindow();
    }

    public void loadMenuWindow() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/resources/MenuWindow.fxml"));
        Pane pane = new Pane();
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MenuController menuController = loader.getController();
        menuController.setMainController(this);
        setWindow(pane);
    }

    public void loadSelectDeviceWindow() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/resources/SelectDeviceWindow.fxml"));
        Pane pane = new Pane();
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SelectDeviceController selectDeviceController = loader.getController();
        selectDeviceController.setMainController(this);
        setWindow(pane);
    }

    public void setWindow(Pane pane) {
        mainStack.getChildren().clear();
        mainStack.getChildren().add(pane);
    }

}

