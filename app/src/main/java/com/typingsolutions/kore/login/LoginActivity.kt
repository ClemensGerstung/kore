package com.typingsolutions.kore.login

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import com.typingsolutions.kore.R
import com.typingsolutions.kore.common.AlertBuilder
import com.typingsolutions.kore.common.EventArgs
import com.typingsolutions.kore.common.IEvent
import com.typingsolutions.kore.common.KoreApplication

class LoginActivity : AppCompatActivity() {
    lateinit var mEditTextAsPassword: AppCompatEditText
    lateinit var mFabAsLogin: FloatingActionButton
    lateinit var mApplication: KoreApplication

    val databaseCallback = IEvent<Int> { _: Any, e: EventArgs<Int> ->
        val exitCode = e.data

        if(exitCode == 0) {
            // TODO: start overview activity
        } else {
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

        mFabAsLogin.setOnClickListener { login() }
    }

    fun login() {
        val password = mEditTextAsPassword.text.toString()
        var pim: Int

        if(mApplication.hasCustomPIM()) {
            AlertBuilder.create(this)
                    .setView(R.layout.loginlayout_dialog_input)
                    .setPositiveButton(getString(R.string.common_string_ok), { dialog, _ ->
                        val editText = (dialog as AlertDialog).findViewById(R.id.loginlayout_edittext_pim) as TextInputEditText
                        pim = (editText.text.toString()).toInt()
                        mApplication.openDatabaseConnection(password, pim)
                    })
                    .setNegativeButton(getString(R.string.common_string_close))
                    .show()

            return
        } else {
            pim = mApplication.calculatePIM(password)
        }

        mApplication.openDatabaseConnection(password, pim)
    }
}
