package controller.Threads;

import controller.VisualisationController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.jnetpcap.PcapIf;

import java.util.List;

public class CaptureThread implements Runnable {

    StringBuilder errbuf;
    List<PcapIf> interfaceDevice;
    VisualisationController visualisationController;

    @FXML
    private TextArea textAreaOutput;

    @FXML
    private TextField amountPacket;

    @FXML
    private TextArea textAreaInfo;

    int number;

    public CaptureThread(TextArea textAreaOutput,
                         TextArea textAreaInfo,
                         TextField amountPacket,
                         List<PcapIf> interfaceDevice,
                         StringBuilder errbuf,
                         int number,
                         VisualisationController visualisationController) {
        this.textAreaOutput = textAreaOutput;
        this.amountPacket = amountPacket;
        this.errbuf = errbuf;
        this.interfaceDevice = interfaceDevice;
        this.number = number;
        this.textAreaInfo = textAreaInfo;

        this.visualisationController = visualisationController;
    }

    public void run() {
        CaptureTask captureTask = new CaptureTask(textAreaOutput, textAreaInfo, amountPacket,
                interfaceDevice, errbuf, number, visualisationController, this);

        Platform.runLater(() -> captureTask.run());

//        CaptureThread2 captureTask = new CaptureThread2(textAreaOutput, textAreaInfo,
//                interfaceDevice, errbuf, number, visualisationController, this);
//
//        captureTask.setDaemon(true);
//        Platform.runLater(() -> captureTask.start());
    }

}
