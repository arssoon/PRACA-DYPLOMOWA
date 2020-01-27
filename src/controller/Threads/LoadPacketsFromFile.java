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
import java.io.IOException;
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
    private TableColumn filtersColumnId;
    private TableView tableView;
    private int counterTranspotLayer;

    public LoadPacketsFromFile(PcapPacket pcapPacket, Tcp tcp, Udp udp, Icmp icmp, Arp arp, Http http, TableColumn frameColumnId,
                               TableColumn ethernetColumnId, TableColumn ipColumnId, TableColumn transportColumnId, TableColumn filtersColumnId,
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
        this.filtersColumnId = filtersColumnId;
        this.tableView = tableView;
        this.vController = vController;
        this.captureThread = captureThread;
    }

    public void invoke() {
        int line = 0;
        int counterIp = 0;
        counterTranspotLayer = 1;

        ObservableList<Frame> listFrame = FXCollections.observableArrayList();
        ObservableList<Ethernet> listEthernet = FXCollections.observableArrayList();
        ObservableList<Ip> listIp = FXCollections.observableArrayList();
        ObservableList<Transport> listTransport = FXCollections.observableArrayList();
        ObservableList<Filters> listFilters = FXCollections.observableArrayList();

        frameColumnId.setCellValueFactory(new PropertyValueFactory<Column, String>("frameName"));
        ethernetColumnId.setCellValueFactory(new PropertyValueFactory<Column, String>("ethernetName"));
        ipColumnId.setCellValueFactory(new PropertyValueFactory<Column, String>("ipName"));
        transportColumnId.setCellValueFactory(new PropertyValueFactory<Column, String>("transportName"));
        filtersColumnId.setCellValueFactory(new PropertyValueFactory<Column, String>("filtersName"));

        try {
            displayAndLoadPacketsToTable(line, counterIp, counterTranspotLayer, listFrame, listEthernet, listIp, listTransport, listFilters);

        } catch (Exception e) {
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //--------------------  loaded Packets and view   ---------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void displayAndLoadPacketsToTable(int line, int counterIp, int counterTranspotLayer, List<Frame> listFrame,
                                              List<Ethernet> listEthernet, List<Ip> listIp, List<Transport> listTransport, List<Filters> listFilters) throws IOException {
        String packetCapture;
        boolean ifIp6 = false;
        CaptureHigerLayers captureHigerLayers;
        BufferedReader in = new BufferedReader(new FileReader("PacketCapture.txt"));

        while ((packetCapture = in.readLine()) != null) {
            //--------------------   FRAME  ---------------------------------------
            firstLayer(packetCapture, line, listFrame);

            //--------------------   ETHERNET  ---------------------------------------
            secondLayer(packetCapture, line, listEthernet);

            //--------------------   IP or ARP  ---------------------------------------
            if (vController.filtersGroup.getSelectedToggle() == vController.arpButton) {
                //--------------------   ARP  ---------------------------------------
                thirdLayer(packetCapture, line, listIp, listTransport, listFilters);

            } else {
                captureHigerLayers = new CaptureHigerLayers(pcapPacket, tcp, udp, icmp, packetCapture,  ifIp6, line,
                        counterIp, listIp, listTransport, listFilters, captureThread, vController).invoke();
                counterIp = captureHigerLayers.getCounterIp();
                ifIp6 = captureHigerLayers.isIfIp6();
            }

            line++;

        }
        if(vController.filtersGroup.getSelectedToggle() == vController.arpButton){
            writePacketsToTable(counterIp, listFrame, listEthernet, listIp, listTransport, listFilters, 10, 10, 10);
        } else {
            writePacketsToTable(counterIp, listFrame, listEthernet, listIp, listTransport, listFilters, 19, 30, 51);
        }

        in.close();
    }

    private void writePacketsToTable(int counterIp, List<Frame> listFrame, List<Ethernet> listEthernet, List<Ip> listIp,
                                     List<Transport> listTransport, List<Filters> listFilters, int max, int maxTransportLine, int maxFiltersLine) {
        ObservableList<Column> contactList;
        for (int j = 0; j < max; j++) {
            contactList = FXCollections.observableArrayList(
                    new Column(listFrame.get(j + 2).getFrameName(),
                            listEthernet.get(j + 9).getEthernetName(),
                            listIp.get(j + 19).getIpName(),
                            listTransport.get(j + maxTransportLine + counterIp).getTransportName(),
                            listFilters.get(j + maxFiltersLine + counterIp).getFiltersName()
                    )
            );

            tableView.getItems().addAll(contactList);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //--------------------  load 3 layer   ---------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void thirdLayer(String packetCapture, int line, List<Ip> listIp, List<Transport> listTransport, List<Filters> listFilters) {
        if (line > 17 && line < 29) {
            listIp.add(new Ip(packetCapture.replaceFirst("Arp: *", "")));
        } else {
            listIp.add(new Ip(""));
            listTransport.add(new Transport(""));
            listFilters.add(new Filters(""));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //--------------------  load 2 layer   ---------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void secondLayer(String packetCapture, int line, List<Ethernet> listEthernet) {
        if (line > 6 && line < 17) {
            listEthernet.add(new Ethernet(packetCapture.replaceFirst("Eth: *", "")));

        } else {
            listEthernet.add(new Ethernet(""));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //--------------------  load 1 layer   ---------------------------------------
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void firstLayer(String packetCapture, int line, List<Frame> listFrame) {
        if (line > 0 && line < 7) {
            listFrame.add(new Frame(packetCapture.replaceFirst("Frame: *", "")));

        } else
            listFrame.add(new Frame(""));
    }

}
