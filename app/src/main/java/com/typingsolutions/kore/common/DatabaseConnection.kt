package com.typingsolutions.kore.common

import android.content.Context
import android.util.Log
import com.typingsolutions.kore.common.data.History
import com.typingsolutions.kore.common.data.Password
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import net.sqlcipher.database.SQLiteOpenHelper

class DatabaseConnection internal constructor(context: Context, private var mPassword: String?, private var mPim: Int) : SQLiteOpenHelper(context, DatabaseConnection.NAME, null, DatabaseConnection.VERSION, DatabaseConnection.getDatabaseHook(mPim)) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(Password.CreateString)
        sqLiteDatabase.execSQL(History.CreateString)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }

    @Synchronized override fun close() {
        super.close()

        mPim = 0

        // TODO: clear string pool
        mPassword = null
    }

    val writableDatabase: SQLiteDatabase?
        @Synchronized get() {
            if (mPassword == null || mPassword?.length == 0) return null

            return super.getWritableDatabase(mPassword)
        }

    companion object {
        val NAME = "kore.db"
        private val VERSION = 1

        private fun getDatabaseHook(pim: Int): SQLiteDatabaseHook {
            val iterations = Integer.toString(pim)
            Log.d(DatabaseConnection::class.java.simpleName, iterations)

            return object : SQLiteDatabaseHook {
                override fun preKey(sqLiteDatabase: SQLiteDatabase) {}

                override fun postKey(sqLiteDatabase: SQLiteDatabase) {
                    sqLiteDatabase.rawExecSQL(String.format("PRAGMA kdf_iter = %s", iterations))
                }
            }
        }
    }
}
