/**
 * Clase main para ejecutar el cliente
 * 
 * Está clase llama al método star() de la clase ClientManager que ejecutará el cliente.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * @version 1.0.0
 * Fecha 30/03/2025
 */

package client;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        // Se instancia un objeto de la clase ServerManager para crear el servidor
        ClientManager clientManager = new ClientManager();
        clientManager.start(); // Se ejecuta el servdor.
    }
}
