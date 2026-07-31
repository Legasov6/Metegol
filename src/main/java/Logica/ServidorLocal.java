// @author Gabriel Tremaria

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

    // Usamos el puerto 5555
    public void iniciarServidor(Runnable onExito) {
        new Thread(() -> {
            try {
                System.out.println("[HOST] Abriendo servidor en el puerto 5555...");
                serverSocket = new ServerSocket(5555);
                System.out.println("[HOST] Esperando a que el Jugador 2 se conecte...");
                
                socket = serverSocket.accept(); // Hace una pausa
                
                System.out.println("[HOST] ¡Conexión establecida con éxito!");
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                javafx.application.Platform.runLater(onExito);

            } catch (IOException e) {
                System.err.println("[HOST] Error en el servidor: " + e.getMessage());
            }
        }).start();
    }
    
    public ObjectOutputStream getOut() { return out; }
    public ObjectInputStream getIn() { return in; }
}