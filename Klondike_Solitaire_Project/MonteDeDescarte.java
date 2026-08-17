class MonteDeDescarte extends PilhaCartas {
    public void adicionarCarta(Carta carta) {
        if (carta != null) {
            if(!carta.isViradaParaCima()) carta.virar();
            cartas.empurrar(carta);
        }
    }

    public Carta removerTopo() {
        if (!estaVazia()) {
            return cartas.desempurrar();
        }
        return null;
    }

    public ListaManual<Carta> getTodasCartasParaReciclar() {
        ListaManual<Carta> paraReciclar = new ListaManual<>();
        while(!cartas.estaVazia()){
            paraReciclar.adicionar(cartas.desempurrar());
        }
        return paraReciclar;
    }
}