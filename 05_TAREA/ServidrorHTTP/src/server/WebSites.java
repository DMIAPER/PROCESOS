/**
 * Clase WebSites
 * 
 * En esta clase se controlarán las solicitudes y se comprobarán si existen las web solicitas.
 * Si existen estan son leidas y se devuelven para que el clientehandler las pinte en la web.
 * 
 * También existe un método que permite obtener de una API un chiste que se mostrará cuando el 
 * usuario solicite la página curiosidad.
 * 
 * @author DMIAPER (Diógenes Miaja Pérez)
 * @version 1.0.0
 * Fecha 08/05/2025
 */

package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;

public class WebSites {

    // Ruta para obtener los ficheros HTML que devolverá el servidor
    public final String HTML_DIR = "src/html/"; // Ruta donde se encuentran los archivos HTML
    // Ruta para obtener chiste desde una API.
    private final String API_URL = "https://v2.jokeapi.dev/joke/Any?lang=es";

    /**
     * Método que devolverá la web seleccionada
     * 
     * @param pagina se recibe un String con el nombre del recurso solicitado.
     * @return
     */
    public String obtenerRespuesta(String pagina) {

        // Se establece el tipo de protocolo HTTP y el tipo 200 para establecer una
        // conexión exitosa
        String cabecera = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n";
        // Se estable el tipo de protocolo HTTT y el tipo 404 para indicar que no se ha
        // encontrado la página.
        String cabecera404 = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n";
        // Variable para almacenar el cuerpo del HTML que se va a mostrar.
        String cuerpo;

        // Control de flujo switch para selecionar la página web a mostar.
        switch (pagina) {
            // Si se recibe "/" se mostrará el HTML index
            case "/":
                cuerpo = cargarCuerpo("index.html");
                // Se finaliza el switch
                break;
            // Si se recibe "/curiosidad" se mostrará una curiosidad.
            case "/curiosidad":
                // Llamamos a la API para obtener un chiste
                String curiosidad = obtenerCuriosidadDesdeAPI();
                // Se obtiene la web curiosidades y se remplaza el contenido por la curiosidad
                // (chiste)
                cuerpo = cargarCuerpo("curiosidades.html").replace("{{curiosidad}}", curiosidad);
                // Se finaliza el switch
                break;
            // Si se recibe "/contacto" se mostrará el HTML del contancto
            case "/contacto":
                cuerpo = cargarCuerpo("contacto.html");
                // Se finaliza el switch
                break;
            // En caso que se reciba un GET que no coincida con ningún HMTL de nuestra WEB
            // se mostrará el HTML de Error 404
            default:
                cuerpo = cargarCuerpo("error404.html");
                // Se finaliza el switch y se muestra la web error 404.
                return cabecera404 + cuerpo;
        }

        // Si el cuerpo está vacío o null, se muestra un error
        if (cuerpo == null || cuerpo.isEmpty()) {
            return cabecera404 + "<html><body><h1>Error al cargar la página.</h1></body></html>";
        }

        // Se devuelve el HTML completo para mostrar la WEB.
        return cabecera + cuerpo;
    }

    /**
     * Método para obtener de una API externa un chiste
     * 
     * @return se devuelve un string con el chiste obtenido.
     */
    private String obtenerCuriosidadDesdeAPI() {
        try {
            // Creamos la URI utilizando el método URI.create() y luego la convertimos a URL
            URI uri = URI.create(API_URL);
            // Se convierte el URI en URL
            URL url = uri.toURL();
            // Se establece la conexión
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // Tiempo de espera para la conexión
            connection.setConnectTimeout(5000);
            // Tiempo de espera para leer el get.
            connection.setReadTimeout(5000); // Timeout para leer

            // Se lee la respuesta de la API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            // Varaibel para obtener la linea
            String inputLine;
            // StringBuilder para almacenar todo los datos recibidos
            StringBuilder response = new StringBuilder();

            // Se recorren todas las líneas de la lectura
            while ((inputLine = in.readLine()) != null) {
                // Se almacenan todas las líneas en el StringBuilder.
                response.append(inputLine);
            }

            // se finaliza el BuferedReader
            in.close();

            // Se contruje un Json para con los daos recibidos.
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Se obtiene le tipo
            String tipo = jsonResponse.getString("type");

            // Control para obtener un chiste compuesto o un chiste simple
            if (tipo.equals("single")) {
                // Caso de chiste simple
                return "\"" + jsonResponse.getString("joke") + "\"";
            } else if (tipo.equals("twopart")) {
                // Caso de chiste en dos partes
                return "\"" + jsonResponse.getString("setup") + "\"<br><em>" + jsonResponse.getString("delivery")
                        + "</em>";
            }

            // En caso de que el tipo no sea conocido
            return "\"No hay chiste disponible.\"";

        } catch (IOException e) {
            // Si hay un error en la conexión o al leer la respuesta, mostramos un mensaje
            // error
            e.printStackTrace();
            return "Error al cargar el chiste desde la API.";
        }
    }

    /**
     * Método para cargar las páginas web según la opción seleccionada por el
     * usuario.
     * 
     * @param html Nombre del archivo HTML que se desea cargar.
     * @return El contenido completo de la página web.
     */
    private String cargarCuerpo(String html) {
        try {
            // Leemos la base del HTML
            String baseHTML = Files.readString(Paths.get(HTML_DIR + "base.html"));
            // Leemos el contenido de la página HTML solicitada
            String contenido = Files.readString(Paths.get(HTML_DIR + html));
            // Insertamos el contenido de la página dentro de la base
            return baseHTML.replace("{{content}}", contenido);
        } catch (IOException e) {
            // Si ocurre un error al leer los archivos, lo imprimimos en consola
            e.printStackTrace();
            // En caso de error, devolvemos una página de error
            return "<html><body><h1>Error al cargar el contenido de la página.</h1></body></html>";
        }
    }
}
