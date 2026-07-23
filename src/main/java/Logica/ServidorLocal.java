package Logica;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorLocal {

    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Usaremos el puerto 5555 por defecto
    // Añade el "Runnable onExito" en los paréntesis
    public void iniciarServidor(Runnable onExito) {
        new Thread(() -> {
            try {
                System.out.println("[HOST] Abriendo servidor en el puerto 5555...");
                serverSocket = new ServerSocket(5555);
                System.out.println("[HOST] Esperando a que el Jugador 2 se conecte...");
                
                socket = serverSocket.accept(); // El hilo se pausa aquí
                
                System.out.println("[HOST] ¡Conexión establecida con éxito!");
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                // ¡LA MAGIA! Ejecuta el cambio de pantalla de forma segura en JavaFX
                javafx.application.Platform.runLater(onExito);

            } catch (IOException e) {
                System.err.println("[HOST] Error en el servidor: " + e.getMessage());
            }
        }).start();
    }
    
    // Dejamos los getters listos para cuando necesitemos enviar/recibir datos
    public ObjectOutputStream getOut() { return out; }
    public ObjectInputStream getIn() { return in; }
}