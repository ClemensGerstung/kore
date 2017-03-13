package com.typingsolutions.kore.common;

import android.os.Handler;
import android.os.Message;

class OpenDatabaseHandler extends Handler {
  private IEvent<Integer> mEvent;

  OpenDatabaseHandler(IEvent<Integer> event) {
    mEvent = event;
  }

  @Override
  public void handleMessage(Message msg) {
    if(mEvent != null)
      mEvent.callback(this, new EventArgs<>(msg.what));
  }
}
