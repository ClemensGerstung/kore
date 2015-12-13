package core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

// because Java sucks...

/**
 * A double chained dictionary. indicated by the key
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public class Dictionary<K, V> implements Iterable<Dictionary.Element>, Iterator<Dictionary.Element>, Cloneable {

  public class Element<T, U> {
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

    @Override
    public String toString() {
      return "Element{" +
          "key=" + key +
          ", value=" + value +
          '}';
    }
  }

  /**
   * The first element in the dictionary
   */
  private Element<K, V> first;
  /**
   * The last element in the dictionary
   */
  private Element<K, V> last;
  /**
   * The current element during iteration
   */
  private Element<K, V> current;

  /**
   * Initialises an empty {@see Dictionary}
   */
  public Dictionary() {
    first = null;
    last = null;
    current = null;
  }


  /**
   * Copy constructor for the {@see Dictionary}
   *
   * @param other the {@see Dictionary} to copy
   */
  public Dictionary(Dictionary<K, V> other) {
    this();
    addAll(other, InsertOption.After, IterationOption.Forwards);
  }

  /**
   * Adds a not existing element to the end of the dictionary
   *
   * @param key   the key of the element
   * @param value the value of the element
   * @return the new added element
   */
  public Element<K, V> addLast(K key, V value) {
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

    return last;
  }

  /**
   * Adds an not existing element to the front of the dictionary
   *
   * @param key   of the element
   * @param value of the element
   * @return the new element
   */
  public Element<K, V> addFirst(K key, V value) {
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
    return first;
  }
  
  /**
   * Adds all elements of another dictionary to the current
   *
   * @param other        the dictionary to copy
   * @param insertOption either add before the current dictionary or behind
   * @param option       iterate the other dictionary from front to end or vice versa
   */
  public void addAll(Dictionary<K, V> other, InsertOption insertOption, IterationOption option) {
    Element<K, V> iterator = (option == IterationOption.Forwards ? other.getFirstIterator() : other.getLastIterator());
    addAll(iterator, insertOption, option);
  }

  /**
   * Recursive method to copy a dictionary
   *
   * @param element         to copy
   * @param insertOption    either add before the current element or behind
   * @param iterationOption iterate the other dictionary from front to end or vice versa
   */
  private void addAll(Element<K, V> element, InsertOption insertOption, IterationOption iterationOption) {
    boolean forward = iterationOption == IterationOption.Forwards;

    if (containsKey(element.key, iterationOption))
      addAll(element, insertOption, iterationOption, forward);

    if (insertOption == InsertOption.After) {
      addLast(element.key, element.value);
    } else {
      addFirst(element.key, element.value);
    }

    addAll(element, insertOption, iterationOption, forward);
  }

  /**
   * Recursive method to copy a dictionary
   *
   * @param element         to copy
   * @param insertOption    either add before the current element or behind
   * @param iterationOption iterate the other dictionary from front to end or vice versa
   * @param forward         {@param iterationOption} == {@see IterationOption.Forwards}
   */
  private void addAll(Element<K, V> element, InsertOption insertOption, IterationOption iterationOption, boolean forward) {
    if (forward && element.hasNext()) {
      addAll(element.getNext(), insertOption, iterationOption);
    } else if (!forward && element.hasPrevious()) {
      addAll(element.getPrevious(), insertOption, iterationOption);
    }
  }

  /**
   * Sets an value for an specific key or adds it if it doesn't exist
   * @param key to search for
   * @param value to set
   * @param option to iterate through the dictionary
   */
  public void setForKey(K key, V value, IterationOption option) {
    Element<K, V> iterator = option == IterationOption.Backwards ? getLastIterator() : getFirstIterator();

    boolean setValue = setForKey(iterator, key, value, option == IterationOption.Backwards);

    if (setValue)
      return;

    if (option == IterationOption.Backwards) {
      addFirst(key, value);
    } else {
      addLast(key, value);
    }
  }

  /**
   * Recursive method to set an value for a key
   * @param element current iterator
   * @param key to search for
   * @param value to set
   * @param forward iteration way through the dictionary
   * @return if element was in dictionary
   */
  private boolean setForKey(Element<K, V> element, K key, V value, boolean forward) {
    if (element.key.equals(key)) {
      element.value = value;
      return true;
    }

    return (forward && element.hasNext() || !forward && element.hasPrevious()) && setForKey(forward ? element.next : element.prev, key, value, forward);
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

  public void clear() {
    if (first == null && last == null)
      throw new IllegalStateException("dictionary is already cleared!");

    Element<K, V> element = first;
    Element<K, V> next = first.getNext();
    while (next != null) {
      element.delete();
      element = next;
      next = next.getNext();
    }

    last.delete();
    first = null;
    last = null;
    current = null;
  }

  public K getKeyAt(int position) {
    if (position >= size())
      throw new IllegalArgumentException("Position is larger than the actual size");

    Element<K, V> element = getFirstIterator();
    for (int i = 0; i < position; i++) {
      element = element.getNext();
    }
    return element.getKey();
  }

  public V getValueAt(int position) {
    if (position >= size())
      throw new IllegalArgumentException("Position is larger than the actual size");

    Element<K, V> element = getFirstIterator();
    for (int i = 0; i < position; i++) {
      element = element.getNext();
    }
    return element.getValue();
  }

  public Element<K, V> removeByKey(K key) {
    if (!containsKey(key, IterationOption.Forwards))
      throw new IllegalArgumentException("Doesn't contain key " + key.toString() + "!");

    for (Element element : this) {
      if (element.key.equals(key)) {
        Element prev = element.prev;
        Element next = element.next;
        prev.setNext(next);
        next.setPrevious(prev);
        return element;
      }
    }

    return null;
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
    Element<K, V> element = getFirstIterator();
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
    ArrayList<K> dictionary = new ArrayList<>();

    if (!hasNext())
      return dictionary;

    return keys(dictionary, getFirstIterator());
  }

  private Collection<K> keys(Collection<K> collection, Element<K, V> element) {
    collection.add(element.key);
    if (element.hasNext())
      return keys(collection, element.getNext());

    return collection;
  }

  public Collection<V> values() {
    Collection<V> dictionary = new ArrayList<>();

    if (!hasNext())
      return dictionary;

    return values(dictionary, getFirstIterator());
  }

  private Collection<V> values(Collection<V> collection, Element<K, V> element) {
    collection.add(element.value);
    if (element.hasNext())
      return values(collection, element.getNext());

    return collection;
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

    current = current.getNext();
    return current;
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