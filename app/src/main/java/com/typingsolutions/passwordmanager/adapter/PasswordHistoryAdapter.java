package com.typingsolutions.passwordmanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.typingsolutions.passwordmanager.BaseAdapter;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;
import com.typingsolutions.passwordmanager.dao.PasswordItem;
import com.typingsolutions.passwordmanager.viewholder.PasswordHistoryViewHolder;
import core.data.Password;
import core.data.PasswordHistory;
import core.data.PasswordProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PasswordHistoryAdapter extends BaseAdapter<PasswordHistoryViewHolder, PasswordDetailActivity> {

  private PasswordContainer mPassword;

  public PasswordHistoryAdapter(PasswordDetailActivity activity, int passwordIndex) {
    super(activity);
    mPassword = (PasswordContainer) activity.getContainerAt(passwordIndex);
  }

  @Override
  public PasswordHistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View view = inflater.inflate(R.layout.password_history_item_layout, viewGroup, false);

    return new PasswordHistoryViewHolder(mActivity, view);
  }

  @Override
  public void onBindViewHolder(PasswordHistoryViewHolder holder, int position) {
    PasswordItem history = mPassword.getPasswordItems().valueAt(position);

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
