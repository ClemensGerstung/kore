package core;

import java.util.Iterator;

// because Java sucks...
public class Dictionary<K, V> implements Iterable<Dictionary.Element>, Iterator<Dictionary.Element>, Cloneable {

    public class Element<T extends K, U extends V> {
        private T key;
        private U value;
        private Element<T, U> next;
        private Element<T, U> prev;

        Element(T key, U value) {
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
        }

        Element(Element<T, U> other) {
            key = other.key;
            value = other.value;
            next = other.next;
            prev = other.prev;
        }

        public T getKey() {
            return key;
        }

        public void setKey(T key) {
            this.key = key;
        }

        public U getValue() {
            return value;
        }

        public void setValue(U value) {
            this.value = value;
        }

        Element<T, U> getNext() {
            return next;
        }

        void setNext(Element<T, U> next) {
            this.next = next;
        }

        Element<T, U> getPrevious() {
            return prev;
        }

        void setPrevious(Element<T, U> prev) {
            this.prev = prev;
        }

        boolean hasNext() {
            return next != null;
        }

        boolean hasPrevious() {
            return prev != null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Element)) return false;

            Element<T, U> element = (Element<T, U>) o;

            if (key != null ? !key.equals(element.key) : element.key != null) return false;
            return !(value != null ? !value.equals(element.value) : element.value != null);

        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
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

    public <T extends K, U extends V> Dictionary(Dictionary<T, U> other) {
        this();

    }

    public void addLast(K key, V value) {
        if(first == null) {
            first = new Element<>(key, value);
        } else {
            Element<K, V> element = first;
            while (element.hasNext()){
                element = element.getNext();
            }
            last = new Element<>(key, value);
            element.setNext(last);
        }
    }

    public void addFirst(K key, V value) {
        if(last == null) {
            last = new Element<>(key, value);
        } else {
            Element<K, V> element = last;
            while (element.hasPrevious()){
                element = element.getPrevious();
            }
            first = new Element<>(key, value);
            element.setPrevious(first);
        }
    }



    public Element getFirstIterator() {
        current = new Element<K, V>(first);
        return current;
    }

    public Element getLastIterator() {
        current = new Element<K, V>(last);
        return current;
    }

    @Override
    public Iterator<Element> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return current.hasNext();
    }

    public boolean hasPrevious() {
        return current.hasPrevious();
    }

    @Override
    public Element next() {
        if (current == null)
            throw new UnsupportedOperationException("No next element");

        current = current.getNext();
        return current;
    }

    public Element previuos() {
        if (current == null)
            throw new UnsupportedOperationException("no previous element");

        current = current.getPrevious();
        return current;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
