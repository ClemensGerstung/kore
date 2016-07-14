package com.typingsolutions.passwordmanager;

public interface IListChangedListener<E> {
  void onItemAdded(int index, E item);
  void onItemRemoved(int index, E item);
  void onItemChanged(int index, E oldItem, E newItem);
}
