package com.typingsolutions.passwordmanager.adapter;

import android.view.View;
import android.view.ViewGroup;
import com.typingsolutions.passwordmanager.BaseAdapter;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;
import com.typingsolutions.passwordmanager.dao.PasswordItem;
import com.typingsolutions.passwordmanager.viewholder.PasswordHistoryViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PasswordHistoryAdapter extends BaseAdapter<PasswordHistoryViewHolder, PasswordDetailActivity> {

  private PasswordContainer mPassword;

  public PasswordHistoryAdapter(PasswordDetailActivity activity, PasswordContainer container) {
    super(activity);
    mPassword = container;
  }

  @Override
  public PasswordHistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View view = inflater.inflate(R.layout.password_history_item_layout, viewGroup, false);

    return new PasswordHistoryViewHolder(mActivity, view);
  }

  @Override
  public void onBindViewHolder(PasswordHistoryViewHolder holder, int position) {
    PasswordItem history = mPassword.getPasswordItems().valueAt(position + 1);

    DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
    String date = dateFormat.format(history.getDate());

    holder.getPassword().setText(history.getPassword());
    holder.getDate().setText(date);
  }

  @Override
  public int getItemCount() {
    return mPassword.getPasswordItems().size() - 1;
  }


}
