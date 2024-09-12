package main.java.model;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import util.UDPConnection;

public class PeerD extends Application {

    private UDPConnection connection;
    private TextArea messageArea;

    @Override
    public void start(Stage primaryStage) {
        connection = UDPConnection.getInstance();

        // Configura el puerto de escucha (5001 para este peer)
        connection.setPort(5001);
        connection.start(); // Inicia el hilo de recepción de mensajes

        // Configura el callback para actualizar la interfaz con los mensajes recibidos
        connection.setUiCallback(this);

        // Componentes de la interfaz gráfica
        messageArea = new TextArea();
        messageArea.setEditable(false); // Área para mostrar mensajes recibidos
        messageArea.setPrefHeight(200);

        TextField inputField = new TextField(); // Campo de texto para enviar mensajes
        Button sendButton = new Button("Enviar");

        // Acción del botón de enviar mensaje
        sendButton.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                connection.sendDatagram(message, "127.0.0.1", 5000); // Enviar a PeerA en el puerto 5000
                messageArea.appendText("Yo: " + message + "\n");
                inputField.clear();
            }
        });

        // Layout de la interfaz gráfica
        VBox layout = new VBox(10);
        layout.getChildren().addAll(messageArea, inputField, sendButton);

        // Configurar y mostrar la escena
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PeerD UDP Chat");
        primaryStage.show();

        // Detener la conexión al cerrar la ventana
        primaryStage.setOnCloseRequest(event -> connection.stopReceiving());
    }

    // Método para actualizar la interfaz con los mensajes recibidos
    public void updateMessageArea(String message) {
        Platform.runLater(() -> messageArea.appendText("Peer: " + message + "\n"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
