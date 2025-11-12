/**
 * Clase ClientHandler Runnable
 * 
 * Clase que representa a los clientes y gestiona las funcionalidades que tiene dentro del servidor. 
 * 
 * Esta clase es el hilo que se ejecutará para interactuar con el cliente.
 * 
 * Se intanciará una lista de la clase ClientRegistry que recibirá la lista que almacenará la cola de hilos.
 * También se instanciará una lista para almacenar las temperaturas y poder calcular la temperatura media.
 * Además, se creará un DecimalFormat con el formato que se mostrarán las temperaturas, con un máximo de dos
 * decimales.
 * 
 * Esta contendrá un constructor para instanciar los atributos. El constructor recibirá los siguiente parámetros:
 *  - Socket: con los parámetros del cliente
 *  - ClientRegistry: se recibe la lista que almacenará los hilos de los clientes
 *  - ServerManager: para poder llamar a los métodos que realian las comprobaciones de la temperatura, etc.
 * 
 * En esta clase se han definido los siguientes métodos que permiten ejecutar correctamente sus funcionalidades.
 *  - Método para genera el ID del cliente.
 *  - Método para calcular la media de temperatura que ha registrado el sensor.
 *  - Método para enviar los mensajes al cliente que ha enviado la información para comporbarla.
 *  - Getter para delvolver el ID del sensor.
 *  - @Overide del método Runnable:
 *      - Con la modificación de este método se recibe la temperatura generada por cliente y se envia al servidor
 *        para que esté comprueba que la temperatura esta dentro de los parámetros que tiene la configuración.
 *        También se llamará a los métodos para crear el ID, y para calcular la media de la temperatura que ha 
 *        detectado el sensor.
 *        Para el cálculo de la temperatura media se añadirá a la lista que se a instanciado para almacenar todas
 *        las temperaturas que a enviado el sensor.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * Fecha: 24-02-2025
 * Version: 1.0.0
 */

package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    // Se instancia los atributos de la clase.
    private final Socket socket; // Socket del cliente
    private ClientRegistry listaClientes; // Lista que recibe el la lista donde se almacenan los clientes
    private ServerManager server; // Se almacenará el servidor
    private String idSensor; // Variable para almacenar el id del cliente
    private List<Double> listaTemp; // Lista para almacenar las temperaturas
    private BufferedReader input; // Se instancia un input para obtener los datos que envie el cliente
    private PrintWriter output; // Se instanacia un output para enviar los mensajes al cliente
    private double temperatura; // Variable para almacenar la temperatura que se recibe del cliente
    private double tempMedia; // Variable para calcular almacenar la temperatura media actual.
    private DecimalFormat df = new DecimalFormat("#.##"); // Se declará el formato que se utilizará para mostrar la
                                                          // temperatura
    private RegistroLogs logger;

    // Contructor del cliente
    public ClientHandler(Socket socket, ClientRegistry listaclientes, RegistroLogs logger, ServerManager server) {
        this.logger = logger;
        this.socket = socket;
        this.listaClientes = listaclientes;
        this.server = server;
        this.idSensor = generaID();
        this.listaTemp = new ArrayList<>();

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
        return "sensorIP:_" + socket.getInetAddress().getHostAddress() + "_" + socket.getPort();
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
     * Método para calcular la media actual de temperatura
     */
    public void calcTemMedia() {
        // Si la lista esta vacía se finaliza el método
        if (listaTemp.isEmpty()) {
            return;
        }
        // Se instancia una variable auxiliar para sumar el total de las temperaturas
        double sumTotal = 0;
        // Se itera la lista de temperaturas almacenadas
        for (double temp : listaTemp) {
            // Se suman las temperaturas
            sumTotal += temp;
        }
        // Se calucla la media de temperaturas.
        tempMedia = sumTotal / listaTemp.size();
        // Se muestra un mensaje para informar de la temperatura media
        sendMessage("La temperatura media actual es de: " + df.format(tempMedia) + " ºC");
    }

    /**
     * Getter para el id del sensor
     * 
     * @return se devuelve un string
     */
    public String getIdSensor() {
        return idSensor;
    }

    /**
     * Se modifica el método Runnable para realizar la ejecución del cliente.
     */
    @Override
    public void run() {
        // Se agrega a la cola el cliente
        listaClientes.addClient(this);
        // Se informa que el cliente se ha conectado con el id
        System.out.println("El sensor " + idSensor + " se ha conectado");
        // Variable axuliar
        String aux;
        // Se controlan los errores
        try {
            // Si el socket no esta cerrado
            if (!socket.isClosed()) {
                // Se obtiene el mensaje que ha enviado el cliente
                while ((aux = input.readLine()) != null) {
                    // Se comprueba que el valor recibido se puede convertir en un valor de tipo
                    // Double
                    if (aux.matches("-?\\d+(\\d.\\d+)?")) {
                        // Almacenamos la temperatura que se ha recibido
                        temperatura = Double.parseDouble(aux);
                        // Se almacena la temperatura recibida
                        listaTemp.add(temperatura);
                        calcTemMedia();
                        String mensaje;
                        // Se comprueba la temperatura y si no se devuele un mensaje vacío
                        if ((mensaje = server.comprobación(temperatura, idSensor)) != null) {
                            // Se llama al método responderCliente de la clase ClientRegistry
                            listaClientes.responderCliente(mensaje, this);
                        }
                        // Si no se puede convertir se devolverá un mensaje en el servidor
                    } else {
                        System.out.println("Error: el valor recibido del sensor " + idSensor
                                + " no es de tipo númerico");
                    }

                }

            }
            // En caso de error
        } catch (IOException e) {
            // Mensaje del error
            String mensaje = "Error en el hilo del cliente " + idSensor + ": ->" + e.getMessage();
            // Se registra el error
            logger.alertServer(mensaje);
            // Se imprime el error
            System.out.println(mensaje);
        }
    }

}
