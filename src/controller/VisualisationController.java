package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;

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
    int number = 0;
    public AtomicBoolean running;

    public VisualisationController() {
        running = new AtomicBoolean(false);
    }

    // List network devices
    @FXML
    private ChoiceBox networkDevicesBox;

    @FXML
    private TextArea textAreaOutput;

    @FXML
    private TextArea textAreaShow;

    @FXML
    private Button listNetworkDevices;
    // Start & stop
    @FXML
    private Button startCaptureButton;

    @FXML
    private Button stopCaptureButton;

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
                "\t\tWybierz urządzenie do przechwytywania...",
                getNameDevices(0),
                getNameDevices(1),
                getNameDevices(2),
                getNameDevices(3),
                getNameDevices(4),
                getNameDevices(5),
                getNameDevices(6),
                getNameDevices(7)
        );

        networkDevicesBox.setItems(networkName);
    }

    //------------------------------------------------------------------------------------------------------------------
    // wyswietlenie wszystkich urzadzen sieciowych dostepnych na komputerze w polu TextArea przez button 'List Network Devices'
    public void ListNetworkInterfaces() {
        if (Pcap.findAllDevs(interfaceDevice, errbuf) != Pcap.OK) {
            throw new IllegalStateException(errbuf.toString());
        }

        for (int i = 0; i <= number; i++) {
            textAreaShow.appendText("\n#" + i + "_Nazwa urzadzenia: " + interfaceDevice.get(i).getName() + " >>>> " +
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
        String myString = networkDevicesBox.getValue().toString();

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
    public void ClickedNetworkDevices() throws IOException {
        number = ListNetworkInterfaces2();
        List<PcapAddr> INT = interfaceDevice.get(number).getAddresses();

        textAreaOutput.appendText("\n------------------------------------------------------- " +
                "\nWybrano urządzenie " + number +
                "\nNazwa : " + interfaceDevice.get(number).getName() +
                "\nID : " + interfaceDevice.get(number).getDescription()+
                "\nIP Address: " + INT.get(0).getAddr()+
                "\nBroadcast Address: " + INT.get(0).getNetmask()
        );
    }

    //------------------------------------------------------------------------------------------------------------------
    public void StartCapturePacket() throws IOException {
        textAreaShow.setText(" ");
        ClickedNetworkDevices();
        number = ListNetworkInterfaces2();
        running.set(true);
        try {
            threadCapture = new CaptureThread(textAreaOutput, textAreaShow,
                    interfaceDevice, errbuf, number, this);

            threadCapture.setDaemon(true);
            Platform.runLater ( () ->threadCapture.start());

        } catch (Exception ex) {
            System.out.println("Wyjątek w klasie : " + this.getClass()+ "\n i metodzie "+ this.getName() +
                    ". \nInformacja dla admina: \n" + ex.getMessage());
            textAreaOutput.appendText("Exceptions:\n" + ex.getMessage());
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public void StopCapturePacket() {
        running.set(false);
//        threadCapture.interrupt();
        textAreaOutput.appendText("\nZatrzynano przechwytywanie");
        System.out.println("\nZatrzynano przechwytywanie");

        // INFORMACJA O ZATRZYMANIU PRZECHWYTYWANIA
        JOptionPane.showMessageDialog(this, "Zatrzymano przechwytywanie pakietów", "INFORMATION MESSAGE", JOptionPane.WARNING_MESSAGE);

    }

    @FXML
    private void initialize() {
        textAreaOutput.appendText("Witam!\nProszę wybrać urządzenie z listy, aby rozpocząć przechwytywanie pakietów.");
        loadNameDevices();
        networkDevicesBox.getSelectionModel().selectFirst();

    }

}

