package com.typingsolutions.kore.login

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatEditText
import com.typingsolutions.kore.R
import com.typingsolutions.kore.common.KoreApplication

class LoginActivity : AppCompatActivity() {
    lateinit var mEditTextAsPassword: AppCompatEditText
    lateinit var mFabAsLogin: FloatingActionButton
    lateinit var mApplication: KoreApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        mApplication = applicationContext as KoreApplication

        mEditTextAsPassword = findViewById(R.id.loginlayout_edittext_password) as AppCompatEditText
        mFabAsLogin = findViewById(R.id.loginlayout_fab_login) as FloatingActionButton

        mFabAsLogin.setOnClickListener { login() }
    }

    fun login() {
        val password = mEditTextAsPassword.text.toString()

    }
}
