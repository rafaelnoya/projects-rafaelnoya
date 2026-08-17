import java.util.Random;

class Baralho {
    private PilhaManual<Carta> cartas;
    private Random random;

    public Baralho() {
        cartas = new PilhaManual<>();
        random = new Random();
        for (Naipe n : Naipe.values()) {
            for (Valor v : Valor.values()) {
                cartas.empurrar(new Carta(v, n));
            }
        }
        embaralhar();
    }

    private void fisherYatesShuffle(Carta[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            Carta temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public void embaralhar() {
        if (cartas.tamanho() <= 1) return;

        ListaManual<Carta> tempLista = new ListaManual<>();
        while(!cartas.estaVazia()){
            tempLista.adicionar(cartas.desempurrar());
        }

        Carta[] arrayCartas = tempLista.paraArrayCarta();
        fisherYatesShuffle(arrayCartas);

        for(Carta c : arrayCartas){
            cartas.empurrar(c);
        }
    }

    public Carta comprarCarta() {
        if (!cartas.estaVazia()) {
            return cartas.desempurrar();
        }
        return null;
    }

    public boolean estaVazio() {
        return cartas.estaVazia();
    }

    public ListaManual<Carta> getCartasRestantes() {
        ListaManual<Carta> restantes = new ListaManual<>();
        PilhaManual<Carta> tempPilha = new PilhaManual<>();

        while(!this.cartas.estaVazia()){
            Carta c = this.cartas.desempurrar();
            restantes.adicionarNoInicio(c);
            tempPilha.empurrar(c);
        }
        while(!tempPilha.estaVazia()){
            this.cartas.empurrar(tempPilha.desempurrar());
        }
        return restantes;
    }
}
