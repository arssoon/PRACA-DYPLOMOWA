package controller;

import controller.Threads.CaptureThread;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.jnetpcap.PcapIf;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class VisualisationController extends Component {

    MainController mainController;
    StringBuilder errbuf;
    List<PcapIf> interfaceDevice;
    CaptureThread captureThread;
    int numberChoose;
    public AtomicBoolean running;
    public final AtomicInteger progress;
    int amountPacket;
    String nameInterface;

    @FXML
    private Button clearTextButton;
    @FXML
    private TextArea textAreaOutput;
    @FXML
    private TextArea textAreaInfo;
    @FXML
    private TextField statusText;
    // Start & stop
    @FXML
    private Button startCaptureButton;
    @FXML
    private Button stopCaptureButton;
    @FXML
    private Button backButton;
    @FXML
    private Button backToMenuButton;
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

    //--------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------- METODY   -----------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------

    // ---------------    KONTRUKTOR  --------------------------------------------------------------------------------
    public VisualisationController() {
        this.progress = new AtomicInteger(0);
        running = new AtomicBoolean(false);
        interfaceDevice = new ArrayList<PcapIf>();
        errbuf = new StringBuilder();
    }

    //------------------------------------------------------------------------------------------------------------------
    public synchronized void action_StartCapturePacket(ActionEvent actionEvent) {
        textAreaInfo.setText(" ");
        textAreaOutput.setText(" ");
        startCaptureButton.setDisable(true);
        stopCaptureButton.setVisible(true);
        stopCaptureButton.setDisable(false);

        running.set(true);
        captureThread = new CaptureThread(textAreaOutput, textAreaInfo, amountPacket, errbuf, nameInterface, this);
        captureThread.start();

        statusText.clear();
        statusText.setPromptText(" CAPTURING... ");

    }

    //------------------------------------------------------------------------------------------------------------------
    public synchronized void action_StopCapturePacket() {
        running.set(false);

        startCaptureButton.setDisable(false);
        stopCaptureButton.setDisable(true);
        textAreaInfo.appendText("\n>>> Zatrzynano przechwytywanie");
        statusText.clear();
        statusText.setPromptText(" CAPTURE STOPPED ");
        captureThread.stopAnimationTimer();

        // INFORMACJA O ZATRZYMANIU PRZECHWYTYWANIA
        JOptionPane.showMessageDialog(this, "Capture of packets is STOPPED.", "INFORMATION MESSAGE", JOptionPane.INFORMATION_MESSAGE);

    }


    //----------------  metoda nasłuchująca pole TextField jaką user wpisał wartość    ----------------------------------
    public void listener_clearText() {
        textAreaOutput.textProperty().addListener((observable) -> {
                    if (textAreaOutput.getText().equals(" ")) {
                        clearTextButton.setVisible(false);
                    } else {
                        clearTextButton.setVisible(true);
                    }
                }
        );
    }

    //------------------------------------------------------------------------------------------------------------------
    public void action_savePackets(ActionEvent actionEvent) {
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

    //------------------------------------------------------------------------------------------------------------------
    public void action_loadPackets(ActionEvent actionEvent) {
        String packetCapture;

        try {
            BufferedReader in = new BufferedReader(new FileReader("PacketCapture.txt"));
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

    //------------------------------------------------------------------------------------------------------------------
    @FXML
    private void initialize() {
        listener_clearText();
        disableAll();
    }

    //------------------------------------------------------------------------------------------------------------------
    private void disableAll() {
        clearTextButton.setVisible(false);
        stopCaptureButton.setVisible(false);
        loadPacketCapture.setDisable(true);

        //filters BUTTON
        disableButton.setDisable(true);
        httpButton.setDisable(true);
        tcpButton.setDisable(true);
        udpButton.setDisable(true);
        dnsButton.setDisable(true);
        icmpButton.setDisable(true);
        smtpButton.setDisable(true);
    }

    //------------------------------------------------------------------------------------------------------------------
    public void action_clearText(ActionEvent actionEvent) {
        textAreaOutput.setText("");
    }

    //------------------------------------------------------------------------------------------------------------------
    // LABEL (X) - zamknięcie całej aplikacji
    @FXML
    public void mouse_handleClose(MouseEvent dragEvent) {
        System.exit(0);
    }

    //------------------------------------------------------------------------------------------------------------------
    // BUTTON - powrót do menu
    @FXML
    public void action_backToMenu(ActionEvent actionEvent) {
        mainController.loadMenuWindow();
    }

    //------------------------------------------------------------------------------------------------------------------
    // BUTTON - powrót do poprzedniego okna
    public void action_backToPreviousWindow(ActionEvent actionEvent) {
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
        numberPacketsController.setNumberChoose(numberChoose);
        numberPacketsController.setErrbuf(errbuf);
        numberPacketsController.setInterfaceDevice(interfaceDevice);
    }

    //--------------------------------------------------------------------------------------------------------------------
//---------------------------------------------------- SETTERY   -----------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    //------------------------------------------------------------------------------------------------------------------
    public void setAmountPacket(int amountPacket) {
        this.amountPacket = amountPacket;
    }

    //------------------------------------------------------------------------------------------------------------------
    public void setErrbuf(StringBuilder errbuf) {
        this.errbuf = errbuf;
    }

    //------------------------------------------------------------------------------------------------------------------
    public void setInterfaceDevice(List<PcapIf> interfaceDevice) {
        this.interfaceDevice = interfaceDevice;
        nameInterface = interfaceDevice.get(numberChoose).getName();
    }

    //------------------------------------------------------------------------------------------------------------------
    public void setNumberChoose(int numberChoose) {
        this.numberChoose = numberChoose;
    }

}
