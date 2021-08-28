package server.dine.pizza.domain.tdo;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Custom Queue
 * @param <T>
 */
public class CQueue<T> extends AbstractQueue<T> {

    private LinkedList<T> elements;

    public CQueue() {
        this.elements = new LinkedList<T>();
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean offer(T t) {
        if (t == null) return false;
        elements.add(t);
        return true;
    }

    @Override
    public T poll() {

        Iterator<T> iter = elements.iterator();
        T t = iter.next();
        if (t != null) {
            iter.remove();
            return t;
        }
        return null;
    }

    @Override
    public T peek() {
        return elements.getFirst();
    }
}
