package model;

import util.UDPConnection;

public class PeerD {
    public static void main(String[] args) {
        UDPConnection connection = UDPConnection.getInstance();
        connection.setPort(5001); // Puerto de escucha
        connection.start(); // Inicia el hilo de recepción

        // Envía un mensaje a otro peer (por ejemplo, PeerA en el puerto 5000)
        connection.sendDatagram("Hola desde PeerD", "127.0.0.1", 5000);

        // Si deseas detener la recepción después de un tiempo, puedes usar:
        // connection.stopReceiving();
    }
}
