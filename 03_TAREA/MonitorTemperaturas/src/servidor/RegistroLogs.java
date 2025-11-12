/**
 * Clase RegistroLogs
 * 
 * En esta clase sirve para registrar los aconticimientos que se produzcan en el servidor.
 * 
 * El formato de la fecha será mostrando el día de la semana, la fecha y la hora en formato 24 horas.
 * 
 * Se creara un fichero (Alertas.log) que almacenárá todos los registros. En caso de que ya exista se escribirán los registros.
 * Para mantener el registro con todos los datos y alertas que hayan surgido se permirtirá seguir introduciendo datos y no se 
 * sobrescribirán los datos ya existentes.
 * 
 * Se declarará una clase interna para definir el formato que se va a usar y no mostrar la clase y el método que se ha utilizado para
 * realizar el registro del evento.
 * 
 * Se obtendrá la fecha actual en el momento que se registro el error.
 * 
 * Se han creado tres métodos:
 *  - Método para registrar el inicio del servidor.
 *  - Método para registrar los errores que surjan en el servidor
 *  - Método para registrar la alertas de temperatura cuando estás no esten dentro de los valores de configuración.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * Fecha: 24-02-2025
 * Version: 1.0.0
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
        String ruta = "src" + File.separator + "registro_alertas" + File.separator + "alertas.log";
        // se declara un logger parqa escribir los mensajes de registro que se generen.
        log = Logger.getLogger(RegistroLogs.class.getName());

        try {
            /**
             * Configurar un FileHandler para escribir los registros en un archivo.
             * Se escribirán los registros en un archivo de texto 'logs_cafeteria.txt'
             * se establece 'true', para indicar que los siguientes mensajes se agregarán
             * al final del texto, y se mantendrán los registros antiguos.
             */
            FileHandler fileHandler = new FileHandler(ruta, true);
            // se utilizar� un formato simple.
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
     * Método que registra las advertencias cuando la temperatura esta fuera del
     * rango configurado
     * 
     * @param mensaje se recibe un String con el tipo de advertencia
     */
    public void regAlertasTemp(String mensaje) {
        // Se registrará el mensaje de advertencia con la fecha del acontecimiento
        log.warning("-Alerta de temperatura: " + fechaActual().format(formato) + " " + mensaje);
    }

}
