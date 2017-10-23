package calin.bodnar.telemetrydata.data.source.service;

import android.util.Base64;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import calin.bodnar.telemetrydata.data.TelemetryData;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

public class TelemetryMonitor {
    private static final byte[] initSequence = {1};
    private PublishSubject<TelemetryData> telemetrySubject = PublishSubject.create();
    private String serverAddressStr;
    private int serverPort;
    private DatagramSocket udpSocket;
    private InetAddress serverAddress;

    public TelemetryMonitor(String serverAddress, int serverPort) {
        this.serverPort = serverPort;
        this.serverAddressStr = serverAddress;
    }

    public Flowable<TelemetryData> getStream() {
        return telemetrySubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    public void start() {
        if (!initUdpClient()) {
            telemetrySubject.onError(new Throwable("Connection error"));
            return;
        }
        boolean run = true;
        try {
            DatagramPacket sendPacket = new DatagramPacket(initSequence, initSequence.length, serverAddress, serverPort);
            udpSocket.send(sendPacket);
            while (run) {
                try {
                    byte[] message = new byte[8];
                    DatagramPacket receivePacket = new DatagramPacket(message, message.length);
                    udpSocket.receive(receivePacket);
                    telemetrySubject.onNext(createTelemetryData(message));
                } catch (IOException e) {
                    run = false;
                    closeUpdSocket();
                    telemetrySubject.onError(e);
                }
            }
        } catch (IOException e) {
            closeUpdSocket();
            telemetrySubject.onError(e);
        }
    }

    private void closeUpdSocket() {
        if (udpSocket != null) {
            udpSocket.close();
            udpSocket = null;
        }
    }

    private boolean initUdpClient() {
        closeUpdSocket();
        try {
            udpSocket = new DatagramSocket(serverPort);
        } catch (SocketException ignore) {
        }
        if (serverAddress == null) {
            try {
                serverAddress = InetAddress.getByName(serverAddressStr);
            } catch (UnknownHostException ignore) {
            }
        }
        return udpSocket != null && serverAddress != null;
    }

    private TelemetryData createTelemetryData(byte[] bytes) {
        byte[] decodedBytes = Base64.decode(bytes, Base64.DEFAULT);
        int timeMarker = ((decodedBytes[0] & 0xF) << 16) | ((decodedBytes[1] & 0xFF) << 8) | (decodedBytes[2] & 0xFF);
        short data = (short) (((decodedBytes[3] & 0xFF) << 8) | (decodedBytes[4] & 0xFF));
        return new TelemetryData(timeMarker, data);
    }
}
