
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CoordinarProcesos {

    //Se define el método principal de la clase, se agregan dos lanzadores de excepciones
    public static void main(String[] args) throws IOException, InterruptedException {

        //condición que se muestra si no se han indicado paramétros.
        if(args.length != 3){
            System.out.println("El comando introducido no es correcto, use: java -jar CoordinarProcesos.jar <fichero_entrada> <número instancias> <fichero_salida>");
        }

        //Variables para almacenar el nombre del archivo.
        String ficheroEntrada = args[0];
        //Variable para almacenar el número de instancias que se van a realizar 
        int numInstancias = Integer.parseInt(args[1]);
        //Variable para almancenar el nombre del archivo de salida.
        String ficheroSalida = args[2];
        String[] nomFicheroSalida= ficheroSalida.split("\\.");
        //Lista para almacenar las palabras del archivo
        List<String> listaPalabras = new ArrayList<>();

        /*
         * Se utiliza un try-with-out para garantizar que se cierre correctamente el recurso.
         * del listado de palabras completo de archivo.txt en la variable lectura que creamos en el mismo
         * caputrador.
         */

        try (var lectura = new BufferedReader(new FileReader(ficheroEntrada))){
            
            //Variable para almacenar palabra a palabra en la lista.
            String linea;
            //Bucle que almacena la palabra en la variable linea y verifica que no este vacia, si no esta vacia se guarda en la lista
            while ((linea = lectura.readLine())!= null) { 
                //se añade a la lista la palabra obtenida.
                listaPalabras.add(linea);
            }
        }

        //variable para indicar cuantas palabras contendra cada instancia.redondeado hacia arriba
        int palabraInstancia = Math.round(listaPalabras.size()/numInstancias);
        List<String> nomSubArchivos = new ArrayList<>();

        //bucle for que se recorre tantas vecese como subArchivos tengamos que crear.
        for(int i=0; i < numInstancias;i++){
            String nuevoArchivo = nomFicheroSalida[0]+"_"+i+".txt";
            nomSubArchivos.add(nuevoArchivo);
            
            /*
             * Se utiliza un try-with-out para cerrar el recurso automáticamente.
             * Se escribe dentro del nuevo sub-archivo la palabra correspondiente
            */
            try(BufferedWriter creaArchivo = new BufferedWriter(new FileWriter(nuevoArchivo))){
                //bucle que recorremos para insertar las palabras el el nuevo archivo.
                for(int a=0; a<palabraInstancia; a++){
                    //se agrega la palabra en archivo.
                    creaArchivo.write(listaPalabras.get(a));
                    //se crea una nueva linea en el archivo para agregar la palabra
                    creaArchivo.newLine();
                }
            }
        }

        //Lista para generar el nuevo archivo de salida
        List<String> archivoSalida = new ArrayList<>();
        //ahora generamos una lista de procesos para que se puede almacenar los procesos que se van a ejecutar.
        List<Process> procesos = new ArrayList<>();
        /*
         * Bucle para generar los procesos que se irán ejecutando según el número de instancias indicadas 
         * al ejecutar la aplicación.
         */
        for(int i=0; i<numInstancias;i++){
            //creamos el nuevo nombre de salida para el archivo procesado
            archivoSalida.add("archivoProcesado"+i+".txt");

           //Construimos el proceso que se va a ejecutar para interactuar con la clase ProcesarPalabras
           ProcessBuilder procesar = new ProcessBuilder(
                "java", "-jar", "ProcesarPalabras.jar", nomSubArchivos.get(i), archivoSalida.get(i)
                );

           //se configura el proceso para que el hijo herede la entrada y salida estandar
           procesar.inheritIO();
           //se inicia el proceso y se almacena en la lista de procesos.
           procesos.add(procesar.start());
        }

        //bucle para ejecutar todos los procesos
        for(Process proceso : procesos){
            //método que espera a que se ejecute por completo el proceso antes de continuar
            proceso.waitFor();
        }
       
        /*
         * Se utiliza un try-with-out para controlar de forma automática el cierre del proceso.
         * Se se crea un nuevo archivo de salida con el nombre indicado por el usuario.
         */
        try(BufferedWriter unificar = new BufferedWriter(new FileWriter(ficheroSalida))){
            //bucle que recorre todos los archivos de salida procesados 
            for(String archivo : archivoSalida){
                /*
                 * Se utiliza un try-with-out para controlar de forma automática el cierre del proceso.
                 * Se lee el archivo de la interacción procesado
                 */
                try(BufferedReader leer = new BufferedReader(new FileReader(archivo))){
                    //variable para almacenar la línea
                    String linea;
                    //bucle que se recorre mientras la variable sea diferente a null
                    while ((linea=leer.readLine()) != null) {
                        //se escribe la palabra en el ficher creado
                        unificar.write(linea);
                        //se inserta en el fichero un salto de línea para escribir la siguiente palabra.
                        unificar.newLine();
                    }
                }
            }
        }

        System.out.println("Se han procesado el fichero "+ficheroEntrada+" durante "+numInstancias+" instancias.");
        System.out.println("Si lo desea puede comprobar el resultado en el fichero de salida indicado "+ficheroSalida+".");

        
    }
}
