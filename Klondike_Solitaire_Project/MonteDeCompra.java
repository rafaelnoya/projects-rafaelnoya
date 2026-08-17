class MonteDeCompra extends PilhaCartas {
    public MonteDeCompra(ListaManual<Carta> cartasDoBaralho) {
        super();
        for (int i = cartasDoBaralho.tamanho() - 1; i >= 0; i--) {
            this.cartas.empurrar(cartasDoBaralho.get(i));
        }
    }

    public Carta comprar() {
        if (!estaVazia()) {
            Carta c = cartas.desempurrar();
            c.virar();
            return c;
        }
        return null;
    }

    public void receberCartasDoDescarte(ListaManual<Carta> cartasDoDescarte) {
        cartasDoDescarte.reverse();
        for (Carta c : cartasDoDescarte) {
            c.virar();
            this.cartas.empurrar(c);
        }
    }
}