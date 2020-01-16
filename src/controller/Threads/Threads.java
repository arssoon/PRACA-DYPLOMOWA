package controller.Threads;

import controller.VisualisationController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.time.LocalTime;
import java.util.List;

public class Threads extends Thread {
    StringBuilder errbuf;
    List<PcapIf> interfaceDevice;
    VisualisationController visualisationController;
    LocalTime localTime = LocalTime.now();

    private TextArea textAreaOutput;

    @FXML
    private TextField amountPacket;

    @FXML
    private TextArea textAreaInfo;

    int number;

    public Threads(TextArea textAreaOutput,
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

    @Override
    public void run() {
        textAreaInfo.appendText("\n\n>>> Start capturing...\n");
        String info = "";
//        Pcap pcap;
        int snaplen = 64 * 1024;           // Capture all packets, no trucation
        int flags = Pcap.MODE_NON_PROMISCUOUS; // capture all packets
        int timeout = 10 * 1000;           // 10 seconds in millis
        String device = interfaceDevice.get(number).getName();
        //Open the selected device to capture packets
        Pcap pcap;


        try {
            pcap = Pcap.openLive(device,
                    snaplen,
                    flags,
                    timeout,
                    errbuf
            );
            if (pcap == null) {
                System.err.printf("Error while opening device for capture: "
                        + errbuf.toString());
            }
            System.out.println("device opened");
            textAreaInfo.appendText("\ndevice opened\n");

            //Create packet handler which will receive packets
            pcap.loop(Integer.parseInt(amountPacket.getText()), new PcapPacketHandler() {

                Tcp tcp = new Tcp();

                @Override
                public void nextPacket(PcapPacket packet, Object t) {
                    if (packet.hasHeader(tcp)) {
                        System.out.println("Packet TCP:" + tcp.getPacket());

                        visualisationController.textAreaOutput.appendText("\n--------------------------------------------------------------------------------\n" +
                                " " + localTime.getHour() + ":" + localTime.getMinute() + ":" + localTime.getSecond() +
                                " >>>> Packet TCP " + tcp.getPacket().getFrameNumber() +
                                "\n--------------------------------------------------------------------------------" +
                                //FRAME:
                                "\nNumer: " + tcp.getPacket().getFrameNumber() + "\n" +
                                "TimeMilis: " + (tcp.getPacket().getCaptureHeader().timestampInMillis() / (1000 * 60*60)+1) % 24 +":"+
                                (tcp.getPacket().getCaptureHeader().timestampInMillis() / (1000 * 60)) % 60 +":" +
                                (tcp.getPacket().getCaptureHeader().timestampInMillis() / 1000 ) % 60 +":" +
                                (tcp.getPacket().getCaptureHeader().timestampInMillis()% 1000) + "\n" +
                                "Caplen: " + tcp.getPacket().getCaptureHeader().caplen() + "\n" +
                                "Wiren: " + tcp.getPacket().getCaptureHeader().wirelen() + "\n" +
                                "Pcaket: " + tcp.getPacket()

                        );

                    }
                    try {
                        sleep(1000);
                    } catch (Exception ex) {
                        System.out.println("Sleep: " + ex.getMessage());
                    }
                    if(visualisationController.running.get() == false) {
                        try {
                            join();
                        } catch (InterruptedException e) {
                            textAreaInfo.appendText("Catch: " +  e.getMessage());
                        }
                        textAreaInfo.setText("PAPAP\n");
                    }
                }

            }, errbuf);

            pcap.close();
        } catch (Exception ex) {
            System.out.println("Wyjątek w klasie : " + this.getClass() + "\n i miejscu " +
                    ". \nInformacja dla admina: \n" + ex.getMessage());

            try {
                visualisationController.StopCapturePacket();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            visualisationController.StopCapturePacket();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
