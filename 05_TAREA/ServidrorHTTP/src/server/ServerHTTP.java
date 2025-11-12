/**
 * Clase Main.
 * 
 * Esta clase construye el servidor y estable las conexiones con los clientes.
 * Se creará un gestor y este se enviará a un hilo independiente para manejar las solicitudes
 * de forma independiente.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * @version 1.0.0
 * Fecha 08/05/2025
 */

package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHTTP {

    // Se inicializa el puerto, se utiliza el 8086
    private static final int PUERTO = 8086;

    /**
     * Método main para ejecutar la aplicación y establecer el servidor.
     */
    public static void main(String[] args) {
        // Se controla el serverSoket, con un try-whit-resource para finalziar el
        // servidor automaticamente.
        // Se crea el servidor
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            // Se imprime que se ha establecido la conexión
            System.out.println("Servidor HTTP escuchando en el puerto " + PUERTO);
            // Bucle infinito que se mantiene a la escucha.
            while (true) {
                // Se crea un cliente
                Socket clienteSocket = serverSocket.accept();
                // Se informa que el cliente se ha conectado
                System.out.println("Cliente conectado desde: " + clienteSocket.getInetAddress());
                // Se crea el getor para el cliente
                ClientHandler manejador = new ClientHandler(clienteSocket);
                // Se ejecuta en un hilo independiente
                new Thread(manejador).start();
            }

            // En caso de error se muestra un mensaje de error.
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
