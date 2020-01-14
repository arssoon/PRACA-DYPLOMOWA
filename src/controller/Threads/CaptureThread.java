package controller.Threads;

import controller.VisualisationController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.jnetpcap.PcapIf;

import java.util.List;

public class CaptureThread implements Runnable {

    StringBuilder errbuf;
    List<PcapIf> interfaceDevice;
    VisualisationController visualisationController;

    @FXML
    private TextArea textAreaOutput;

    @FXML
    private TextArea textAreaInfo;

    int number;

    public CaptureThread(TextArea textAreaOutput,
                         TextArea textAreaInfo,
                         List<PcapIf> interfaceDevice,
                         StringBuilder errbuf,
                         int number,
                         VisualisationController visualisationController) {
        this.textAreaOutput = textAreaOutput;
        this.errbuf = errbuf;
        this.interfaceDevice = interfaceDevice;
        this.number = number;
        this.textAreaInfo = textAreaInfo;

        this.visualisationController = visualisationController;
    }

    public void run() {
        CaptureTask captureTask = new CaptureTask(textAreaOutput, textAreaInfo,
                interfaceDevice, errbuf, number, visualisationController, this);

        captureTask.run();

//        CaptureThread2 captureTask = new CaptureThread2(textAreaOutput, textAreaInfo,
//                interfaceDevice, errbuf, number, visualisationController, this);
//
//        captureTask.setDaemon(true);
//        Platform.runLater(() -> captureTask.start());
    }
}
