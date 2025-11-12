/**
 * Clase Registrologs
 * 
 * En esta clase se gestionarán los eventos que se produzcan durante la ejecución 
 * de los procesos e hilos.
 * 
 * Se registrarán los siguientes eventos:
 * - clase interna para asignar formato a los mensaje. 
 * - Incio del turno de trabajo.
 * - Asignación de clientes.
 * - Registro de incio de pedido.
 * - Registro de finalización de pedido.
 * - Registro de finalización del turno.
 * - Registro de errores.
 * - Mostrar el resgitro completo.
 * - Escrbir en un fichero los registros para consultas posteriores.
 * 
 * @author dmiaper (Diógenes Miaja Pérez)
 * fecha: 21/12/2024
 * versión: 1.0
 */

package cafeteria;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class Registrologs {

    
    /**
     * Se declara el format de fecha que se va utilizar en los registros, ejemplo:
     * viernes, 27 de diciembre de 2024, 06:02:58 p. m.
     */
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy, hh:mm:ss a");

    //Se declara un Logger para capturar y manejar los registros
    private static Logger log;
    /**
     * Se declara una lista para guardar y poder mostrar los registros más al 
     * final de la ejecución del programa.
     */
    private List<String> registros;

    // Constructor del registro.
    public Registrologs() {
        String ruta = "src"+File.separator+"cafeteria_logs"+File.separator+"logs_cafeteria.txt";
        //se declara una lista para almacenar los registros.
        this.registros = new ArrayList<>();
        //se declara un logger parqa escribir los mensajes de registro que se generen.
        log = Logger.getLogger(Registrologs.class.getName());

        try {
            /**
             * Configurar un FileHandler para escribir los registros en un archivo.
             * Se escribirán los registros en un archivo de texto 'logs_cafeteria.txt'
             * se establece 'true', para indicar que los siguientes mensajes se agregarán
             * al final del texto, y se mantendrán los registros antiguos.
             */
            FileHandler fileHandler = new FileHandler(ruta,true);
            //se utilizará un formato simple.
            fileHandler.setFormatter(new CustomFormatter());
            //se agrega el filhandler para que se use cuando se registren los mensajes.
            log.addHandler(fileHandler);
            
            // Se configura el log para que no use los handlers predeterminados 
            log.setUseParentHandlers(false);
            
        } catch (IOException e) {
            //Controlamos posibles errores FileHandler
            System.err.println("Error al configurar el FileHandler: " + e.getMessage());
            //se conrfigura un consoleHandler como respaldo
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new CustomFormatter());
            log.addHandler(consoleHandler);
        }
    }
    
    /**
     * Calse interna que permite definir el formate que se almacenará en el fichero
     * y asi no mostrar la clase y el método que se utilizan para registrar el evento
     */
    private static class CustomFormatter extends Formatter {
        //se sobre escribe
        @Override
        public String format(LogRecord record) {
            //solo se guarda el registro del evento.
            return String.format("%s%n", record.getMessage());
        }
    }

    /**
     * Devuelve la fecha y hora actuales a ser llamada.
     * @return 
     */
    public LocalDateTime fechaActual() {
        // Se devuelve la fecha y hora.
        return LocalDateTime.now();
    }

    
    /**
     * Función para registrar el inicio del turn, se recibe como parámetro 
     * el id de la máquina, para saber a que máquina pertenecen los datos que se
     * van a registrar.
     * @param idMaquina 
     */
    public void regInicioTurno(int idMaquina) {
        /**
         * Mensaje que se almacenará para el incio de turno de la máquina.
         * Se obtiene la fecha actual, y el id de la máquina.
         */
        String mensaje = String.format("[%s] - Inicio del turno de máquina id# %s", fechaActual().format(formato), idMaquina);
        //se almacena el mensaje en el array.
        registros.add(mensaje);
        //se muestra el mensaje 
        log.info(mensaje);
        //Mensaje separación
        mensaje = "----------------------------------------------------------";
        registros.add(mensaje);
        //se muestra el mensaje
        log.info(mensaje);
    }
     
    /**
     * Función para registra la asignación de clientes a la máquina.
     * Se obtiene como parámetros el id de la máquina el objeto Cliente.
     * @param id
     * @param cliente 
     */
    public void regInicio(int id, Cliente cliente) {
        /**
         * Mensaje que obtiene la fecha, el id de cliente y el de la máquina
         */
        String mensaje = String.format("[%s] - Cliente id# %s asignado a la máquina %s", fechaActual().format(formato), cliente.getId(), id);
        //se registra el mensaje
        registros.add(mensaje);
        //se muestra el mensaje
        log.info(mensaje);
    }

    /**
     * Función que registra el incio del pedido.
     * Recibe como parámetro el objeto cliente
     * @param cliente 
     */
    public void regInicioPedido(Cliente cliente) {
        //variable que se instancia en null, para almacenar el tipo de bebida.
        String bebida = null;
        /**
         * Estructura de control switch para asignar el valor correspondiente 
         * segun el pedido que haga el cliente.
         */
        switch (cliente.getPedido()) {
            case CAFE -> bebida = "café";
            case TE -> bebida = "té";
            case CHOCOLATE_CALIENTE -> bebida = "chocolate caliente";
        }

        //Condición para evitar que registre en caso de que no haya bebida.
        if (bebida != null) {
            //Mensaje que registra la fecha actual y el pedido que se va realizar
            String mensaje = String.format("[%s] - Se ha iniciado la preparación del pedido %s.", fechaActual().format(formato), bebida);
            //se almacena el mensaje
            registros.add(mensaje);
            //se muestra el mensaje
            log.info(mensaje);
        }
    }
    
    /**
     * Función que registra la finalización del pedido.
     * Recibe como parámetro el objeto cliente.
     * @param cliente 
     */
    public void regFinPedido(Cliente cliente) {
        //variable que se instancia en null, para almacenar el tipo de bebida.
        String bebida = null;
        /**
         * Estructura de control switch para asignar el valor correspondiente 
         * segun el pedido que haga el cliente.
         */
        switch (cliente.getPedido()) {
            case CAFE -> bebida = "café";
            case TE -> bebida = "té";
            case CHOCOLATE_CALIENTE -> bebida = "chocolate caliente";
        }
        //Condición para evitar que registre en caso de que no haya bebida.
        if(bebida!=null){
            //Mensaje que registra la fecha actual y el pedido que se a finalizado
            String mensaje = String.format("[%s] - Se ha finalizado la preparación del pedido %s.", fechaActual().format(formato), bebida);
            //se registra el mensaje
            registros.add(mensaje);
            //se muestra el mensaje
            log.info(mensaje);
        }
    }

    /**
     * Fucnión para finalizar el turno, se ejecuta al finalizar la aplicación.
     * Se Reciben como parámetro los cafés, tés y chocolates calientes, que ha
     * realizado la máquina.
     * @param cafes
     * @param tes
     * @param chocos 
     */
    public void regFinTurno(String cafes, String tes, String chocos) {
        //Mensaje decorativo
        String mensaje = "----------------------------------------------------------";
        //se registra el mensaje
        registros.add(mensaje);
        //se muestra el mensaje
        log.info(mensaje);
        
        //Mensaje que almacen la fecha en la que se finaliza el turno.
        mensaje = String.format("[%s] - Se ha finalizado el turno.", fechaActual().format(formato));
        //Se registra el mensaje
        registros.add(mensaje);
        //se muestra el mensaje
        log.info(mensaje);
        //se registran el string con el mensaje de las bebidas realizadas.
        registros.add(cafes);
        registros.add(tes);
        registros.add(chocos);
        //se muestran los mensajes recibidos.
        log.info(cafes);
        log.info(tes);
        log.info(chocos);
        
        //se mensaje decorativo
        mensaje = "----------------------------------------------------------";
        //se registra el mensaje
        registros.add(mensaje);
        //se muestra el mensaje
        log.info(mensaje);
        //se llama a la función para almacenar los datos registrados el un archivo.
        guardarRegArchivo();
    }

    /**
     * Función para registrar posibles errores a la hora de gestionar los pedidos.
     * Recibe como parametros el id de la máquina y el el mensaje del error.
     * @param id
     * @param men 
     */
    public void regError(int id, String men) {
        //Mensaje que almacena la fecha en la que se ha producido el error, el id de la máquina y el mensaje del error.
        String mensaje = String.format("[%s] - Máquina id# [%s] - Se ha producido un ERROR: [%s]", fechaActual().format(formato), id, men);
        //se registra el mensaje
        registros.add(mensaje);
        //se muestre con advertencia el error-
        log.severe(mensaje);
    }

    /**
     * Función para mostrar los registros que se han almacenado en el Array
     */
    public void mostarRegistro() {
        //bucle for para recorre el Array de registros
        for (String registro : registros) {
            //se muestra el mensaje que contiene el registro
            System.out.println(registro);
        }
    }

    
    /**
     * Se escriben los registros en el fichero logs_cafeteria en la carpeta 
     * cafeteria_logs
     */
    public void guardarRegArchivo() {
        //Ruta del fichero.
        String rutaArchivo = "src"+File.separator+"cafeteria_logs"+File.separator+"logs_cafeteria.txt";
        //se controla el cierre automático del ficehro
        try (FileWriter writer = new FileWriter(rutaArchivo, true)) {
            //Bucle para obtener los registros dentro del array.
            for (String registro : registros) {
                //se escribe el registro con un salto de linea
                writer.write(registro + System.lineSeparator());
            }
        } catch (IOException e) {
            //Controlamos el error
            System.err.println("Error al guardar los registros en el archivo: " + e.getMessage());
        }
    }

}
