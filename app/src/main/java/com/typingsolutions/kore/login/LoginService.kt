package com.typingsolutions.kore.login

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class LoginService : Service() {
    private val mBinder: IBinder = LoginBinder(this)

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }


}

class LoginBinder(val mLoginService: LoginService): Binder()