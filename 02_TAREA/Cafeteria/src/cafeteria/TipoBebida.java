/**
 * Clase Tipo Bebida
 * 
 * Clase enumaración, para definir el conjunto de constantes relacionadas entre si. 
 * 
 * Esto permite, definir las tres bebidas y sus caracterí­sticas para usarlas en cualquier
 * otra clase de la aplicación, por eso se sustituye la palabra reservada class por enum.
 * 
 * se definirán tres constantes:
 * 1-CAFE(2000) le agregamos como parámetro el tiempo de preparación de la bebida.
 * 2-TE(1500) le agregamos como parámetro el tiempo de preparación de la bebida.
 * 3-CHOCOLATE_CALIENTE (2500) le agregamos como parámetro el tiempo de preparación de la bebida.
 * 
 * @author dmiaper (Diógenes Miaja Pérez)
 * fecha: 21/12/2024
 * versión: 1.0
 */

package cafeteria;

//Se sustituye la palabra reservada class por enum
public enum TipoBebida {
    //constante 1 - café con un parámetro de 2000 milisegundos
    CAFE(2000),
    //constante 2 - té con un parámetro de 1500 milisegundos
    TE(1500),
    //constante 3 - chocolate caliente con un parámetro de 2500 milisegundos
    CHOCOLATE_CALIENTE(2500);
    
    //variable para almacenar el tiempo de praración que se mantendrá durante todo sus uso el valor.
    private final int tiempoPreparacion;
    
    /**
     * Función construir el tiempo de la bebida
     * @param tiempoPreparacion 
     */
    TipoBebida(int tiempoPreparacion){        
        this.tiempoPreparacion = tiempoPreparacion;
    }
    
    /**
     * Se devuelve el valor del tiempo de preparación de la bebida
     * @return 
     */
    public int getTiempoPreparacion(){
        return tiempoPreparacion;
    }    
}
