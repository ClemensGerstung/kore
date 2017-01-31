package com.typingsolutions.kore.setup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.typingsolutions.kore.R;
import com.typingsolutions.kore.common.DatabaseConnection;
import com.typingsolutions.kore.common.KoreApplication;
import net.sqlcipher.database.SQLiteDatabase;

public class SetupActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setup_layout);

    KoreApplication app = (KoreApplication) getApplicationContext();

    app.setOnDatabaseOpened((sender, e) -> Log.d(getClass().getSimpleName(), "" + e.getData()));
  }


}
