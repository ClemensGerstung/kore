package com.typingsolutions.passwordmanager.adapter.viewholder;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import core.DatabaseProvider;


public class PasswordOverviewViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener, DialogInterface.OnClickListener,
    IItemTouchHelperViewHolder, DatabaseProvider.OnOpenListener {

  private PasswordOverviewActivity activity;

  public final TextView program;
  public final TextView username;
  public final TextView password;
  public final TextView icon;
  public int id;
  private boolean safe = false;

  public PasswordOverviewViewHolder(final PasswordOverviewActivity activity, final View itemView) {
    super(itemView);

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

    itemView.setOnClickListener(this);
  }

  public void makeSafe() {
    ViewManager parent = (ViewManager) password.getParent();
    parent.removeView(password);
    parent.removeView(username);

    safe = true;
  }

  @Override
  public void onClick(View v) {
    if (safe) {
      AlertDialog dialog = new AlertDialog.Builder(activity)
          .setTitle("Reenter your password")
          .setView(R.layout.reenter_password_layout)
          .setPositiveButton("OK", this)
          .create();
      dialog.show();
    } else {
      startDetailActivity();
    }
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    AlertDialog alert = (AlertDialog) dialog;
    EditText editText = (EditText) alert.findViewById(R.id.reenterpasswordlayout_edittext_password);
    String password = editText.getText().toString();

    activity.setRefreshing(true);
    DatabaseProvider.getConnection(activity).tryOpen(password, this);

    alert.dismiss();
  }

  @Override
  public void onItemSelected() {
    itemView.setBackgroundColor(0x99FAFAFA);
  }

  @Override
  public void onItemClear() {
    itemView.setBackgroundColor(0x00FAFAFA);
  }

  @Override
  public void open() {
    activity.setRefreshing(false);
    startDetailActivity();
  }


  private void startDetailActivity() {
    Intent intent = new Intent(activity, PasswordDetailActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    intent.putExtra(PasswordDetailActivity.START_DETAIL_INDEX, id);

    BaseDatabaseActivity.logout = false;
    activity.startActivity(intent);
  }

  @Override
  public void refused() {
    activity.setRefreshing(false);
    activity.makeSnackbar("Your passwords do not match");
  }
}
