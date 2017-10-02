package com.typingsolutions.kore.overview

import android.os.AsyncTask
import com.typingsolutions.kore.common.DatabaseConnection
import com.typingsolutions.kore.common.data.Password

class LoadPasswordAsyncTask : AsyncTask<DatabaseConnection, Void,  Array<Password>>()
{
    override fun doInBackground(vararg params: DatabaseConnection?): Array<Password> {
        var passwords = Password.loadPasswords(params[0]!!.database)

        return passwords
    }


}