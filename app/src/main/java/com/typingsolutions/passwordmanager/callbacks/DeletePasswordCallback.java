package com.typingsolutions.passwordmanager.callbacks;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.typingsolutions.passwordmanager.BaseClickCallback;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import com.typingsolutions.passwordmanager.BaseCallback;
import com.typingsolutions.passwordmanager.activities.PasswordOverviewActivity;
import core.data.Password;
import core.data.PasswordProvider;

public class DeletePasswordCallback extends BaseClickCallback<PasswordDetailActivity> {
    private Password currentPassword;

    public DeletePasswordCallback(PasswordDetailActivity passwordDetailActivity, Password password) {
        super(passwordDetailActivity);
        this.currentPassword = password;
    }

    @Override
    public void setValues(Object... values) {
    }

    @Override
    public void onClick(View v) {
//        AlertDialog dialog = new AlertDialog.Builder(mActivity)
//                .setTitle("Delete mTextViewAsPassword")
//                .setMessage("Are you sure to delete this mTextViewAsPassword?")
//                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        PasswordProvider.getInstance(context).removePassword(currentPassword);
//                        passwordDetailActivity.onBackPressed();
//                    }
//                })
//                .setNegativeButton("DISCARD", null)
//                .create();
//        dialog.show();
    }
}
