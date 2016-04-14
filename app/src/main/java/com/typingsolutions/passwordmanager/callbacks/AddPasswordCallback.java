package com.typingsolutions.passwordmanager.callbacks;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.BaseDatabaseActivity;
import com.typingsolutions.passwordmanager.activities.CreatePasswordActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.BaseCallback;


public class AddPasswordCallback extends BaseClickCallback<PasswordOverviewActivity> {

  public AddPasswordCallback(PasswordOverviewActivity passwordOverviewActivity) {
    super(passwordOverviewActivity);
  }

  @Override
  public void onClick(View view) {
    BaseDatabaseActivity.logout = false;


    Intent intent = new Intent(mActivity, CreatePasswordActivity.class);
    Bundle bundle;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Log.d(getClass().getSimpleName(), "override");
      //bundle = ActivityOptionsCompat
      //    .makeCustomAnimation(context, R.anim.activity_open_animation, R.anim.no_animation)
      //    .toBundle();

      ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, view, "asdf");

      mActivity.startActivity(intent);
      //passwordOverviewActivity.overridePendingTransition(R.anim.activity_open_animation, R.anim.no_animation);

    } else {
      mActivity.startActivity(intent);
    }

  }
}
