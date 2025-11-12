/**
 * Clase Server (main del servidor)
 * 
 * Main de del servidor que instancia a un nuevo servidor y llama al método 
 * start de la clase ServerManager que ejecuta el servidor.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * @version 1.0.0
 * Fecha: 31-03-2025  
 */
package servidor;

public class Server {

    /**
     * Método main del servidor
     */
    public static void main(String[] args) throws InterruptedException {
        // Se instancia un objeto de la clase ServerManager para crear el servidor
        ServerManager serverManager = new ServerManager();
        serverManager.start(); // Se ejecuta el servdor.
    }

}