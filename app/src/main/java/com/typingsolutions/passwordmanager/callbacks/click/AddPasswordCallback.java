package com.typingsolutions.passwordmanager.callbacks.click;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;
import com.typingsolutions.passwordmanager.activities.CreatePasswordActivity;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import com.typingsolutions.passwordmanager.BaseCallback;


public class AddPasswordCallback extends BaseCallback {
    private PasswordOverviewActivity passwordOverviewActivity;

    public AddPasswordCallback(PasswordOverviewActivity passwordOverviewActivity) {
        super(passwordOverviewActivity);
        this.passwordOverviewActivity = passwordOverviewActivity;
    }

    @Override
    public void setValues(Object... values) {
    }

    @Override
    public void onClick(View view) {
        passwordOverviewActivity.doNotLogout();
        Intent intent = new Intent(context, CreatePasswordActivity.class);
        Bundle bundle;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(getClass().getSimpleName(), "override");
            //bundle = ActivityOptionsCompat
            //    .makeCustomAnimation(context, R.anim.activity_open_animation, R.anim.no_animation)
            //    .toBundle();

            ActivityOptionsCompat.makeSceneTransitionAnimation(passwordOverviewActivity, view, "asdf");

            context.startActivity(intent);
            //passwordOverviewActivity.overridePendingTransition(R.anim.activity_open_animation, R.anim.no_animation);

        } else {
            context.startActivity(intent);
        }

    }
}
