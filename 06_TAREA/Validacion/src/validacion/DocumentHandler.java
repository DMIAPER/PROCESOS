/**
 * DocumentHandler.java
 * 
 * Esta clase se encargará de validar los datos introducidos por el usuario, como el
 * nombre de usuario y el nombre del archivo. También se encargará de validar si el
 * archivo es accesible y si está dentro del directorio permitido.
 * 
 * @author DMIAPER (Diógenes Míaja Pérez)
 * @version 1.0.0
 * Fecha 2025-05-07
 */

package validacion;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class DocumentHandler {

    // Se declaran las variables de la clase
    private Registros registros;

    // Constructor de la clase DocumentHandler
    public DocumentHandler() {
        this.registros = new Registros();
    }

    /**
     * Solicita y valida el nombre de usuario.
     * 
     * @param scanner objeto Scanner para leer la entrada del usuario
     * @return nombre de usuario validado
     */
    public void solicitarUsuario(Scanner scanner) {
        // Se declara la variable usuario
        String usuario;
        // Se inicia un bucle infinito para solicitar el nombre de usuario
        while (true) {
            // Se solicita el nombre de usuario al usuario
            System.out.print("Introduce tu nombre de usuario (8 caracteres, solo letras minúsculas): ");
            // Se lee la entrada del usuario y se eliminan caracteres peligrosos
            usuario = limpiarEntrada(scanner.nextLine());
            // Se valida el nombre de usuario
            if (usuario.matches("[a-z]{8}")) {
                // Se indica que el nombre introducido es v
                System.out.println(String.format("El nombre que ha introducido es válido: ", usuario));
                // Si el nombre de usuario es válido, se registra en el log
                registros.registroLog("Usuario válido: " + usuario);
                // Se sale del bucle
                break;
                // Si el nombre de usuario no es válido, se muestra un mensaje de error
            } else {
                System.out.println(
                        "El nombre de usuario no es válido. Debe tener 8 caracteres y solo letras minúsculas.");
                // Se registra el intento de usuario inválido en el log
                registros.warningLog("Intento de usuario inválido: " + usuario);
            }
        }

    }

    /**
     * Solicita y valida el nombre del archivo.
     * 
     * @param scanner objeto Scanner para leer la entrada del usuario
     * @return nombre de archivo validado
     */
    public String solicitarArchivo(Scanner scanner) {
        // Se declara la variable nombreArchivo
        String nombreArchivo;
        // Se inicia un bucle infinito para solicitar el nombre del archivo
        while (true) {
            // Se solicita el nombre del archivo al usuario
            System.out.println(
                    "El nombre del archivo debe tener como máximo 8 caracteres, un punto y 3 caracteres para la extensión. ejemplo: archivo.txt");
            System.out.print("Introduce el nombre del archivo: ");
            // Se lee la entrada del usuario y se eliminan caracteres peligrosos
            nombreArchivo = limpiarEntrada(scanner.nextLine());
            // Se valida el nombre del archivo
            if (validarAccesoArchivo(nombreArchivo)) {
                // Si el nombre del archivo es válido, se registra en el log
                registros.registroLog("Nombre de archivo válido: " + nombreArchivo);
                // Se sale del bucle
                break;
            } else {
                // Se registra el intento de archivo inválido en el log
                registros.warningLog("Intento de archivo inválido: " + nombreArchivo);
            }
        }
        // Se devuelve el nombre del archivo validado
        return nombreArchivo;
    }

    /**
     * Método para mostrar el contenido de un archivo.
     * 
     * @param nombreArchivo nombre del archivo a mostrar
     */
    public void mostarContendioArchivo(String nombreArchivo) {
        // Se obtiene la ruta segura del archivo
        Path rutaArchivo = obtenerRutaSegura(nombreArchivo);
        // Se verifica si la ruta es nula
        if (rutaArchivo == null) {
            return;
        }
        // Se intenta leer el contenido del archivo
        try (BufferedReader br = Files.newBufferedReader(rutaArchivo)) {
            System.out.println("\nContenido del archivo:");
            String linea;
            // Se lee línea por línea el contenido del archivo
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
            }
            // Se registra en el log que el archivo se ha leído correctamente
            registros.registroLog("Archivo leído correctamente: " + nombreArchivo);
        } catch (IOException e) {
            // Si ocurre un error al leer el archivo, se muestra un mensaje de error y se
            // registra en el log
            System.err.println("Error al leer el archivo: " + e.getMessage());
            registros.warningLog("Error al leer el archivo: " + e.getMessage());
        }
    }

    /**
     * Método para eliminar caracteres peligrosos de la entrada del usuario.
     * 
     * @param input se recibe la entrada del usuario
     * @return se devuelve la entrada sin caracteres peligrosos
     */
    private String limpiarEntrada(String input) {
        return input.replaceAll("[<>\"'&]", "");
    }

    /**
     * Método para obtener la ruta segura del archivo.
     * 
     * @param nombreArchivo nombre del archivo a validar
     * @return ruta segura del archivo o null si no es válida
     */
    private Path obtenerRutaSegura(String nombreArchivo) {
        // Se obtiene la ruta base del proyecto
        String directorioBase = System.getProperty("user.dir") + "/src/documentos/";
        // Se crea la ruta del archivo
        Path rutaArchivo = Paths.get(directorioBase, nombreArchivo).normalize();
        // Se verifica si la ruta es válida
        if (rutaArchivo.toAbsolutePath().startsWith(directorioBase)) {
            return rutaArchivo;
        } else {
            return null;
        }
    }

    /**
     * Método para validar si un archivo es accesible y está dentro del directorio
     * permitido.
     * 
     * @param nombreArchivo nombre del archivo a validar
     * @return true si el archivo es accesible y seguro, false en caso contrario
     */
    private boolean validarAccesoArchivo(String nombreArchivo) {

        // Se compureba el formato
        if (!nombreArchivo.matches("^[a-zA-Z0-9]{1,8}\\.[a-zA-Z0-9]{3}$")) {
            System.out.println("El formato del nombre del archivo introducido no es válido.");
            return false;
        }

        // Se obtiene la ruta segura del archivo
        Path rutaArchivo = obtenerRutaSegura(nombreArchivo);
        // Se verifica si la ruta es nula
        if (rutaArchivo == null) {
            return false;
        }
        // Se registra en el log la verificación del archivo
        registros.registroLog("Verificando archivo en: " + rutaArchivo.toAbsolutePath());

        // Se verifica si el archivo existe
        if (!Files.exists(rutaArchivo)) {
            // Si el archivo no existe, se mostrará un mensaje informado de que no existe el
            // archivo.
            System.out.println("El archivo no existe en la ruta: " + rutaArchivo);
            // Si el archivo no existe, se registra en el log y se devuelve false
            registros.warningLog("El archivo no existe: " + rutaArchivo);
            return false;
        }

        // Se verifica si el archivo tiene permisos de lectura
        if (!Files.isReadable(rutaArchivo)) {
            // Si no se tiene permisos de lectura, se mostrará un mensaje
            System.out.println("El archivo no tiene permisos de lectura: " + rutaArchivo);
            // Si no tiene permisos de lectura, se registra en el log y se devuelve false
            registros.warningLog("El archivo no tiene permisos de lectura: " + rutaArchivo);
            return false;
        }
        // Si el archivo es accesible, se registra en el log y se devuelve true
        registros.registroLog("El archivo es accesible: " + nombreArchivo);
        return true;
    }

    /**
     * Método para cerrar el Registro y liberar recursos.
     */
    public void cerrarRegistro() {
        // Se llama al método que finaliza el proceso de registro.
        this.registros.cerrarLogger();

    }

}
