/**
 * Clase RegistroLogs
 * 
 * Esta clase creará en caso de que no exista un fichero para registar la actividad del 
 * Servidor (Almacén principal).
 * 
 * - Se definirá un formato concreto para la fecha, este formato será:
 *    - (día de la semana, fecha completa, y hora en formato 24 horas).
 * 
 * - Se instanciará un clase interna para editar el formato del registro, y así
 *   eliminamos que nos muestre el método y la clase que instancia el logger.
 * 
 * - Método para devolver la fecha y hora actual.
 * - Método para registrar el incio del registro.
 * - Método para registar la conexión del cliente.
 * - Método para registrar los errores.
 * - Método para registar los pedidos.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * @version 1.0.0
 * Fecha: 31-03-2025 
 */

package servidor;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class RegistroLogs {
    /**
     * Se declara el format de fecha que se va utilizar en los registros, ejemplo:
     * viernes, 27 de diciembre de 2024, con la hora en formato 24 horas.
     */
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy, HH:mm:ss ");

    // Se instancia log para realizar los registros
    private Logger log;

    /**
     * Constructor de la clase
     */
    public RegistroLogs() {
        // Se instancia la ruta donde se realizarán los registros.
        String ruta = "registro_alertas" + File.separator + "alertas.log";
        // se declara un logger parqa escribir los mensajes de registro que se generen.
        log = Logger.getLogger(RegistroLogs.class.getName());

        try {
            /**
             * Configurar un FileHandler para escribir los registros en un archivo.
             * Se escribirán los registros en un archivo de texto 'alertas.txt'
             * se establece 'true', para indicar que los siguientes mensajes se agregarán
             * al final del texto, y se mantendrán los registros antiguos.
             */
            FileHandler fileHandler = new FileHandler(ruta, true);
            // se utilizará un formato simple.
            fileHandler.setFormatter(new CustomFormatter());
            // se agrega el filhandler para que se use cuando se registren los mensajes.
            log.addHandler(fileHandler);

            // Se configura el log para que no use los handlers predeterminados
            log.setUseParentHandlers(false);

        } catch (IOException e) {
            // Controlamos posibles errores FileHandler
            System.err.println("Error al configurar el FileHandler: " + e.getMessage());
            // se conrfigura un consoleHandler como respaldo
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new CustomFormatter());
            log.addHandler(consoleHandler);
        }
    }

    /**
     * Clase interna que permite definir el formate que se almacenará en el fichero
     * y asi no mostrar la clase y el método que se utiliza para registrar el evento
     */
    private static class CustomFormatter extends Formatter {
        // se sobre escribe
        @Override
        public String format(LogRecord record) {
            // solo se guarda el registro del evento.
            return String.format("%s%n", record.getMessage());
        }
    }

    /**
     * Devuelve la fecha y hora actuales a ser llamada.
     * 
     * @return
     */
    public LocalDateTime fechaActual() {
        // Se devuelve la fecha y hora.
        return LocalDateTime.now();
    }

    /**
     * Función para registrar el inicio del servidor
     * 
     * @param se recibe el puerto de la conexión del servidor
     */
    public void regInicioServidor(int PORT) {
        /**
         * Se registrará el inicio del servidor.
         * Se obtiene la fecha actual, y además se almacerá el puerto de conexión
         * establecido.
         */
        String mensaje;
        // Mensaje separaciónn
        mensaje = "----------------------------------------------------------";
        log.info(mensaje);
        // Mensaje de inicio del servidor.
        mensaje = String.format("[%s] - Inicio del servidor en el puerto, %s", fechaActual().format(formato),
                PORT);
        // se muestra el mensaje
        log.info(mensaje);
        // Mensaje separaciónn
        mensaje = "----------------------------------------------------------";
        log.info(mensaje);
    }

    /**
     * Método para registrar los errores que se produzcan en el servidor.
     * 
     * @param mensaje se recibe un String con el tipo de error
     */
    public void alertServer(String mensaje) {
        log.warning("Error de proceso: " + fechaActual().format(formato) + " " + mensaje);
    }

    /**
     * Método que registará los pedidos
     * 
     * @param idalmacen recibe el identificado del usuario.
     * @param producto  recibe el producto solicitado.
     * @param cantidad  recibe la cantidad de productos solicitados.
     * @param mensaje   recibe el mensaje si ha aceptado o no el pedido.
     */
    public void regPedidos(String idalmacen, String producto, int cantidad, String mensaje) {
        // Se registrará el mensaje de información del pedido.
        log.info("[" + fechaActual().format(formato) + "] Pedido realizado por Almacén:  " + idalmacen
                + " - Producto solicitado: " + producto + " - Cantidad solicitada: " + cantidad + " - " + mensaje);
    }

    public void regAlmacen(String idAlmacen) {
        // Se registrará el almacén que se ha conectado
        log.info("[" + fechaActual().format(formato) + "] - Almacén " + idAlmacen + "se ha conectado.");
    }

    public void pedidoErroneo(String idAlmacen, String mensaje) {
        // Se registrará el erro de pedidio
        log.warning("[" + fechaActual().format(formato) + "] - Almacén " + idAlmacen + " " + mensaje);
    }
}
