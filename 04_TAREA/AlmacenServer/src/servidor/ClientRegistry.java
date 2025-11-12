/**
/**
 * CLASE ClientRegistry
 * 
 * Clase para gestionar los clientes creado una cola que gestionará los mùltiples hilos de la Clase
 * ClientHandler.
 * 
 * Esta clase cuenta con un método para agregar clientes a la cola QUEUE.
 * También se eliminará el cliente de la lista cuando este se desconecte.
 * Se crea un método para enviar el mensaje que le corresponde a cada cliente
 * de este modo se evita que el cliente reciba su respuesta y no el de otro cliente.
 * Por último, se crea un método para devolver la cantidad de clientes que están registrados y
 * así poder asignarles el ID.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * @version 1.0.0
 * Fecha: 31-03-2025 
 */

package servidor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientRegistry {

    // Se instancia una cola segura para múltiples hilos (ConcurrentLinkedQueue)
    // que almacena los clientes conectados.
    private final Queue<ClientHandler> clientes = new ConcurrentLinkedQueue<>();

    // Método para agregar clientes a la lista de clientes
    public void addClient(ClientHandler cliente) {
        clientes.add(cliente);
    }

    // Método para eliminar clientes de la lista
    public void removeClient(ClientHandler cliente) {
        clientes.remove(cliente);
    }

    // Método para enviar los mensajes al sensor que corresponde
    public void responderCliente(String mensaje, ClientHandler sender) {
        // Bucle for que recorre la lista de clientes
        for (ClientHandler cliente : clientes) {
            // Si el cliente es igual al que envia el mensaje
            if (cliente == sender) {
                // se le envia la respuesta al sensor que ha enviado la temperatura.
                cliente.sendMessage(mensaje);
            }
        }
    }

    // Método que devuelve la cantidad de clientes que hay.
    public int getClientsCount() {
        return clientes.size();
    }

}