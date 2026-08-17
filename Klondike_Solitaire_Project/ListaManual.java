import java.util.NoSuchElementException;
import java.util.Iterator;

class ListaManual<T> implements Iterable<T> {
    private No<T> inicio;
    private No<T> fim;
    private int tamanho;

    public ListaManual() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    public void adicionar(T elemento) { // Adiciona no final
        No<T> novoNo = new No<>(elemento);
        if (estaVazia()) {
            inicio = novoNo;
            fim = novoNo;
        } else {
            fim.proximo = novoNo;
            fim = novoNo;
        }
        tamanho++;
    }

    public void adicionarNoInicio(T elemento) {
        No<T> novoNo = new No<>(elemento);
        if (estaVazia()) {
            inicio = novoNo;
            fim = novoNo;
        } else {
            novoNo.proximo = inicio;
            inicio = novoNo;
        }
        tamanho++;
    }

    public T removerDoInicio() {
        if (estaVazia()) {
            throw new NoSuchElementException("Lista vazia");
        }
        T dadoRemovido = inicio.dado;
        inicio = inicio.proximo;
        if (inicio == null) {
            fim = null;
        }
        tamanho--;
        return dadoRemovido;
    }

    public T removerDoFim() {
        if (estaVazia()) {
            throw new NoSuchElementException("Lista vazia");
        }
        if (inicio == fim) { // Somente um elemento
            T dado = inicio.dado;
            inicio = null;
            fim = null;
            tamanho--;
            return dado;
        }
        No<T> atual = inicio;
        while (atual.proximo != fim) {
            atual = atual.proximo;
        }
        T dadoRemovido = fim.dado;
        fim = atual;
        fim.proximo = null;
        tamanho--;
        return dadoRemovido;
    }

    public T get(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice: " + indice + ", Tamanho: " + tamanho);
        }
        No<T> atual = inicio;
        for (int i = 0; i < indice; i++) {
            atual = atual.proximo;
        }
        return atual.dado;
    }

    public T getUltimo() {
        if (estaVazia()) {
            throw new NoSuchElementException("Lista vazia");
        }
        return fim.dado;
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean estaVazia() {
        return tamanho == 0;
    }

    public void limpar() {
        inicio = null;
        fim = null;
        tamanho = 0;
    }

    public void reverse() {
        if (tamanho <= 1) {
            return;
        }
        PilhaManual<T> pilhaAux = new PilhaManual<>();
        No<T> atual = inicio;
        while(atual != null) {
            pilhaAux.empurrar(atual.dado);
            atual = atual.proximo;
        }
        limpar();
        while(!pilhaAux.estaVazia()){
            adicionar(pilhaAux.desempurrar());
        }
    }

    public Carta[] paraArrayCarta() {
        Carta[] array = new Carta[tamanho];
        No<T> atual = inicio;
        for(int i = 0; i < tamanho; i++) {
            array[i] = (Carta) atual.dado; // Requer que T seja Carta
            atual = atual.proximo;
        }
        return array;
    }

    @Override
    public Iterator<T> iterator() {
        return new IteradorListaManual<>(inicio);
    }
}