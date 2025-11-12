/**
 * Clase ConfiServer
 * 
 * Está clase nos permiritará realizar la configuración del server con los parámetros 
 * del config.properties.
 * 
 * El config.properties contendrá los siguientes parámetros:
 * - num_port: esta clave contendrá el número del puerto de conexión del servidor.
 * - productoN: habrá tantas claves como se necesiten regitrar por podructos.
 *              El valor de está clave contedrá el nombre del dispositivo más la cantidad en Stock.
 *              Estarán separados el dispositivo y la cantidad por una coma.
 * 
 * Cuando se ejecute por primera vez el servidor se configurará el número del puerto y los porductos 
 * serán almacenados en la lista de tipo Map<> que tendrá la clave que será el producto y el valor 
 * será la cantidad.
 * 
 * Esta clase contiene 5 métodos
 * - Metódo constructor de la clase. Recibe un parámetro es el que permitirá realizar los registros en el logger
 * - Método configurar. Este método obtendrá los datos del fichero config.properties que permite configurar
 *   el puerto, y cargar los productos en una lista Map<>.
 * - Método para actualizar la lista de productos. Este método limpia los datos de la lista y carga los datos 
 *   nuevos del properties. se entiende que cuando se actualicen estos datos, se han actualizado los stock y/o
 *   productos que existen en el almacén principal.
 * - Getter para devolver el número del puerto.
 * - El último método devuelve la lsita de productos con el stock.    
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * @version 1.0.0
 * Fecha: 31-03-2025 
 */

package servidor;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigServer {

    // Se instancia los atributos
    private int port; // almacenará el número del puerto de conexión.
    private Map<String, Integer> listaProductos; // Se almacenarán los productos y sus cantidades
    private Properties properties; // Se contedrá los parámetros del propertie
    private RegistroLogs logger; // Se para almacenar el logger

    /**
     * Constructo de la clase.
     */
    public ConfigServer(RegistroLogs logger) {
        this.properties = new Properties(); // Se inicializa el properties
        this.listaProductos = new HashMap<>(); // Se instancia la lista para almacenar los productos
        this.logger = logger; // Se recibe el logger para los registros.
    }

    /**
     * Método para configurar el servidor.
     * Leerá el fichero config.properties y utilizará los parámetros que contiene
     * para configurar el servidor y se cargará la lista de productos con la
     * cantidad de Stock disponible de los productos.
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
            // Se llama al método actProductos para obtener los productos y almacenarlos en
            // la lista.
            actProductos();
            // Se informa que se ha configurado el serividor y se mostrarán los dispositivos
            // disponibles en el almacén principal
            // así como la cantidad disponible de los mismos.
            System.out.println("Se ha configurado el servidor");

        } catch (Exception e) {
            // Mensaje en caso de error
            String mensaje = "Error al configurar el servidor:-> " + e.getMessage();
            // Se registra el error
            logger.alertServer(mensaje);
            // Se devuelve un mensaje en caso de error.
            System.err.println(mensaje);
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
            // Es caso de que el fichero contenga información se limpiará el fichero
            // para cargar los nuevos datos tras la actualización de inventario.
            if (!listaProductos.isEmpty()) {
                this.listaProductos = new HashMap<>();
            }

            // Bucle for para rellenar la lista con el key que será el dispositivo y
            // el valor será el Stock disponible del mismo
            for (Object key : properties.keySet()) {
                // Si el key es distinto a num_port, se registra en la lista.
                if (!key.equals("num_port")) {
                    // Se convierte almacena el valor completo del key del properties
                    String prodCantidad = properties.getProperty(key.toString());
                    // Se utiliza split para separar el valor obtenido
                    String separado[] = prodCantidad.split(",");

                    // Se controla que la cantidad no tenga espacios en blanco
                    String numero = separado[1].replaceAll(" ", "");
                    // Se comprueba que el valor se un número para convertirlo en entero.
                    if (numero.matches("\\d+")) {
                        // Si valor de cantidad es numérico se almacenará en la lista
                        // Se asigna como key el nombre del dispositivoo y el value se convertirá
                        // en un valor entero y será la cantidad registrada.
                        listaProductos.put(separado[0], Integer.parseInt(numero));
                    } else {
                        // En caso de que la cantidad no sea numérica se mostará un mensaje informado al
                        // usuario
                        System.err.println(
                                "Compruebe que la cantidad del producto " + separado[0] + " tenga un valor numérico");
                    }
                }
            }

            listaProductos.forEach((key, value) -> {
                System.out.println("El dispositivo " + key + " tiene un Stock de " + value + " unidades");
            });

            // En caso de error se devuelve un mensaje
        } catch (Exception e) {
            // Mensaje de error
            String mensaje = "Error al actualizar la lista de productos:-> " + e.getMessage();
            // Se registra el error
            logger.alertServer(mensaje);
            // Se devuelve un mensaje en caso de error.
            System.err.println(mensaje);
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
    public Map<String, Integer> getProductos() {
        return listaProductos;
    }
}
