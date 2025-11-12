/**
 * Clase ServerManager
 * 
 * Esta clase servirá como constructor del servidor.
 * 
 * Esta clase establecerá la conexión del servidor, y estará pendiente de las conexiones de los clietnes. 
 * Además, gestionará los pedidos y el stock del almacén principal.
 * 
 * - Método que star():
 *   Realizará la conexión del servidor y estará a la escucha de las conexiones de los clietnes. Y en 
 *   caso de que se realice algún cambio en el fichero config.properties, realizará la actualización de
 *   los productos y stock del almacén.
 * 
 * - Método realizarPedido():
 *   Este método realizará el pedido, y devolverá un mensaje al cliente.
 * 
 * - Método comprobarStock():
 *   Este método devolovera "Aceptado", "Rechazado" o "No Existe", según el contenido del almacén.
 * 
 * - Método existeProducto():
 *   Este método devolver un valor booleano si el producto existe o no.
 * 
 * - Método serviceWatch():
 *   Este método es un hilo que estará dormido hasta que se realice algún cambio en el fichero config.properties.
 * 
 * - Método actualizarLista():
 *   Este método actualizará la lista de productos cuando se modifique el fichero config.properties.
 * 
 * - Método shutdown():
 *   Finalizará el servidor y el hilo de clietnes.  
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * @version 1.0.0
 * Fecha: 31-03-2025 
 */

package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerManager {

    // Se instancia la clase ConfigServer para configurar el servidor.
    private ConfigServer config;
    // Se crea la lista de clientes
    private ClientRegistry listaClientes;
    // Se instancia un objeto Cliente
    private ClientHandler cliente;
    // Se instancia una variable que almacenará el puerto
    private final int PORT;
    // Se instancia un serversocket
    private ServerSocket serversocket;
    // Se instancia un objeto de la clase RegistroLogs
    private RegistroLogs logger;
    // Se almacenan los productos
    private Map<String, Integer> productos;
    // Se instancia una cola
    private final ExecutorService threadPool;
    // Se instancian para actualizar los pedidos
    private boolean actuliza;
    // Variable para evitar que se realice dos veces la actulización.
    private static long ultimaActualización = 0;

    /**
     * Contructor de la clase
     */
    public ServerManager() {
        // Se inicializa el registro para las alertas
        this.logger = new RegistroLogs();
        // Se instancia la configuración del servidor
        this.config = new ConfigServer(logger);
        // Se sonfigura el servidor
        config.configurar();
        // Se le asigna el puerto que se va a usar
        this.PORT = config.getPort();
        // Se instancia un pool para ejecutar los hilos
        this.threadPool = Executors.newCachedThreadPool();
        // Se instnacia una lista de clientes
        this.listaClientes = new ClientRegistry();
        // Se obtiene la lista de productos
        this.productos = config.getProductos();
        // Se ejecuta un hilo para controlar los cambios dinámicos de las temperaturas
        // de control.
        serviceWatch();
    }

    /**
     * Método para ejecutar el servidor.
     */
    public void start() {
        // Se capturan los errores
        try {
            // Se inicia el serividor
            serversocket = new ServerSocket(PORT);
            // Se informa por pantalla aque se ha ejecutado el servidor y el puerto.
            logger.regInicioServidor(PORT);
            System.out.println("El servidor esta a inciado en el puerto " + PORT);
            // Bucle que se mantiene activo mientras el servidor este ejecutado.
            while (!serversocket.isClosed()) {
                // Se instancia un socket para conectarse con los clientes
                Socket clientSocket = serversocket.accept();
                // Se informa de que se ha conectado un cliente
                System.out.println("Se ha conectado un nuevo cliente: " + clientSocket.getLocalSocketAddress());
                // Se registra un nuevo cliente
                cliente = new ClientHandler(clientSocket, listaClientes, logger, this);
                // Ejecutamos el pool con los clientes.
                threadPool.execute(cliente);
                // Control de flujo para actualizar los datos de control si se ha modificado el
                // fichero config.properties.
                if (actuliza) {
                    // Se llama al método que actulizará los datos de control de temperatura.
                    actualizarLista();
                    // Se vuelve a asiganar false a la variable para no ejecutar el proceso hasta
                    // que se vuelve a modificar el fichero.
                    actuliza = false;
                }

            }
            // cuando se finaliza el proceso se finaliza el servidor.
            shutdown();
            // Si surje un error al inciar el servidor se mostrará el mensaje de error.
        } catch (IOException e) {
            // Mensaje de error
            String mensaje = "Error al inicar el server (IOException): -> " + e.getMessage();
            // Se registra el error
            logger.alertServer(mensaje);
            // Se imprime el error
            System.err.println(mensaje);
        } catch (Exception e) {
            // Mensaje de error
            String mensaje = "Error al inicar el server (Exception): -> " + e.getMessage();
            // Se registra el error
            logger.alertServer(mensaje);
            // Se imprime el error
            System.err.println(mensaje);
        }
    }

    /**
     * Método que procesa el pedido y envia el mensaje al cliente.
     * 
     * @param pedido    se recibe el pedido que ha realizado.
     * @param idalmacen se recibe el identificador del almacen que ha realizado el
     *                  pedido.
     * @return se devuelve un mensaje informando de la situación del pedido.
     */
    public String realizarPedido(String pedido, String idalmacen) {
        // Variable para almacenar el mensaje que se enviará
        String mensaje;
        String aux;
        // Lista para almacenar el pedido y la cantidad
        String solicitud[] = pedido.split(", ");
        // variable que almacena el producto solicitado
        String producto = solicitud[0];
        // Variable que almacena la cantidad solicita en un valor entero
        int cantidad = Integer.parseInt(solicitud[1]);

        // Se recibe el mensaje del que se recibe del método comprobarStock
        aux = comprobarStock(producto, cantidad);

        if (aux.equals("ACEPTADO")) {
            mensaje = "Su pedido ha sido realizado";
            System.out.println("Peido procesado: " + producto + " - cantidad: " + cantidad + " - " + aux);
            // Se registra el pedido en el logger.
            logger.regPedidos(idalmacen, producto, cantidad, aux);
        } else if (aux.equals("RECHAZADO")) {
            aux = aux + " (Stock insuficiente)";
            mensaje = "Su pedido ha sido rechazado por falta de stock";
            System.out.println("Peido procesado: " + producto + " - cantidad: " + cantidad + " - " + aux);
            // Se registra el pedido en el logger.
            logger.regPedidos(idalmacen, producto, cantidad, aux);
        } else {
            // Se envía un mensaje informando que el producto solicitado no existe.
            mensaje = "Su pedido no se puede realizar porque no existe el producto solicitado.";
            System.out.println("No se puede realizar el pedido debido a que no se dispone del producto en el almacén.");
            logger.pedidoErroneo(idalmacen, mensaje);
        }

        // se devuelve el mensaje
        return mensaje;
    }

    /**
     * Método para comprobar si hay stock y realizar el pedidio.
     * 
     * Si hay stock se realiza el pedido y se indicará que se ha realizado el
     * pedido.
     * Si no hay stock se enviará el mensaje deciendo que no se puede
     * realizar su solicitado.
     * 
     * @param productoSolicitado se recibe el producto solicitado
     * @param cantidadSolicitada se recibe la cantidad solicitada
     * @return se devuelve un mensaje con la operación realizada.
     */
    public String comprobarStock(String productoSolicitado, int cantidadSolicitada) {

        boolean existe = existeProducto(productoSolicitado);

        // Variable que almacena el mensaje
        String mensaje;
        // Si el producto solicitado existe se realiza el pedido
        if (existe) {
            // Variable que obtendrá la cantidad de stock que hay del producto solicitado en
            // el alamacén
            int cantidad = productos.get(productoSolicitado);

            // Si la cantidad es mayor o igual a la cantidad solicitada
            if (cantidad >= cantidadSolicitada) {
                // Se resta la cantidad de stock por la cantidad solicitada
                cantidad = productos.get(productoSolicitado) - cantidadSolicitada;
                // Se registra la nueva cantidad que queda tras realizar el pedido.
                productos.put(productoSolicitado, cantidad);
                // Mensaje que indica que se ha procesado el pedido
                mensaje = "ACEPTADO";
            } else {
                // Si el stock es menor se informa de que no se puede realizar el pedido
                mensaje = "RECHAZADO";
            }
            // Si no existe se devuelve que no existe el producto
        } else {
            mensaje = "NO EXITE";
        }

        // Se devuelve el mensaje
        return mensaje;
    }

    /**
     * Método para comprobar que exista el producto en la lista
     * 
     * @param producto se recibe el producto solicitado
     * @return se devuelve un valor booleando según el resultado obtendio.
     */
    public boolean existeProducto(String producto) {
        boolean existe = false;

        if (productos.containsKey(producto)) {
            existe = true;
        }
        return existe;
    }

    /**
     * Método que inicia un hilo dedicado a controlar los cambios del fichero
     * config.properties. Esto nos permitirá contorlar los cambios y actualizar
     * el stock de productos del almacén principal.
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
                                config.actProductos();
                                // Se actualiza la lista de productos.
                                actualizarLista();
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
                // Se registra el error
                logger.alertServer(mensaje);
                // Se imprime el mensaje
                System.err.println(mensaje);
            } catch (InterruptedException e) {
                // Mensaje de error
                String mensaje = "Error en el método de configuración dinámica (Exception):->" + e.getMessage();
                // Se registra el error
                logger.alertServer(mensaje);
                // Se imprime el error
                System.err.println(mensaje);
            }
        });

    }

    /**
     * Método que actualizará la lista de productos.
     * Este método será llamado cuando se realice algún cambio en el
     * config.properties.
     */
    public void actualizarLista() {
        // Se limpia la lista de productos
        this.productos = new HashMap<>();
        // Se carga con el nuevo stock de productos
        this.productos = config.getProductos();
    }

    /**
     * Método para finalizar el servidor.
     */
    public void shutdown() {
        // Se controlan los errores
        try {
            // Si el servidor esta conectado
            if (serversocket != null) {
                // Se finaliza el servidor.
                serversocket.close();
            }
            // Se vinaliza el pool de hilos
            threadPool.shutdown();
            // En caso de error
        } catch (IOException e) {
            // Mensaje de error
            String mensaje = "Error al cerrar el server:-> " + e.getMessage();
            // Se registra el error
            logger.alertServer(mensaje);
            // Se imprime el error
            System.err.println(mensaje);
        }
    }

}