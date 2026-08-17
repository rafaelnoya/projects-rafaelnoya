// Nave de defesa
public class Nave {

    // nomes dos modulos especiais
    public static final String[] NOMES_MODULOS = {
            "Selection Sort - Scanner de prioridade",
            "Insertion Sort - Reorganizacao tatica",
            "Bubble Sort - Pulso de impacto",
            "Merge Sort - Divisao de esquadrilha",
            "Quick Sort - Ataque hiperluz",
            "Heap Sort - Dominio orbital"
    };

    private int linhaAtual;
    private int totalLinhas;
    private boolean[] modulosUsados;

    public Nave(int totalLinhas) {
        this.totalLinhas = totalLinhas;
        this.linhaAtual = totalLinhas / 2; // comeca no meio do campo
        this.modulosUsados = new boolean[6];
    }

    public boolean subirLinha() {
        if (linhaAtual > 0) {
            linhaAtual--;
            return true;
        }
        System.out.println("A nave ja esta na primeira linha.");
        return false;
    }

    public boolean descerLinha() {
        if (linhaAtual < totalLinhas - 1) {
            linhaAtual++;
            return true;
        }
        System.out.println("A nave ja esta na ultima linha.");
        return false;
    }

    public boolean moduloDisponivel(int indice) {
        if (indice < 0 || indice >= 6) return false;
        return !modulosUsados[indice];
    }

    public void marcarModuloUsado(int indice) {
        if (indice >= 0 && indice < 6) {
            modulosUsados[indice] = true;
        }
    }

    public void exibirModulos() {
        System.out.println("\nModulos Especiais de Defesa:");
        for (int i = 0; i < 6; i++) {
            String status;
            if (modulosUsados[i]) {
                status = "[USADO]     ";
            } else {
                status = "[DISPONIVEL]";
            }
            System.out.println("  " + (i + 1) + ") " + status + " " + NOMES_MODULOS[i]);
        }
        System.out.println();
    }

    public boolean todosModulosUsados() {
        for (boolean u : modulosUsados) {
            if (!u) return false;
        }
        return true;
    }

    public int getLinhaAtual() {
        return linhaAtual;
    }
}