package org.kurodev.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class ObservableList<E> extends ArrayList<E> {
    private Consumer<ObservableList<E>> onChange;

    public ObservableList(Consumer<ObservableList<E>> onChange) {

        this.onChange = onChange;
    }

    private void notifyListener() {
        if (onChange != null)
            onChange.accept(this);
    }

    @Override
    public void trimToSize() {
        super.trimToSize();
        notifyListener();
    }

    @Override
    public E set(int index, E element) {
        notifyListener();
        return super.set(index, element);
    }

    @Override
    public boolean add(E e) {
        boolean added = super.add(e);
        if (added)
            notifyListener();
        return added;
    }

    @Override
    public void add(int index, E element) {
        notifyListener();
        super.add(index, element);
    }

    @Override
    public E remove(int index) {
        notifyListener();
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = super.remove(o);
        if (removed)
            notifyListener();
        return removed;
    }

    @Override
    public void clear() {
        notifyListener();
        super.clear();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {

        boolean added = super.addAll(c);
        if (added) notifyListener();
        return added;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean added = super.addAll(index, c);
        if (added) notifyListener();
        return added;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        notifyListener();
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean removed = super.removeAll(c);
        if (removed) notifyListener();
        return removed;
    }

    public void setOnChange(Consumer<ObservableList<E>> onChange) {
        this.onChange = onChange;
    }
}
