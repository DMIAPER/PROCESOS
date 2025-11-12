/**
 * Clase Cafeterí­a
 * 
 * Clase principal del programa.
 * 
 * En esta clase se gestiona el acceso de los clientes que se irán agregando según 
 * vayan entrando en la cafeterí­a. Se va a simular un acceso de clientes incial mediante 
 * una función para generar clientes. Más adelante cuando se hayan realizado un 55% de 
 * los pedidos, se volverán a agregar unos clientes adicionales para simular una cafetería.
 * 
 * También se van a crear las máquinas que realizarán la simulación de los pedidos.
 * 
 * Por otro lado, se creará un semáforo para la gestión de la cola y que se vayan coordinando
 * los clientes entre las máquinas que vayan quedando libres.
 * 
 * Se crearán los hilos que simularán el proceso de las máquinas.
 * 
 * Por último, la aplicación una vez haya finalizado el trabajo, mostrará por consola un
 * el registro de las actividades almacenadas en el registro de las máquinas.
 * 
 * @author dmiaper (Diógenes Miaja Pérez)
 * fecha: 21/12/2024
 * versión: 1.0
 */
package cafeteria;


import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Cafeteria {

    //Atributos principales de la clase
    //Se instancia el número de máquinas que se pueden utilizar, se declará esta
    //variable por si en un futuro hay que introducir más máquinas
    private static final int numMaquinas = 3;
    //Se instancia un BlokingQueue del objeto cliente con una capacidad maxima de 90 clientes
    private static final BlockingQueue<Cliente> colaClientes = new ArrayBlockingQueue<>(90);
    //Se crea un semáforo con el número de permisos igual al número de máquinas declaradas.
    private static final Semaphore semaforo = new Semaphore(numMaquinas);
    //Se instancia el objeto random para generar número aleatorios.
    private static Random random = new Random();    
    //Variable booleana para gestionar la creación de más clientes.
    private static boolean generarClientes = false;
    //Variable que almacena la cantidad total de clientes
    private static int cliTotales;
    
    /**
     * Función para generar los primeros clientes.
     * Se obtiene el valor generado aleatoriamente como parámetro.
     * @param cliTotales 
     */ 
    public static void gernerarClientes(int cliTotales){
        //Bucle para generar el los clientes con su pedido.
        for(int x=0; x<cliTotales; x++){
            try{
                //sa crear un objeto cliente
                Cliente cliente = new Cliente();
                //se añade a al cola de clientes
                colaClientes.put(cliente);
            }catch(InterruptedException  e){
                //Controlamos las excepciones que puedan surgir.
                //se maneja las interrupciones en caso de algún problema
                Thread.currentThread().interrupt();
                //se muestra un mensaje con el error.
                System.out.println("Error al generar al cliente: "+e.getMessage());
            }
        }
    }
    
    /**
     * Función para generar más clientes cuando se hayan preparado un 55% de los 
     * pedidos. 
     * Recibe como parametro el total de los clientes que se generarón al inciar 
     * el turno.
     * @param cliTotales 
     */
    public static void masClientes(int cliTotales){
        //se sincroniza para evitar que múltiples hilos intenten modificar la 
        //variable generarClientes
        synchronized (Cafeteria.class){
            //obtenemos el porcentaje actual
            float porCiento = (colaClientes.size()*100)/cliTotales;
            //si el porcentaje es menor a 45 y generarClietnes es falso
            if (porCiento <45 && !generarClientes){
                //se genera un valor aleatorio, como máximo 40 clientes
                int clientesNuevos = random.nextInt(30)+10;
                //se llama a la función generarClientes pasando como parámetro los el nuevo número de clientes
                gernerarClientes(clientesNuevos);
                //Mensaje que informa que se han generado los nuevos clientes.
                System.out.println("************************************************");
                System.out.println("Se van a generar "+clientesNuevos+" para simular el acceso de nuevos clientes");
                System.out.println("************************************************");
                //se cambia el valor a true cuando se hayuan generado los nuevos clientes.
                generarClientes = true;
            }
        }
    }
    
    /**
     * Se crea un Hilo para monitorizar la cantidad de clientes y así generar los nuevos clientes
     * @param cliTotales
     * @return 
     */
    public static Thread crearMonitorClientes(int cliTotales){
        //se devuelve el hilo
        return new Thread(()->{
            //bucle while que finaliza cuando se generarClientes sea cierto
            while(!generarClientes){
                //controlamos las excepciones
                try{
                    //se congela el hilo un segundo
                    Thread.sleep(1000);
                    //se ejecuta la función masClietnes 
                    masClientes(cliTotales);
                    
                }catch(InterruptedException e){
                    //Controlamos las excepciones
                    Thread.currentThread().interrupt();
                    //se muestra por pantalla el error.
                    System.out.println("Error al monitorear los clientes: "+ e.getMessage());
                }
           }
        });
    }
    
    /**
     * Función que devuelve el siguiente cliente.
     * @return
     * @throws InterruptedException 
     */
    public static Cliente siguienteCliente() throws InterruptedException{
        //se devuelve el último cliente de forma bloqueante
        return colaClientes.take();
    }
    
    /**
     * Función para devolver si la cola de clientes esta vacía
     * @return 
     */
    public static boolean colaVacia(){
        //se devuelve true o false si la cola esta vacia o contiene algún cliente.
        return colaClientes.isEmpty();
    }
    
    /**
     * Función main
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //se crea un pool del número de máquinas que se haya declarado.
        ExecutorService ejecutorSevicios = Executors.newFixedThreadPool(numMaquinas);
        
        //se crean las máquina de hacer bebidas
        for(int i=0; i<numMaquinas; i++){
            //se crean las máquinas
            ejecutorSevicios.submit(new Maquina(semaforo));
        }

        //se van a crear aleatoriamente el número de cliente entre 10 y 40
        cliTotales = random.nextInt(30)+10;
        gernerarClientes(cliTotales);
        
        //se crea el hilo para monitorizar la cantidad de clientes.
        Thread monitorClientes = crearMonitorClientes(cliTotales);
        monitorClientes.start();
        
        try{
            //se incia la finalización del pool de los hilos creados
            ejecutorSevicios.shutdown();
            //Se espera a qu el hilo finalice
            monitorClientes.join();
        }catch(InterruptedException e){
            //se controlan las excepciones
            Thread.currentThread().interrupt();
            //Se muestra por pantalla el error que pueda surgir.
            System.out.println("Error:" +e.getMessage());
        }
             
    }
    
}
