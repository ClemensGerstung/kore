package com.typingsolutions.passwordmanager.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.typingsolutions.passwordmanager.R;

import java.io.InputStream;


public class LicenseFragment extends Fragment {
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.license_layout, container, false);
    WebView webView = (WebView) root.findViewById(R.id.licenselayout_webview_about);
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
    return root;
  }
}
