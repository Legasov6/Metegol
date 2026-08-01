package Logica;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Representa la entidad del Host en la arquitectura de red. Abre
 * puertos del sistema para escuchar y aceptar la conexión
 * TCP/IP entrante del Cliente
 * @author GabrielTremaria
 */
public class ServidorLocal {

    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
     * Abre el puerto de escucha 5555 en un hilo secundario y se
     * pausa en accept() hasta recibir a un usuario. Si es exitoso, 
     * inicializa los flujos de lectura y escritura.
     * @param onExito 
     */
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