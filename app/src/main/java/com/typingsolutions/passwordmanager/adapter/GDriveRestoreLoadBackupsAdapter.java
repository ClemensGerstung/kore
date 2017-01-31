package com.typingsolutions.passwordmanager.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.BaseAdapter;
import com.typingsolutions.passwordmanager.BaseViewHolder;
import com.typingsolutions.passwordmanager.activities.BackupActivity;
import com.typingsolutions.passwordmanager.fragments.ScheduleBackupFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GDriveRestoreLoadBackupsAdapter extends BaseAdapter {
  private List<String> mList;

  public GDriveRestoreLoadBackupsAdapter(BaseActivity activity) {
    super(activity);
    mList = new ArrayList<>();

    new GDriveBackupFileMetaLoader().execute();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = View.inflate(mActivity, android.R.layout.simple_list_item_1, null);

    int[] attrs = new int[]{android.R.attr.selectableItemBackground};
    TypedArray ta = mActivity.obtainStyledAttributes(attrs);
    Drawable drawableFromTheme = ta.getDrawable(0);
    ta.recycle();

    v.setBackground(drawableFromTheme);
    ((TextView) v).setTextColor(0xFF000000);
    v.setClickable(true);

    return new BaseViewHolder<>(mActivity, v);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ((TextView) holder.itemView).setText(mList.get(position));
  }

  @Override
  public int getItemCount() {
    return mList.size();
  }

  void add(String name) {
    mList.add(name);
    mActivity.runOnUiThread(this::notifyDataSetChanged);
  }

  public class GDriveBackupFileMetaLoader extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
      SharedPreferences prefs = mActivity.getSharedPreferences(BackupActivity.class.getName(), Context.MODE_PRIVATE);
      String name = prefs.getString(ScheduleBackupFragment.PREF_ACCOUNT_NAME, null);

      try {
        GoogleAccountCredential credential = GoogleAccountCredential
            .usingOAuth2(mActivity.getApplicationContext(), Arrays.asList(ScheduleBackupFragment.SCOPES))
            .setBackOff(new ExponentialBackOff());

        credential.setSelectedAccountName(name);
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Drive service = new Drive.Builder(transport, jsonFactory, credential)
            .setApplicationName("kore")
            .build();

        File folderMetadata = new File();
        folderMetadata.setName("kore");
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

        List<File> results = service.files().list().setQ("name='kore' and trashed=false").execute().getFiles();
        File folder = null;

        if (results.isEmpty()) {
          // TODO: error
          return null;
        }

        folder = results.get(0);

        List<File> files = service.files()
            .list()
            .setOrderBy("createdTime desc")
            .setQ("'" + folder.getId() + "' in parents")
            .execute()
            .getFiles();

        for (File file : files) {
          add(file.getName());
        }


      } catch (IOException e) {
        // TODO: error
        Log.e(getClass().getSimpleName(), e.getMessage());
      }


      return null;
    }
  }
}
