/**
 * Clase ClientHandler
 * 
 * Esta clase obtendrá las solicitudes GET y se envirán las páginas web que se solicitán.
 * También se obtendrá las imagenes que usan las web y se pintará en el navegador.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * @version 1.0.0
 * Fecha 08/05/2025
 */

package server;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;

public class ClientHandler implements Runnable {

    // Socket para almacenar el socket del cliente.
    private Socket socket;
    // Objeto para gestionar las respuestas
    private WebSites selPaSites;

    /**
     * Constructor para inicializar los atributos.
     * 
     * @param socket se recibe el socket del cliete.
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.selPaSites = new WebSites();
    }

    /**
     * Método Runnable para gestionar la lógica princiapa de procesamiento de
     * peticiones HTTP
     */
    @Override
    public void run() {
        // Método para leer la peticiones del cliente - se controla a través de un
        // try-with-out.
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream out = socket.getOutputStream()) {

            // Se almacena la línea recibida.
            String requestLine = in.readLine();

            // Si la línea es distita a null y comienza con "GET"
            if (requestLine != null && requestLine.startsWith("GET")) {
                // Se muestra la peticón recibida por el cliente
                System.out.println("Petición: " + requestLine.toString());
                // Se obtiene el paht para la página que se va a mostrar.
                String path = requestLine.split(" ")[1];
                // Si se recibe un petición de resource
                if (path.startsWith("/resource/")) {
                    // Se llama al método para obtener las imágenes para la web.
                    servirArchivoEstatico(path, out);
                    // De lo contraio
                } else {
                    // Se obtiene la pagina a mostrar
                    String response = selPaSites.obtenerRespuesta(path);
                    // Se envía la respuesta recibida.
                    out.write(response.getBytes());
                }
            }
            // Se controal las excepciones
        } catch (IOException e) {
            e.printStackTrace();
            // Se finaliza si o si la conexión
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Método para obtener las imágenes y devolverlas según la página solicitada.
     * 
     * @param path recibe la ruta
     * @param out  se recibe un OutputStream para poder pintar en la web.
     */
    private void servirArchivoEstatico(String path, OutputStream out) {
        try {
            // Directorio raíz donde están las imágenes, ajusta si cambia tu estructura
            String filePath = "src/html" + path;
            Path archivo = Paths.get(filePath);

            // Se comprueba si existe el archivo solicitado.
            if (!Files.exists(archivo)) {
                // Se llama al método para devolver error 404
                enviar404(out);
                // Se finaliza el método
                return;
            }

            // Se obtiene archivo
            String contentType = Files.probeContentType(archivo);
            // Si el contenido es null se le asigan el tipo
            if (contentType == null)
                contentType = "application/octet-stream";

            // Se almacena los bytes del archivo.
            byte[] contenido = Files.readAllBytes(archivo);

            // Se construye la cabecera HTTP
            PrintWriter writer = new PrintWriter(out, false);
            // Protocolo de conexión y estado
            writer.print("HTTP/1.1 200 OK\r\n");
            // tipo de contenido
            writer.print("Content-Type: " + contentType + "\r\n");
            // Longitud del contenido
            writer.print("Content-Length: " + contenido.length + "\r\n");
            // Líenas en blaco obligatoria
            writer.print("\r\n");
            // Nos aseguramos que se envien los datos inmediatamente.
            writer.flush();

            // Se envía el contenido
            out.write(contenido);
            // Nos aseguramos que se envien los datos inmediatamente.
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para devolever un error 404
     * 
     * @param out se recibe un OutputStream para poder pintar en la web.
     * @throws IOException se controlan las excepciones
     */
    private void enviar404(OutputStream out) throws IOException {
        // Se contruye el HTML a mostrar
        String mensaje = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n"
                + "<html><body><h1>404 - Archivo no encontrado</h1></body></html>";
        // Se envia el mensaje
        out.write(mensaje.getBytes());
    }
}