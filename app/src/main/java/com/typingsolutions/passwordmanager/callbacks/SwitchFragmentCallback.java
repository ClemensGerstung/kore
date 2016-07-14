package com.typingsolutions.passwordmanager.callbacks;

import android.view.View;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.activities.SetupActivity;

public class SwitchFragmentCallback extends BaseClickCallback<SetupActivity> {

  public enum Direction {
    Next,
    Previous
  }

  private Direction mDirection;

  public SwitchFragmentCallback(SetupActivity activity, Direction direction) {
    super(activity);
    this.mDirection = direction;
  }

  @Override
  public void onClick(View v) {
    if(mDirection == Direction.Next) {
      mActivity.moveToNextPage();
    } else {
      mActivity.moveToPreviousPage();
    }
  }
}
