package controller;

import controller.Threads.Threads;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VisualisationController extends Component {

    StringBuilder errbuf = new StringBuilder();
    List<PcapIf> interfaceDevice = new ArrayList<PcapIf>();
    ObservableList<String> networkName;
    Threads thread;
    Task task;
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
    public TextArea textAreaOutput;

    @FXML
    private TextArea textAreaInfo;

    @FXML
    private TextField amountPacket;

    @FXML
    private Button listNetworkDevices;
    //-------------------------------------
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
    public synchronized void StartCapturePacket() throws IOException, InterruptedException {
        if (number == 7) {
            // INFORMACJA O NIE WYBRANIU DEVICE NETWORK
            JOptionPane.showMessageDialog(this, "Please, select your network devices.", "INFORMATION MESSAGE", JOptionPane.INFORMATION_MESSAGE);

        } else {
            if (amountPacket.getText().equals("")) {
                // INFORMACJA O NIE WPISANIU LICZBY PAKIETOW DO PRZECHWYCENIA
                JOptionPane.showMessageDialog(this, "Please, enter the number of packets to capture.", "INFORMATION MESSAGE", JOptionPane.INFORMATION_MESSAGE);

            } else {
                textAreaInfo.setText(" ");
                textAreaOutput.setText(" ");
                startCaptureButton.setDisable(true);
                listNetworkDevices.setDisable(true);
                //TODO dodać label STATUS
//                startCaptureButton.setText("CAPTURING...");
                stopCaptureButton.setVisible(true);
                stopCaptureButton.setDisable(false);
                number = ListNetworkInterfaces2();
                running.set(true);
                thread = new Threads(textAreaOutput, textAreaInfo, amountPacket,
                        interfaceDevice, errbuf, number, this);
                thread.start();
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public synchronized void StopCapturePacket() throws InterruptedException {
        running.set(false);
        startCaptureButton.setDisable(false);
        stopCaptureButton.setDisable(true);
//        startCaptureButton.setText("START");
        listNetworkDevices.setDisable(false);

        textAreaInfo.appendText("\n>>> Zatrzynano przechwytywanie");
        System.out.println("\nZatrzynano przechwytywanie");
        thread.stopAnimationTimer();
        // INFORMACJA O ZATRZYMANIU PRZECHWYTYWANIA
        JOptionPane.showMessageDialog(this, "Zatrzymano przechwytywanie pakietów", "INFORMATION MESSAGE", JOptionPane.INFORMATION_MESSAGE);

    }

    @FXML
    private void initialize() {
        textAreaInfo.appendText("Witam!\nProszę wybrać urządzenie z listy, aby rozpocząć przechwytywanie pakietów.\n");
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


    public void savePackets(ActionEvent actionEvent) {

        String packetCapture = textAreaOutput.getText();

        try {
            PrintStream out = new PrintStream(
                    new FileOutputStream("PacketCapture.txt"));

            out.println(packetCapture);

            out.close();

            JOptionPane.showMessageDialog(null, "Packet SAVED.", "INFORMATION MESSAGE", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ERROR! Could not SAVE packets.", "INFORMATION MESSAGE", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void loadPackets(ActionEvent actionEvent) {
        String packetCapture;

        try {
            BufferedReader in = new BufferedReader(
                    new FileReader("PacketCapture.txt"));

            while ((packetCapture = in.readLine()) != null) {
                textAreaOutput.appendText(packetCapture + "\n");
            }
            in.close();

            JOptionPane.showMessageDialog(null, "Packets LOADED.", "INFORMATION MESSAGE", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ERROR! Could not LOAD packets.", "INFORMATION MESSAGE", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

