/**
 * Clase Client (main para ejecutar los clientes)
 * 
 * El cliente se conectará al localhost y el puerto que esta configurado en el cliente.properties, de este
 * modo se podrá configurar más adelante el puerto sin tener que hacerlo en el código.
 * 
 * Esta clase sirve para generar las temperaturas de forma aleatoria mediante Random,
 * esté esta predefinido para que genere números enteros entre el 0 y el 45.
 * 
 * Una vez se haya generado la temperatura aleatoria se enviará al servidor, y estará a la escucha hasta 
 * que el servidor le devuelva algún mensaje.
 * 
 * 
 */
package cliente;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.Random;

public class Client {

    /**
     * Método main para ejecutar el cliente
     * 
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        final String HOST = "localhost"; // Se instancia una variable con el HOST al que se conectará el clietne
        final int PORT = configurar(); // Se instncia el PUERTO utilizando la configuración del properties.
        Random random = new Random(); // Se instancia un método Random.
        DecimalFormat df = new DecimalFormat("#.##"); // Se instancia el formato que tendrá las temperaturas
        // Se crear el socket con los parámetros
        try (Socket socket = new Socket(HOST, PORT);
                // Se instancian los métodos para enviar y recibir datos
                BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);) {

            // Si esta conectado el cliente
            if (socket.isConnected()) {
                // Se identifica la conexión.
                System.out.println("Me he conectado al puerto " + PORT);

                // Se craea un hilo para enviar y recibir datos entre el cliente y el servidor
                new Thread(() -> {
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
                    // Se ejecuta el error.
                }).start();
                // Bucle while para enviar los datos generados al servidor.
                while (true) {

                    // Método para comprobar que el servidor sigua funcionando si se envía un valo
                    // no númerico
                    // char temperatura = (char) ('A' + random.nextInt(26));
                    int temperatura = random.nextInt(50); // Se genera una temperatura
                    // Se muestra la temperatura generda por consola
                    System.out.println("La temperatura es " + df.format(temperatura) + " ºC");
                    serverOutput.println(temperatura); // Se envia la temperatura al servidor
                    Thread.sleep(10000); // Se duerme el hilo 10 segundos, antes de generar la nueva temperatura
                }

            }
            // En caso de error
        } catch (IOException e) {
            // Se imprime el mensaje de error
            System.out.println("Error al conectarse al servidor -> " + e.getMessage());
        }
    }

    /**
     * Método para configurar el cliente
     * 
     * @return se devuelve el puerto con un valor de tipo entero
     */
    public static int configurar() {
        // Variable para devolver el puerto
        int port = 0;
        // Se instancia un porperties para obtener las propiedades del fichero
        // cliente.properties
        Properties propiedades = new Properties();

        // Se obtiene la ruta del fichero para obtener los parámetros de configuración.
        // se utiliza un try-with-Resource para controlar el cierre del fichero
        // automáticamente.
        try (FileInputStream fichero = new FileInputStream(new File("cliente.properties"))) {
            // se carga el fichero properties
            propiedades.load(fichero);
            // Obtenemos los valores de los parámetros y los convertimos en tipo entero.
            // Se asigna el número del puerto que se va a usar para el servidor
            port = Integer.parseInt(propiedades.get("num_port").toString());
            // Número de clientes permitidos

        } catch (IOException e) {
            // se devuelve un mensaje en caso de error.
            System.out.println("Error->: " + e.getMessage());
        }

        // Se devuelve el puerto de la conexión.
        return port;
    }
}