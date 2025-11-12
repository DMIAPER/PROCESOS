/**
 * Clase Máquina.
 * 
 * Al implementar en esta clase la interfaza Runnable, nos permite definir la 
 * lógica que sera ejecutada como un hilo independiente y que sea pasada como 
 * argumento a un objeto de tipo Thread, y se facilite su ejecución.
 * 
 * El constructor de esta clase recibirá como parámetro un objeto semaphore, el cual
 * se utilizará para limitar el acceso concurrente el recurso, asegurando que la máquina
 * que la máquina solo atiende a un cliente a la vez y evitando posibles erores de
 * concurrencia. El constructor generará el id de la máquina de forma automática. 
 * Por último, también se construirá un objeto Registrologs, para ir manejando y registrando
 * los registro de los eventos que se generen durante el turno de trabajo de las
 * máquinas.
 * 
 * También cuenta con una función que permite gestionar los pedidos, que contiene un 
 * Thread.sleep. para simular el periodo que tarde en prerarse un pedido. 
 * En esta función se utilizará un switch para controlar los tiempos de parado 
 * según el tipo de bebida que haya solicitado el cliente.
 * 
 * En la función finTurno(), se utiliza para indicar en al registro que ha finalizado 
 * el turno de trabajo y que ya hay más pedidos que gestionar. Enviará la cantidad de 
 * bebidas preparadas por la máquina al registro para su posterior impresión por
 * pantalla y escritura en el archivo de registros.
 * 
 * Por último, se ha reescrito el método run(), para que contenga la logica de la 
 * clase. En este método se inicia el turno registrandolo, mediante un bucle while
 * se irán recibiendo los clientes para ir procesando sus pedidos así como registrandolos.
 * Una vez finalice el turno se imprimirá por pantalla los registros. 
 * 
 * @author dmiaper (Diógenes Miaja Pérez)
 * fecha: 21/12/2024
 * versión: 1.0
 */
package cafeteria;

import java.util.concurrent.Semaphore;

/**
 * Clase que implementa la interfaz Runnable, permitiendo definir la lógica que 
 * será ejecutada en un hilo independiente.
 */
public class Maquina implements Runnable{

    //Contador estático para generar el número de id de las máquinas.
    private static int contadorId=0;
    //variables que contarán la cantidad que bebebidas por tipos que realizan las máquinas.
    private int cafe = 0;
    private int te = 0;
    private int chocolate = 0;
    
    //Atriburtos principales.
    private int id;  //se almacena el id de la máquina
    private Semaphore semaforo; //se instacia un semaforo para controlar el proceso
    private Registrologs registros;
    
    /**
     * Constructor para el objeto máquinam que recibe un semáforo para sincronizar 
     * los procesos.
     * @param s 
     */
    public Maquina(Semaphore s){
        //se genera el id automáticamente
        this.id = ++contadorId;
        //se instancia el semáforo.
        this.semaforo = s;
        //se instancia el objeto Registrologs
        this.registros = new Registrologs();       
    }
    
    /**
     * Función para procesar los pedidos, recibe como parámetro un objeto cliente
     * se agrega un controlador de interrupción de excepciones.
     * @param cliente
     * @throws InterruptedException 
     */
    public void preparacionPedido(Cliente cliente)throws InterruptedException{

        //se obtiene el tipo de bebida que ha pedido el cliente
        TipoBebida pedido = cliente.getPedido();
        
        /**
         * se llama a la función de mensaje de cliente para se muestre por pantalla 
         * se envia el id de la máquina y el pedido
         */
        cliente.mensaje(id, pedido);
        
        //Estructura switch que procesará un pedido según el pedido que almacena la variable pedido
        switch(pedido){
            case CAFE->{
                //se registra el incio del pedido, se envia el objeto cliente
                registros.regInicioPedido(cliente);
                //se congela el proceso para simular el tiempo de preparación. 
                //el tiempo esta definido por el objeto indicado por el pedido.
                Thread.sleep(pedido.getTiempoPreparacion());
                //se incrementa el cafe en 1 
                cafe++;
                //mensaje que indica que se ha preparado un café.
                System.out.println("Café listo.");
                //se registra la finalización del proceso.
                registros.regFinPedido(cliente);
            }
            case TE->{
                //se registra el inicio del pedido, se enviará el objeto cliente.
                registros.regInicioPedido(cliente);
                //se congela el proceso para simular el tiempo de preparación.
                Thread.sleep(pedido.getTiempoPreparacion());
                //se incrementa en 1 la variable te
                te++;
                //mensaje que indica que se ha preparado un té
                System.out.println("Té listo.");
                //se registra la finalización del pedido
                registros.regFinPedido(cliente);
            }
            case CHOCOLATE_CALIENTE->{
                //se regisrtra el incio del pedido, se enviará el objeto cliente
                registros.regInicioPedido(cliente);
                //se congela el proceso para simular el tiempo de preparación.
                Thread.sleep(pedido.getTiempoPreparacion());
                //se incrementa en 1 el chocolate caliente.
                chocolate++;
                //mensaje que indica que se ha preparado un chocolante caliente
                System.out.println("Chocolate calietne listo.");
                //ase registra la finalización del pedido.
                registros.regFinPedido(cliente);
            }
        }
    }
        
    /**
     * Función para indicar que se ha finalizado el turno de trabajo.
     * Esta función enviará los mensaje de las bebidas que se han preparado 
     * para su posterior registro.
     */
    public void finTurno(){
        //Mensaje para indicar las bebidas que sean preparado 
        String cafesMsg = String.format("Se han preparado %s cafés.", cafe);
        String tesMsg = String.format("Se han preparado %s tés.", te);
        String chocosMsg = String.format("Se han preparado %s chocolates calientes.", chocolate);
        //se envian los mensajes al registro
        registros.regFinTurno(cafesMsg, tesMsg, chocosMsg);
        //se muestra el registro completo de eventos que ha realizado la máquina durante el turno
        registros.mostarRegistro();
    }
    
    /**
     * Función ejecutable para la interface.
     */
    @Override
    public void run(){
        //se inciar el registro de turno, se envia el id de la máquina
        registros.regInicioTurno(id);
        //Se muestra el mensaje de que la máquina a comenzado a trabajar.
        System.out.println("Se va a inciar el proceso de trabajo de la máquina n. "+id);
        
        //controlamos las excepciones 
        try {
            //Buclce while que se ejecutará mientras la cola de clientes no este vacia.
            while (!Cafeteria.colaVacia()) {  
                //se adquiere el semáformo para empezar a trabajar.
                semaforo.acquire();
                // Se obtiene el cliente que este a al cabeza de la cola de clietnes.
                Cliente cliente = Cafeteria.siguienteCliente();
                //si el cliente no es null
                if (cliente != null) {
                    //registramos el incio del pedido, se envia el id de la máquina y el objeto cliente
                    registros.regInicio(id, cliente);
                    //se procesa el pedido
                    preparacionPedido(cliente);
                }
                //se libera el semáforo si no se esta atendiendo al cliente
                semaforo.release();
            }
             
        } catch (InterruptedException e) {
            //Se restablece el estado de interrupción del hilo.
            Thread.currentThread().interrupt();
            //se registra el error en caso de que surja, se envia el id de la máquina y el mensaje del error.
            registros.regError(id, e.getMessage());
        }finally{
            //se finaliza el turno.
            finTurno();
        }
    }
}

