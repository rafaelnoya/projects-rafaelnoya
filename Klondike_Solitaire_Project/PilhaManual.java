import java.util.NoSuchElementException;

class PilhaManual<T> {
    private ListaManual<T> listaInterna;

    public PilhaManual() {
        this.listaInterna = new ListaManual<>();
    }

    public void empurrar(T elemento) {
        listaInterna.adicionarNoInicio(elemento);
    }

    public T desempurrar() {
        if (estaVazia()) {
            throw new NoSuchElementException("Pilha vazia");
        }
        return listaInterna.removerDoInicio();
    }

    public T verTopo() {
        if (estaVazia()) {
            throw new NoSuchElementException("Pilha vazia");
        }
        return listaInterna.get(0);
    }

    public boolean estaVazia() {
        return listaInterna.estaVazia();
    }

    public int tamanho() {
        return listaInterna.tamanho();
    }

    public void limpar() {
        listaInterna.limpar();
    }

    public ListaManual<T> paraListaManual() {
        ListaManual<T> novaLista = new ListaManual<>();
        for(T item : this.listaInterna) {
            novaLista.adicionar(item);
        }
        return novaLista;
    }
}