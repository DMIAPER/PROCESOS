/**
 * Clase ConfigClient
 * 
 * Esta clase abrirá el fichero config.properties y obtendrá la información para configurar el servidor.
 * 
 * - En método constructor inttanciara una lista, y un Properties.
 * - Método para configuar el clietne.
 *   Este método leerá el fichero config.properties y obtendrá los datos para configurar el puerto. 
 *   También, prodría incluir el HOST o cualquier otro configuración que fuera importante.
 *   Una vez se ha obtenido el número de puerto de la conexión, se añadirán los productos a la lista.
 *   Por último, se mostrará, si todo ha ido bien, los mensajes de se ha configurado el puerto y la lista de 
 *   productos que se pueden solicitar.
 * 
 * - Método actProductos:
 *   Este método se llamará des la clase ClientManager para actualizar los productos.
 *   Abrirá el fichero config.properties y solo obtendrá los datos del de los productos.
 *   Lo primero que hará es vaciar la lista y luego volver a cargar la nueva lista de productos.
 * 
 * - Getters:
 *   Habrá dos getters uno para devolver el número de puerto y otro para delvolver la lista de productos.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * @version 1.0.0
 * Fecha: 31-03-2025 
 */

package client;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConfigClient {
    // Se instancia los atributos
    private int port;
    private List<String> listaProductos; // Se almacenarán los productos.
    private Properties properties; // Se contedrá los parámetros del propertie

    /**
     * Constructo de la clase.
     */
    public ConfigClient() {
        this.properties = new Properties(); // Se inicializa el properties
        this.listaProductos = new ArrayList<>(); // Se instancia la lista para almacenar los productos
    }

    /**
     * Método para configurar la clase cliente
     * Este método cargará el número de puerto y el generará la lista de productos
     */
    public void configurar() {
        // Se obtiene la ruta del fichero para obtener los parámetros de configuración.
        // se utiliza un try-with-Resource para controlar el cierre del fichero
        // automáticamente.
        try (FileInputStream fichero = new FileInputStream(new File("config.properties"))) {
            // se carga el fichero properties
            this.properties.load(fichero);
            // Obtenemos los valores de los parámetros y los convertimos en tipo entero.
            // Se asigna el número del puerto que se va a usar para el servidor
            this.port = Integer.parseInt(this.properties.get("num_port").toString());
            // Bucle for para rellenar la lista con con los productos
            for (Object key : properties.keySet()) {
                // Si el key es distinto a num_port, se registra en la lista.
                if (!key.equals("num_port")) {
                    // Se obtiene el producto del propertie
                    listaProductos.add(properties.getProperty(key.toString()));
                }
            }

            // Se informa que se ha configurado del cliente y se mostrarán los dispositivos
            // disponibles que se pueden solicitar.
            System.out.println("Se ha configurado el servidor");
            // Bucle para mostar los datos.
            listaProductos.forEach((value) -> {
                System.out.println("El dispositivo " + value + " se encuentra disponible para ser solicitado");
            });

        } catch (Exception e) {
            // Mensaje en caso de error
            String mensaje = "Error al configurar el servidor:-> " + e.getMessage();
            // Se devuelve un mensaje en caso de error.
            System.out.println(mensaje);
        }

    }

    /**
     * Método para actualizar los productos
     */
    public void actProductos() {
        // Se obtiene la ruta del fichero para obtener los parámetros de configuración.
        // se utiliza un try-with-Resource para controlar el cierre del fichero
        // automáticamente.
        try (FileInputStream fichero = new FileInputStream(new File("config.properties"))) {
            // Se carga el fichero
            properties.load(fichero);
            // Se limpia el fichero para cargar los nuevos datos tras la actualización de
            // inventario.
            this.listaProductos = new ArrayList<>();
            // Bucle for para rellenar la lista con el key que será el dispositivo y
            // el valor será el Stock disponible del mismo
            for (Object key : properties.keySet()) {
                // Si el key es distinto a num_port, se registra en la lista.
                if (!key.equals("num_port")) {
                    // Se asigna como key el nombre del dispositivo.
                    listaProductos.add(properties.getProperty(key.toString()));
                }
            }
            System.out.println("Se ha actualizado la lista de productos.");

            listaProductos.forEach((value) -> {
                System.out.println("El dispositivo " + value + " se encuentra disponible para ser solicitado");
            });

            // En caso de error se devuelve un mensaje
        } catch (Exception e) {
            // Mensaje de error
            String mensaje = "Error al actualizar los nuevo rango de control de temperatura:-> " + e.getMessage();
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
     * Getter para devolver la lista de productos
     * 
     * @return se devuelve un Map<String, Integer> con los productos
     */
    public List<String> getProductos() {
        return listaProductos;
    }
}
