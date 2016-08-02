package com.typingsolutions.passwordmanager.adapter;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.*;
import com.typingsolutions.passwordmanager.BaseAdapter;
import com.typingsolutions.passwordmanager.IContainer;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.dao.PasswordContainer;
import com.typingsolutions.passwordmanager.utils.ViewUtils;
import com.typingsolutions.passwordmanager.viewholder.PasswordOverviewViewHolder;
import core.data.Password;

import java.util.*;
import java.util.regex.Pattern;

public class PasswordOverviewAdapter extends BaseAdapter<PasswordOverviewViewHolder, PasswordOverviewActivity> {
  private List<Integer> mRemovedItems = new ArrayList<>();
  private PasswordContainerComparator mComperator = new PasswordContainerComparator();

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

      boolean match = mActivity.isSafe() ?
          p.matcher(container.getProgram()).matches() :
          p.matcher(container.getDefaultPassword()).matches() ||
              p.matcher(container.getProgram()).matches() ||
              p.matcher(container.getUsername()).matches();
      boolean removed = mRemovedItems.contains(container.getId());

      if (!match) {
        if (!removed) {
          remove(container, i);
        }
      } else {
        if (removed) {
          add(container, i);
        }
      }
    }
  }

  private void remove(PasswordContainer container, int pos) {
    mRemovedItems.add(container.getId());
    notifyItemRemoved(pos);
  }

  private void add(PasswordContainer container, int pos) {
    mRemovedItems.remove((Integer) container.getId());
    notifyItemInserted(pos);
  }

  public void order(OrderOptions option) {
    Collections.sort(mActivity.getItems(), new PasswordContainerComparator(option));
    notifyDataSetChanged();

    //sort(0, mActivity.containerCount() - 1, option);
  }

  private void sort(int left, int right, OrderOptions option) {
    if (left >= right) {
      return;
    }

    IContainer pivot = mActivity.getContainerAt(left + (right - left) / 2);
    int i = left;
    int j = right;

    while (i <= j) {
      while (compare(i, pivot, option) < 0 && i < right) i++;
      while (compare(j, pivot, option) > 0 && j > left) j--;

      if (i <= j) {
        notifyItemMoved(i, j);
        Log.d(getClass().getSimpleName(), "From " + i + " to " + j);

        i++;
        j--;
      }
    }

    if (left < j)
      sort(left, j, option);

    if (right > i)
      sort(i, right, option);
  }

  private int compare(int index, IContainer pivot, OrderOptions option) {
    return mComperator.compare((PasswordContainer) mActivity.getContainerAt(index), (PasswordContainer) pivot, option);
  }

  public static enum OrderOptions {
    PasswordAscending,
    PasswordDescending,
    UsernameAscending,
    UsernameDescending,
    ProgramAscending,
    ProgramDescending,
    Custom
  }

  private class PasswordContainerComparator implements Comparator<IContainer> {
    OrderOptions mOrderOption;

    public PasswordContainerComparator() {
      this(null);
    }

    public PasswordContainerComparator(OrderOptions mOrderOption) {
      this.mOrderOption = mOrderOption;
    }

    public int compare(PasswordContainer left, PasswordContainer right, OrderOptions orderOptions) {
      mOrderOption = orderOptions;
      return compare(left, right);
    }

    @Override
    public int compare(IContainer l, IContainer r) {
      PasswordContainer left = (PasswordContainer) l;
      PasswordContainer right = (PasswordContainer) r;

      switch (mOrderOption) {
        case PasswordAscending:
          return left.getDefaultPassword().compareToIgnoreCase(right.getDefaultPassword());
        case PasswordDescending:
          return right.getDefaultPassword().compareToIgnoreCase(left.getDefaultPassword());
        case ProgramAscending:
          return left.getProgram().compareToIgnoreCase(right.getProgram());
        case ProgramDescending:
          return right.getProgram().compareToIgnoreCase(left.getProgram());
        case UsernameAscending:
          return left.getUsername().compareToIgnoreCase(right.getUsername());
        case UsernameDescending:
          return right.getUsername().compareToIgnoreCase(left.getUsername());
        case Custom:
          return Integer.compare(left.getPosition(), right.getPosition());
      }

      return 0;
    }
  }
}