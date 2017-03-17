package com.typingsolutions.kore.common

import android.os.AsyncTask
import android.os.Handler
import net.sqlcipher.database.SQLiteDatabase

internal class OpenDatabaseAsyncTask(private val mDatabaseConnection: DatabaseConnection, private val mHandler: Handler?) : AsyncTask<Void, Void, Void>() {
    private var mExitCode: Int = 0

    override fun doInBackground(vararg params: Void): Void? {
        try {
            val database = mDatabaseConnection.database
            if (database != null && database.isOpen) {
                mExitCode = 1
            } else {
                mExitCode = 3
            }
        } catch (e: Exception) {
            // TODO: send extra code for wrong password?
            mExitCode = 2
        }

        return null
    }

    override fun onPostExecute(result: Void?) {
        mHandler?.sendEmptyMessage(mExitCode)
    }
}
