package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.jnetpcap.PcapIf;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class NumberPacketsController extends Component {
    MainController mainController;
    StringBuilder errbuf;
    List<PcapIf> interfaceDevice;
    int numberChoose;
    int numbersToCapture;

    @FXML
    private TextField amountPacket;
    @FXML
    private Button backButton;
    @FXML
    private Button nextButton;

    public NumberPacketsController() {
    }

    public void action_nextWindow(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/resources/VisualisationWindow.fxml"));
        Pane pane = null;

        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        VisualisationController visualisationController = loader.getController();
        visualisationController.setMainController(mainController);
        mainController.setWindow(pane);

        visualisationController.setAmountPacket(Integer.parseInt(this.amountPacket.getText()));


        //załądowanie zmiennych z Controllera SelectDeviceCOntroller
        visualisationController.setNumberChoose(numberChoose);
        visualisationController.setErrbuf(errbuf);
        visualisationController.setInterfaceDevice(interfaceDevice);

    }

    @FXML
    public void action_captureAtt(ActionEvent actionEvent) {
        amountPacket.setText("-1");
    }

    @FXML
    public void mouse_handleClose(MouseEvent dragEvent) {
        System.exit(ABORT);
    }

    @FXML
    public void action_backToMenu(ActionEvent actionEvent) {
        mainController.loadSelectDeviceWindow();

    }

    //----------------  metoda sprawdzająca czy wartość podana jest liczbą    ----------------------------------
    private boolean isNumeric(TextField textField) {
        String isNumber = textField.getText();
        try {
            int i = Integer.parseInt(isNumber);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    //----------------  metoda nasłuchująca pole TextField jaką user wpisał wartość    ----------------------------------
    public void listener_textFieldIsEmpty() {
        amountPacket.textProperty().addListener((observable) -> {
                    if ((amountPacket.getText().length() != 0) &&
                            !(amountPacket.getText().equals("0"))
                            && isNumeric(amountPacket)) {
                        nextButton.setDisable(false);
                    } else {
                        nextButton.setDisable(true);
                    }
                }
        );
    }

    public void initialize() {
        listener_textFieldIsEmpty();
    }

    @FXML
    public void action_numberPacket(ActionEvent actionEvent) {
        numbersToCapture = Integer.parseInt(amountPacket.getText());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        nextButton.setDisable(true);
    }

    public void setErrbuf(StringBuilder errbuf) {
        this.errbuf = errbuf;
    }

    public void setInterfaceDevice(List<PcapIf> interfaceDevice) {
        this.interfaceDevice = interfaceDevice;
    }

    public void setNumberChoose(int numberChoose) {
        this.numberChoose = numberChoose;
    }
}
