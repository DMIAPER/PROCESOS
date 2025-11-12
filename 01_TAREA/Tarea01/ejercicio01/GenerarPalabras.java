/*
 * Aplicación que genera 30 palabras aleatorias para este clase se necesita la libreria Random, 
 * para obtener valores aleatorios. 
 */
//librería random para generar números aleatorios
 import java.util.Random;

//clase que contiene todo el código de la aplicación
public class GenerarPalabras {

    //se ejecuta la aplicación
    public static void main(String[] args) {
        //variables que contiene la letras a usar
        String vocales = "aeiou";
        String consonantes = "bcdfghjklmnpqrstvwxyz";
        //creamos el obtjeto random para poder usarlo.
        Random aleatorio = new Random();
        //variable que almacena el número de palabras a generar
        int numPalabras = 30;

        //bucle que se recorre tantas veces como se haya especificado
        for (int i = 0; i < numPalabras; i++) {
            //variable para alterna entre vocal y consonante
            // Asignamos aleatoriamente el valor de la variable booleana.
            Boolean usarVocal = aleatorio.nextBoolean();
            //variable que almacena la longitud de palabras
            int longitud = aleatorio.nextInt(6) + 3; //Con este método se genera un valor entre 3 y 8
            //variable que va almaenando los carateres que se van generando
            StringBuilder palabra = new StringBuilder();

            //bucle que genera la palabra según la logitud aleatoria
            for (int j = 0; j < longitud; j++) {
                 // Si usarVocal es cierto, se obtiene una vocal aleatoria
                if (usarVocal) {
                    palabra.append(vocales.charAt(aleatorio.nextInt(vocales.length())));
                } else {
                    // Si usarVocal es falso, se obtiene una consonante aleatoria
                    palabra.append(consonantes.charAt(aleatorio.nextInt(consonantes.length())));
                }

                // Se cambia el valor booleano que tenga la variable por el contrario
                usarVocal = !usarVocal;
            }
            int aux = i+1;
            //se imprime la palabra generada y se le agrega una valor númerico para ver la posición origal antes de ordenarse
            System.out.println(palabra.toString()+" "+aux); 
        }
        
    }

}
