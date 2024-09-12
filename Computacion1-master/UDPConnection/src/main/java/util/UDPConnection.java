package util;

import javafx.application.Platform;
import main.java.App;
import main.java.model.PeerA;
import main.java.model.PeerD;

import java.io.IOException;
import java.net.*;

public class UDPConnection extends Thread {

    private DatagramSocket socket;
    private static UDPConnection instance;
    private boolean running = true; // Controla el ciclo de recepción de mensajes
    private PeerA uiCallback; // Callback para actualizar la interfaz gráfica

    private PeerD uiCallbackD; // Callback para actualizar la interfaz gráfica

    private UDPConnection() {}

    public static UDPConnection getInstance(){
        if (instance == null){
            instance = new UDPConnection();
        }
        return instance;
    }

    // Establece el puerto de escucha
    public void setPort(int port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    // Permite establecer el callback de la interfaz
    public void setUiCallback(PeerD uiCallbackD) {
        this.uiCallbackD = uiCallbackD;
    }

    public void setUiCallbackA(PeerA uiCallback) {
        this.uiCallback = uiCallback;
    }

    @Override
    public void run(){
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                this.socket.receive(packet); // Espera a recibir un paquete

                String message = new String(packet.getData(), 0, packet.getLength()).trim(); // Decodifica el mensaje correctamente

                if (uiCallback != null) {
                    Platform.runLater(() -> uiCallback.updateMessageArea(message));
                }

                if (uiCallbackD != null) {
                    Platform.runLater(() -> uiCallbackD.updateMessageArea(message));
                }

            } catch (SocketException e) {
                if (running) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // Método para detener la recepción de mensajes
    public void stopReceiving() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    // Envía un datagrama a otro peer
    public void sendDatagram(String message, String ipDest, int portDest) {
        new Thread(() -> {
            try {
                InetAddress ipAddress = InetAddress.getByName(ipDest);
                DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), ipAddress, portDest);
                socket.send(packet);
                System.out.println("Mensaje enviado a " + ipDest + ":" + portDest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
