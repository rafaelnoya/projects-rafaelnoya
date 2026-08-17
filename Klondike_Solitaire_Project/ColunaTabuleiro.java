class ColunaTabuleiro {
    private ListaManual<Carta> cartas;

    public ColunaTabuleiro() {
        this.cartas = new ListaManual<>();
    }

    public void adicionarCartaNaDistribuicao(Carta carta) {
        cartas.adicionar(carta);
    }

    public void revelarTopoSeNecessario() {
        if (!cartas.estaVazia() && !cartas.getUltimo().isViradaParaCima()) {
            cartas.getUltimo().virar();
        }
    }

    public boolean podeReceberSequencia(ListaManual<Carta> sequencia) {
        if (sequencia == null || sequencia.estaVazia()) return false;
        Carta primeiraCartaDaSequencia = sequencia.get(0);

        if (!primeiraCartaDaSequencia.isViradaParaCima()) return false;

        if (cartas.estaVazia()) {
            return primeiraCartaDaSequencia.getValor() == Valor.REI;
        } else {
            Carta topoAtual = verTopo();
            if (topoAtual == null || !topoAtual.isViradaParaCima()) return false;

            return !primeiraCartaDaSequencia.getCor().equals(topoAtual.getCor()) &&
                    primeiraCartaDaSequencia.getValor().getValorNumerico() == topoAtual.getValor().getValorNumerico() - 1;
        }
    }

    public boolean adicionarSequencia(ListaManual<Carta> sequencia) {
        if (podeReceberSequencia(sequencia)) {
            for(Carta c : sequencia) {
                if (!c.isViradaParaCima()) c.virar();
                cartas.adicionar(c);
            }
            return true;
        }
        return false;
    }

    public ListaManual<Carta> getSequenciaParaMover(int numCartas) {
        if (numCartas <= 0 || numCartas > cartas.tamanho()) return null;

        int startIndex = cartas.tamanho() - numCartas;
        if (startIndex < 0) return null;

        ListaManual<Carta> sequencia = new ListaManual<>();
        for (int i = 0; i < numCartas; i++) {
            Carta c = cartas.get(startIndex + i);
            if (!c.isViradaParaCima()) return null;
            sequencia.adicionar(c);
        }

        for (int i = 0; i < sequencia.tamanho() - 1; i++) {
            Carta c1 = sequencia.get(i);
            Carta c2 = sequencia.get(i + 1);
            if (c1.getCor().equals(c2.getCor()) ||
                    c1.getValor().getValorNumerico() != c2.getValor().getValorNumerico() + 1) {
                return null;
            }
        }
        return sequencia;
    }

    public void removerCartasDoTopo(int numCartas) {
        if (numCartas > 0 && numCartas <= cartas.tamanho()) {
            for (int i = 0; i < numCartas; i++) {
                cartas.removerDoFim();
            }
            revelarTopoSeNecessario();
        }
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

    @Override
    public String toString() {
        if (cartas.estaVazia()) return "[  ]";
        StringBuilder sb = new StringBuilder();
        for (Carta c : cartas) {
            sb.append(c.toString()).append(" ");
        }
        return sb.toString().trim();
    }
}