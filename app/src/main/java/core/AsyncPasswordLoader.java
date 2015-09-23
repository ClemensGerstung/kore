package core;

import android.content.Context;
import android.database.Cursor;

public class AsyncPasswordLoader extends android.content.AsyncTaskLoader<Cursor> {
    public AsyncPasswordLoader(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        return null;
    }



}