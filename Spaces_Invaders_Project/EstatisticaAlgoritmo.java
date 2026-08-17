// Classe para guardar os dados de execucao de um algoritmo de ordenacao
public class EstatisticaAlgoritmo {

    private String nomeAlgoritmo;
    private int linha;
    private long comparacoes;
    private long trocas;
    private long tempoNs;

    public EstatisticaAlgoritmo(String nomeAlgoritmo, int linha, long comparacoes, long trocas, long tempoNs) {
        this.nomeAlgoritmo = nomeAlgoritmo;
        this.linha = linha;
        this.comparacoes = comparacoes;
        this.trocas = trocas;
        this.tempoNs = tempoNs;
    }

    public String getNomeAlgoritmo() {
        return nomeAlgoritmo;
    }

    public int getLinha() {
        return linha;
    }

    public long getComparacoes() {
        return comparacoes;
    }

    public long getTrocas() {
        return trocas;
    }

    public long getTempoNs() {
        return tempoNs;
    }

    // converte nanosegundos para milisegundos
    public double getTempoMs() {
        return tempoNs / 1000000.0;
    }

    @Override
    public String toString() {
        return nomeAlgoritmo + " | Linha: " + linha
                + " | Comparacoes: " + comparacoes
                + " | Trocas: " + trocas
                + " | Tempo: " + getTempoMs() + " ms";
    }
}