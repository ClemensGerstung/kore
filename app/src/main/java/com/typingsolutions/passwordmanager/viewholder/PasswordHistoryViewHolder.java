package com.typingsolutions.passwordmanager.viewholder;

import android.view.View;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.BaseViewHolder;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;

public class PasswordHistoryViewHolder extends BaseViewHolder<PasswordDetailActivity> {
  final TextView mTextViewAsPassword;
  final TextView mTextViewAsDate;

  public PasswordHistoryViewHolder(PasswordDetailActivity activity, View itemView) {
    super(activity, itemView);
    mTextViewAsPassword = (TextView) itemView.findViewById(R.id.passwordhistoryitemlayout_textview_password);
    mTextViewAsDate = (TextView) itemView.findViewById(R.id.passwordhistoryitemlayout_textview_date);
  }

  public TextView getPassword() {
    return mTextViewAsPassword;
  }

  public TextView getDate() {
    return mTextViewAsDate;
  }
}
