package core.async;

import android.content.Context;
import android.util.Log;
import core.Dictionary;

import java.util.Arrays;

public class AsyncDatabasePipeline {
  private Context context;
  private Dictionary<String, Object[]> queries;
  private boolean working;
  private final Thread looper;

  private final Runnable looping = new Runnable() {
    @Override
    public void run() {
      working = true;
      synchronized (AsyncDatabasePipeline.this.looper) {
        Dictionary.Element element = queries.getFirstIterator();

        while (queries.hasNext()) {
          // TODO: Run DB-Query
          Log.d(AsyncDatabasePipeline.class.getSimpleName(), String.format("%s => %s", element.getKey(), Arrays.toString((Object[]) element.getValue())));

          queries.removeByKey((String) element.getKey());
          element = queries.next();
        }
        try {
          looper.wait();
        } catch (InterruptedException ignored) {
        }
      }
      working = false;
    }
  };


  private static AsyncDatabasePipeline PIPELINE;

  public static void add(String query, Object... params) {
    if (PIPELINE == null)
      throw new IllegalStateException("Pipeline has not been initialized! Call getPipeline() first!");

    PIPELINE.addQuery(query, params);
  }

  public void addQuery(String query, Object... params) {
    queries.addLast(query, params);
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

}
