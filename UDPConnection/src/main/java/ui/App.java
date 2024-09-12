package main.java.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import util.UDPConnection;

public class App extends Application {

    private UDPConnection connection;
    private TextArea messageArea;

    @Override
    public void start(Stage primaryStage) {
        connection = UDPConnection.getInstance();

        // Configura el puerto de escucha (por ejemplo, 5001 para este peer)
        connection.setPort(5000);
        connection.start(); // Inicia el hilo de recepción de mensajes
        // Componentes de la interfaz
        messageArea = new TextArea();
        messageArea.setEditable(false); // Área de mensajes recibidos
        messageArea.setPrefHeight(200);

        TextField inputField = new TextField(); // Campo de texto para enviar mensajes
        Button sendButton = new Button("Enviar");

        sendButton.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                connection.sendDatagram(message, "127.0.0.1", 5001); // Envía al peer destino (cambia la IP según el peer destino)
                messageArea.appendText("Yo: " + message + "\n");
                inputField.clear();
            }
        });

        // Layout de la interfaz
        VBox layout = new VBox(10);
        layout.getChildren().addAll(messageArea, inputField, sendButton);

        // Configurar y mostrar la escena
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Peer UDP Chat");
        primaryStage.show();
    }

    // Método para actualizar la interfaz con los mensajes recibidos
    public void updateMessageArea(String message) {
        Platform.runLater(() -> messageArea.appendText("Peer: " + message + "\n"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
