/**
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@FreeBSD.ORG> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Poul-Henning Kamp
 * ----------------------------------------------------------------------------
 */

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

  /**
   * Element for the dictionary to save keys and values
   *
   * @param <T> the type of the key
   * @param <U> the type of the value
   */
  public class Element<T, U> {
    /**
     * Key of the element
     */
    private T key;

    /**
     * Value of the element
     */
    private U value;

    /**
     * element's successor
     */
    private Element<T, U> next;
    /**
     * element's predecessor
     */
    private Element<T, U> prev;

    /**
     * Constructor to create an element
     *
     * @param key   for the element
     * @param value for the element
     */
    /*package*/ Element(T key, U value) {
      this.key = key;
      this.value = value;
      this.next = null;
      this.prev = null;
    }

    /**
     * Copy constructor
     *
     * @param other element to copy
     */
    /*package*/ Element(Element<T, U> other) {
      key = other.key;
      value = other.value;
      next = other.next;
      prev = other.prev;
    }

    /**
     * Gets the element's key
     *
     * @return the element's key
     */
    public T getKey() {
      return key;
    }

    /**
     * Gets the value of the element
     *
     * @return the value
     */
    public U getValue() {
      return value;
    }

    /**
     * Sets the value of the element
     *
     * @param value to set
     */
    public void setValue(U value) {
      this.value = value;
    }

    /**
     * Gets the successor
     *
     * @return next element
     */
    /*package*/ Element<T, U> getNext() {
      return next;
    }

    /**
     * Sets the successor
     *
     * @param next element for the current
     */
    /*package*/ void setNext(Element<T, U> next) {
      this.next = next;
    }

    /**
     * Gets the predecessor
     *
     * @return the previous element
     */
    /*package*/  Element<T, U> getPrevious() {
      return prev;
    }

    /**
     * Sets the predecessor
     *
     * @param prev element to the current
     */
    /*package*/  void setPrevious(Element<T, U> prev) {
      this.prev = prev;
    }

    /**
     * Indicates if there is an successor
     *
     * @return {@code true} if there is one
     */
    /*package*/  boolean hasNext() {
      return next != null;
    }

    /**
     * Indicates if there is an predecessor
     *
     * @return {@code true} if there is one
     */
    /*package*/ boolean hasPrevious() {
      return prev != null;
    }

    /**
     * Resets the element
     */
    /*package*/ void delete() {
      key = null;
      value = null;
      next = null;
      prev = null;
    }

    /**
     * Compares two elements
     *
     * @param o element to compare with
     * @return {@code true} if equals
     */
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Element)) return false;

      Element<T, U> element = (Element<T, U>) o;

      if (key != null ? !key.equals(element.key) : element.key != null) return false;
      return !(value != null ? !value.equals(element.value) : element.value != null);

    }

    /**
     * Calculates an unique hash value for the element
     *
     * @return the calculated hash
     */
    @Override
    public int hashCode() {
      int result = key != null ? key.hashCode() : 0;
      result = 31 * result + (value != null ? value.hashCode() : 0);
      return result;
    }

    /**
     * Human readable string
     *
     * @return the human readable string
     */
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
   *
   * @param key    to search for
   * @param value  to set
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
   *
   * @param element current iterator
   * @param key     to search for
   * @param value   to set
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

  /**
   * Inserts an element before or after another element with the spezific search key
   *
   * @param search       key to search element for
   * @param key          of the new element
   * @param value        of the new element
   * @param insertOption insert new element before or after the the found elements
   * @param option       to iterate through the dictionary
   * @return the new element
   */
  public Element<K, V> insertKey(K search, K key, V value, InsertOption insertOption, IterationOption option) {
    if (!containsKey(search, option))
      throw new IllegalArgumentException("Doesn't contain key " + search.toString() + "!");

    if (containsKey(key, option))
      throw new IllegalArgumentException("Already contains key " + key.toString() + "!");

    Element<K, V> iterator = option == IterationOption.Backwards ? getLastIterator() : getFirstIterator();

    return insertKey(iterator, search, key, value, insertOption, option);
  }

  /**
   * Recursive method to insert an element
   *
   * @param element      current element to check key
   * @param search       key to search element for
   * @param key          of the new element
   * @param value        of the new element
   * @param insertOption insert new element before or after the the found elements
   * @param option       to iterate through the dictionary
   * @return the new element
   */
  private Element<K, V> insertKey(Element<K, V> element, K search, K key, V value, InsertOption insertOption, IterationOption option) {
    if (element.getKey().equals(search)) {
      Element<K, V> inserted = new Element<K, V>(key, value);
      if (insertOption == InsertOption.Before) {
        Element<K, V> tmp = element.prev;
        tmp.setNext(inserted);
        inserted.setPrevious(tmp);
        inserted.setNext(element);
        element.setPrevious(inserted);
      } else if (insertOption == InsertOption.After) {
        Element<K, V> tmp = element.next;
        tmp.setPrevious(inserted);
        inserted.setNext(tmp);
        inserted.setPrevious(element);
        element.setNext(inserted);
      }
      return inserted;
    }

    if (option == IterationOption.Forwards && element.hasNext()) {
      return insertKey(element.getNext(), search, key, value, insertOption, option);
    } else if (option == IterationOption.Backwards && element.hasPrevious()) {
      return insertKey(element.getPrevious(), search, key, value, insertOption, option);
    }

    return null;
  }

  /**
   * Searches for a key in the dictionary
   *
   * @param key    to search for
   * @param option iterate through the dictionary forwards or backwards
   * @return {@code true} if key is in dictionary otherwise {@code false}
   */
  public boolean containsKey(K key, IterationOption option) {
    Element<K, V> iterator = option == IterationOption.Backwards ? getLastIterator() : getFirstIterator();

    return containsKey(iterator, key, option);
  }

  /**
   * Recursive method to search for an key in the dictionary
   *
   * @param element current element to check key
   * @param key     to search for
   * @param option  iterate through the dictionary forwards or backwards
   * @return {@code true} if key is in dictionary otherwise {@code false}
   */
  private boolean containsKey(Element<K, V> element, K key, IterationOption option) {
    if (element.getKey().equals(key))
      return true;

    if (option == IterationOption.Forwards && element.hasNext()) {
      return containsKey(element.getNext(), key, option);
    } else if (option == IterationOption.Backwards && element.hasPrevious()) {
      return containsKey(element.getPrevious(), key, option);
    }

    return false;
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



  /**
   * Clears the dictionary
   */
  public void clear() {
    if (first == null && last == null)
      throw new IllegalStateException("Dictionary is already cleared!");

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

  /**
   * Counts the number of elements in the dictionary
   *
   * @return the number of elements
   */
  public int size() {
    Element<K, V> element = getFirstIterator();

    return size(element, 0);
  }

  /**
   * Recursive method to count the number of elements
   *
   * @param element current element
   * @param size    the current size
   * @return the size
   */
  private int size(Element<K, V> element, int size) {
    if (element.hasNext())
      size(element.getNext(), size + 1);

    return size;
  }

  /**
   * Gets the first element of the dictionary
   *
   * @return the first element
   */
  public Element<K, V> getFirstIterator() {
    current = first;
    return current;
  }

  /**
   * Gets the last element of the dictionary
   *
   * @return the last element
   */
  public Element<K, V> getLastIterator() {
    current = last;
    return current;
  }

  /**
   * Gets all keys from the current dictionary in one collection
   *
   * @return the new collection with all keys
   */
  public Collection<K> keys() {
    ArrayList<K> dictionary = new ArrayList<>();

    if (!hasNext())
      return dictionary;

    return keys(dictionary, getFirstIterator());
  }

  /**
   * Recursive method to get all keys
   *
   * @param collection the collection with all inserted keys
   * @param element    to insert the key into the collection
   * @return the collection
   */
  private Collection<K> keys(Collection<K> collection, Element<K, V> element) {
    collection.add(element.key);
    if (element.hasNext())
      return keys(collection, element.getNext());

    return collection;
  }

  /**
   * Gets all values of the current dictionary in one collection
   *
   * @return the new collection with all values
   */
  public Collection<V> values() {
    Collection<V> dictionary = new ArrayList<>();

    if (!hasNext())
      return dictionary;

    return values(dictionary, getFirstIterator());
  }

  /**
   * Recursive method to get all values of the current dictionary
   *
   * @param collection the collection with all values
   * @param element    element to insert the value into the collection
   * @return the collection
   */
  private Collection<V> values(Collection<V> collection, Element<K, V> element) {
    collection.add(element.value);
    if (element.hasNext())
      return values(collection, element.getNext());

    return collection;
  }

  /**
   * Copies the current dictionary
   *
   * @return the cloned dictionary
   */
  @SuppressWarnings("CloneDoesntCallSuperClone")
  @Override
  public Dictionary<K, V> clone() {
    return new Dictionary<>(this);
  }

  /**
   * Returns the iterator
   *
   * @return this dictionary
   */
  @Override
  public Iterator<Element> iterator() {
    current = first;
    return this;
  }

  /**
   * Checks if there is an successor to the current iterator
   *
   * @return {@code true} if there is one
   */
  @Override
  public boolean hasNext() {
    return current != null;
  }

  /**
   * Same as {@code hasNext()} but for the predecessor
   *
   * @return {@code true} if there is one
   */
  public boolean hasPrevious() {
    return hasNext();
  }

  /**
   * Gets the nex element for iteration
   *
   * @return the successor
   */
  @Override
  public Element next() {
    if (current == null)
      throw new UnsupportedOperationException("No next element");

    current = current.getNext();
    return current;
  }

  /**
   * Gets the previous element in the iteration
   *
   * @return the predecessor
   */
  public Element<K, V> previous() {
    if (current == null)
      throw new UnsupportedOperationException("no previous element");

    current = current.getPrevious();
    return current;
  }

  /**
   * Optional method
   *
   * @throws UnsupportedOperationException
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  /**
   * Option to iterate through the dictionary
   */
  public enum IterationOption {
    /**
     *
     */
    Backwards,

    /**
     *
     */
    Forwards
  }

  /**
   * Option to insert elements into the dictionary
   */
  public enum InsertOption {
    /**
     *
     */
    Before,

    /**
     *
     */
    After
  }
}