package controller;

import controller.Threads.CaptureThread;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.jnetpcap.PcapIf;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private TableColumn frameColumnId;
    @FXML
    private TableColumn ethernetColumnId;
    @FXML
    private TableColumn ipColumnId;
    @FXML
    private TableColumn transportColumnId;
    @FXML
    private TableColumn filtersColumnId;
    @FXML
    private TableView<Frame> tableView;
    @FXML
    private TextArea textAreaPacket;
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
    public ToggleButton enableButton;
    @FXML
    public ToggleButton disableButton;
    @FXML
    public ToggleButton httpButton;
    @FXML
    public ToggleButton icmpButton;
    @FXML
    public ToggleButton httpSslButton;
    @FXML
    public ToggleButton popButton;
    @FXML
    public ToggleButton dnsButton;
    @FXML
    public ToggleButton ftpButton;
    @FXML
    public ToggleButton smtpButton;
    @FXML
    public ToggleButton arpButton;
    @FXML
    public ToggleGroup filtersGroup;
    //dodatkowe
    ToggleButton nowSelectedButton;
    ToggleButton oldSelectedButton;
    //--------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------- METODY   -----------------------------------------------------
//--------------------------------------------------------------------------------------------------------------------

    // ---------------    KONTRUKTOR  --------------------------------------------------------------------------------
    public VisualisationController() {
        this.progress = new AtomicInteger(0);
        running = new AtomicBoolean(false);
        interfaceDevice = new ArrayList<PcapIf>();
        errbuf = new StringBuilder();
        nowSelectedButton = new ToggleButton();
        oldSelectedButton = new ToggleButton();

    }

    //------------------------------------------------------------------------------------------------------------------
    public synchronized void action_StartCapturePacket(ActionEvent actionEvent) throws FileNotFoundException {
        startCaptureButton.setDisable(true);
        stopCaptureButton.setVisible(true);
        stopCaptureButton.setDisable(false);

        running.set(true);
        captureThread = new CaptureThread(tableView, frameColumnId, ethernetColumnId,
                ipColumnId, transportColumnId, filtersColumnId,
                textAreaPacket, amountPacket, errbuf, nameInterface, this);
        Platform.runLater(() -> captureThread.start());

        statusText.clear();
        statusText.setPromptText(" PRZECHWYTYWANIE... ");
    }

    //------------------------------------------------------------------------------------------------------------------
    public synchronized void action_StopCapturePacket() {
        running.set(false);
        startCaptureButton.setDisable(false);
        stopCaptureButton.setDisable(true);

        statusText.clear();
        statusText.setPromptText(" ZATRZYMANO ");
        captureThread.interrupt();

    }


    //----------------  metoda nasłuchująca pole TextField jaką user wpisał wartość    ----------------------------------
    public void listener_clearText() {
        tableView.getItems().addListener((ListChangeListener<Frame>) observable -> {
            if (tableView.getItems().isEmpty()) {
                clearTextButton.setVisible(false);
                savePacketCapture.setDisable(true);
            } else {
                clearTextButton.setVisible(true);
                savePacketCapture.setDisable(false);
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------
    @FXML
    private void initialize() {
        clearTextButton.setVisible(false);
        stopCaptureButton.setDisable(true);
        savePacketCapture.setDisable(true);

        listener_filtersButton();
        listener_clearText();
        disableButtonFilter();

    }

    //------------------------------------------------------------------------------------------------------------------
    public void action_enableButtonFilter(ActionEvent actionEvent) {
        enableButtonFilter();
    }

    //------------------------------------------------------------------------------------------------------------------
    public void action_disableButtonFilter(ActionEvent actionEvent) {
        disableButtonFilter();

    }

    //------------------------------------------------------------------------------------------------------------------
    private void disableButtonFilter() {
        enableButton.setDisable(false);
        disableButton.setDisable(true);
        httpButton.setDisable(true);
        httpSslButton.setDisable(true);
        dnsButton.setDisable(true);
        icmpButton.setDisable(true);
        smtpButton.setDisable(true);
        popButton.setDisable(true);
        ftpButton.setDisable(true);
        arpButton.setDisable(true);

    }

    //------------------------------------------------------------------------------------------------------------------
    private void enableButtonFilter() {
        enableButton.setDisable(true);
        disableButton.setDisable(false);
        httpButton.setDisable(false);
        dnsButton.setDisable(false);
        icmpButton.setDisable(false);
        smtpButton.setDisable(false);
        httpSslButton.setDisable(false);
        popButton.setDisable(false);
        arpButton.setDisable(false);
        ftpButton.setDisable(false);
    }

    //--------  nasłuchiwanie aktywnego ToggleButtona w filtrach    ---------------------------------
    public void listener_filtersButton() {
        filtersGroup.selectedToggleProperty().addListener((observable) -> {
                    if (filtersGroup.getSelectedToggle().isSelected()) {
                        nowSelectedButton = (ToggleButton) filtersGroup.getSelectedToggle();
                        nowSelectedButton.setDisable(true);
                        oldSelectedButton.setDisable(false);
                        oldSelectedButton = (ToggleButton) filtersGroup.getSelectedToggle();
                    }

                }
        );
    }

    //------------------------------------------------------------------------------------------------------------------
    public void action_clearText(ActionEvent actionEvent) {
        tableView.getItems().clear();

    }

    //------------------------------------------------------------------------------------------------------------------
    // LABEL (X) - zamknięcie całej aplikacji
    @FXML
    public void mouse_handleClose(MouseEvent dragEvent) {
        System.exit(ABORT);
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
//        if(amountPacket > 0) {
//            textAreaPacket.setText("\t    CEL: " + amountPacket);
//        }
//        else {
//            textAreaPacket.setText("Przechwytywanie NA ŻYWO");
//        }
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
