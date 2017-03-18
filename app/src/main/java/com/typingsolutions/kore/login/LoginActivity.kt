package com.typingsolutions.kore.login

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.typingsolutions.kore.R
import com.typingsolutions.kore.common.AlertBuilder
import com.typingsolutions.kore.common.IEvent
import com.typingsolutions.kore.common.KoreApplication
import com.typingsolutions.kore.common.ViewUtil
import com.typingsolutions.kore.overview.OverviewActivity

class LoginActivity : AppCompatActivity() {
    lateinit var mEditTextAsPassword: AppCompatEditText
    lateinit var mFabAsLogin: FloatingActionButton
    lateinit var mApplication: KoreApplication
    lateinit var mService: LoginService
    private var mBound: Boolean = false
    private val mServiceConnection = LoginServiceConnection()

    val databaseCallback = IEvent<Int> { _, e ->
        val exitCode = e.data
        Log.d(javaClass.simpleName, "ExitCode: " + exitCode)

        if (exitCode == 1) {
            mService.reset()
            val intent = Intent(this,  OverviewActivity::class.java)
            mApplication.startActivity(intent)
        } else {
            mApplication.closeDatabaseConnection()
            mService.increaseTries()
            Snackbar.make(mFabAsLogin, "You have entered a wrong password", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        mApplication = applicationContext as KoreApplication
        mApplication.setOnDatabaseOpened(databaseCallback)

        mEditTextAsPassword = findViewById(R.id.loginlayout_edittext_password) as AppCompatEditText
        mFabAsLogin = findViewById(R.id.loginlayout_fab_login) as FloatingActionButton

        mFabAsLogin.setOnClickListener {
            if (!mService.IsBlocked) {
                login()
            } else {
                Snackbar.make(mFabAsLogin, getString(R.string.loginlayout_string_wrongpassword), Snackbar.LENGTH_LONG).show()
            }
        }
        mFabAsLogin.hide()
        mEditTextAsPassword.addTextChangedListener(LoginTextWatcher())
    }

    override fun onStart() {
        super.onStart()

        if (!mBound) {
            val intent = Intent(this, LoginService::class.java)
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        if (mBound) {
            unbindService(mServiceConnection)
            mBound = false
        }

        ViewUtil.clearText(mEditTextAsPassword)
        mEditTextAsPassword.clearComposingText()

        super.onStop()
    }

    fun login() {
        val password = mEditTextAsPassword.text.toString()
        var pim: Int

        if (mApplication.hasCustomPIM()) {
            AlertBuilder.create(this)
                    .setView(R.layout.loginlayout_dialog_input)
                    .setPositiveButton(getString(R.string.common_string_ok), { dialog, _ ->
                        // get
                        val editText = (dialog as AlertDialog).findViewById(R.id.loginlayout_edittext_pim) as TextInputEditText
                        pim = (editText.text.toString()).toInt()

                        // login
                        mApplication.openDatabaseConnection(password, pim)

                        // cleanup
                        ViewUtil.clearText(editText)
                        editText.clearComposingText()
                    })
                    .setNegativeButton(getString(R.string.common_string_close))
                    .show()

            return
        } else {
            pim = mApplication.calculatePIM(password)
        }

        mApplication.openDatabaseConnection(password, pim)
    }

    inner class LoginServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LoginBinder
            mService = binder.mLoginService
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }
    }

    inner class LoginTextWatcher : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if(s.isNullOrEmpty()) {
                mFabAsLogin.hide()
            } else {
                mFabAsLogin.show()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun afterTextChanged(s: Editable?) {

        }
    }
}
