package com.typingsolutions.kore.login

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import com.typingsolutions.kore.common.EventArgs

internal class LoginHandler(val mService: LoginService, looper: Looper) : Handler(looper) {
    override fun handleMessage(msg: Message?) {
        mService.OnBlocked?.callback(mService, null)
        var lastTime = SystemClock.elapsedRealtime()

        do {
            val time = SystemClock.elapsedRealtime()
            val diff = lastTime - time
            lastTime = time

            mService.mTimeLeftBlocked = mService.mTimeLeftBlocked - diff

            val percentage = mService.mTimeLeftBlocked.toFloat() / mService.mTimeTotalBlocked
            mService.OnTick?.callback(mService, EventArgs(percentage))

            SystemClock.sleep(32)
        } while (mService.IsBlocked)

        mService.OnUnblocked?.callback(mService, null)
    }
}