import java.util.NoSuchElementException;
import java.util.Iterator;

class IteradorListaManual<T> implements Iterator<T> {
    private No<T> atual;

    public IteradorListaManual(No<T> inicio) {
        this.atual = inicio;
    }

    @Override
    public boolean hasNext() {
        return atual != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        T dado = atual.dado;
        atual = atual.proximo;
        return dado;
    }
}