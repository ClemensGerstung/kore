package com.typingsolutions.passwordmanager.callbacks;

import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseFragment;
import com.typingsolutions.passwordmanager.activities.SetupActivity;

public class SetupPasswordCallback extends SwitchFragmentCallback {

  public SetupPasswordCallback(SetupActivity activity, @IdRes int oldFragment, BaseFragment<SetupActivity> fragment, @AnimRes int inAnim, @AnimRes int outAnim) {
    super(activity, oldFragment, fragment, inAnim, outAnim);
  }

  @Override
  public void onClick(View v) {
//    if (!loginActivity.isPasswordSafe()) {
//      AlertDialog alertDialog = new AlertDialog.Builder(context)
//          .setTitle("Your password doesn't seem to be safe")
//          .setMessage("We recommend to use lower and upper letters, digits, some special characters and at least 8 characters. Do you want to keep it anyway?")
//          .setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//              dialog.dismiss();
//              setup();
//            }
//          })
//          .setNegativeButton("CHANGE", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//              dialog.dismiss();
//              loginActivity.retypePassword();
//            }
//          })
//          .create();
//
//      alertDialog.show();
//      return;
//    }
//
//    setup();

    super.onClick(v);
  }

  private void setup() {
//    if(loginActivity.setupDatabase()) {
//      Intent intent = new Intent(context, PasswordOverviewActivity.class);
//      context.startActivity(intent);
//      ActivityCompat.finishAfterTransition(loginActivity);
//    }
  }
}
