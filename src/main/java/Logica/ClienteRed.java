package Logica;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Representa al participante invitado que se conecta a un 
 * servidor preexistente proporcionando una dirección de red.
 * @author GabrielTremaria
 */
public class ClienteRed {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    /**
     * En un hilo en segundo plano, intenta conectarse al puerto 
     * 5555 de la dirección IP proporcionada. Si tiene éxito, captura
     * los flujos de red para comunicarse con el Host
     * @param ipServidor es la IP proporcionada por el Host
     * @param onExito 
     */
    public void conectarAlServidor(String ipServidor, Runnable onExito) {
        new Thread(() -> {
            try {
                System.out.println("[CLIENTE] Intentando conectar a la IP: " + ipServidor + "...");
                socket = new Socket(ipServidor, 5555);
                System.out.println("[CLIENTE] ¡Conectado con éxito al Host!");

                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());
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