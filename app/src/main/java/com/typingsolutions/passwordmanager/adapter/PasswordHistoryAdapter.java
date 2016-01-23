package com.typingsolutions.passwordmanager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import core.data.Password;
import core.data.PasswordHistory;
import core.data.PasswordProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PasswordHistoryAdapter extends RecyclerView.Adapter<PasswordHistoryAdapter.ViewHolder> {

  private Context context;
  private LayoutInflater inflater;
  private Password password;

  public PasswordHistoryAdapter(Context context, int passwordIndex) {
    super();
    this.context = context;
    this.inflater = LayoutInflater.from(context);
    this.password = PasswordProvider.getInstance(context).getById(passwordIndex);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View view = inflater.inflate(R.layout.password_history_item_layout, viewGroup, false);

    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    PasswordHistory history = password.getItemAt(position + 1);

    DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.getDefault());
    String date = dateFormat.format(history.getChangedDate());

    holder.password.setText(history.getValue());
    holder.date.setText(date);
  }

  @Override
  public int getItemCount() {
    return password.getHistoryCount()-1;
  }


  public class ViewHolder extends RecyclerView.ViewHolder {
    final TextView password;
    final TextView date;

    public ViewHolder(View itemView) {
      super(itemView);
      password = (TextView) itemView.findViewById(R.id.passwordhistoryitemlayout_textview_password);
      date = (TextView) itemView.findViewById(R.id.passwordhistoryitemlayout_textview_date);
    }
  }
}
