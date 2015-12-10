package com.typingsolutions.passwordmanager.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import com.typingsolutions.passwordmanager.R;

import java.io.InputStream;


public class AboutActivity extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.about_layout);
    WebView webView = (WebView) findViewById(R.id.aboutlayout_webview_about);
    InputStream rawStream = getResources().openRawResource(R.raw.about);

    try {
      if(rawStream == null || rawStream.available() == 0)
        throw new IllegalStateException("Couldn't read source");

      final byte[] data = new byte[rawStream.available()];
      rawStream.read(data);
      String html = new String(data);
      webView.loadData(html, "text/html", "UTF-8");
    } catch (Exception e) {

    }
  }
}
