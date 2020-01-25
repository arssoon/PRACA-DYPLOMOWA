package controller.Threads;

import controller.VisualisationController;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class CaptureThread extends Thread {
    VisualisationController vController;
    private StringBuilder errbuf;
    //    private AnimationTimer timer;
    private TableColumn frameColumnId;
    private TableColumn ethernetColumnId;
    private TableColumn ipColumnId;
    private TableColumn transportColumnId;
    private TableColumn filtersColumnId;
    private TableView tableView;
    private TextArea textAreaPacket;
    public boolean checkPacketUdp;
    int amountPacket;
    String nameDevice;

    public CaptureThread(TableView tableView, TableColumn frameColumnId,
                         TableColumn ethernetColumnId, TableColumn ipColumnId,
                         TableColumn transportColumnId, TableColumn filtersColumnId,
                         TextArea textAreaPacket, int amountPacket,
                         StringBuilder errbuf, String nameDevice,
                         VisualisationController vController
    ) {
        this.tableView = tableView;
        this.frameColumnId = frameColumnId;
        this.ethernetColumnId = ethernetColumnId;
        this.ipColumnId = ipColumnId;
        this.transportColumnId = transportColumnId;
        this.filtersColumnId = filtersColumnId;
        this.textAreaPacket = textAreaPacket;
        this.amountPacket = amountPacket;
        this.errbuf = errbuf;
        this.nameDevice = nameDevice;
        this.vController = vController;
        this.checkPacketUdp = false;
    }

    @Override
    public void run() {
        Pcap pcap;
        //Open the selected device to capture packets
        try {
            pcap = Pcap.openLive(nameDevice,
                    Pcap.DEFAULT_SNAPLEN,
                    Pcap.DEFAULT_PROMISC,
                    Pcap.DEFAULT_TIMEOUT,
                    errbuf
            );
            if (pcap == null) {
                System.out.println("Error while opening device for capture: " + errbuf.toString());
            }

//            textAreaInfo.appendText("\n>>> Przechwytywanie w TOKU...\n");
            pcap.loop(amountPacket, new PcapPacketHandler() {
                Tcp tcp;
                Udp udp;
                Http http;
                Arp arp;
                Icmp icmp;

                @Override
                public void nextPacket(PcapPacket packet, Object t) {
                    tcp = new Tcp();
                    udp = new Udp();
                    http = new Http();
                    arp = new Arp();
                    icmp = new Icmp();
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    capturePackets(packet, tcp, udp, icmp, arp, http);
                    if(packet.hasHeader(http) ){
                        System.out.println(http);
                    }
                    sleepThread();
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                }
            }, errbuf);
            pcap.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        vController.action_StopCapturePacket();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //-------------------------------  PRZECHWYTYWANIE PAKIETÓW  ------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void capturePackets(PcapPacket pcapPacket, Tcp tcp, Udp udp, Icmp icmp, Arp arp, Http http) {
        try {
            if ((vController.disableButton.isDisabled()) || (vController.filtersGroup.getSelectedToggle() == null)) {
                // ----------------------  TCP  -----------------------------------------------------
                if (pcapPacket.hasHeader(tcp)) {
                    checkPacketUdp = false;
                    savePacketsToFile(pcapPacket, tcp, udp, icmp, arp, http);
                    loadPacketsFromFile(pcapPacket, tcp, udp, icmp, arp, http);
                }
                // ----------------------  UDP  -----------------------------------------------------
                else if (pcapPacket.hasHeader(udp)) {
                    checkPacketUdp = true;
                    savePacketsToFile(pcapPacket, tcp, udp, icmp, arp, http);
                    loadPacketsFromFile(pcapPacket, tcp, udp, icmp, arp, http);
                }
            }
            // ----------------------  Filtrowanie pakietow -----------------------------------------------------
            else {
                packetFilter(pcapPacket, tcp, udp, http, arp, icmp);
                //TODO usuń toooooooooooooo \/
                if (pcapPacket.hasHeader(icmp)) {
                    System.out.println("-------------klasa głowna---------------" + icmp);
                }
                if (pcapPacket.hasHeader(arp)) {
                    System.out.println("-------------klasa głowna---------------" + arp);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------    zapis do pliku  -------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void savePacketsToFile(PcapPacket pcapPacket, Tcp tcp, Udp udp, Icmp icmp, Arp arp, Http http) throws FileNotFoundException {

        String packetCapture = null;
        if (pcapPacket.hasHeader(tcp))
            packetCapture = tcp.getPacket().toString();
        else if (pcapPacket.hasHeader(udp))
            packetCapture = udp.getPacket().toString();
        else if (pcapPacket.hasHeader(icmp))
            packetCapture = icmp.getPacket().toString();
        else if (pcapPacket.hasHeader(arp))
            packetCapture = arp.getPacket().toString();
        else if (pcapPacket.hasHeader(http))
            packetCapture = http.getPacket().toString();

        PrintStream out = new PrintStream(new FileOutputStream("PacketCapture.txt"));
        try {
            out.append(packetCapture);
            for (int i = 0; i < 5; i++) {
                out.println(" ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ERROR! Nie można zapisać pakietów.", "INFORMATION MESSAGE", JOptionPane.ERROR_MESSAGE);
        } finally {
            out.close();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //--------------------------    odczyt z pliku  -------------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void loadPacketsFromFile(PcapPacket pcapPacket, Tcp tcp, Udp udp, Icmp icmp, Arp arp, Http http) {
        new LoadPacketsFromFile( pcapPacket, tcp, udp, icmp, arp, http, frameColumnId,
                ethernetColumnId, ipColumnId, transportColumnId, filtersColumnId,
                tableView, vController, this
        ).invoke();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //--------------------  FILTERS PACKET   ---------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void packetFilter(PcapPacket packet, Tcp tcp, Udp udp, Http http, Arp arp, Icmp icmp) throws
            FileNotFoundException {
        //--------------------  HTTP    ---------------------------------------
        if (vController.filtersGroup.getSelectedToggle() == vController.httpButton) {
            if (packet.hasHeader(http)) {
                savePacketsToFile(packet, tcp, udp, icmp, arp, http);
                loadPacketsFromFile(packet, tcp, udp, icmp, arp, http);
            }
        }
        //--------------------  DNS    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.dnsButton) {
            if (packet.hasHeader(tcp)) {
                if ((tcp.source() == 53) || (tcp.destination() == 53)) {
                    savePacketsToFile(packet, tcp, udp, icmp, arp, http);
                    loadPacketsFromFile(packet, tcp, udp, icmp, arp, http);
                }
            }
        }
        //--------------------  ICMP    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.icmpButton) {
            if (packet.hasHeader(icmp)) {
                savePacketsToFile(packet, tcp, udp, icmp, arp, http);
                loadPacketsFromFile(packet, tcp, udp, icmp, arp, http);
            }
        }
        //--------------------  ARP    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.arpButton) {
            if (packet.hasHeader(arp)) {
                savePacketsToFile(packet, tcp, udp, icmp, arp, http);
                loadPacketsFromFile(packet, tcp, udp, icmp, arp, http);
            }
        }
        //--------------------  HTTP SSL   ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.httpSslButton) {
            if (packet.hasHeader(tcp)) {
                if ((tcp.source() == 443) || (tcp.destination() == 443)) {
                    savePacketsToFile(packet, tcp, udp, icmp, arp, http);
                    loadPacketsFromFile(packet, tcp, udp, icmp, arp, http);
                }
            }
        }
        //--------------------  FTP    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.ftpButton) {
            if (packet.hasHeader(tcp)) {
                if ((tcp.source() == 21) || (tcp.destination() == 21)) {
                    savePacketsToFile(packet, tcp, udp, icmp, arp, http);
                    loadPacketsFromFile(packet, tcp, udp, icmp, arp, http);
                }
            }
        }
        //--------------------  POP3    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.popButton) {
            if (packet.hasHeader(tcp)) {
                if ((tcp.source() == 110) || (tcp.destination() == 110)) {
                    savePacketsToFile(packet, tcp, udp, icmp, arp, http);
                    loadPacketsFromFile(packet, tcp, udp, icmp, arp, http);
                }
            }
        }
        //--------------------  SMTP    ---------------------------------------
        else if (vController.filtersGroup.getSelectedToggle() == vController.smtpButton) {
            if (packet.hasHeader(tcp)) {
                if ((tcp.source() == 25) || (tcp.destination() == 25)) {
                    savePacketsToFile(packet, tcp, udp, icmp, arp, http);
                    loadPacketsFromFile(packet, tcp, udp, icmp, arp, http);
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //----------------------------  Usypianie wątku  ------------------------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void sleepThread() {
        try {
            sleep(200);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!vController.running.get()) {
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}