abstract class PilhaCartas {
    protected PilhaManual<Carta> cartas;

    public PilhaCartas() {
        this.cartas = new PilhaManual<>();
    }

    public boolean estaVazia() {
        return cartas.estaVazia();
    }

    public int tamanho() {
        return cartas.tamanho();
    }

    public Carta verTopo() {
        if (!cartas.estaVazia()) {
            return cartas.verTopo();
        }
        return null;
    }
}