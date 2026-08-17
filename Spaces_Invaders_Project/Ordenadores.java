// Modulos especiais de defesa.
public class Ordenadores {

    // 1) Selection Sort
    public static EstatisticaAlgoritmo selectionSort(int[] linha, int numLinha) {
        long comparacoes = 0;
        long trocas = 0;

        int[] v = extrairInvasores(linha);
        long ini = System.nanoTime();

        int n = v.length;
        for (int i = 0; i < n - 1; i++) {
            int menor = i;
            for (int j = i + 1; j < n; j++) {
                comparacoes++;
                if (v[j] < v[menor]) {
                    menor = j;
                }
            }
            if (menor != i) {
                int tmp = v[i];
                v[i] = v[menor];
                v[menor] = tmp;
                trocas++;
            }
        }

        long tempo = System.nanoTime() - ini;
        reinserirInvasores(linha, v);

        return new EstatisticaAlgoritmo("Selection Sort", numLinha, comparacoes, trocas, tempo);
    }

    // 2) Insertion Sort
    public static EstatisticaAlgoritmo insertionSort(int[] linha, int numLinha) {
        long comparacoes = 0;
        long trocas = 0;

        int[] v = extrairInvasores(linha);
        long ini = System.nanoTime();

        int n = v.length;
        for (int i = 1; i < n; i++) {
            int chave = v[i];
            int j = i - 1;
            while (j >= 0 && v[j] > chave) {
                comparacoes++;
                v[j + 1] = v[j];
                trocas++;
                j--;
            }
            // conta a ultima comparacao que quebrou o while
            if (j >= 0) comparacoes++;
            v[j + 1] = chave;
        }

        long tempo = System.nanoTime() - ini;
        reinserirInvasores(linha, v);

        return new EstatisticaAlgoritmo("Insertion Sort", numLinha, comparacoes, trocas, tempo);
    }

    // 3) Bubble Sort
    public static EstatisticaAlgoritmo bubbleSort(int[] linha, int numLinha) {
        long comparacoes = 0;
        long trocas = 0;

        int[] v = extrairInvasores(linha);
        long ini = System.nanoTime();

        int n = v.length;
        boolean trocou;
        for (int i = 0; i < n - 1; i++) {
            trocou = false;
            for (int j = 0; j < n - 1 - i; j++) {
                comparacoes++;
                if (v[j] > v[j + 1]) {
                    int tmp = v[j];
                    v[j] = v[j + 1];
                    v[j + 1] = tmp;
                    trocas++;
                    trocou = true;
                }
            }
            if (!trocou) break;
        }

        long tempo = System.nanoTime() - ini;
        reinserirInvasores(linha, v);

        return new EstatisticaAlgoritmo("Bubble Sort", numLinha, comparacoes, trocas, tempo);
    }

    // 4) Merge Sort
    private static long mergeComp;
    private static long mergeTrocas;

    public static EstatisticaAlgoritmo mergeSort(int[] linha, int numLinha) {
        mergeComp = 0;
        mergeTrocas = 0;

        int[] v = extrairInvasores(linha);
        long ini = System.nanoTime();
        mergeSortRec(v, 0, v.length - 1);
        long tempo = System.nanoTime() - ini;

        reinserirInvasores(linha, v);
        return new EstatisticaAlgoritmo("Merge Sort", numLinha, mergeComp, mergeTrocas, tempo);
    }

    private static void mergeSortRec(int[] arr, int ini, int fim) {
        if (ini >= fim) return;
        int meio = (ini + fim) / 2;
        mergeSortRec(arr, ini, meio);
        mergeSortRec(arr, meio + 1, fim);
        merge(arr, ini, meio, fim);
    }

    // intercala as duas metades ja ordenadas
    private static void merge(int[] arr, int ini, int meio, int fim) {
        int tamEsq = meio - ini + 1;
        int tamDir = fim - meio;

        int[] esq = new int[tamEsq];
        int[] dir = new int[tamDir];

        for (int i = 0; i < tamEsq; i++) esq[i] = arr[ini + i];
        for (int j = 0; j < tamDir; j++) dir[j] = arr[meio + 1 + j];

        int i = 0, j = 0, k = ini;
        while (i < tamEsq && j < tamDir) {
            mergeComp++;
            if (esq[i] <= dir[j]) {
                arr[k++] = esq[i++];
            } else {
                arr[k++] = dir[j++];
                mergeTrocas++;
            }
        }
        while (i < tamEsq) arr[k++] = esq[i++];
        while (j < tamDir) arr[k++] = dir[j++];
    }

    // 5) Quick Sort
    private static long quickComp;
    private static long quickTrocas;

    public static EstatisticaAlgoritmo quickSort(int[] linha, int numLinha) {
        quickComp = 0;
        quickTrocas = 0;

        int[] v = extrairInvasores(linha);
        long ini = System.nanoTime();
        quickSortRec(v, 0, v.length - 1);
        long tempo = System.nanoTime() - ini;

        reinserirInvasores(linha, v);
        return new EstatisticaAlgoritmo("Quick Sort", numLinha, quickComp, quickTrocas, tempo);
    }

    private static void quickSortRec(int[] arr, int ini, int fim) {
        if (ini >= fim) return;
        int p = particionar(arr, ini, fim);
        quickSortRec(arr, ini, p - 1);
        quickSortRec(arr, p + 1, fim);
    }

    private static int particionar(int[] arr, int ini, int fim) {
        int pivo = arr[fim];
        int i = ini - 1;

        for (int j = ini; j < fim; j++) {
            quickComp++;
            if (arr[j] <= pivo) {
                i++;
                int tmp = arr[i];
                arr[i] = arr[j];
                arr[j] = tmp;
                quickTrocas++;
            }
        }

        int tmp = arr[i + 1];
        arr[i + 1] = arr[fim];
        arr[fim] = tmp;
        quickTrocas++;

        return i + 1;
    }

    // 6) Heap Sort
    private static long heapComp;
    private static long heapTrocas;

    public static EstatisticaAlgoritmo heapSort(int[] linha, int numLinha) {
        heapComp = 0;
        heapTrocas = 0;

        int[] v = extrairInvasores(linha);
        long ini = System.nanoTime();

        int n = v.length;

        // constroi o heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapificar(v, n, i);
        }

        // extrai do maior para o menor
        for (int i = n - 1; i > 0; i--) {
            int tmp = v[0];
            v[0] = v[i];
            v[i] = tmp;
            heapTrocas++;
            heapificar(v, i, 0);
        }

        long tempo = System.nanoTime() - ini;
        reinserirInvasores(linha, v);

        return new EstatisticaAlgoritmo("Heap Sort", numLinha, heapComp, heapTrocas, tempo);
    }

    // mantem a propriedade de max-heap descendo a partir do no "raiz"
    private static void heapificar(int[] arr, int n, int raiz) {
        int maior = raiz;
        int esq = 2 * raiz + 1;
        int dir = 2 * raiz + 2;

        if (esq < n) {
            heapComp++;
            if (arr[esq] > arr[maior]) maior = esq;
        }
        if (dir < n) {
            heapComp++;
            if (arr[dir] > arr[maior]) maior = dir;
        }

        if (maior != raiz) {
            int tmp = arr[raiz];
            arr[raiz] = arr[maior];
            arr[maior] = tmp;
            heapTrocas++;
            heapificar(arr, n, maior);
        }
    }

    // -metodos auxiliares

    // Pega so os valores > 0 da linha.
    // Necessario para nao ordenar os zeros junto.
    private static int[] extrairInvasores(int[] linha) {
        int qtd = 0;
        for (int x : linha) {
            if (x > 0) qtd++;
        }
        int[] v = new int[qtd];
        int idx = 0;
        for (int x : linha) {
            if (x > 0) v[idx++] = x;
        }
        return v;
    }

    // Coloca os invasores ordenados de volta na linha, mantendo os zeros no lugar
    private static void reinserirInvasores(int[] linha, int[] ordenados) {
        int idx = 0;
        for (int i = 0; i < linha.length; i++) {
            if (linha[i] > 0) {
                linha[i] = ordenados[idx++];
            }
        }
    }
}