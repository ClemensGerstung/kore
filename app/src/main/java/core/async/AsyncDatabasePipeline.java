package core.async;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Pair;
import core.DatabaseProvider;
import core.Dictionary;
import net.sqlcipher.Cursor;

@Deprecated
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
      Dictionary.Element<Pair<String, Object[]>, AsyncQueryListener> element;
      DatabaseProvider provider = DatabaseProvider.getConnection(context);
      synchronized (AsyncDatabasePipeline.this.looper) {
        element = queries.removeFirst();
        working = true;
      }

      while (working) {
        try {
          String query = element.getKey().first.toUpperCase();
          if (query.startsWith("INSERT")) {
            long result = provider.insert(element.getKey().first, element.getKey().second);
            raiseActionListener(element.getValue(), result >= 0, "INSERT result is -1!", result);
          } else if (query.startsWith("UPDATE") || query.startsWith("DELETE")) {
            long result = provider.update(element.getKey().first, element.getKey().second);
            raiseActionListener(element.getValue(), result >= 0, "UPDATE/DELETE result is -1!", result);
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
        synchronized (AsyncDatabasePipeline.this.looper) {
          looper.wait();
          looping.run();
        }
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

  public void addQuery(String query, @Nullable AsyncQueryListener listener, Object... params) {
    queries.addLast(new Pair<>(query, params), listener);

    synchronized (looper) {
      if (working)
        return;

      if (!looper.isAlive()) {
        looper.start();
      } else {
        looper.notify();
      }
    }
  }

  public static void end() {
    if (PIPELINE == null)
      throw new IllegalStateException("Pipeline has not been initialized! Call getPipeline() first!");

    PIPELINE.endLooper();
  }

  public void endLooper() {
    synchronized (looper) {
      exit = true;
      looper.notify();
      looper.interrupt();
    }
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
    this.exit = false;
    this.looper = new Thread(looping);
  }

  public interface AsyncQueryListener {
    void executed(Object... results);

    void failed(String message);
  }
}
