package com.typingsolutions.passwordmanager.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;

public class AboutFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.about_layout, container, false);
    TextView textView = (TextView) root.findViewById(R.id.aboutlayout_textview_version);
    try {
      String packageName = getActivity().getPackageName();
      PackageInfo info = getActivity().getPackageManager().getPackageInfo(packageName, 0);
      textView.setText(textView.getText().toString().replace("{Major}", info.versionName));
      textView.setText(textView.getText().toString().replace("{Release}", Integer.toString(info.versionCode)));
    } catch (PackageManager.NameNotFoundException e) {
      Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    }

    return root;
  }
}
