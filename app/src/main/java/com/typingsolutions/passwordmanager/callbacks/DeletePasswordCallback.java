package com.typingsolutions.passwordmanager.callbacks;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import core.data.Password;
import core.data.UserProvider;

public class DeletePasswordCallback extends BaseCallback {
    private Password currentPassword;

    public DeletePasswordCallback(Context context) {
        super(context);
    }

    @Override
    public void setValues(Object... values) {
        if (values.length == 0) return;

        if (values[0] instanceof Password) {
            currentPassword = (Password) values[0];
        }
    }

    @Override
    public void onClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Delete password")
                .setMessage("Are you sure to delete this password?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserProvider.getInstance(context).removePassword(currentPassword);
                    }
                })
                .setNegativeButton("NOPE", null)
                .create();
        dialog.show();
    }
}
