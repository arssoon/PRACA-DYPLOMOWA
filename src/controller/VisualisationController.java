package controller;

import controller.Threads.CaptureThread;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import org.jnetpcap.PcapIf;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class VisualisationController extends Component {

    StringBuilder errbuf;
    List<PcapIf> interfaceDevice;
    CaptureThread captureThread;
    int number;
    public AtomicBoolean running;
    int amountPacket;

    @FXML
    private MainController mainController;
    // List network devices
    @FXML
    public TextArea textAreaOutput;
    @FXML
    private TextArea textAreaInfo;
    @FXML
    private TextField show;
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
    @FXML
    private Button savePacketCapture;
    @FXML
    private Button loadPacketCapture;

    //filtry
    @FXML
    private ToggleButton enableButton;
    @FXML
    private ToggleButton disableButton;
    @FXML
    private Label filtersLabel;
    @FXML
    private ToggleButton smtpButton;
    @FXML
    private ToggleButton icmpButton;
    @FXML
    private ToggleButton dnsButton;
    @FXML
    private ToggleButton udpButton;
    @FXML
    private ToggleButton tcpButton;
    @FXML
    private ToggleButton httpButton;
    @FXML
    private Label amountLabel;

//--------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------- METODY   -----------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------


    // ---------------    KONTRUKTOR  --------------------------------------------------------------------------------
    public VisualisationController() {
        running = new AtomicBoolean(false);
        interfaceDevice = new ArrayList<PcapIf>();
        errbuf = new StringBuilder();

    }

    //------------------------------------------------------------------------------------------------------------------
    public synchronized void action_StartCapturePacket() {

        if (number == 7) {
            // INFORMACJA O NIE WYBRANIU DEVICE NETWORK
            JOptionPane.showMessageDialog(this, "Please, select your network devices.", "INFORMATION MESSAGE", JOptionPane.INFORMATION_MESSAGE);

        } else {
                textAreaInfo.setText(" ");
                textAreaOutput.setText(" ");
                startCaptureButton.setDisable(true);
                listNetworkDevices.setDisable(true);
                //TODO dodać label STATUS
//                startCaptureButton.setText("CAPTURING...");
                stopCaptureButton.setVisible(true);
                stopCaptureButton.setDisable(false);
                running.set(true);
                captureThread = new CaptureThread(textAreaOutput, textAreaInfo, amountPacket,
                        interfaceDevice, errbuf, number, this);
                captureThread.start();

        }
    }

    //------------------------------------------------------------------------------------------------------------------
    public synchronized void action_StopCapturePacket() throws InterruptedException {
        running.set(false);
        startCaptureButton.setDisable(false);
        stopCaptureButton.setDisable(true);
//        startCaptureButton.setText("START");
        listNetworkDevices.setDisable(false);

        textAreaInfo.appendText("\n>>> Zatrzynano przechwytywanie");
        System.out.println("\nZatrzynano przechwytywanie");
        captureThread.stopAnimationTimer();
        // INFORMACJA O ZATRZYMANIU PRZECHWYTYWANIA
        JOptionPane.showMessageDialog(this, "Zatrzymano przechwytywanie pakietów", "INFORMATION MESSAGE", JOptionPane.INFORMATION_MESSAGE);

    }

    @FXML
    private void initialize() {
        textAreaInfo.appendText("Witam!\nProszę wybrać urządzenie z listy, aby rozpocząć przechwytywanie pakietów.\n");
        stopCaptureButton.setVisible(false);


    }

    // label (X) - zamknięcie całej aplikacji
    @FXML
    public void mouse_handleClose(MouseEvent dragEvent) {
        System.exit(0);
    }

    // button - powrót do menu
    @FXML
    public void action_backToMenu(ActionEvent actionEvent) {

        mainController.loadMenuWindow();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void action_savePackets(ActionEvent actionEvent) {
        show.setText(String.valueOf(amountPacket));
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

    public void action_loadPackets(ActionEvent actionEvent) {
        System.out.println("Twoj szeczesliwy nr to: " + amountPacket);
        System.out.println("Numer urzadzenia: " + number);
        System.out.println("Interface: " + interfaceDevice.get(number).getName());
        System.out.println("ErrorBuf: " + errbuf.toString());

//        String packetCapture;
//
//        try {
//            BufferedReader in = new BufferedReader(
//                    new FileReader("PacketCapture.txt"));
//
//            while ((packetCapture = in.readLine()) != null) {
//                textAreaOutput.appendText(packetCapture + "\n");
//            }
//            in.close();
//
//            JOptionPane.showMessageDialog(null, "Packets LOADED.", "INFORMATION MESSAGE", JOptionPane.INFORMATION_MESSAGE);
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(null, "ERROR! Could not LOAD packets.", "INFORMATION MESSAGE", JOptionPane.INFORMATION_MESSAGE);
//        }
    }

//--------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------- SETTERY   -----------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------
    public void setAmountPacket(int amountPacket) {
        this.amountPacket = amountPacket;
    }

    public void setErrbuf(StringBuilder errbuf) {
        this.errbuf = errbuf;
    }

    public void setInterfaceDevice(List<PcapIf> interfaceDevice) {
        this.interfaceDevice = interfaceDevice;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}


//    private void disableAll() {
//        startCaptureButton.setVisible(false);
//        stopCaptureButton.setVisible(false);
//        amountPacket.setVisible(false);
//        savePacketCapture.setVisible(false);
//        loadPacketCapture.setVisible(false);
//
//        //filters BUTTON
//        enableButton.setVisible(false);
//        disableButton.setVisible(false);
//        httpButton.setVisible(false);
//        tcpButton.setVisible(false);
//        udpButton.setVisible(false);
//        dnsButton.setVisible(false);
//        icmpButton.setVisible(false);
//        smtpButton.setVisible(false);
//
//        //labels
//        filtersLabel.setVisible(false);
//        amountLabel.setVisible(false);
//
//        textAreaOutput.setVisible(false);
//        textAreaInfo.setVisible(false);
//    }

