
import java.io.*;

/*
 * Clase que lee un archivo línea por línea y si la palabra contiene un número par de caracteres lo escribe 
 * en un nuevo archivo en mayúscula, si el número es inpar lo escribe en minúscula.
 */

public class ProcesarPalabras {

    public static void main(String[] args) throws IOException {

        //Variables para almacenar los parámetros recibidos 
        String ficheroEntrada = args[0];
        String ficheroSalida = args[1];

        /*
         * Se utiliza un try-whit-out para controlar el cierre de los procesos automáticamente
         * los ficheros de lectura y escritura
         */
        try (BufferedReader lectura = new BufferedReader(new FileReader(ficheroEntrada));
            BufferedWriter escritura= new BufferedWriter(new FileWriter(ficheroSalida))) {
            //Variable para almacenar la palabra de la línea
            String linea;
            //bucel que se recorre mientras la variable línea contenga un valor y no este vacía
            while ((linea = lectura.readLine()) != null) {
                //comprobamos que la longitud de la palabra sea múltiplo de 2 y que el resultado sea 0
                if (linea.length() % 2 == 0) {
                    //Si se cumple la condición la palabra se combierte a mayúscula.
                    escritura.write(linea.toUpperCase());
                //si no es múltiplo de 2, se ejecutará el siguiente código.
                } else {
                    //Si se cumple esta condición la palabra se escribirá todo en minúscula.
                    escritura.write(linea.toLowerCase());
                }
                //se agrega un salto de línea para escribir la nueva palabra.
                escritura.newLine();
            }
        
        } 
    }



}
