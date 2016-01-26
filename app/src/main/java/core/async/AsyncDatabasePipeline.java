package core.async;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import core.DatabaseProvider;
import core.Dictionary;
import net.sqlcipher.Cursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AsyncDatabasePipeline {
  private Context context;
  private Dictionary<Pair<String, Object[]>, AsyncQueryListener> queries;
  private boolean working;
  private boolean exit;
  private final Thread looper;

  private final Runnable looping = new Runnable() {
    @Override
    public void run() {
      if (exit)
        return;
      Dictionary.Element<Pair<String, Object[]>, AsyncQueryListener> element = null;
      DatabaseProvider provider = DatabaseProvider.getConnection(context);
      synchronized (AsyncDatabasePipeline.this.looper) {
        element = queries.getFirstIterator();
        working = queries.hasNext();
      }

      while (working) {

        try {
          String query = element.getKey().first.toUpperCase();
          if (query.startsWith("INSERT")) {
            long result = provider.insert(element.getKey().first, element.getKey().second);
            raiseActionListener(element.getValue(), result >= 0, "INSERT result is -1!", result);
          } else if (query.startsWith("UPDATE") || query.startsWith("REMOVE")) {
            long result = provider.update(element.getKey().first, element.getKey().second);
            raiseActionListener(element.getValue(), result >= 0, "UPDATE/REMOVE result is -1!", result);
          } else if (query.startsWith("SELECT")) {
            Cursor cursor = provider.query(element.getKey().first, (String[]) element.getKey().second);
            raiseActionListener(element.getValue(), cursor != null, "Couldn't query", cursor);
          } else {
            throw new IllegalArgumentException("No known SQLiteQuery");
          }
        } catch (Exception e) {
          raiseActionListener(element.getValue(), false, e.getMessage());
        }


        synchronized (AsyncDatabasePipeline.this.looper) {
          element = queries.removeFirst();
          working = queries.hasElements();
        }
      }

      try {
        looper.wait();
        looper.run();
      } catch (InterruptedException ignored) {
      }
    }
  };

  private void raiseActionListener(AsyncQueryListener listener, boolean additionalCondition, String failMessage, Object... results) {
    if (listener == null)
      return;

    if (additionalCondition) {
      listener.executed(results);
    } else {
      listener.failed(failMessage);
    }
  }

  private static AsyncDatabasePipeline PIPELINE;

  public static void add(String query, Object... params) {
    if (PIPELINE == null)
      throw new IllegalStateException("Pipeline has not been initialized! Call getPipeline() first!");

    PIPELINE.addQuery(query, params);
  }

  public void addQuery(String query, Object... params) {
    //queries.addLast(query, params);
    if (!working) {
      if (!looper.isAlive()) {
        looper.start();
      } else {
        synchronized (looper) {
          looper.notify();
        }
      }
    }
  }

  public static void end() {

  }

  public static AsyncDatabasePipeline getPipeline(Context context) {
    if (PIPELINE == null) {
      PIPELINE = new AsyncDatabasePipeline(context);
    }
    return PIPELINE;
  }

  protected AsyncDatabasePipeline(Context context) {
    this.context = context;
    this.queries = new Dictionary<>();
    this.working = false;
    this.looper = new Thread(looping);
  }

  public interface AsyncQueryListener {
    void executed(Object... results);

    void failed(String message);
  }
}
