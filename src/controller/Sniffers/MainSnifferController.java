package controller.Sniffers;

import controller.VisualisationController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.jnetpcap.PcapIf;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.util.List;

public class MainSnifferController {


    private static StringBuilder errbuf;

    List<PcapIf> interfaceDevice;
    VisualisationController visualisationController;

    @FXML
    private TextArea textAreaOutput;

    @FXML
    private TextArea textAreaInfo;

    int number;

    public MainSnifferController(TextArea textAreaOutput,
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

    static PcapNetworkInterface getNetworkDevice() {
            PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;
    }
    @FXML
    public void showTrans() throws IOException {
//        PcapNetworkInterface device = getNetworkDevice();
//        System.out.println("You chose: " + getNetworkDevice());
//
//        // New code below here
//        if (device == null) {
//            System.out.println("No device chosen.");
//            System.exit(1);
//        }
//        Task<Void> task = new Task<Void>() {
//            public Void call() throws Exception {
//
//// Open the device and get a handle
//                int snapshotLength = 65536; // in bytes
//                int readTimeout = 50; // in milliseconds
//                final PcapHandle handle;
//
//                return null;
//            }
//        };
//
//        Thread thread = new Thread(task);
//        thread.start();

    }

    private static void printPacket(Packet packet, PcapHandle ph) {
        StringBuilder sb = new StringBuilder();
        sb.append("A packet captured at ")
                .append(ph.getTimestamp())
                .append(":");
        System.out.println(sb);
        System.out.println(packet);
    }

}
