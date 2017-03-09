package com.typingsolutions.kore.common

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import net.sqlcipher.database.SQLiteDatabase

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*


class KoreApplication : Application() {

    var databaseConnection: DatabaseConnection? = null
        private set

    private var mHandler: OpenDatabaseHandler? = null
    private var mOpenDatabaseTask: OpenDatabaseAsyncTask? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(javaClass.simpleName, "onCreate - Application")
        SQLiteDatabase.loadLibs(this)
    }

    override fun onTerminate() {
        super.onTerminate()

        if (mOpenDatabaseTask!!.status == AsyncTask.Status.RUNNING) {
            mOpenDatabaseTask!!.cancel(true)
        }

        if (databaseConnection != null) {
            databaseConnection!!.close()
        }
    }

    fun wasSetup(): Boolean {
        return getDatabasePath(DatabaseConnection.NAME).exists()
    }

    fun openDatabaseConnection(password: String, pim: Int) {
        if (databaseConnection != null)
            return

        databaseConnection = DatabaseConnection(this, password, pim)
        mOpenDatabaseTask = OpenDatabaseAsyncTask(databaseConnection!!, mHandler)

        mOpenDatabaseTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    fun setOnDatabaseOpened(onDatabaseOpened: IEvent<Int>) {
        mHandler = OpenDatabaseHandler(onDatabaseOpened)
    }

    fun calculatePIM(value: String): Int {
        val shaDigest = MessageDigest.getInstance("SHA-256")
        val bigInt: BigInteger
        var pim = -1
        val random = Random()

        shaDigest.update(value.toByteArray())
        bigInt = BigInteger(shaDigest.digest())
        shaDigest.reset()
        random.setSeed(bigInt.toLong())

        pim = random.nextInt(10000) + 20000

        return pim
    }
}



