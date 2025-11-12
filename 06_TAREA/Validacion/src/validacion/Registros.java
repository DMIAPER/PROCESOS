/**
 * Clase Registros
 * 
 * Esta clase se encargará de gstiones los logs de la aplicación, como la creación de
 * directorios, la creación de archivos y la escritura de logs.
 * 
 * @author DMIAPER (Diógenes Míaja Pérez)
 * @version 1.0.0
 * Fecha 2025-05-07
 */

package validacion;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Registros {

    // Se declaran las variables y objetos necesarios.
    private final String DIRECTORIO_BASE;
    private final String NOMBRE_ARCHIVO_LOG;
    private Logger logger;
    // Constantes para mensajes comunes
    private final String LOG_PREFIX_ERROR = "ERROR: ";
    private final String LOG_PREFIX_WARNING = "ADVERTENCIA: ";
    private final String LOG_SISTEMA_CONFIGURADO = "Sistema de logging configurado correctamente";
    private final String LOG_DIRECTORIO_CREADO = "Directorio creado: %s";
    private final String LOG_ARCHIVO_CREADO = "Archivo de log creado: %s";
    private final String LOG_ERROR_CONFIGURACION = "Error al configurar el logger: %s";

    /**
     * Constructor de la clase Registros.
     * Inicializa el directorio base y el nombre del archivo de log.
     * Se crea el directorio de logs y el archivo de log si no existen.
     */
    public Registros() {
        // Se obtiene el directorio base del proyecto y se establece la ruta
        this.DIRECTORIO_BASE = System.getProperty("user.dir") + "/src/logs/";
        // crear el nombre del archivo de log
        this.NOMBRE_ARCHIVO_LOG = "logger.log";
        // Se crea el logger
        this.logger = Logger.getLogger(Registros.class.getName());
        // Se llama al método para crear el directorio de logs
        crearDirectorio(DIRECTORIO_BASE);
        // Se llama al método para crear el archivo de log
        crearArchivoLog(NOMBRE_ARCHIVO_LOG);
        // Se llama al método para configurar el logger
        configurarLogger(NOMBRE_ARCHIVO_LOG);
    }

    /**
     * Método para crear un directorio.
     * 
     * @param nombreDirectorio Nombre del directorio a crear.
     */
    private void crearDirectorio(String nombreDirectorio) {
        // Se controla las excepciones
        try {
            // Se instancia un objeto File con la ruta del directorio
            File directorio = new File(nombreDirectorio);
            // Se verifica si el directorio no existe
            if (!directorio.exists()) {
                // Se crea el directorio
                directorio.mkdirs();
                // Se registra un mensaje de información en el log
                logger.info(String.format(LOG_DIRECTORIO_CREADO, nombreDirectorio));
            }
        } catch (Exception e) {
            // Se muestra un mensaje de error en la consola y se imprime la traza de la
            // excepción
            logger.severe(LOG_PREFIX_ERROR + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método para crear un archivo de log.
     * 
     * @param nombreArchivo Nombre del archivo de log a crear.
     */
    private void crearArchivoLog(String nombreArchivo) {
        // Se controla las excepciones
        try {
            // Se instancia un objeto File con la ruta del archivo de log
            File archivoLog = new File(DIRECTORIO_BASE + nombreArchivo);
            // Se verifica si el archivo de log no existe
            if (!archivoLog.exists()) {
                // Se crea el archivo de log
                archivoLog.createNewFile();
                // Se muestra un mensaje de información en el log
                logger.info(String.format(LOG_ARCHIVO_CREADO, nombreArchivo));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para configurar el logger.
     * 
     * @param nombreArchivo Nombre del archivo de log a configurar.
     */
    private void configurarLogger(String nombreArchivo) {
        // Se controla las excepciones
        try {
            // Se crear el FileHandler con la ruta completa
            FileHandler fileHandler = new FileHandler(DIRECTORIO_BASE + nombreArchivo, true);
            // Se establece el formato del log con el formato SimpleFormatter
            fileHandler.setFormatter(new SimpleFormatter());
            // Se añade el FileHandler al logger
            logger.addHandler(fileHandler);
            // Se establece el nivel de log a ALL para registrar todos los mensajes
            logger.setLevel(Level.ALL);
            // Se desactiva el uso de los handlers del padre para evitar duplicados
            logger.setUseParentHandlers(false);
            // Se registra un mensaje de información en el log
            logger.info(LOG_SISTEMA_CONFIGURADO);
        } catch (IOException e) {
            System.err.println(LOG_PREFIX_ERROR + LOG_ERROR_CONFIGURACION.replace("{}", e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * Método para escribir un mensaje en el archivo de log.
     * 
     * @param mensaje Mensaje a escribir en el log.
     */
    public void registroLog(String mensaje) {
        logger.info(mensaje);
    }

    /**
     * Método para escribir un mensaje de error en el archivo de log.
     * 
     * @param mensaje Mensaje de error a escribir en el log.
     */
    public void errorLog(String mensaje) {
        logger.severe(LOG_PREFIX_ERROR + mensaje);
    }

    /**
     * Método para escribir un mensaje de alerta en el archivo log.
     * 
     * @param mensaje Mensaje de alerta a escribir en el log.
     */
    public void warningLog(String mensaje) {
        logger.warning(LOG_PREFIX_WARNING + mensaje);
    }

    /**
     * Método para cerrar el logger y liberar los recursos.
     */
    public void cerrarLogger() {
        for (Handler handler : logger.getHandlers()) {
            handler.close();
        }
    }

}
