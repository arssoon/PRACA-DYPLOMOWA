package controller.Threads;

import controller.VisualisationController;
import javafx.animation.AnimationTimer;
import javafx.scene.control.TextArea;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.time.LocalTime;

public class CaptureThread extends Thread {
    LocalTime localTime = LocalTime.now();
    StringBuilder errbuf;
    VisualisationController vController;
    private AnimationTimer timer;
    private final TextArea textAreaInfo;
    private TextArea textAreaOutput;
    int amountPacket;
    String nameDevice;
    String tcpOut = "";


    public CaptureThread(TextArea textAreaOutput,
                         TextArea textAreaInfo,
                         int amountPacket,
                         StringBuilder errbuf,
                         String nameDevice,
                         VisualisationController vController) {
        this.textAreaOutput = textAreaOutput;
        this.textAreaInfo = textAreaInfo;
        this.amountPacket = amountPacket;
        this.errbuf = errbuf;
        this.nameDevice = nameDevice;
        this.vController = vController;

    }

    @Override
    public void run() {
        textAreaInfo.appendText("\n\n>>> Start capturing...\n");
        Pcap pcap;
        int snaplen = 64 * 1024;           // Capture all packets, no trucation
        int flags = Pcap.MODE_NON_PROMISCUOUS; // capture all packets
        int timeout = 10 * 1000;           // 10 seconds in millis
        //Open the selected device to capture packets
        try {
            //TODO ustaw wszystko na default
            pcap = Pcap.openLive(nameDevice,
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

            timer  = new AnimationTimer() {
                @Override
                public void handle(long l) {
//                     textAreaOutput.setText(tcpOut);
                    textAreaOutput.caretPositionProperty();
                }
            };

            PcapBpfProgram filter = new PcapBpfProgram();
            String expression = "port 443";
            int optimize = 0; // 1 means true, 0 means false
            int netmask = 0;

            int r = pcap.compile(filter, expression, optimize, netmask);
            if (r != Pcap.OK) {
                System.out.println("Filter error: " + pcap.getErr());
            }
//            pcap.setFilter(filter);

            timer.start();
            //Create packet handler which will receive packets
            pcap.loop(amountPacket, new PcapPacketHandler() {
                Udp udp;
                Tcp tcp;
                Http http;
                @Override
                public void nextPacket(PcapPacket packet, Object t) {
                    tcp = new Tcp();
                    udp = new Udp();
                    http = new Http();
//                    if (packet.hasHeader(tcp)) {
//                        textAreaInfo.setText(String.valueOf(vController.progress.incrementAndGet()));
//
//                        //TODO zmienic ------ setText --- na --- appendText ---
//                        textAreaOutput.setText("\n--------------------------------------------------------------------------------\n" +
//                                " " + localTime.getHour() + ":" + localTime.getMinute() + ":" + localTime.getSecond() +
//                                " >>>> Packet TCP " + tcp.getPacket().getFrameNumber() +
//                                "\n--------------------------------------------------------------------------------" +
//                                //FRAME:
//                                "\nNumer: " + tcp.getPacket().getFrameNumber() + "\n" +
//                                "TimeMilis: " + (tcp.getPacket().getCaptureHeader().timestampInMillis() / (1000 * 60*60)+1) % 24 +":"+
//                                (tcp.getPacket().getCaptureHeader().timestampInMillis() / (1000 * 60)) % 60 +":" +
//                                (tcp.getPacket().getCaptureHeader().timestampInMillis() / 1000 ) % 60 +":" +
//                                (tcp.getPacket().getCaptureHeader().timestampInMillis()% 1000) + "\n" +
//                                "Caplen: " + tcp.getPacket().getCaptureHeader().caplen() + "\n" +
//                                "Wiren: " + tcp.getPacket().getCaptureHeader().wirelen() + "\n" +
//                                "Pcaket: " + tcp.getPacket()
//
//                        );
//                    } else
//                        if (packet.hasHeader(pcap.setFilter(filter))) {
//                        System.out.println("Packet FILTER:" + pcap.setFilter(filter));
//
//                    } else if (packet.hasHeader(http)) {
//                            textAreaOutput.setText("Packet HTTP:" + http.getPacket());
//
//                        }

//                    if( packet.hasHeader(tcp))
//                    {
//                        if (tcp.source() == 80) {
//                            System.out.println("HTTP protocol" + tcp.getPacket());
//                        }
//                    }else
                        if( packet.hasHeader(udp))
                    {
                        if ((udp.source() == 53) || (udp.destination() == 53)) {
                            textAreaOutput.appendText("HTTP protocol" + udp.getPacket());
                        }
                    }

                    if(amountPacket != 10)
                    try {
                        sleep(80);
                    } catch (Exception ex) {
                        System.out.println("Sleep: " + ex.getMessage());
                    }
                    if(vController.running.get() == false) {
                        try {
                            join();
                        } catch (InterruptedException e) {
                            textAreaInfo.appendText("Catch: " +  e.getMessage());
                        }

                    }

                }

            }, errbuf);
            pcap.close();

        } catch (Exception ex) {
            System.out.println("Wyjątek w klasie : " + this.getClass() + "\n i miejscu " +
                    ". \nInformacja dla admina: \n" + ex.getMessage());

            vController.action_StopCapturePacket();
        }
        vController.action_StopCapturePacket();
    }

    public void stopAnimationTimer() {
        timer.stop();
    }
}
