from filosofos import Filosofo
import time
import threading
import keyboard

# Variable global para controlar la ejecución del programa
ejecutando = True

def monitor_teclado():
    # Método para monitorear si se pulsa la tecla "esc" del teclado.
    global ejecutando
    # Se obtiene la variable ejecutando 
    keyboard.wait('esc')
    # Cuando se pulsa la tecla "Esc" se finaliza el proceso
    print("")
    print("#######################")
    print("Tecla ESC detectada. Finalizando la simulación...")
    print("#######################")
    print("")
    # Se informa que se ha pulsado la tecla y se ha finalizado el programa
    ejecutando = False
    # Se cambia el valor de ejecuntando para finalizar los hilos y la aplicación.

def main():
    # Método que ejecuta la aplicación
    global ejecutando
    # Variable de control para finalizar el proceso.
    N = 5
    # Número de filósofos y palillos
    palillos = [threading.Lock() for _ in range(N)]
    # Crear los palillos como objetos Lock
    
    filosofos = [
    # Crear los filósofos
        Filosofo(f"Filósofo {i+1}", i, (i + 1) % N, palillos)
        for i in range(N)
    ]

    threading.Thread(target=monitor_teclado, daemon=True).start()
     # Iniciar el hilo que monitoreará la tecla ESC
    
    for f in filosofos:
    # Iniciar los hilos de los filósofos
        f.start()

    while ejecutando:
        # Mantener el programa principal ejecutándose hasta que se presione ESC
        time.sleep(1)

    print("Deteniendo filósofos...")
    # Detener cada filósofo de forma ordenada
    for f in filosofos:
        f.detener()

    for f in filosofos:
    # Esperar a que todos los hilos terminen (con timeout)
        f.join(timeout=2)

    print("")
    print("#######################")
    print("Simulación finalizada.")
    print("#######################")
    print("")
    # Se informa de que se ha finalizado la simulación

    exit(0)
    # Se finaliza el proceso

if __name__ == "__main__":
    # se ejecuta la aplicación.
    main()