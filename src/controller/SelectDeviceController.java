package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jnetpcap.protocol.sigtran.SctpChunk.ABORT;

public class SelectDeviceController {

    MainController mainController;
    List<PcapIf> interfaceDevice;
    StringBuilder errbuf;
    AnchorPane anchorPane;
    int number;

    @FXML
    private ComboBox devicesComboBox;
    @FXML
    private Button backButton;
    @FXML
    private Button nextButton;

    public SelectDeviceController() {
        interfaceDevice = new ArrayList<PcapIf>();
        errbuf = new StringBuilder();
    }

    // -----    zaladowanie do ChoiceBox'a urzadzen sieciowych  ------------------------------------------------------
    private void loadNameDevices() {
        ObservableList<String> networkName;

        if (Pcap.findAllDevs(interfaceDevice, errbuf) != Pcap.OK) {
            throw new IllegalStateException(errbuf.toString());
        }

        for(int i=0; i < interfaceDevice.size(); i++){
            networkName = FXCollections.observableArrayList(
                    "#" + i + " : " + interfaceDevice.get(i).getName() + " >>>> " +
                            interfaceDevice.get(i).getDescription()
            );
            devicesComboBox.getItems().addAll(networkName);
        }
    }

    // -----    wyodrębnienie numeru wybranego urządzenia    ------------------------------------------------------
    public int chooseNumberDevice() {
        int chooseNumber;

        String myString = devicesComboBox.getValue().toString();

        // wyodrębnienie z nazwy numeru jaki uzytkownik wybral w polu ChoiceBox
        Pattern p = Pattern.compile("\\d");
        Matcher m = p.matcher(myString);
        List<Integer> ints = new ArrayList<Integer>();

        while (m.find()) {
            String i = m.group();
            ints.add(Integer.valueOf(i));
        }
        // wpisanie do zmiennej pierwszej wartosci z tablicy 'ints'
        chooseNumber = ints.get(0);

        return chooseNumber;
    }

    //------------------------------------------------------------------------------------------------------------------
    public void action_chooseNetworkDevice(ActionEvent actionEvent) {
        number = chooseNumberDevice();
        nextButton.setDisable(false);
    }

    public void loadNumberPacketsControllerWindow() {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/resources/NumberPacketsWindow.fxml"));
        Pane pane = null;

        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        NumberPacketsController numberPacketsController = loader.getController();
        numberPacketsController.setMainController(mainController);
        mainController.setWindow(pane);

        //-------   Przesłanie zmiennych do controlera VisualisationController  -----------------------------------
        numberPacketsController.setNumberChoose(number);
        numberPacketsController.setErrbuf(errbuf);
        numberPacketsController.setInterfaceDevice(interfaceDevice);
    }

    public void action_nextWindow(ActionEvent actionEvent) {
        loadNumberPacketsControllerWindow();
    }

    @FXML
    private void initialize() {
        nextButton.setDisable(true);
        loadNameDevices();

    }

    public void mouse_handleClose(MouseEvent dragEvent) {
        System.exit(ABORT);
    }

    @FXML
    public void action_backToMenu(ActionEvent actionEvent) {
        mainController.loadMenuWindow();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
