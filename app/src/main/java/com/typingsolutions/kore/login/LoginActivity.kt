package com.typingsolutions.kore.login

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import com.typingsolutions.kore.R
import com.typingsolutions.kore.common.AlertBuilder
import com.typingsolutions.kore.common.EventArgs
import com.typingsolutions.kore.common.IEvent
import com.typingsolutions.kore.common.KoreApplication
import com.typingsolutions.kore.setup.SetupActivity

class LoginActivity : AppCompatActivity() {
    lateinit var mEditTextAsPassword: AppCompatEditText
    lateinit var mFabAsLogin: FloatingActionButton
    lateinit var mApplication: KoreApplication

    val databaseCallback = IEvent<Int> { sender: Any, e: EventArgs<Int> ->
        val exitCode = e.data

        if(exitCode == 0) {
            // TODO: login
        } else {
            Snackbar.make(mFabAsLogin, "WRONG", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        mApplication = applicationContext as KoreApplication
        mApplication.setOnDatabaseOpened(databaseCallback)

        mEditTextAsPassword = findViewById(R.id.loginlayout_edittext_password) as AppCompatEditText
        mFabAsLogin = findViewById(R.id.loginlayout_fab_login) as FloatingActionButton

        mFabAsLogin.setOnClickListener { login() }
    }

    fun login() {
        val password = mEditTextAsPassword.text.toString()
        val ownPim = getSharedPreferences(SetupActivity.NAME, Context.MODE_PRIVATE).getBoolean("pim", false)
        var pim: Int = 0

        if(ownPim) {
            AlertBuilder.create(this)
                    .setMessage("Enter your pim...")
                    .setPositiveButton("OK", { dialog, which ->  /*TODO*/})
                    .show()

            return
        } else {
            pim = mApplication.calculatePIM(password)
        }

        mApplication.openDatabaseConnection(password, pim)
    }
}
