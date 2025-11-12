/**
 * Clase ConfiServer
 * 
 * Está clase leerá el fichero config.properties, y utilizará los datos que contiene este fichero para configurar 
 * el servidor. 
 * 
 * El fichero config.properties contiene el puerto del servidor, y los parámetros de control de temperatura, temperatura máxima y
 * temperatura mínima.
 * 
 * El puerto solo se podrá configurar al inciar el servidor, aunqué posteriormente se cambie este datos, no se efecturá el cambio 
 * hasta que se reinicie el servidor.
 * 
 * La clase tambien permitirá realizar cambios en la configuración de los valores de control de temperatura cuando el servidor 
 * se lo indique.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * Fecha: 24-02-2025
 * Version: 1.0.0
 */

package servidor;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigServer {

    // Se instancia los atributos
    private int port;
    private double temp_min;
    private double temp_max;
    private Properties properties;
    private RegistroLogs logger;

    /**
     * Constructo de la clase.
     */
    public ConfigServer(RegistroLogs logger) {
        this.properties = new Properties();
        this.logger = logger;
    }

    /**
     * Método para configurar el servidor.
     * Leerá el fichero config.properties y utilizará los parámetros que contiene
     * para configurar el servidor y
     * las temperaturas de control.
     */
    public void configurar() {
        // Se obtiene la ruta del fichero para obtener los parámetros de configuración.
        // se utiliza un try-with-Resource para controlar el cierre del fichero
        // automáticamente.
        try (FileInputStream fichero = new FileInputStream(new File("config.properties"))) {
            System.out.println("Se ha configurado el servidor");
            // se carga el fichero properties
            this.properties.load(fichero);
            // Obtenemos los valores de los parámetros y los convertimos en tipo entero.
            // Se asigna el número del puerto que se va a usar para el servidor
            this.port = Integer.parseInt(this.properties.get("num_port").toString());
            // Se obtienen los rangos de las temperaturas y se convierten a tipo double.
            this.temp_min = Double.parseDouble(this.properties.get("temp_min").toString());
            this.temp_max = Double.parseDouble(this.properties.get("temp_max").toString());

        } catch (Exception e) {
            // Mensaje en caso de error
            String mensaje = "Error al configurar el servidor:-> " + e.getMessage();
            // Se registra el error
            logger.alertServer(mensaje);
            // Se devuelve un mensaje en caso de error.
            System.out.println(mensaje);
        }

    }

    /**
     * Método para actualizar la temperatura
     */
    public void updateTemp() {
        // Se obtiene la ruta del fichero para obtener los parámetros de configuración.
        // se utiliza un try-with-Resource para controlar el cierre del fichero
        // automáticamente.
        try (FileInputStream fichero = new FileInputStream(new File("config.properties"))) {
            properties.load(fichero);
            // Se obtienen los rangos de las temperaturas para verificar los valores.
            this.temp_max = Double.parseDouble(this.properties.get("temp_max").toString());
            this.temp_min = Double.parseDouble(this.properties.get("temp_min").toString());
            System.out.println("Se ha cactualizado el servidor");

            // En caso de error se devuelve un mensaje
        } catch (Exception e) {
            // Mensaje de error
            String mensaje = "Error al actualizar los nuevo rango de control de temperatura:-> " + e.getMessage();
            // Se registra el error
            logger.alertServer(mensaje);
            // Se devuelve un mensaje en caso de error.
            System.out.println(mensaje);
        }
    }

    /**
     * Getter para devolver el puerto configurado
     * 
     * @return se devuelve un valor de tipo entero
     */
    public int getPort() {
        return port;
    }

    /**
     * Getter para devolver la temperatura máxima configurada
     * 
     * @return se devuelve un valor de tipo double
     */
    public double getTempMax() {
        return temp_max;
    }

    /**
     * Getter para devolver la temperatura mínima configurada
     * 
     * @return se devuelve un valor de tipo double
     */
    public double getTempMin() {
        return temp_min;
    }

}
