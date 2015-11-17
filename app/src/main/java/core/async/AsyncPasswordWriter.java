package core.async;

import android.content.Context;
import android.os.AsyncTask;

public class AsyncPasswordWriter extends AsyncTask<String, Void, Void> {
    private Context context;

    public AsyncPasswordWriter(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        return null;
    }
}
