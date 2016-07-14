package com.typingsolutions.passwordmanager.adapter.viewholder;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewUtils;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.BaseViewHolder;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import core.DatabaseProvider;


public class PasswordOverviewViewHolder extends BaseViewHolder<PasswordOverviewActivity> {

  private PasswordOverviewActivity activity;

  public final TextView program;
  public final TextView username;
  public final TextView password;
  public final TextView icon;
  public int id;
  private boolean safe = false;

  public PasswordOverviewViewHolder(final PasswordOverviewActivity activity, final View itemView) {
    super(activity, itemView);

    this.activity = activity;

    program = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_program);
    username = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_username);
    password = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_password);
    icon = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_icon);
    icon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
            .setTitle("asdf")
            .create();
        dialog.show();
      }
    });
  }

  public void makeSafe() {
    ViewManager parent = (ViewManager) password.getParent();
    parent.removeView(password);
    parent.removeView(username);

    safe = true;
  }

  @Override
  public void onItemSelected() {
    itemView.setBackgroundColor(0xFFFFFFFF);
    ViewCompat.setElevation(itemView, 10.f);
  }

  @Override
  public void onItemReleased() {
    ViewCompat.setElevation(itemView, 0.f);
  }

  @Override
  public void onItemClear() {

  }

  private void startDetailActivity() {
    Intent intent = new Intent(activity, PasswordDetailActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    intent.putExtra(PasswordDetailActivity.START_DETAIL_INDEX, id);

    BaseDatabaseActivity.logout = false;
    activity.startActivity(intent);
  }

}
