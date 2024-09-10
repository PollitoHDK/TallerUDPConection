package util;

import java.io.IOException;
import java.net.*;

public class UDPConnection extends Thread {

    private DatagramSocket socket;
    private static UDPConnection instance;
    private boolean running = true; // Controla cuándo detener el hilo de recepción

    private UDPConnection() {}

    public static UDPConnection getInstance(){
        if (instance == null){
            instance = new UDPConnection();
        }
        return instance;
    }

    public void setPort(int port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.err.println("Error al asignar el puerto: " + port + ". Está en uso.");
            e.printStackTrace(); // Esto te ayudará a entender por qué no se está creando el socket
        }
    }


    @Override
    public void run(){
        // Recepción continua de mensajes
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[24], 24);

                System.out.println("Esperando mensaje...");
                this.socket.receive(packet); // Recibe un paquete

                String msj = new String(packet.getData()).trim(); // Decodifica el mensaje
                System.out.println("Mensaje recibido: " + msj);

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopReceiving() {
        running = false; // Detiene el bucle de recepción
        if (socket != null && !socket.isClosed()) {
            socket.close(); // Cierra el socket para liberar recursos
        }
    }

    public void sendDatagram(String msj, String ipDest, int portDest) {
        new Thread(() -> { // Crea un nuevo hilo para el envío de mensajes
            try {
                InetAddress ipAddress = InetAddress.getByName(ipDest);
                DatagramPacket packet = new DatagramPacket(msj.getBytes(), msj.length(), ipAddress, portDest);
                socket.send(packet);
                System.out.println("Mensaje enviado a " + ipDest + ":" + portDest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
