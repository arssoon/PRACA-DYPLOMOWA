package controller;

import controller.Threads.CaptureThread;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;
import org.pcap4j.core.PcapNetworkInterface;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VisualisationController extends Component {

    StringBuilder errbuf = new StringBuilder();
    List<PcapIf> interfaceDevice = new ArrayList<PcapIf>();
    ObservableList<String> networkName;
    CaptureThread threadCapture;

    PcapNetworkInterface device;
    int number = 0;
    public AtomicBoolean running;

    public VisualisationController() {
        running = new AtomicBoolean(false);
    }

    @FXML
    private MainController mainController;

    // List network devices
    @FXML
    private ComboBox devicesComboBox;

    @FXML
    private TextArea textAreaOutput;

    @FXML
    private TextArea textAreaInfo;

    @FXML
    private Button listNetworkDevices;
    // Start & stop
    @FXML
    private Button startCaptureButton;

    @FXML
    private Button stopCaptureButton;

    @FXML
    private Button backButton;

    //------------------------------------------------------------------------------------------------------------------
    private String getNameDevices(int number) {
        if (Pcap.findAllDevs(interfaceDevice, errbuf) != Pcap.OK) {
            throw new IllegalStateException(errbuf.toString());
        }
        this.number = number;
        return "#" + number + " : " + interfaceDevice.get(number).getName() + " >>>> " + interfaceDevice.get(number).getDescription();
    }

    //------------------------------------------------------------------------------------------------------------------
    // zaladowanie do ChoiceBox'a urzadzen sieciowych
    private void loadNameDevices() {
        networkName = FXCollections.observableArrayList(
                getNameDevices(0),
                getNameDevices(1),
                getNameDevices(2),
                getNameDevices(3),
                getNameDevices(4),
                getNameDevices(5),
                getNameDevices(6),
                getNameDevices(7)
        );

        devicesComboBox.setItems(networkName);
    }

    //------------------------------------------------------------------------------------------------------------------
    // wyswietlenie wszystkich urzadzen sieciowych dostepnych na komputerze w polu TextArea przez button 'List Network Devices'
    public void ListNetworkInterfaces() {
        if (Pcap.findAllDevs(interfaceDevice, errbuf) != Pcap.OK) {
            throw new IllegalStateException(errbuf.toString());
        }

        for (int i = 0; i <= number; i++) {
            textAreaOutput.appendText("\n#" + i + "_Nazwa urzadzenia: " + interfaceDevice.get(i).getName() + " >>>> " +
                    interfaceDevice.get(i).getDescription());
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public int ListNetworkInterfaces2() throws IOException {
        int a;
        if (Pcap.findAllDevs(interfaceDevice, errbuf) != Pcap.OK) {
            throw new IllegalStateException(errbuf.toString());
        }
//        for (int i = 0; i <= number; i++) {
//            textAreaShow.appendText("\n#" + i + "_Nazwa urzadzenia: " + interfaceDevice.get(i).getName() + "\t" + interfaceDevice.get(i).getDescription());
//
//        }
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
    public void chooseNetworkDevice(ActionEvent actionEvent) throws IOException {
        //TODO wyrzuca błąd przy loopbacku i innych urzadzeniach
        number = ListNetworkInterfaces2();
        List<PcapAddr> INT = interfaceDevice.get(number).getAddresses();

        textAreaOutput.appendText("\n------------------------------------------------------- " +
                "\nWybrano urządzenie " + number +
                "\nNazwa : " + interfaceDevice.get(number).getName() +
                "\nID : " + interfaceDevice.get(number).getDescription() +
                "\nIP Address: " + INT.get(0).getAddr() +
                "\nBroadcast Address: " + INT.get(0).getNetmask()
        );
    }
    //------------------------------------------------------------------------------------------------------------------
    public void StartCapturePacket() throws IOException {
        if(number == 7) {
            // INFORMACJA O ZATRZYMANIU PRZECHWYTYWANIA
            JOptionPane.showMessageDialog(this, "Please, select your network devices.", "INFORMATION MESSAGE", JOptionPane.WARNING_MESSAGE);

        } else {
            textAreaOutput.setText(" ");
            startCaptureButton.setDisable(true);
            listNetworkDevices.setDisable(true);
            startCaptureButton.setText("CAPTURING...");
            stopCaptureButton.setVisible(true);
            stopCaptureButton.setDisable(false);
            number = ListNetworkInterfaces2();
            running.set(true);

            CaptureThread captureThread = new CaptureThread(textAreaOutput, textAreaInfo,
                    interfaceDevice, errbuf, number, this);
            captureThread.run();

//            MainSnifferController main = new MainSnifferController(textAreaOutput, textAreaInfo,
//                    interfaceDevice, errbuf, number, this);
//            main.showTrans();
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public void StopCapturePacket() {
        running.set(false);
        startCaptureButton.setDisable(false);
        stopCaptureButton.setDisable(true);
        startCaptureButton.setText("START");
        listNetworkDevices.setDisable(false);

        textAreaInfo.appendText("\n>>> Zatrzynano przechwytywanie");
        System.out.println("\nZatrzynano przechwytywanie");


        // INFORMACJA O ZATRZYMANIU PRZECHWYTYWANIA
        JOptionPane.showMessageDialog(this, "Zatrzymano przechwytywanie pakietów", "INFORMATION MESSAGE", JOptionPane.WARNING_MESSAGE);

    }

    @FXML
    private void initialize() {
        textAreaInfo.appendText( "Witam!\nProszę wybrać urządzenie z listy, aby rozpocząć przechwytywanie pakietów.\n");
        loadNameDevices();
        stopCaptureButton.setVisible(false);


    }

    // label (X) - zamknięcie całej aplikacji
    @FXML
    public void handleClose(MouseEvent dragEvent) {
        System.exit(0);
    }

    // button - powrót do menu
    @FXML
    public void backToMenu(ActionEvent actionEvent) {
        mainController.loadMenuWindow();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


}

