/**
 * Clase ServerManager que construye el servidor para conectar los clientes
 * 
 * Con esta clase construuiremos un servidor, el cual nos permitirá interactuar con los clientes que se conenecten con el.
 * 
 * Mediante el constructor configurarermos el servidor, obtetniendo la información de configuracióndel fichero
 * config.properties. Para obtener los datos del fichero, se utilizará la clase ConfigServer.
 * 
 * El método start() ejecutará el servidor y se mantendrá a la escucha del acceso de los clientes. Cuando un cliente se conecte 
 * el servidor lo registrará. En caso que se cambie el config.properties se actualizarán los datos.
 * 
 * El método comprobación() recibirá un parámetro de tipo double que será la temperatura generada por el clietnes (sensor). Según la 
 * configuración del servidor y la temperatura recibida del cliente se enviará un mensaje u otro.
 * 
 * Método calcTemMedia() realizará el calculo medio de todas las temperaturas recibidas de los sensores.
 * 
 * Método ServiceWathc() construye un único hilo que estará a la escucha de los cambios que se puedan producir en el fichero 
 * config.properties. En el caso de haberse producido se realizará el cambio de los parámetros de control de temperatura.
 * 
 * Método actualizarTemperatura() actualizará las temperaturas de control de la clase, tambien mostrará un mensaje informado de ello.
 * 
 * Método shutdown() finalizará el serividor y el finalizará también el poll de los hilos.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * Fecha: 24-02-2025
 * Version: 1.0.0
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerManager {

    // Se instancia la clase ConfigServer para configurar el servidor.
    ConfigServer config;
    // Se instancia una variable que almacenará el puerto
    private final int PORT;
    // Se instancia un serversocket
    private ServerSocket serversocket;
    // Se crea la lista de clientes
    ClientRegistry listaClientes;
    // Se instancia un objeto Cliente
    ClientHandler cliente;
    // Se instancia un objeto de la clase RegistroLogs
    private RegistroLogs logger;
    // Se instancia una cola
    private final ExecutorService threadPool;
    // Lista para almacenar todas las temperaturas que se reciban de los sensores.
    private List<Double> listaTemp;
    // Se instancian los parámetros de las temperaturas
    private double temp_max;
    private double temp_min;
    private double tempMedia;
    private boolean actuliza;
    // Variable para evitar que se realice dos veces la actulización.
    private static long ultimaActualización = 0;
    // Formato de para mostrar dos decimales
    DecimalFormat df = new DecimalFormat("#.##");

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
        // Se instancia una lista para almacenar las temperaturas de los sensores
        this.listaTemp = new ArrayList<>();

        // Se configuran las temperaturas de control
        this.temp_max = config.getTempMax();
        this.temp_min = config.getTempMin();
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
            // Se informa por pantall aque se ha ejecutado el servidor y el puerto.
            logger.regInicioServidor(PORT);
            System.out.println("El servidor esta a inciado en el puerto " + PORT);
            // Bucle que se mantiene activo mientras el servidor este ejecutado.
            while (!serversocket.isClosed()) {
                // Se instancia un socket para conectarse con los clientes
                Socket clientSocket = serversocket.accept();
                // Se informa de que se ha conectado un cliente
                System.out.println("Se ha conectado un nuevo cliente: " + clientSocket);
                // Se registra un nuevo cliente
                cliente = new ClientHandler(clientSocket, listaClientes, logger, this);
                // Ejecutamos el pool con los clientes.
                threadPool.execute(cliente);
                // Control de flujo para actualizar los datos de control si se ha modificado el
                // fichero config.properties.
                if (actuliza) {
                    // Se llama al método que actulizará los datos de control de temperatura.
                    actualizarTemperatura();
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
     * Método para comprobar que la temperatura esta dentro de los valores.
     * 
     * @param temperatura se recibe la temperatura que ha enviado el servidor.
     * @return se devuelve un mensaje de tipo String
     * 
     */
    public String comprobación(double temperatura, String sensor) {
        // Se agrega la temperatura recibida a la lista
        listaTemp.add(temperatura);
        String mensaje = null; // Variable inicializada con null, para devolver el mensaje.
        String alert; // variable para almacenar los mensajes de registro
        // Si la temperatura es menor a la configurada
        if (temperatura < temp_min) {
            // Mensaje que se muestra para cuando la temperatura es menor a la establecida
            alert = "El sensor " + sensor + " esta por debajo de la temperatura mínima establecida en "
                    + df.format(temp_min)
                    + " ºC";
            // Se muestra el mensaje de la alerta
            System.err.println(alert);
            // Se registrran las alertas de temperaturas con error.
            logger.regAlertasTemp(alert);
            // se devuelve al servidor el mensaje informado que la temperatura esta por
            // debajo de los parámetros
            mensaje = "la temperatura es menor a la temperatura mínima establecida en " + df.format(temp_min) + " ºC";
            // Si la temperatura es mayor a la configurada
        } else if (temperatura > temp_max) {
            // Mensaje que se muestra para cuando la temperatura es mayor a la establecida
            alert = "El sensor " + sensor + " ha superado la temperatura máxima establecida en " + df.format(temp_max)
                    + " ºC";
            // Se muestra el mensaje de la alerta
            System.err.println(alert);
            // Se registrran las alertas de temperaturas con error.
            logger.regAlertasTemp(alert);
            // se devuelve al servidor el mensaje informado que la temperatura esta por
            // encima de los parámetros
            mensaje = "la temperatura es mayor a la temperatura máxima establecida en " + df.format(temp_max) + " ºC";
        }

        // Se calcula la media global de temperaturas de todos los sensores.
        calcTemMedia();
        // Se devuelve el mensaje.
        return mensaje;
    }

    /**
     * Método se calcula la media general de las temeperaturas recibidas de todos
     * los sensores
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
        System.out.println("La temperatura media general actual es de: " + df.format(tempMedia) + " ºC");
    }

    /**
     * Método que inicia un hilo dedicado a controlar los cambios del fichero
     * config.properties. Esto nos permitirá
     * contorlar los cambios y actualizar los valores de control de la temperatura.
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
                System.out.println("Estoy a pendiente de que modifiques el fichero");

                // Se registra el directorio para detectar los eventos de modificación de
                // archivos.
                path.register(service, StandardWatchEventKinds.ENTRY_MODIFY);
                // Se crea una llave para almacenar los eventos
                WatchKey key = null;
                // Bucle que se mantiene activo a la espera de un evento
                // Con take() se bloquea el hilo hasta se se produzca un evento.
                while ((key = service.take()) != null) {
                    // Se intera sobre la lista de eventos generados por el cambio en el directorio
                    for (WatchEvent<?> event : key.pollEvents()) {

                        // Si el fichero cambiado en igual a config.properties, se realizará la
                        // actualización de temperaturas de control.
                        if (file.equals(event.context().toString())) {
                            // Se obtiene el momento en el que se ha actuailzado el fichero
                            long tiempoTranscurrido = System.currentTimeMillis();
                            // si el tiempo transcurrido desde la última actualización
                            // de este modo se evita la repitición debido al sistema operativo.
                            if (tiempoTranscurrido - ultimaActualización > 500) {
                                // Se almacena el tiempo de la última actulización.
                                ultimaActualización = tiempoTranscurrido;
                                // Se informa de que se ha cambiado el fichero.
                                System.out.println("Se ha modificado :" + event.kind() + " Archivo " + event.context());
                                // Se llama al método que actualiza la temperaturas de configuración del
                                // ConfigServer.
                                config.updateTemp();
                                // Se actualizan las temperaturas del servidor.
                                actualizarTemperatura();
                                // Se informa que se han actualizado las temperaturas
                                System.out.println("Se han actualizado los intervalos de temperaturas");
                            }

                        }
                    }
                    // Se reinicia la llave para obtener nuevos eventos.
                    key.reset();

                }
                // En caso de error
            } catch (IOException e) {
                // Mensaje de error
                String mensaje = "Error en el método de configuración dinámica (IOException):-> " + e.getMessage();
                // Se registra el error
                logger.alertServer(mensaje);
                // Se imprime el mensaje
                System.err.println(mensaje);
            } catch (InterruptedException e) {
                // Mensaje de error
                String mensaje = "Error en el método de configuración dinámica (Exception):-> " + e.getMessage();
                // Se registra el error
                logger.alertServer(mensaje);
                // Se imprime el error
                System.err.println(mensaje);
            }
        });
    }

    /**
     * Método para actualizar la temperatura cuando se haya realizado un cambio en
     * config.properties.
     */
    public void actualizarTemperatura() {
        // Se cambia el nuevo valor de las temperaturas de control.
        this.temp_max = config.getTempMax();
        this.temp_min = config.getTempMin();
        // Mensaje de infromando de la nuevas temperaturas.
        System.out.println("La nueva temp_max :" + df.format(temp_max) + " ºC");
        System.out.println("La nueva temp_min :" + df.format(temp_min) + " ºC");
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
