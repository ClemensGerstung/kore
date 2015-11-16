package core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

// because Java sucks...
public class Dictionary<K, V> implements Iterable<Dictionary.Element>, Iterator<Dictionary.Element>, Cloneable {

    public class Element<K, V> {
        private K key;
        private V value;
        private Element<K, V> next;
        private Element<K, V> prev;

        Element(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
        }

        Element(Element<K, V> other) {
            key = other.key;
            value = other.value;
            next = other.next;
            prev = other.prev;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        Element<K, V> getNext() {
            return next;
        }

        void setNext(Element<K, V> next) {
            this.next = next;
        }

        Element<K, V> getPrevious() {
            return prev;
        }

        void setPrevious(Element<K, V> prev) {
            this.prev = prev;
        }

        boolean hasNext() {
            return next != null;
        }

        boolean hasPrevious() {
            return prev != null;
        }

        void delete() {
            key = null;
            value = null;
            next = null;
            prev = null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Element)) return false;

            Element<K, V> element = (Element<K, V>) o;

            if (key != null ? !key.equals(element.key) : element.key != null) return false;
            return !(value != null ? !value.equals(element.value) : element.value != null);

        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Element{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    private Element<K, V> first;
    private Element<K, V> last;
    private Element<K, V> current;

    public Dictionary() {
        first = null;
        last = null;
        current = null;
    }

    public Dictionary(Dictionary<K, V> other) {
        this();
        addAll(other, InsertOption.After, IterationOption.Forwards);
    }

    public void addLast(K key, V value) {
        if (containsKey(key, IterationOption.Backwards))
            throw new IllegalArgumentException("Already contains key " + key.toString() + "!");

        if (first == null) {
            first = new Element<>(key, value);
            last = first;
        } else {
            Element<K, V> element = last;
            last = new Element<>(key, value);
            last.setPrevious(element);
            element.setNext(last);
        }
    }

    public void addFirst(K key, V value) {
        if (containsKey(key, IterationOption.Forwards))
            throw new IllegalArgumentException("Already contains key " + key.toString() + "!");

        if (last == null) {
            last = new Element<>(key, value);
            first = last;
        } else {
            Element<K, V> element = first;
            first = new Element<>(key, value);
            first.setNext(element);
            element.setPrevious(first);
        }
    }

    public void addAll(Dictionary<K, V> other, InsertOption insertOption, IterationOption option) {
        Element<K, V> iterator = (option == IterationOption.Forwards ? other.getFirstIterator() : other.getLastIterator());
        while (other.hasNext()) {
            if (containsKey(iterator.key, option))
                continue;

            if (insertOption == InsertOption.After)
                addLast(iterator.key, iterator.value);
            else
                addFirst(iterator.key, iterator.value);

            iterator = option == IterationOption.Forwards ? other.next() : other.previous();
        }
    }

    public void setForKey(K key, V value, IterationOption option) {
        Element<K, V> iterator = option == IterationOption.Backwards ? getLastIterator() : getFirstIterator();

        boolean setValue = false;
        while (iterator != null) {
            if (iterator.getKey().equals(key)) {
                iterator.setValue(value);
                setValue = true;
                break;
            }

            iterator = option == IterationOption.Backwards ? iterator.getPrevious() : iterator.getNext();
        }

        if (!setValue) {
            if (option == IterationOption.Backwards)
                addFirst(key, value);
            else
                addLast(key, value);
        }
    }

    public void insertKey(K search, K key, V value, InsertOption insertOption, IterationOption option) {
        if (!containsKey(search, option))
            throw new IllegalArgumentException("Doesn't contain key " + search.toString() + "!");

        if (containsKey(key, option))
            throw new IllegalArgumentException("Already contains key " + key.toString() + "!");

        Element<K, V> iterator = option == IterationOption.Backwards ? getLastIterator() : getFirstIterator();

        while (iterator != null) {
            if (iterator.getKey().equals(search)) {
                Element<K, V> element = new Element<K, V>(key, value);
                if (insertOption == InsertOption.Before) {
                    Element<K, V> tmp = iterator.prev;
                    tmp.setNext(element);
                    element.setPrevious(tmp);
                    element.setNext(iterator);
                    iterator.setPrevious(element);
                } else if (insertOption == InsertOption.After) {
                    Element<K, V> tmp = iterator.next;
                    tmp.setPrevious(element);
                    element.setNext(tmp);
                    element.setPrevious(iterator);
                    iterator.setNext(element);
                }
                break;
            }

            iterator = option == IterationOption.Backwards ? iterator.getPrevious() : iterator.getNext();
        }
    }

    public boolean containsKey(K key, IterationOption option) {
        boolean result = false;
        Element<K, V> iterator = option == IterationOption.Backwards ? getLastIterator() : getFirstIterator();

        while (iterator != null) {
            if (iterator.getKey().equals(key)) {
                result = true;
                break;
            }

            iterator = option == IterationOption.Backwards ? iterator.getPrevious() : iterator.getNext();
        }
        return result;
    }

    public boolean containsValue(V value, IterationOption option) {
        boolean result = false;
        Element<K, V> iterator = option == IterationOption.Backwards ? getLastIterator() : getFirstIterator();

        while (iterator != null) {
            if (iterator.getValue().equals(value)) {
                result = true;
                break;
            }

            iterator = option == IterationOption.Backwards ? iterator.getPrevious() : iterator.getNext();
        }
        return result;
    }

    // TODO: check
    public void clear() {
        Element element = first;
        Element next = first.getNext();
        while (element.hasNext()){
            element.delete();
            element = next;
            next = next.getNext();
        }
    }

    public K getKeyAt(int position) {
        if(position >= size())
            throw new IllegalArgumentException("Position is larger than the actual size");

        Element<K, V> element = getFirstIterator();
        for (int i = 0; i < position; i++) {
            element = element.getNext();
        }
        return element.getKey();
    }

    public V getValueAt(int position) {
        if(position >= size())
            throw new IllegalArgumentException("Position is larger than the actual size");

        Element<K, V> element = getFirstIterator();
        for (int i = 0; i < position; i++) {
            element = element.getNext();
        }
        return element.getValue();
    }

    public V getByKey(K search, IterationOption option) {
        if (!containsKey(search, option))
            throw new IllegalArgumentException("Doesn't contain key " + search.toString() + "!");

        Element<K, V> iterator = option == IterationOption.Backwards ? getLastIterator() : getFirstIterator();

        while (iterator != null) {
            if (iterator.getKey().equals(search)) {
                return iterator.getValue();
            }
            iterator = option == IterationOption.Backwards ? iterator.getPrevious() : iterator.getNext();
        }

        return null;
    }

    public int size() {
        Element element = getFirstIterator();
        int result = 0;

        while (element != null) {
            result++;
            element = element.next;
        }

        return result;
    }

    public Element<K, V> getFirstIterator() {
        current = first;
        return current;
    }

    public Element<K, V> getLastIterator() {
        current = last;
        return current;
    }

    public Collection<K> keys() {
        Collection<K> list = new ArrayList<>();

        for (Element<K, V> element : this) {
            list.add(element.key);
        }

        return list;
    }

    public Collection<V> values() {
        Collection<V> list = new ArrayList<>();

        for (Element<K, V> element : this) {
            list.add(element.value);
        }

        return list;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Dictionary<K, V> clone() {
        return new Dictionary<>(this);
    }

    @Override
    public Iterator<Element> iterator() {
        current = first;
        return this;
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    public boolean hasPrevious() {
        return hasNext();
    }

    @Override
    public Element next() {
        if (current == null)
            throw new UnsupportedOperationException("No next element");

        Element<K, V> retElement = current;
        current = current.getNext();
        return retElement;
    }

    public Element previous() {
        if (current == null)
            throw new UnsupportedOperationException("no previous element");

        current = current.getPrevious();
        return current;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public enum IterationOption {
        Forwards,
        Backwards
    }

    public enum InsertOption {
        Before,
        After
    }
}