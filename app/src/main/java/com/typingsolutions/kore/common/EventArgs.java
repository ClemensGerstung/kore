package com.typingsolutions.kore.common;

public class EventArgs<T> {
  private T mData;

  public EventArgs(T data) {
    this.mData = data;
  }

  public T getData() {
    return mData;
  }
}
