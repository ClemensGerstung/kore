package com.typingsolutions.passwordmanager;

import android.os.AsyncTask;
import com.typingsolutions.passwordmanager.database.DatabaseConnection;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAsyncTask<Progress, Result> extends AsyncTask<DatabaseConnection, Progress, Result> {
  private List<IExecutionCallback<Result>> mCallbacks;

  public BaseAsyncTask() {
    this.mCallbacks = new ArrayList<>();
  }

  public void registerCallback(IExecutionCallback<Result> callback) {
    mCallbacks.add(callback);
  }

  public void unregisterCallback(IExecutionCallback<Result> callback) {
    mCallbacks.remove(callback);
  }

  protected void raiseCallbacks(Result result) {
    for (IExecutionCallback<Result> callback : mCallbacks){
      callback.executed(result);
    }
  }

  protected void raiseCallbacks(int code, String message) {
    for (IExecutionCallback<Result> callback : mCallbacks){
      callback.failed(code, message);
    }
  }

  protected void releaseCallbacks() {
    mCallbacks.clear();
  }

  public interface IExecutionCallback<Result> {
    void executed(Result result);
    void failed(int code, String message);
  }
}
