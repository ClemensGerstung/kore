package com.typingsolutions.passwordmanager.viewholder;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.view.ViewManager;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.AlertBuilder;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.BaseViewHolder;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;


public class PasswordOverviewViewHolder extends BaseViewHolder<PasswordOverviewActivity> {

  private PasswordOverviewActivity mActivity;

  public final TextView mTextViewAsProgram;
  public final TextView mTextViewAsUsername;
  public final TextView mTextViewAsPassword;
  public final TextView mTextViewAsIcon;
  public int mCurrentId;
  private boolean safe = false;

  public PasswordOverviewViewHolder(final PasswordOverviewActivity activity, final View itemView) {
    super(activity, itemView);
    itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startDetailActivity();
      }
    });

    this.mActivity = activity;

    mTextViewAsProgram = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_program);
    mTextViewAsUsername = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_username);
    mTextViewAsPassword = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_password);
    mTextViewAsIcon = (TextView) itemView.findViewById(R.id.passwordlistitemlayout_textview_icon);
    mTextViewAsIcon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//        AlertBuilder.create(activity)
//            .setTitle("Icon")
//            .setMessage("Here should be some icons")
//            .show();

        AlertDialog dialog = new AlertDialog.Builder(activity)
            .setTitle("asdf")
            .create();
        dialog.show();
      }
    });
  }

  public void makeSafe() {
    ViewManager parent = (ViewManager) mTextViewAsPassword.getParent();
    parent.removeView(mTextViewAsPassword);
    parent.removeView(mTextViewAsUsername);

    safe = true;
  }

  @Override
  public void onItemSelected() {
//    itemView.setBackgroundColor(0xFFFFFFFF);
//    ViewCompat.setElevation(itemView, 10.f);
  }

  @Override
  public void onItemReleased() {
//    ViewCompat.setElevation(itemView, 0.f);
  }

  @Override
  public void onItemClear() {

  }

  private void startDetailActivity() {
    Intent intent = new Intent(mActivity, PasswordDetailActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    intent.putExtra(PasswordDetailActivity.START_DETAIL_INDEX, mCurrentId);

    BaseDatabaseActivity.logout = false;
    mActivity.startActivity(intent);
  }

}
