package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.jnetpcap.PcapIf;

import java.util.List;

public class CaptureThread extends Thread {

    StringBuilder errbuf;
    List<PcapIf> interfaceDevice;
    VisualisationController visualisationController;

    @FXML
    private TextArea textAreaOutput;

    @FXML
    private TextArea textAreaShow;

    int number;

    public CaptureThread(TextArea textAreaOutput,
                         TextArea textAreaShow,
                         List<PcapIf> interfaceDevice,
                         StringBuilder errbuf,
                         int number,
                         VisualisationController visualisationController) {
        this.textAreaOutput = textAreaOutput;
        this.textAreaShow = textAreaShow;
        this.errbuf = errbuf;
        this.interfaceDevice = interfaceDevice;
        this.number = number;

        this.visualisationController = visualisationController;

    }

    public void run() {
        CaptureTask captureTask = new CaptureTask(textAreaOutput, textAreaShow,
                interfaceDevice, errbuf, number, visualisationController, this);
        Platform.runLater(() -> captureTask.run());

    }
}
