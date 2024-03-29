package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.awt.event.ActionEvent;
import java.io.IOException;

import static org.jnetpcap.protocol.sigtran.SctpChunk.ABORT;

public class MenuController {

    @FXML
    public MainController mainController;

    @FXML
    private Label closeLabel;

    @FXML
    private void handleClose(ActionEvent event) {
        System.exit(0);
    }

    // Menu -> VISUALISATION
    @FXML
    public void openCapturePackets() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/resources/SelectDeviceWindow.fxml"));
        Pane pane = null;

        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SelectDeviceController selectDeviceController = loader.getController();
        selectDeviceController.setMainController(mainController);
        mainController.setWindow(pane);
    }


    // Menu -> HELP
    @FXML
    public void openHelp() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/resources/HelpWindow.fxml"));
        Pane pane = null;

        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HelpController helpController = loader.getController();
        helpController.setMainController(mainController);
        mainController.setWindow(pane);
    }

    // Menu -> ABOUT
    @FXML
    public void openAbout() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/resources/AboutWindow.fxml"));
        Pane pane = null;

        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AboutController aboutController = loader.getController();
        aboutController.setMainController(mainController);
        mainController.setWindow(pane);
    }

    // Menu -> EXIT
    @FXML
    public void openExit() {
        System.exit(ABORT);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;

    }

    public void handleClose(MouseEvent dragEvent) {
        System.exit(ABORT);
    }

}

