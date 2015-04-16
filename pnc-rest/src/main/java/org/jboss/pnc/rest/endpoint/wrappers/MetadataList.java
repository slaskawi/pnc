package org.jboss.pnc.rest.endpoint.wrappers;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MetadataList<E> implements List<E> {

    private final List<E> wrappedList;
    private final int pageIndex;
    private final int pageSize;

    public MetadataList(List<E> wrappedList, int pageIndex, int pageSize) {
        this.wrappedList = wrappedList;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int size() {
        return wrappedList.size();
    }

    @Override
    public boolean isEmpty() {
        return wrappedList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return wrappedList.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return wrappedList.iterator();
    }

    @Override
    public Object[] toArray() {
        return wrappedList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return wrappedList.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return wrappedList.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return wrappedList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return wrappedList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return wrappedList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return wrappedList.retainAll(c);
    }

    @Override
    public void clear() {
        wrappedList.clear();
    }

    @Override
    public E get(int index) {
        return wrappedList.get(index);
    }

    @Override
    public E set(int index, E element) {
        return wrappedList.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        wrappedList.add(index, element);
    }

    @Override
    public E remove(int index) {
        return wrappedList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return wrappedList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return wrappedList.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return wrappedList.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return wrappedList.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return wrappedList.subList(fromIndex, toIndex);
    }
}
