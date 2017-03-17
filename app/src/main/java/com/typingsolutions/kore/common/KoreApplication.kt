package com.typingsolutions.kore.common

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.typingsolutions.kore.setup.SetupActivity
import net.sqlcipher.database.SQLiteDatabase
import java.lang.Error
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*


class KoreApplication : Application() {

    private var mHandler: OpenDatabaseHandler? = null
    private var mOpenDatabaseTask: OpenDatabaseAsyncTask? = null
    var databaseConnection: DatabaseConnection? = null
        private set

    override fun onCreate() {
        super.onCreate()
        Log.d(javaClass.simpleName, "onCreate - Application")
        SQLiteDatabase.loadLibs(applicationContext)
    }

    override fun onTerminate() {
        super.onTerminate()

        if (mOpenDatabaseTask?.status == AsyncTask.Status.RUNNING) {
            mOpenDatabaseTask?.cancel(true)
        }

        if (databaseConnection != null) {
            databaseConnection?.close()
        }
    }

    fun wasSetup(): Boolean {
        return getDatabasePath(DatabaseConnection.NAME).exists()
    }

    fun hasCustomPIM(value: Boolean? = null): Boolean {
        val pref = getSharedPreferences(SetupActivity.NAME, Context.MODE_PRIVATE)
        val result: Boolean

        if (value != null) {
            val edit = pref.edit()
            edit.putBoolean("pim", true)
            edit.apply()
            result = edit.commit()
        } else {
            result = pref.getBoolean("pim", false)
        }

        return result
    }

    fun openDatabaseConnection(password: String, pim: Int) {
        if (databaseConnection != null)
            return

        databaseConnection = DatabaseConnection(this, password, pim)
        mOpenDatabaseTask = OpenDatabaseAsyncTask(databaseConnection!!, mHandler)

        mOpenDatabaseTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    fun setOnDatabaseOpened(onDatabaseOpened: IEvent<Int>) {
        mHandler = OpenDatabaseHandler(onDatabaseOpened)
    }

    fun closeDatabaseConnection() {
        if(databaseConnection == null)
            return

        databaseConnection?.close()
        databaseConnection = null
    }

    fun calculatePIM(value: String): Int {
        val shaDigest = MessageDigest.getInstance("SHA-256")
        val bigInt: BigInteger
        val pim: Int
        val random = Random()

        shaDigest.update(value.toByteArray())
        bigInt = BigInteger(shaDigest.digest())
        shaDigest.reset()
        random.setSeed(bigInt.toLong())

        pim = random.nextInt(10000) + 20000

        return pim
    }
}



