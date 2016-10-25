package com.typingsolutions.passwordmanager.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.adapter.GDriveRestoreLoadBackupsAdapter;
import com.typingsolutions.passwordmanager.utils.LinearLayoutManager;

public class GDriveRestoreBottomSheetFragment extends BottomSheetDialogFragment {
  private OnDismissListener mOnDismissListener;

  private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
      if (newState == BottomSheetBehavior.STATE_HIDDEN) {
        dismiss();
        if(mOnDismissListener != null)
          mOnDismissListener.onDismiss(GDriveRestoreBottomSheetFragment.this);
      }

    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

    }
  };

  @Override
  public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);

    if(mOnDismissListener != null)
      mOnDismissListener.onDismiss(this);
  }

  @Override
  public void setupDialog(Dialog dialog, int style) {
    super.setupDialog(dialog, style);

    View root = View.inflate(getContext(), R.layout.gdrive_restore_layout, null);

    RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.gdriverestorelayout_recyclerview_items);
    recyclerView.setAdapter(new GDriveRestoreLoadBackupsAdapter((BaseActivity) getActivity()));
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    dialog.setContentView(root);
    View parent = (View) root.getParent();

    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
    CoordinatorLayout.Behavior behavior = params.getBehavior();

    if (behavior != null && behavior instanceof BottomSheetBehavior) {
      ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
    }
  }

  public void setOnDismissListener(OnDismissListener onDismissListener) {
    mOnDismissListener = onDismissListener;
  }

  public static interface OnDismissListener {
    void onDismiss(GDriveRestoreBottomSheetFragment fragment);
  }
}
