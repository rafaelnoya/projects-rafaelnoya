enum Valor {
    AS("A", 1), DOIS("2", 2), TRES("3", 3), QUATRO("4", 4),
    CINCO("5", 5), SEIS("6", 6), SETE("7", 7), OITO("8", 8),
    NOVE("9", 9), DEZ("T", 10), VALETE("J", 11), DAMA("Q", 12), REI("K", 13);

    private final String simbolo;
    private final int valorNumerico;

    Valor(String simbolo, int valorNumerico) {
        this.simbolo = simbolo;
        this.valorNumerico = valorNumerico;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public int getValorNumerico() {
        return valorNumerico;
    }

    public static Valor fromValorNumerico(int num) {
        for (Valor v : values()) {
            if (v.valorNumerico == num) {
                return v;
            }
        }
        return null;
    }
}
