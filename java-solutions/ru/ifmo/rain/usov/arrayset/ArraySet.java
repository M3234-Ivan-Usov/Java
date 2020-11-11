package ru.ifmo.rain.usov.arrayset;

import java.util.*;
import java.lang.*;
//java -cp . -p . -m info.kgeorgiy.java.advanced.arrayset SortedSet ru.ifmo.rain.usov.arrayset.ArraySet


public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {
    private final List<T> X;
    private final Comparator<? super T> comparator;

    public ArraySet() {
        X = Collections.emptyList();
        comparator = null;
    }

    public ArraySet(Collection<? extends T> collection) {
        X = List.copyOf(new TreeSet<>(collection));
        comparator = null;
    }

    public ArraySet(Collection<? extends T> collection, Comparator<? super T> manual) {
        TreeSet<T> temporary = new TreeSet<>(manual);
        temporary.addAll(collection);
        X = List.copyOf(temporary);
        comparator = manual;
    }

    private ArraySet(List<T> elements, Comparator<? super T> manual) {
        X = elements;
        comparator = manual;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        try {
            return Index((T) Objects.requireNonNull(o)) >= 0;
        } catch (ClassCastException e) {
            return false;
        }
    }

    private int Index(T t) {
        return Collections.binarySearch(X, Objects.requireNonNull(t), comparator);
    }

    private int lowerIndex(T t, boolean inclusive) {
        int index = Index(t);
        if (index < 0) {
            return -index - 2;
        }
        return inclusive ? index : index - 1;
    }

    private int higherIndex(T t, boolean inclusive) {
        int index = Index(t);
        if (index < 0) {
            return -index - 1;
        }
        return inclusive ? index : index + 1;
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(X).iterator();
    }

    private ArraySet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        if (fromElement == null || toElement == null) {
            throw new IllegalArgumentException("Not null elements expected");
        }
        int fromIndex = higherIndex(fromElement, fromInclusive);
        int toIndex = lowerIndex(toElement, toInclusive);
        return toIndex < fromIndex ?
                new ArraySet<>(Collections.emptyList(), comparator) :
                new ArraySet<>(X.subList(fromIndex, toIndex + 1), comparator);
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ArraySet<T> subSet(T fromElement, T toElement) {
        if (comparator != null && comparator.compare(fromElement, toElement) > 0 ||
                comparator == null && fromElement instanceof Comparable &&
                        ((Comparable) fromElement).compareTo(toElement) > 0) {
            throw new IllegalArgumentException("Left bound should be not greater than the right one");
        }
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public ArraySet<T> headSet(T toElement) {
        if (isEmpty()) {
            return this;
        }
        return subSet(first(), true, toElement, false);
    }

    @Override
    public ArraySet<T> tailSet(T fromElement) {
        if (isEmpty()) {
            return this;
        }
        return subSet(fromElement, true, last(), true);
    }
    @Override
    public T first() {
        if (X.isEmpty())
            throw new NoSuchElementException("ArraySet is empty");
        else return X.get(0);
    }

    @Override
    public T last() {
        if (X.isEmpty())
            throw new NoSuchElementException("ArraySet is empty");
        else return X.get(X.size() - 1);
    }

    @Override
    public int size() {
        return X.size();
    }
}
