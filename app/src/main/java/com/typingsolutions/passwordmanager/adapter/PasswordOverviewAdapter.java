package com.typingsolutions.passwordmanager.adapter;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.*;
import com.typingsolutions.passwordmanager.BaseAdapter;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;
import com.typingsolutions.passwordmanager.utils.ViewUtils;
import com.typingsolutions.passwordmanager.viewholder.PasswordOverviewViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PasswordOverviewAdapter extends BaseAdapter<PasswordOverviewViewHolder, PasswordOverviewActivity> {
  private List<Integer> removedItems = new ArrayList<>();

  private static final String PASSWORD_FILTER_PREFIX = "pw:";
  private static final String USERNAME_FILTER_PREFIX = "us:";
  private static final String PROGRAM_FILTER_PREFIX = "pr:";

  private static final int IS_NOT_FILTERED = 0;
  private static final int IS_PASSWORD_FILTERED = 1;
  private static final int IS_USERNAME_FILTERED = 2;
  private static final int IS_PROGRAM_FILTERED = 4;
  private boolean safe;

  public PasswordOverviewAdapter(PasswordOverviewActivity activity) {
    super(activity);
  }

  @Override
  public PasswordOverviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View view = inflater.inflate(R.layout.password_list_item_layout, viewGroup, false);

    safe = false; // TODO: read is Safe

    PasswordOverviewViewHolder passwordOverviewViewHolder
        = new PasswordOverviewViewHolder(mActivity, view);
    if (safe) {
      passwordOverviewViewHolder.makeSafe();
    }

    return passwordOverviewViewHolder;
  }

  @Override
  public void onBindViewHolder(PasswordOverviewViewHolder viewHolder, int position) {
    PasswordContainer password = (PasswordContainer) mActivity.getContainerAt(position);

    if (!safe) {
      viewHolder.mTextViewAsPassword.setText(password.getDefaultPassword());
      viewHolder.mTextViewAsUsername.setText(password.getUsername());
    }

    viewHolder.mTextViewAsProgram.setText(password.getProgram());
    viewHolder.mCurrentId = password.getId();
    String upperCase = password.getProgram().toUpperCase();
    viewHolder.mTextViewAsIcon.setText(upperCase.toCharArray(), 0, 1);

    ViewUtils.setColor(viewHolder.mTextViewAsIcon, password.getProgram(), password.getDefaultPassword());
  }

  @Override
  public int getItemCount() {
    return mActivity.containerCount();
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