/**
 * Clase Clientes
 * 
 * Clase para generar el cliente y su pedido.
 * 
 * Cada vez que se cree un cliente, automáticamente se generará un id así como un 
 * tipo de pedido. 
 * 
 * Cuando el cliente este siendo atendido, se devolverá el pedido que ha solicitado, 
 * y además, se devolverá un mesaje por pantalla informado de que máquina le esta 
 * atendiendo y que ha pedidio.
 * 
 * @author dmiaper (Diógenes Miaja Pérez)
 * fecha: 21/12/2024
 * versión: 1.0
 */
package cafeteria;

import java.util.logging.Logger;
import java.util.Random;

/**
 *
 * @author dmiap
 */
public class Cliente {
   
    //contador para generar el ID de forma automática del cliente
    private static int contadorID=0  ;
          
    //Attributos principales
    private final int id; // número para identificar al cliente
    private final TipoBebida pedido;//se almacena el pedido que solicitará el cliente
    //log para mostrar la información de por consola. 
    
    //Contructor del cliente
    public Cliente(){
        this.id = ++contadorID;
        this.pedido = TipoBebida.values()[new Random().nextInt(TipoBebida.values().length)];
    }
   
    /**
     * Devuelve el pedido que desea realizaer el cliente
     * @return 
     */
    public TipoBebida getPedido(){
        return pedido;
    }
    
    public int getId(){
        return id;
    } 
    
    public void mensaje(int idMaquina, TipoBebida bebida){
        
        String mensaje =null; 
        switch(bebida){
            case CAFE ->{
                mensaje = String.format("Cliente id: %s. Estoy siendo atendido en la máquina número %s y me esta preparando un café", id ,idMaquina);
            }
            case TE ->{
             mensaje = String.format("Cliente id: %s. Estoy siendo atendido en la máquina número %s y me esta preparando un té", id ,idMaquina);
            }
            case CHOCOLATE_CALIENTE ->{
             mensaje = String.format("Cliente id: %s. Estoy siendo atendido en la máquina número %s y me esta preparando un chocolate calietne", id ,idMaquina);
            }
        }
        if(mensaje!=null){
            System.out.println(mensaje);
        }
        
    }
    
    
}
