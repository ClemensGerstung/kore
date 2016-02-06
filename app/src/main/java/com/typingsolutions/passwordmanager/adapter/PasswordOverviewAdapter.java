package com.typingsolutions.passwordmanager.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.utils.ViewUtils;
import core.DatabaseProvider;
import com.typingsolutions.passwordmanager.adapter.viewholder.PasswordOverviewViewHolder;
import core.data.Password;
import core.data.PasswordHistory;
import core.data.PasswordProvider;

import java.util.ArrayList;
import java.util.List;

public class PasswordOverviewAdapter extends RecyclerView.Adapter<PasswordOverviewViewHolder>
    implements IItemTouchHelperAdapter {
  private List<Integer> removedItems = new ArrayList<>();

  private PasswordOverviewActivity activity;
  private LayoutInflater inflater;


  private static final String PASSWORD_FILTER_PREFIX = "pw:";
  private static final String USERNAME_FILTER_PREFIX = "us:";
  private static final String PROGRAM_FILTER_PREFIX = "pr:";

  private static final int IS_NOT_FILTERED = 0;
  private static final int IS_PASSWORD_FILTERED = 1;
  private static final int IS_USERNAME_FILTERED = 2;
  private static final int IS_PROGRAM_FILTERED = 4;
  private boolean safe;
  private int removedFilteredItems;

  public PasswordOverviewAdapter(PasswordOverviewActivity passwordOverviewActivity) {
    super();
    this.activity = passwordOverviewActivity;
    inflater = LayoutInflater.from(passwordOverviewActivity);
    removedFilteredItems = 0;
  }

  @Override
  public PasswordOverviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = inflater.inflate(R.layout.password_list_item_layout, viewGroup, false);

    safe = PasswordProvider.getInstance(activity).isSafe();

    PasswordOverviewViewHolder passwordOverviewViewHolder
        = new PasswordOverviewViewHolder(activity, view);
    if (safe) {
      passwordOverviewViewHolder.makeSafe();
    }

    return passwordOverviewViewHolder;
  }

  @Override
  public void onBindViewHolder(PasswordOverviewViewHolder viewHolder, int position) {
    Password password = PasswordProvider.getInstance(activity).get(position);
    PasswordHistory history = password.getFirstHistoryItem();

    if (!safe) {
      viewHolder.password.setText(history.getValue());
      viewHolder.username.setText(password.getUsername());
    }

    viewHolder.program.setText(password.getProgram());
    viewHolder.id = password.getId();
    String upperCase = password.getProgram().toUpperCase();
    viewHolder.icon.setText(upperCase.toCharArray(), 0, 1);

    ViewUtils.setColor(viewHolder.icon, password.getProgram(), password.getFirstItem());

  }

  @Override
  public int getItemCount() {
    return PasswordProvider.getInstance(activity).size() - removedFilteredItems;
  }

  public synchronized void filter(String query) {

    if (query.isEmpty()) {
      resetFilter();
      return;
    }

    if (query.startsWith(PASSWORD_FILTER_PREFIX)) {
      String filter = query.replace(PASSWORD_FILTER_PREFIX, "");
      resetFilter();
      filter(filter, IS_PASSWORD_FILTERED);
    } else if (query.startsWith(USERNAME_FILTER_PREFIX)) {
      String filter = query.replace(USERNAME_FILTER_PREFIX, "");
      resetFilter();
      filter(filter, IS_USERNAME_FILTERED);
    } else if (query.startsWith(PROGRAM_FILTER_PREFIX)) {
      String filter = query.replace(PROGRAM_FILTER_PREFIX, "");
      resetFilter();
      filter(filter, IS_PROGRAM_FILTERED);
    } else {
      filter(query, IS_NOT_FILTERED);
    }

  }

  private void filter(String query, int flag) {
    PasswordProvider provider = PasswordProvider.getInstance(activity);
    for (int i = provider.size() - 1; i >= 0; i--) {
      Password password = provider.get(i);
      if (!matches(password, query, flag)) {
        removedFilteredItems++;
        notifyItemRemoved(i);
        removedItems.add(i);
      } else {
        if (!removedItems.contains(i)) return;
        removedFilteredItems--;
        notifyItemInserted(i);
        removedItems.remove((Object) i);
      }
    }
  }

  private boolean matches(Password password, String simpleQuery, int filterFlags) {
    boolean returnValue = false;

    String program = password.getProgram();
    String passwordValue = password.getFirstItem();
    String username = password.getUsername();

    switch (filterFlags) {
      case 0: // IS_NOT_FILTERED
        returnValue = PasswordProvider.getInstance(activity).isSafe() ? program.contains(simpleQuery)
            : program.contains(simpleQuery) || passwordValue.contains(simpleQuery) || username.contains(simpleQuery);
        break;
      case 1: // IS_PASSWORD_FILTERED
        returnValue = passwordValue.contains(simpleQuery);
        break;
      case 2: // IS_USERNAME_FILTERED
        returnValue = username.contains(simpleQuery);
        break;
      case 4: // IS_PROGRAM_FILTERED
        returnValue = program.contains(simpleQuery);
        break;
    }

    Log.d("Query", String.format("%s --> %s (%s) => %s", password, simpleQuery, filterFlags, returnValue));

    return returnValue;
  }

  public void resetFilter() {
    for (Integer i : removedItems) {
      notifyItemInserted(i);
    }
    removedFilteredItems = 0;
    removedItems.clear();
  }

  @Override
  public void onItemMove(int fromPosition, int toPosition) {
    PasswordProvider.getInstance(activity).swapPassword(fromPosition, toPosition);
    notifyItemMoved(fromPosition, toPosition);
  }

  @Override
  public void onItemDismiss(final int position) {
    AlertDialog dialog = new AlertDialog.Builder(activity)
        .setMessage("Delete this password?")
        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            PasswordProvider.getInstance(activity).removePassword(position);
            notifyItemRemoved(position);
          }
        })
        .setNegativeButton("DISCARD", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            notifyItemChanged(position);
          }
        })
        .create();
    dialog.show();

  }
}