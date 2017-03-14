package com.typingsolutions.kore.login

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import com.typingsolutions.kore.common.EventArgs

internal class LoginHandler(val mService: LoginService, looper: Looper) : Handler(looper) {
    var mTimeLeftBlocked: Long = 0

    override fun handleMessage(msg: Message?) {
        mService.OnBlocked?.callback(mService, null)
        mTimeLeftBlocked = msg?.data?.getLong("time")!!
        var lastTime = SystemClock.elapsedRealtime()

        do {
            val time = SystemClock.elapsedRealtime()
            val diff = time - lastTime
            lastTime = time

            mTimeLeftBlocked -= diff

            val percentage = 100 - mTimeLeftBlocked.toFloat() / mService.mTimeTotalBlocked
            mService.OnTick?.callback(mService, EventArgs(percentage))

            SystemClock.sleep(200)
        } while (mService.IsBlocked)

        mService.OnUnblocked?.callback(mService, null)
    }
}