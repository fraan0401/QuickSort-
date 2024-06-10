//QuickSort by Francisco Gomez

//importamos las librerias para ejecutar el metodo concurrente 
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class QuickSort { //creamos la clase 

    //Inicio Algoritmo

    // Método principal para ordenar un arreglo usando QuickSort secuencial
    public static void quickSortSequential(int[] matriz, int bajo, int alto) {
        if (bajo < alto) {
            int pi = partition(matriz, bajo, alto);
            quickSortSequential(matriz, bajo, pi - 1);
            quickSortSequential(matriz, pi + 1, alto);
        }
    }

    // Método para encontrar la posición de partición
    private static int partition(int[] matriz, int bajo, int alto) { //definimos Partition que es lo que divide las partes del ordenamiento
        int pivot = matriz[alto]; //se define el pivote 
        int i = (bajo - 1); 

        for (int j = bajo; j < alto; j++) { //for para modificar la posicion del pivote en la matriz
            if (matriz[j] <= pivot) {
                i++;
                int temp = matriz[i];
                matriz[i] = matriz[j];
                matriz[j] = temp;
            }
        }

        int temp = matriz[i + 1];
        matriz[i + 1] = matriz[alto];
        matriz[alto] = temp;

        return i + 1;
    }

    //Fin Algoritmo

    // Clase interna para el concurrente
    public static class Concurrente extends RecursiveAction { //se llama a RecursiveAction para el metodo concurrente
        private int[] matriz; //se definen las variables
        private int bajo;
        private int alto;
        private int cantidadDeHilos;

        public Concurrente(int[] matriz, int bajo, int alto, int cantidadDeHilos) { //se define la concurrencia agregando la cantidad de hilos
            this.matriz = matriz; 
            this.bajo = bajo;
            this.alto = alto;
            this.cantidadDeHilos = cantidadDeHilos;
        }

        @Override
        protected void compute() { //se utiliza el metodo compute para realizarlo de manera recursiva y paralela
            if (alto - bajo < cantidadDeHilos) { //si el diferencial es menor a la cantidad de hilos lo ejecuta de manera secuencial
                quickSortSequential(matriz, bajo, alto);
            } else {                                // sino aplica las instancias del concurrente
                int pi = partition(matriz, bajo, alto);

                Concurrente leftTask = new Concurrente( //instancia para definir los menores
                    matriz,
                    bajo,
                    pi - 1,
                    cantidadDeHilos
                );

                Concurrente rightTask = new Concurrente(  //instancia para definir los mayores
                    matriz,
                    pi + 1,
                    alto,
                    cantidadDeHilos
                );

                invokeAll(leftTask, rightTask); // ejecuta las 2 instancias en paralelo
            }
        }
    }

    public static void main(String[] args) { // se define el main
        // Crear una matriz con números randoms.
        int tam = 100_000; // Tamaño del matriz
        int[] matriz = new int[tam];

        // Rellena el array con números aleatorios
        for (int i = 0; i < tam; i++) {
            matriz[i] = (int)(Math.random() * tam);
        }

        // Metodo Secuencial
        int[] matrizSecuencial = matriz.clone();//clono la matriz

        long tiempoInicioSecuencial = System.nanoTime(); //inicia el contador de tiempo
        quickSortSequential(
            matrizSecuencial, //matriz
            0, //bajo
            tam - 1 //alto
        );
        long tiempoFinSecuencial = System.nanoTime(); //finaliza el contador de tiempo 
        
        // resta el tiempo final menos el inicial para determinar cuanto demora el proceso
        long tiempoFinalSecuencial = tiempoFinSecuencial - tiempoInicioSecuencial;

        System.out.println("Array ordenado secuencialmente:");
        System.out.println(String.format("Tiempo tomado -> %,d", tiempoFinalSecuencial) + " ns"); //imprime el metodo con el tiempo de trabajo    

        // Concurrente
        int cantidadDeHilos = 10_000; //cantidad de hilos a usar en la concurrencia,
        ForkJoinPool pool = new ForkJoinPool(); //defino el pool para paralelizar las tareas

        int[] matrizConcurrente = matriz.clone(); //clono la matriz
        Concurrente concurrente = new Concurrente( //nueva instancia de concurrente
            matrizConcurrente, //matriz
            0, //bajo
            tam - 1, //alto
            cantidadDeHilos 
        );

        long tiempoInicioConcurrente = System.nanoTime(); //inicia el contador de tiempo
        pool.invoke(concurrente); //ejecuta el programa de manera concurrente 
        long tiempoFinConcurrente = System.nanoTime();//finaliza el contador de tiempo 
        
        // resta el tiempo final menos el inicial para determinar cuanto demora el proceso
        long tiempoFinalConcurrente = tiempoFinConcurrente - tiempoInicioConcurrente;

        System.out.println("Array ordenado concurrentemente:");
        System.out.println(String.format("Tiempo tomado -> %,d", tiempoFinalConcurrente) + " ns");//imprime el metodo con el tiempo de trabajo 
    }

}
