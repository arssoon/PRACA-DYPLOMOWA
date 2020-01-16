
//------------------------------------------------------------------------------------------------------------------
//										Praca dyplomowa - CaptureTask klasa
//------------------------------------------------------------------------------------------------------------------
package controller.Threads;

import controller.VisualisationController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.jnetpcap.PcapIf;

import java.time.LocalTime;
import java.util.List;

public class CaptureTask extends Task {

    StringBuilder errbuf;
    List<PcapIf> interfaceDevice;
    VisualisationController visualisationController;
    CaptureThread captureThread;
    LocalTime localTime = LocalTime.now();

    private TextArea textAreaOutput;

    private TextArea textAreaInfo;

    @FXML
    private TextField amountPacket;

    int number;

    public CaptureTask(TextArea textAreaOutput,
                       TextArea textAreaInfo,
                       TextField amountPacket,
                       List<PcapIf> interfaceDevice,
                       StringBuilder errbuf,
                       int number,
                       VisualisationController visualisationController,
                       CaptureThread captureThread
    ) {
        this.textAreaOutput = textAreaOutput;
        this.textAreaInfo = textAreaInfo;
        this.amountPacket = amountPacket;
        this.errbuf = errbuf;
        this.interfaceDevice = interfaceDevice;
        this.number = number;
        this.visualisationController = visualisationController;
        this.captureThread = captureThread;
    }

    @Override
    public Void call() {
//            textAreaInfo.appendText("\n\n>>> Start capturing...\n");
//            String info = "";
//            Pcap pcap;
//            try {
//                int snaplen = 64 * 1024;           // Capture all packets, no trucation
//                int flags = Pcap.MODE_NON_PROMISCUOUS; // capture all packets
//                int timeout = 10 * 1000;           // 10 seconds in millis
//
//                //Open the selected device to capture packets
//                pcap = Pcap.openLive(interfaceDevice.get(number).getName(),
//                        snaplen,
//                        flags,
//                        timeout,
//                        errbuf
//                );
//
//
//                if (pcap == null) {
//                    System.err.printf("Error while opening device for capture: "
//                            + errbuf.toString());
//                }
//                System.out.println("device opened");
//                textAreaInfo.appendText("\ndevice opened\n");
//
//                //Create packet handler which will receive packets
//                pcap.loop(10, new PcapPacketHandler() {
//
//                    Tcp tcp = new Tcp();
//
//                    @Override
//                    public void nextPacket(PcapPacket packet, Object t) {
//                        if (packet.hasHeader(tcp)) {
//                            System.out.println("Packet TCP:" + tcp.getPacket());
//                            visualisationController.textAreaOutput.appendText("\n--------------------------------------------------------------------------------\n" +
//                                    " " + localTime.getHour() + ":" + localTime.getMinute() + ":" + localTime.getSecond() +
//                                    " >>>> Packet TCP " + tcp.getPacket().getFrameNumber() +
//                                    "\n--------------------------------------------------------------------------------" +
//                                    tcp.getPacket()
//                            );
//
//                        }
//                    }
//
//                }, errbuf);
//
//                pcap.close();
//                visualisationController.StopCapturePacket();
//            } catch (Exception ex) {
//                System.out.println("Wyjątek w klasie : " + this.getClass() + "\n i miejscu " +
//                        ". \nInformacja dla admina: \n" + ex.getMessage());
//
//                try {
//                    visualisationController.StopCapturePacket();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
            return null;
        }
}

