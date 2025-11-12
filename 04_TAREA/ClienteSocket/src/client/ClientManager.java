/**
 * Clase Client (main para ejecutar los clientes)
 * 
 * Esta clase gestionará el entorno del almacen que solicitará al almacen principal 
 * los pedidos.
 * 
 * En esta clase se controlar lo siguiente:
 *  - Contiene el método para conectarse y comunicarse con el servidor.
 *  - El almacen podrá realizar una solicitud de productos aleatorios cada 5 o 10 segundos.
 *  - Los pedidos incluyen el nombre del producto y una cantidad aleatoria.
 *  - recibirá la confirmación o rechazo del pedido y se mostrará por pantalla.
 *  - Se creará un hilo que estará a la espera de que se cambien el archivo config.properties.
 *    Esto se hace por si en el almacén principal se obtienen nuevos productos y si se elimina. 
 *    Así, solo con actulizar el config.properties se actualiza el listado de productos sin tener 
 *    que reiniciar el cliente. 
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * @version 1.0.0
 * Fecha: 31-03-2025 
 * 
 */

package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientManager {
    // Se instancia la configuración del servidor.
    private ConfigClient configClient = new ConfigClient();
    // Se instancia una variable para controlar los cambios y evitar que se repita
    // la actualización debido al SO.
    private long ultimaActualización;
    private List<String> productos;
    private final int PORT;
    private final String HOST;
    private Random random;
    private String producto;

    /**
     * Conmtructor de la clase
     */
    public ClientManager() {
        // Se inicia la configuración
        configClient.configurar();
        this.HOST = "localhost"; // Se indica el host para la conexión.
        this.PORT = configClient.getPort(); // Se obtiene el puerto de config.properties
        this.productos = configClient.getProductos(); // Se obtine la lista de productos de config.properties
        this.ultimaActualización = 0; // Se inicializa en 0 la última actualización
        this.random = new Random(); // Se inicializa el ramdon para obtener los números aleatorios
        this.producto = ""; // Se inicializa el producto en ""
        // Se ejecuta el hilo para controlar los cambios de la lista de productos.
        serviceWatch();
    }

    /**
     * Método para ejecutar el cliente
     * Recibirá el mensaje del cliente y se mostrará por pantalla.
     * Se generará automáticamente el pedido y se enviará al servidor (almacén
     * principal).
     * 
     * @throws InterruptedException se la interrución.
     */
    public void start() throws InterruptedException {
        try (Socket socket = new Socket(HOST, PORT);
                // Se instancian los métodos para enviar y recibir datos
                BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);) {

            // Se identifica la conexión.
            System.out.println("Me he conectado al Almacén principal " + PORT);

            Thread hilo = new Thread(() -> {
                // String que contendrá el mensaje que se recibe del cliente.
                String mensaje;
                try {
                    // Cuando se reciba algún dato
                    while ((mensaje = serverInput.readLine()) != null) {
                        // Se imprime el mensaje recibido.
                        System.out.println(mensaje);
                    }
                } catch (IOException e) {
                    // En caso de error se imprime el mensaje
                    System.out.println("Error al recibir mensaje -> " + e.getMessage());
                }
            });
            // Se ejecuta en el hilo
            hilo.start();

            // Bucle while para enviar los datos generados al servidor.
            while (true) {
                // Se generá un nuevo tiempo de espera aleatorio
                int seg = random.nextInt(5001) + 5000;
                // Se generá la cantidad de pedidos
                int cantidad = random.nextInt(60) + 1;
                // Se se genera un valor aleatorio para para seleccionar un producto.
                int numPro = random.nextInt(productos.size());
                // Se obtiene el producto que se va a pedir.
                producto = productos.get(numPro);

                // Si se producto no esta vacío se realiza el pedido.
                if (!producto.isEmpty()) {
                    // Se muestra el pedido solicitado
                    System.out.println(
                            "Se han solicitado '" + cantidad + "' unidades del producto '" + producto + "'");
                    // Se combina el producto y cantidad separados por una "," para
                    // poder separarlo de forma sencilla el producto de la cantidad
                    producto = producto + ", " + cantidad;
                    // Se envia la solicitud de pedido
                    serverOutput.println(producto);
                    Thread.sleep(seg); // Se duerme el hilo x segundos, antes de generar un nuevo pedido
                }

            }
            // En caso de error
        } catch (IOException e) {
            // Se imprime el mensaje de error
            System.out.println("Error al conectarse al servidor -> " + e.getMessage());
        }
    }

    /**
     * Método que inicia un hilo dedicado a controlar los cambios del fichero
     * config.properties. Esto nos permitirá contorlar los cambios y actualizar
     * el listado de los productos del almacén principal.
     */
    public void serviceWatch() {
        // Se crea un pool para un único hilo
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // Se ejecuta el hilo
        executor.submit(() -> {
            // Se controlan los errores.
            try {
                // Se instancia un WatchService para monitorear los eventos del sistema.
                WatchService service = FileSystems.getDefault().newWatchService();
                // Se instancia el fichero que se va a controlar.
                String file = "config.properties";
                // Se instancia la ruta a escuchar.
                Path path = Paths.get(".\\");
                // Se informa de que el hilo esta a la escucha de cualquier cambio.
                System.out.println("Estoy pendiente de que modifiques el fichero");
                // Se registra el directorio para detectar los eventos de modificación de
                // archivos.
                path.register(service, StandardWatchEventKinds.ENTRY_MODIFY);
                // Se crea una llave para almacenar los eventos
                WatchKey key = null;
                // Bucle que se mantiene activo a la espera de un evento
                // Con take() se bloquea el hilo hasta se se produzca un evento.
                while ((key = service.take()) != null) {
                    // Se itera sobre la lista de eventos generados por el cambio en el directorio
                    for (WatchEvent<?> event : key.pollEvents()) {
                        // Si el fichero cambiado en igual a config.properties, se realizará la
                        // actualización del stock.
                        if (file.equals(event.context().toString())) {
                            // Se obtiene el momento en el que se ha actuailzado el fichero
                            long tiempoTranscurrido = System.currentTimeMillis();
                            // si el tiempo transcurrido desde la última actualización es menor a 500
                            // milisegundos.
                            // De este modo se evita la repitición debido al sistema operativo.
                            if (tiempoTranscurrido - ultimaActualización > 500) {
                                // Se almacena el tiempo de la última actulización.
                                ultimaActualización = tiempoTranscurrido;
                                // Se informa de que se ha cambiado el fichero.
                                System.out.println("Se ha modificado :" + event.kind() + " Archivo " + event.context());
                                // Se llama al método que actualiza el stock en la configuración del servidor
                                configClient.actProductos();
                                // Se actualiza la lista de productos.
                                actProductos();
                            }
                        }
                    }

                    // Se reinicia la llave para obtener nuevos eventos.
                    key.reset();
                }

                // En caso de error
            } catch (IOException e) {
                // Mensaje de error
                String mensaje = "Error en el método de configuración dinámica:-> " + e.getMessage();
                // Se imprime el mensaje
                System.err.println(mensaje);
            } catch (InterruptedException e) {
                // Mensaje de error
                String mensaje = "Error en el método de configuración dinámica (Exception):->" + e.getMessage();
                // Se imprime el error
                System.err.println(mensaje);
            }
        });

    }

    public void actProductos() {
        this.productos = configClient.getProductos();
    }

}
