package com.typingsolutions.passwordmanager.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.view.*;
import com.typingsolutions.passwordmanager.BaseAdapter;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;
import com.typingsolutions.passwordmanager.utils.ViewUtils;
import com.typingsolutions.passwordmanager.viewholder.PasswordOverviewViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PasswordOverviewAdapter extends BaseAdapter<PasswordOverviewViewHolder, PasswordOverviewActivity> {
  private List<Integer> mRemovedItems = new ArrayList<>();

  public PasswordOverviewAdapter(PasswordOverviewActivity activity) {
    super(activity);
  }

  @Override
  public PasswordOverviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = inflater.inflate(R.layout.password_list_item_layout, viewGroup, false);

    PasswordOverviewViewHolder passwordOverviewViewHolder = new PasswordOverviewViewHolder(mActivity, view);
    if (mActivity.isSafe()) {
      passwordOverviewViewHolder.makeSafe();
    }

    return passwordOverviewViewHolder;
  }

  @Override
  public void onBindViewHolder(PasswordOverviewViewHolder viewHolder, int position) {
    PasswordContainer password = (PasswordContainer) mActivity.getContainerAt(position);

    viewHolder.mTextViewAsPassword.setText(password.getDefaultPassword());
    viewHolder.mTextViewAsUsername.setText(password.getUsername());

    viewHolder.mTextViewAsProgram.setText(password.getProgram());
    viewHolder.mCurrentId = password.getId();
    String upperCase = password.getProgram().toUpperCase();
    viewHolder.mTextViewAsIcon.setText(upperCase.toCharArray(), 0, 1);

    ViewUtils.setColor(viewHolder.mTextViewAsIcon, password.getProgram(), password.getDefaultPassword());
  }

  @Override
  public int getItemCount() {
    return mActivity.containerCount() - mRemovedItems.size();
  }

  @Override
  public void onItemMove(int fromPosition, int toPosition) {
//    PasswordProvider.getInstance(mActivity).swapPassword(fromPosition, toPosition);
    notifyItemMoved(fromPosition, toPosition);
  }

  @Override
  public void onItemDismiss(final int position) {
    AlertDialog dialog = new AlertDialog.Builder(mActivity)
        .setMessage("Delete this password?")
        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
//            PasswordProvider.getInstance(mActivity).removePassword(position);
            notifyItemRemoved(position);
          }
        })
        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            notifyItemChanged(position);
          }
        })
        .create();
    dialog.show();
  }

  public void search(String query) {
    Pattern p = Pattern.compile(query.isEmpty() ? ".*" : ".*(" + query + ").*", Pattern.CASE_INSENSITIVE);
    for (int i = mActivity.containerCount() - 1; i >= 0; i--) {
      PasswordContainer container = (PasswordContainer) mActivity.getContainerAt(i);
//      if (mRemovedItems.contains(container.getId())) continue;

      if (mActivity.isSafe()) {
        if (!p.matcher(container.getProgram()).matches()) {
          remove(container, i);
        } else {
          if (mRemovedItems.contains(container.getId())) {
            add(container, i);
          }
        }
      } else {
        if (!p.matcher(container.getDefaultPassword()).matches() ||
            !p.matcher(container.getProgram()).matches() ||
            !p.matcher(container.getUsername()).matches()) {
          remove(container, i);
        }
      }
    }
  }

  public void reset() {

  }

  private void remove(PasswordContainer container, int pos) {
    mRemovedItems.add(container.getId());
    notifyItemRemoved(pos);
  }

  private void add(PasswordContainer container, int pos) {
    mRemovedItems.remove((Integer) container.getId());
    notifyItemInserted(pos);
  }
}