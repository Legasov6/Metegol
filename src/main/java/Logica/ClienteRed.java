package Logica;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteRed {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Añade el "Runnable onExito"
    public void conectarAlServidor(String ipServidor, Runnable onExito) {
        new Thread(() -> {
            try {
                System.out.println("[CLIENTE] Intentando conectar a la IP: " + ipServidor + "...");
                socket = new Socket(ipServidor, 5555);
                System.out.println("[CLIENTE] ¡Conectado con éxito al Host!");

                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                // ¡LA MAGIA! 
                javafx.application.Platform.runLater(onExito);

            } catch (IOException e) {
                System.err.println("[CLIENTE] Error de conexión: " + e.getMessage());
            }
        }).start();
    }
    
    // Getters para enviar/recibir datos después
    public ObjectOutputStream getOut() { return out; }
    public ObjectInputStream getIn() { return in; }
}