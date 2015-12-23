package core.async;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import core.Dictionary;

import java.util.List;

public class AsyncDatabasePipeline extends AsyncTask<String, Void, Void> {
  private Context context;
  private Dictionary<String, Object[]> queries;
  private boolean working;

  private static AsyncDatabasePipeline PIPELINE;

  public static void AddQuery(String query, Object... params) {
    PIPELINE.queries.addLast(query, params);
    if(!PIPELINE.working)
      Looper.loop();
  }

  public static AsyncDatabasePipeline getPipeline(Context context) {
    if (PIPELINE == null)
      PIPELINE = new AsyncDatabasePipeline(context);
    return PIPELINE;
  }

  protected AsyncDatabasePipeline(Context context) {
    this.context = context;
    this.queries = new Dictionary<>();
    this.working = false;
  }

  @Override
  protected Void doInBackground(String... params) {
    Looper.prepare();
    

    Looper.loop();
    return null;
  }
}
