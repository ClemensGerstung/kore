package com.typingsolutions.kore.login

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.typingsolutions.kore.common.IEvent


class LoginService : Service() {
    @Volatile private var mServiceLooper: Looper? = null
    @Volatile private var mLoginHandler: LoginHandler? = null
    private val mBinder: IBinder = LoginBinder(this)
    private var mTries: Int = 0
    internal var mTimeTotalBlocked: Long = 0

    var OnBlocked: IEvent<Any?>? = null
    var OnUnblocked: IEvent<Any?>? = null
    var OnTick: IEvent<Float>? = null

    val IsBlocked: Boolean
        get() = mLoginHandler?.mTimeLeftBlocked!! > 0

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }


    override fun onUnbind(intent: Intent?): Boolean {
        // TODO: might be improved by preventing others to change the XML file values, ideas are welcome!
        if(mTries > 0) {
            val editor = getSharedPreferences(javaClass.simpleName, Context.MODE_PRIVATE).edit()
            editor.putInt("tries", mTries)
            editor.putLong("locked", mLoginHandler?.mTimeLeftBlocked!!)
            editor.putLong("total", mTimeTotalBlocked)
            editor.apply()
        }

        mServiceLooper?.quit()
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()

        val thread = HandlerThread("IntentService[" + "LoginThread" + "]")
        thread.start()

        mServiceLooper = thread.looper
        mLoginHandler = LoginHandler(this, mServiceLooper!!)

        val prefs = getSharedPreferences(javaClass.simpleName, Context.MODE_PRIVATE)
        mTries = prefs.getInt("tries", 0)
        val leftLockTime = prefs.getLong("locked", 0)
        mTimeTotalBlocked = prefs.getLong("total", 0)
        Log.d(javaClass.simpleName, "$mTries $leftLockTime $mTimeTotalBlocked")

        prefs.edit().clear().apply()

        if(mTries > 0 && leftLockTime > 0 && mTimeTotalBlocked > 0) {
            val bundle = Bundle()
            bundle.putLong("time", leftLockTime)
            val msg = mLoginHandler?.obtainMessage()
            msg?.data = bundle
            mLoginHandler?.sendMessage(msg)
        }
    }

    fun increaseTries() {
        mTries++
        var start = true

        if (mTries > 12 && mTries % 6 == 0) {
            if(mTries > 2147483640 /* = Int.MAX_VALUE - (Int.MAX_VALUE % 12)*/) {   // who will ever reach this?
                mTries = 12
            }

            mTimeTotalBlocked = (5 * 60 * 1000)
        } else if (mTries <= 12 && mTries % 3 == 0) {
            mTimeTotalBlocked = ((mTries * 10 * 1000).toLong())
        } else {
            start = false
        }

        if(start) {
            val bundle = Bundle()
            bundle.putLong("time", mTimeTotalBlocked)
            val msg = mLoginHandler?.obtainMessage()
            msg?.data = bundle
            mLoginHandler?.sendMessage(msg)
        }
    }

    fun reset() {
        mTries = 0
    }
}