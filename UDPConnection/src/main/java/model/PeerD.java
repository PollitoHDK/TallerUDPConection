package main.java.model;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import util.UDPConnection;

import java.util.Objects;

public class PeerD extends Application {

    private UDPConnection connection;
    private TextArea messageArea;

    @Override
    public void start(Stage primaryStage) {
        connection = UDPConnection.getInstance();

        connection.setPort(5000);
        connection.start(); // Inicia el hilo de recepción de mensajes

        connection.setUiCallback(this);

        // Componentes de la interfaz gráfica
        messageArea = new TextArea();
        messageArea.setEditable(false); // Área para mostrar mensajes recibidos
        messageArea.setPrefHeight(200);
        messageArea.getStyleClass().add("message-area"); // Aplicar estilo

        TextField inputField = new TextField(); // Campo de texto para enviar mensajes
        inputField.getStyleClass().add("input-field"); // Aplicar estilo

        Button sendButton = new Button("Enviar");
        sendButton.getStyleClass().add("send-button"); // Aplicar estilo

        // Acción del botón de enviar mensaje
        sendButton.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                connection.sendDatagram(message, "127.0.0.1", 5001); // Enviar a PeerA en el puerto 5000
                messageArea.appendText("Yo: " + message + "\n");
                inputField.clear();
            }
        });

        // Cabecera
        Label header = new Label("Chat UDP");
        header.getStyleClass().add("header"); // Aplicar estilo

        // Layout de la interfaz gráfica
        VBox layout = new VBox(10);
        layout.getChildren().addAll(header, messageArea, inputField, sendButton);
        layout.getStyleClass().add("layout"); // Aplicar estilo

        // Configurar y mostrar la escena
        Scene scene = new Scene(layout, 400, 300);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm()); // Agregar archivo CSS

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
