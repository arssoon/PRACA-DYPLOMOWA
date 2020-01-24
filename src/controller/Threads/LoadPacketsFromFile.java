package controller.Threads;

import controller.TableViewColumn.*;
import controller.VisualisationController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Arp;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class LoadPacketsFromFile {
    VisualisationController vController;
    CaptureThread captureThread;
    private PcapPacket pcapPacket;
    private Tcp tcp;
    private Udp udp;
    private Icmp icmp;
    private Arp arp;
    private Http http;
    private TableColumn frameColumnId;
    private TableColumn ethernetColumnId;
    private TableColumn ipColumnId;
    private TableColumn transportColumnId;
    private TableColumn filtrColumnId;
    private TableView tableView;

    public LoadPacketsFromFile(PcapPacket pcapPacket, Tcp tcp, Udp udp, Icmp icmp, Arp arp, Http http, TableColumn frameColumnId,
                               TableColumn ethernetColumnId, TableColumn ipColumnId, TableColumn transportColumnId, TableColumn filtrColumnId,
                               TableView tableView, VisualisationController vController, CaptureThread captureThread
    ) {
        this.pcapPacket = pcapPacket;
        this.tcp = tcp;
        this.udp = udp;
        this.icmp = icmp;
        this.arp = arp;
        this.http = http;
        this.frameColumnId = frameColumnId;
        this.ethernetColumnId = ethernetColumnId;
        this.ipColumnId = ipColumnId;
        this.transportColumnId = transportColumnId;
        this.filtrColumnId = filtrColumnId;
        this.tableView = tableView;
        this.vController = vController;
        this.captureThread = captureThread;
    }

    public void invoke() {
        String packetCapture;
        BufferedReader in;
        boolean ifIp6 = false;
        int line = 0;
        int counterIp = 0;
        int counterTranspotLayer = 0;
        CaptureWithoutFilters captureWithoutFilters;

        List<Frame> listFrame = new ArrayList();
        List<Ethernet> listEthernet = new ArrayList();
        List<Ip> listIp = new ArrayList();
        List<Transport> listTransport = new ArrayList();
        List<Filtr> listFiltr = new ArrayList();

        frameColumnId.setCellValueFactory(new PropertyValueFactory<Column, String>("frameName"));
        ethernetColumnId.setCellValueFactory(new PropertyValueFactory<Column, String>("ethernetName"));
        ipColumnId.setCellValueFactory(new PropertyValueFactory<Column, String>("ipName"));
        transportColumnId.setCellValueFactory(new PropertyValueFactory<Column, String>("transportName"));
        filtrColumnId.setCellValueFactory(new PropertyValueFactory<Column, String>("filtrName"));

        ObservableList<Column> contactList;
        try {
            in = new BufferedReader(new FileReader("PacketCapture.txt"));

            while ((packetCapture = in.readLine()) != null) {
                //--------------------   FRAME  ---------------------------------------
                firstLayer(packetCapture, line, listFrame);

                //--------------------   ETHERNET  ---------------------------------------
                secondLayer(packetCapture, line, listEthernet);

                //--------------------   IP or ARP  ---------------------------------------
                if (!vController.disableButton.isDisabled() && vController.filtersGroup.getSelectedToggle() == vController.arpButton) {
                    //--------------------   ARP  ---------------------------------------
                    if (line > 16 && line < 29) {
                        listIp.add(new Ip(packetCapture.replaceFirst("Arp: *", "")));
//                        System.out.println(arp);
                    } else {
                        listIp.add(new Ip(""));
//                        listTransport.add(new Transport(""));
                    }
                } else {
                    captureWithoutFilters = new CaptureWithoutFilters(pcapPacket, tcp, udp, icmp, arp, http, packetCapture, ifIp6, line,
                            counterIp, counterTranspotLayer, listIp, listTransport, captureThread).invoke();
                    ifIp6 = captureWithoutFilters.isIfIp6();
                    counterIp = captureWithoutFilters.getCounterIp();
                    counterTranspotLayer = captureWithoutFilters.getCounterTranspotLayer();

                }
                //                --------------------   FILTRY  ---------------------------------------
                //                if (line > 17 & line < 29) {
                //
                //                } else {
                //                    listFiltr.add(new Filtr(""));
                //                }
                line++;
            }

            for (int j = 0; j < 19; j++) {
                contactList = FXCollections.observableArrayList(
                        new Column(listFrame.get(j + 2).getFrameName(),
                                listEthernet.get(j + 9).getEthernetName(),
                                listIp.get(j + 19).getIpName(),
                                listTransport.get(j + 30 + counterIp).getTransportName(),
                                null
                        )
                );

                tableView.getItems().addAll(contactList);
            }

            in.close();
        } catch(Exception e){
        }
    }

    private void secondLayer(String packetCapture, int line, List<Ethernet> listEthernet) {
        if (line > 6 && line < 17) {
            listEthernet.add(new Ethernet(packetCapture.replaceFirst("Eth: *", "")));

        } else {
            listEthernet.add(new Ethernet(""));
        }
    }

    private void firstLayer(String packetCapture, int line, List<Frame> listFrame) {
        if (line > 0 && line < 7) {
            listFrame.add(new Frame(packetCapture.replaceFirst("Frame: *", "")));

        } else
            listFrame.add(new Frame(""));
    }
}
