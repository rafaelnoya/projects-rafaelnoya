class Carta {
    private final Valor valor;
    private final Naipe naipe;
    private boolean viradaParaCima;

    public Carta(Valor valor, Naipe naipe) {
        this.valor = valor;
        this.naipe = naipe;
        this.viradaParaCima = false;
    }

    public Valor getValor() {
        return valor;
    }

    public Naipe getNaipe() {
        return naipe;
    }

    public String getCor() {
        return naipe.getCor();
    }

    public boolean isViradaParaCima() {
        return viradaParaCima;
    }

    public void virar() {
        this.viradaParaCima = !this.viradaParaCima;
    }

    @Override
    public String toString() {
        if (viradaParaCima) {
            return valor.getSimbolo() + naipe.getSimbolo();
        }
        return "[XX]";
    }
}