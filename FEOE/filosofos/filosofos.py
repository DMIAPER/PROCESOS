import threading
import time
import random

class Filosofo(threading.Thread):
    """
    Clase que representa a un filósofo en el problema de la cena de los filósofos.
    Hereda de threading.Thread para ejecutarse como un hilo independiente.
    """

    def __init__(self, nombre, palillo_izq_id, palillo_der_id, palillos):
        """
        Constructor de la clase Filosofo.
        Args:
            nombre (str): Nombre identificativo del filósofo
            palillo_izq_id (int): Índice del palillo izquierdo
            palillo_der_id (int): Índice del palillo derecho
            palillos (list): Lista que se comparte entre los hilos de objetos Lock que representan a los palillos
        """
        super().__init__()
        self.nombre = nombre
        self.palillo_izq_id = palillo_izq_id
        self.palillo_der_id = palillo_der_id
        self.palillos = palillos
        self.comidas = 0
        # se contabilizará las veces que comen
        self.ejecutando = True
        # variable para controlar la parada del hilo de forma controlada.

    def detener(self):
        #Método para detener la ejecución del filósofo de forma segura
        self.ejecutando = False

    def pensar(self):
        #Simula el tiempo que el filósofo pasa pensando
        print(f"{self.nombre} está pensando... 🤔")
        time.sleep(random.uniform(1, 3))

    def comer(self):
        # Método para simular que el filósofo esta comiendo
        print(f"{self.nombre} tiene hambre y quiere comer... 😋")
        
        # Ordenamos los palillos por ID para evitar deadlocks
        primer_id = min(self.palillo_izq_id, self.palillo_der_id)
        segundo_id = max(self.palillo_izq_id, self.palillo_der_id)
        
        # Se obtiene el palillo primer_id, y se bloquea
        with self.palillos[primer_id]:
            print(f"{self.nombre} tomó el palillo {primer_id}")
            time.sleep(0.5)
            # Se espera 0.5 segundos para coger el siguiente palillo
            
            with self.palillos[segundo_id]:
            # Se obtiene el segundo palillo, y se bloquea
                print(f"{self.nombre} tomó el palillo {segundo_id} y está comiendo 🍜")
                # Se informa de que esta comiendo
                time.sleep(random.uniform(1, 3))
                # Se simula que esta comiendo
                self.comidas += 1
                # Se suma al contador 
                print(f"{self.nombre} terminó de comer (comida #{self.comidas})")
                # Se informa que ha teminado de comer
        
        print(f"{self.nombre} soltó los palillos")
        # Se informa que el filóso solto los

    def run(self):
        #Método principal del hilo que alterna entre pensar y comer mientras ejecutando sea True
        
        while self.ejecutando:
            # Bucle que se ejecuta mientras ejecutando sea true
            self.pensar()
            # se llama al metodo pensar()
            if not self.ejecutando:
            # Si ejecutando es false se para el bucle.
                break
            # Se llama al método.
            self.comer()

