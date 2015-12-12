package com.typingsolutions.passwordmanager.callbacks.click;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.typingsolutions.passwordmanager.activities.PasswordDetailActivity;
import com.typingsolutions.passwordmanager.callbacks.BaseCallback;
import core.data.Password;
import core.data.PasswordProvider;

public class DeletePasswordCallback extends BaseCallback {
    private Password currentPassword;
    private PasswordDetailActivity passwordDetailActivity;

    public DeletePasswordCallback(Context context, Password currentPassword, PasswordDetailActivity passwordDetailActivity) {
        super(context);
        this.currentPassword = currentPassword;
        this.passwordDetailActivity = passwordDetailActivity;
    }

    @Override
    public void setValues(Object... values) {
    }

    @Override
    public void onClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Delete password")
                .setMessage("Are you sure to delete this password?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PasswordProvider.getInstance(context).removePassword(currentPassword);
                        passwordDetailActivity.onBackPressed();
                    }
                })
                .setNegativeButton("NOPE", null)
                .create();
        dialog.show();
    }
}
