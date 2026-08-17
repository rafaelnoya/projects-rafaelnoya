class Fundacao {
    private ListaManual<Carta> cartas;
    private final Naipe naipeAlvo;

    public Fundacao(Naipe naipeAlvo) {
        this.cartas = new ListaManual<>();
        this.naipeAlvo = naipeAlvo;
    }

    public Naipe getNaipeAlvo() {
        return naipeAlvo;
    }

    public boolean podeReceberCarta(Carta carta) {
        if (carta.getNaipe() != this.naipeAlvo) {
            return false;
        }
        if (cartas.estaVazia()) {
            return carta.getValor() == Valor.AS;
        } else {
            Carta topo = verTopo();
            return carta.getValor().getValorNumerico() == topo.getValor().getValorNumerico() + 1;
        }
    }

    public boolean adicionarCarta(Carta carta) {
        if (podeReceberCarta(carta)) {
            if (!carta.isViradaParaCima()) carta.virar();
            cartas.adicionar(carta);
            return true;
        }
        return false;
    }

    public Carta removerTopo() {
        if (!cartas.estaVazia()) {
            return cartas.removerDoFim();
        }
        return null;
    }

    public Carta verTopo() {
        if (!cartas.estaVazia()) {
            return cartas.getUltimo();
        }
        return null;
    }

    public boolean estaVazia() {
        return cartas.estaVazia();
    }

    public int tamanho() {
        return cartas.tamanho();
    }

    @Override
    public String toString() {
        if (estaVazia()) {
            return "[" + naipeAlvo.getSimbolo() + "]";
        }
        return "[" + verTopo().toString() + "]";
    }
}