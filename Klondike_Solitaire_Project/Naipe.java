enum Naipe {
    COPAS('♥', "vermelho"),
    OUROS('♦', "vermelho"),
    ESPADAS('♠', "preto"),
    PAUS('♣', "preto");

    private final char simbolo;
    private final String cor;

    Naipe(char simbolo, String cor) {
        this.simbolo = simbolo;
        this.cor = cor;
    }

    public char getSimbolo() {
        return simbolo;
    }

    public String getCor() {
        return cor;
    }
}