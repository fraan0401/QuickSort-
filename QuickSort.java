import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class QuickSort {

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
    private static int partition(int[] matriz, int bajo, int alto) {
        int pivot = matriz[alto];
        int i = (bajo - 1);

        for (int j = bajo; j < alto; j++) {
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
    public static class Concurrente extends RecursiveAction {
        private int[] matriz;
        private int bajo;
        private int alto;
        private int cantidadDeHilos;

        public Concurrente(int[] matriz, int bajo, int alto, int cantidadDeHilos) {
            this.matriz = matriz;
            this.bajo = bajo;
            this.alto = alto;
            this.cantidadDeHilos = cantidadDeHilos;
        }

        @Override
        protected void compute() {
            if (alto - bajo < cantidadDeHilos) {
                quickSortSequential(matriz, bajo, alto);
            } else {
                int pi = partition(matriz, bajo, alto);

                Concurrente leftTask = new Concurrente(
                    matriz,
                    bajo,
                    pi - 1,
                    cantidadDeHilos
                );

                Concurrente rightTask = new Concurrente(
                    matriz,
                    pi + 1,
                    alto,
                    cantidadDeHilos
                );

                invokeAll(leftTask, rightTask);
            }
        }
    }

    public static void main(String[] args) {
        // Crear una matriz con números randoms.
        int tam = 100_000; // Tamaño del matriz
        int[] matriz = new int[tam];

        // Rellena el array con números aleatorios
        for (int i = 0; i < tam; i++) {
            matriz[i] = (int)(Math.random() * tam);
        }

        // Secuencial
        int[] matrizSecuencial = matriz.clone();//clono la matriz

        long tiempoInicioSecuencial = System.nanoTime();
        quickSortSequential(
            matrizSecuencial, //matriz
            0, //bajo
            tam - 1 //alto
        );
        long tiempoFinSecuencial = System.nanoTime();
        long tiempoFinalSecuencial = tiempoFinSecuencial - tiempoInicioSecuencial;

        System.out.println("Array ordenado secuencialmente:");
        System.out.println(
            String.format("Tiempo tomado -> %,d", tiempoFinalSecuencial) + " ns"
        );

        // Concurrente
        int cantidadDeHilos = 10_000; //cantidad de hilos a usar en la concurrencia,
        ForkJoinPool pool = new ForkJoinPool(); //defino el pool para paralelizar las tareas

        int[] matrizConcurrente = matriz.clone(); //clono la matriz
        Concurrente concurrente = new Concurrente(
            matrizConcurrente, //matriz
            0, //bajo
            tam - 1, //alto
            cantidadDeHilos
        );

        long tiempoInicioConcurrente = System.nanoTime();
        pool.invoke(concurrente);
        long tiempoFinConcurrente = System.nanoTime();
        long tiempoFinalConcurrente = tiempoFinConcurrente - tiempoInicioConcurrente;

        System.out.println("Array ordenado concurrentemente:");
        System.out.println(
            String.format("Tiempo tomado -> %,d", tiempoFinalConcurrente) + " ns"
        );
    }

}
