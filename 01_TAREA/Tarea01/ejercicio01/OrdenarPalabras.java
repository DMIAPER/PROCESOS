import java.util.*;

public class OrdenarPalabras {

    public static void main(String[] args) {
        /*
         * Se utiliza un try-with-out para garantizar que se finaliza correctamente el recurso.
         * mediante el constructor Scanner generamos obtenemos las palabras.
         */
        try (Scanner entrada = new Scanner(System.in) // Leer desde la entrada estándar
        ){
            // Leer desde la entrada estándar
            List<String> palabras = new ArrayList<>();
            // Leer palabras desde la entrada estándar
            while (entrada.hasNextLine()) {
                String palabra = entrada.nextLine().trim(); // Elimina espacios innecesarios
                if (!palabra.isEmpty()) {
                    palabras.add(palabra); // Agregar palabra a la lista
                }
            }   // Ordenar las palabras alfabéticamente
            Collections.sort(palabras);
            int aux = 1;
            // Imprimir las palabras ordenadas
            for (String palabra : palabras) {
                System.out.println(aux+": "+palabra);
                aux++;
            }
            // Cerrar el flujo de entrada
        }
    }
}
