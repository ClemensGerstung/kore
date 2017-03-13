package com.typingsolutions.kore.login

import android.app.Service
import android.content.Intent
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import com.typingsolutions.kore.common.IEvent

class LoginService : Service() {
    @Volatile private var mServiceLooper: Looper? = null
    @Volatile private var mLoginHandler: LoginHandler? = null
    private val mBinder: IBinder = LoginBinder(this)
    private var mTries: Int = 0
    internal var mTimeTotalBlocked: Long = 0
    internal var mTimeLeftBlocked: Long = 0

    var OnBlocked: IEvent<Any?>? = null
    var OnUnblocked: IEvent<Any?>? = null
    var OnTick: IEvent<Float>? = null

    val IsBlocked: Boolean
        get() = mTimeLeftBlocked > 0

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    private fun setTotalTimeBlocked(value: Long) {
        mTimeTotalBlocked = value
        mTimeLeftBlocked = value
    }

    override fun onCreate() {
        super.onCreate()

        val thread = HandlerThread("IntentService[" + "LoginThread" + "]")
        thread.start()

        mServiceLooper = thread.looper
        mLoginHandler = LoginHandler(this, mServiceLooper!!)
    }

    fun increaseTries() {
        mTries++
        var start = true

        if(mTries > 12 && mTries % 6 == 0) {
            setTotalTimeBlocked(5 * 60 * 1000)
        } else if(mTries <= 12 && mTries % 3 == 0) {
            setTotalTimeBlocked((mTries * 10 * 1000).toLong())
        } else {
            start = false
        }

        if(start) {
            val msg = mLoginHandler?.obtainMessage()
            mLoginHandler?.sendMessage(msg)
        }
    }
}