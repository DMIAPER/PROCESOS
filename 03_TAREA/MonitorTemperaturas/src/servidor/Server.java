/**
 * Clase Server (main del servidor)
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * 
 * Main de del servidor que instancia a un nuevo servidor y llama al método start de la clase ServerManager que ejecuta el servidor.
 * 
 * Fecha: 24-02-2025 
 * version: 1.0.0 
 */
package servidor;

public class Server {

    /**
     * Método main del servidor
     */
    public static void main(String[] args) throws InterruptedException {
        ServerManager serverManager = new ServerManager(); // Se instancia un objeto de la clase ServerManager para
                                                           // crear un servidor.
        serverManager.start(); // Se ejecuta el servdor.
    }

}
