package com.typingsolutions.passwordmanager.adapter.viewholder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.EditTextImpl;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import core.DatabaseProvider;
import com.typingsolutions.passwordmanager.adapter.PasswordOverviewAdapter;


public class PasswordOverviewViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener, DialogInterface.OnClickListener,
               IItemTouchHelperViewHolder {

  private Context context;
  private PasswordOverviewAdapter passwordOverviewAdapter;

  public final TextView program;
  public final TextView username;
  public final TextView password;
  public final TextView icon;
  public int id;
  private boolean safe = false;

  public PasswordOverviewViewHolder(Context context, PasswordOverviewAdapter passwordOverviewAdapter, final View itemView) {
    super(itemView);

    this.context = context;
    this.passwordOverviewAdapter = passwordOverviewAdapter;

    program = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_program);
    username = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_username);
    password = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_password);
    icon = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_icon);
    icon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

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
      AlertDialog dialog = new AlertDialog.Builder(context)
          .setTitle("Reenter your password")
          .setView(R.layout.reenter_password_layout)
          .setPositiveButton("OK", this)
          .create();
      dialog.show();
    } else {
      Intent intent = new Intent(context, PasswordDetailActivity.class);
      intent.putExtra(PasswordDetailActivity.START_DETAIL_INDEX, id);
      context.startActivity(intent);
    }
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    AlertDialog alert = (AlertDialog) dialog;
    EditText editText = (EditText) alert.findViewById(R.id.reenterpasswordlayout_edittext_password);
    String password = editText.getText().toString();

    passwordOverviewAdapter.setRefreshing(true);
    DatabaseProvider.getConnection(context)
        .tryOpen(password, passwordOverviewAdapter.getOnOpenListener());
    passwordOverviewAdapter.setCurrentId(id);

    alert.dismiss();
  }

  @Override
  public void onItemSelected() {

  }

  @Override
  public void onItemClear() {
    itemView.setBackgroundColor(0);
  }
}
