/**
 * Clase principal que implementa un sistema de validación de usuarios y lectura
 * de archivos, con registro de actividad y medidas de seguridad.
 * 
 * @author DMIAPER (Diógenes Míaja Pérez)
 * @version 1.0.0
 * Fecha 2025-05-07
 */

package validacion;

import java.util.Scanner;

public class ValidacionApp {

    private static final DocumentHandler documentHandler = new DocumentHandler();

    /**
     * Método principal que ejecuta el programa.
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Se crea un objeto Scanner para leer la entrada del usuario
        Scanner scanner = new Scanner(System.in);
        // Se solicita el nombre de usuario al usuario
        documentHandler.solicitarUsuario(scanner);
        // Se solicita el nombre del archivo al usuario
        String nombreArchivo = documentHandler.solicitarArchivo(scanner);
        // Se lee el contenido del archivo y se muestra en la consola
        documentHandler.mostarContendioArchivo(nombreArchivo);
        // Se llama al método para finalizar el fichero de registros.
        documentHandler.cerrarRegistro();
        // Se cierra el objeto Scanner
        scanner.close();
    }
}