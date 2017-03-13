package com.typingsolutions.kore.common;


public interface IEvent<T> {
  void callback(Object sender, EventArgs<T> e);
}
