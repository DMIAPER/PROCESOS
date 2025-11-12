/**
 * Clase ClientHandler Runnable
 * 
 * Esta clase representará al cliente y nos permitirá interactuar con el almacén que solicita el pedido.
 * 
 * Esta clase contiene los siguientes métodos.
 * 
 * - Constructor de la clase.
 *   - Recibe un coske ce conexión.
 *   - Recibe la lista para registrar el cliente.
 *   - Recibe el logger para el registro.
 *   - Recibe el servidor.
 *   - Se instancia input y output para enviar y recibir información al almacén.
 * 
 * - Método para generar el identificador del cliente.
 * - Método para enviar mensaje al almacén.
 * - Método getter para devolver el identificador del almacén.
 * - Método runnable
 *   - Este método registrará el almacén en la lista de almacenes.
 *   - Recibirá el mensaje del cliente y le solicitará al almacén principal
 *     el pedidido.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * @version 1.0.0
 * Fecha: 31-03-2025 
 */

package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    // Se instancia los atributos de la clase.
    private final Socket socket; // Socket del cliente
    private ClientRegistry listaClientes; // Lista que recibe el la lista donde se almacenan los clientes
    private ServerManager server; // Se almacenará el servidor
    private String idAlmacen; // Variable para almacenar el id del cliente
    private BufferedReader input; // Se instancia un input para obtener los datos que envie el cliente
    private PrintWriter output; // Se instanacia un output para enviar los mensajes al cliente
    private RegistroLogs logger;

    // Contructor del cliente
    public ClientHandler(Socket socket, ClientRegistry listaclientes, RegistroLogs logger, ServerManager server) {
        this.logger = logger;
        this.socket = socket;
        this.listaClientes = listaclientes;
        this.server = server;
        this.idAlmacen = generaID();

        // Se contorla la lectura y envío de datos
        try {
            // Se inicializa la lectura de datos
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Se inicializa el envío de datos
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            // Mensaje del error
            String mensaje = "Error en la lectura y envío de datos: ->" + e.getMessage();
            // Se registra el error
            logger.alertServer(mensaje);
            // En caso de error se envía un mensaje con el error
            System.err.println(mensaje);
        }
    }

    /**
     * Método para generar el sensor
     * 
     * @return se devuelve un string con el id del sensor
     */
    private String generaID() {
        return "almacenIP:_" + socket.getInetAddress() + "_" + socket.getPort();
    }

    /**
     * Método para enviar el mensaje al cliente
     * 
     * @param message cadena que se recibe para enviarla al cliente
     */
    public void sendMessage(String message) {
        output.println(message);
    }

    /**
     * Getter para el id del almacén
     * 
     * @return se devuelve un string
     */
    public String getIdAlmacen() {
        return idAlmacen;
    }

    /**
     * Se modifica el método Runnable para realizar la ejecución del cliente.
     */
    @Override
    public void run() {
        String mensaje;
        // Se agrega a la cola el cliente
        listaClientes.addClient(this);
        // Se informa que el cliente se ha conectado con el id
        System.out.println("El almacen " + idAlmacen + " se ha conectado");
        // Se registra el cliente
        logger.regAlmacen(idAlmacen);
        // Variable axuliar
        String aux;
        // Se controlan los errores
        try {
            // Si el socket no esta cerrado
            if (!socket.isClosed()) {
                // Se obtiene el mensaje que ha enviado el cliente
                while ((aux = input.readLine()) != null) {
                    // Si el mensaje no es nulo se devuelve el mensaje al cliente.
                    if ((mensaje = server.realizarPedido(aux, idAlmacen)) != null) {
                        // Se enviará al cliente que ha realizado la solicitud la respuesta del
                        // servidor.
                        listaClientes.responderCliente(mensaje, this);
                    } else {
                        System.out.println("Error al realizar el pedido.");
                        logger.pedidoErroneo(idAlmacen, "Error al realizar el pedido, no se ha recibido respuesta.");
                    }
                }
            }
            // En caso de error
        } catch (IOException e) {
            // Mensaje del error
            mensaje = "Error en el hilo del cliente " + idAlmacen + ": ->" + e.getMessage();
            // Se registra el error
            logger.alertServer(mensaje);
            // Se imprime el error
            System.err.println(mensaje);
        }
    }
}
