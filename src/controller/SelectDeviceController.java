package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectDeviceController extends Component {

    private MainController mainController;
    List<PcapIf> interfaceDevice;
    StringBuilder errbuf;
    int number;

    @FXML
    private ComboBox devicesComboBox;
    @FXML
    private Label aboutLabel;

    @FXML
    private Label closeLabelAbout;

    @FXML
    private Button backButton;

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

    //------------------------------------------------------------------------------------------------------------------
    public int ListNetworkInterfaces2() throws IOException {
        int a;

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
        a = ints.get(0);
        return a;
    }
    //------------------------------------------------------------------------------------------------------------------
    public void action_chooseNetworkDevice(ActionEvent actionEvent) throws IOException {
        //TODO wyrzuca błąd przy loopbacku i innych urzadzeniach

        number = ListNetworkInterfaces2();
        List<PcapAddr> INT = interfaceDevice.get(number).getAddresses();

//        textAreaOutput.appendText("\n------------------------------------------------------- " +
//                        "\nWybrano urządzenie " + number +
//                        "\nNazwa : " + interfaceDevice.get(number).getName() +
//                        "\nID : " + interfaceDevice.get(number).getDescription()
////                "\nIP Address: " + INT.get(0).getAddr() +
////                "\nBroadcast Address: " + INT.get(0).getNetmask()
//        );
    }
    @FXML
    private void initialize() {
        loadNameDevices();
    }

    public void handleClose(MouseEvent dragEvent) {
        System.exit(0);
    }

    @FXML
    public void backToMenu(ActionEvent actionEvent) throws IOException {
        mainController.loadMenuWindow();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void action_ChooseNetworkDevice(ActionEvent actionEvent) {

    }

    public void action_nextWindow(ActionEvent actionEvent) {
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
    }
}
