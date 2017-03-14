package com.typingsolutions.kore.setup

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.typingsolutions.kore.R
import com.typingsolutions.kore.common.ViewUtil

class SimpleSetupFragment : Fragment(), IPasswordProvider {
    private lateinit var mEditTextAsEnterPassword: TextInputEditText
    private lateinit var mEditTextAsRepeatPassword: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater!!.inflate(R.layout.setup_fragment_2, container, false)
        mEditTextAsEnterPassword = root.findViewById(R.id.setuplayout_edittext_passwordenter) as TextInputEditText
        mEditTextAsRepeatPassword = root.findViewById(R.id.setuplayout_edittext_passwordrepeat) as TextInputEditText

        mEditTextAsEnterPassword.addTextChangedListener(EnableSetupTextWatcher(activity as SetupActivity, mEditTextAsRepeatPassword))
        mEditTextAsRepeatPassword.addTextChangedListener(EnableSetupTextWatcher(activity as SetupActivity, mEditTextAsEnterPassword))

        return root
    }

    override fun getPassword1(): CharSequence? {
        return mEditTextAsEnterPassword.text
    }

    override fun getPassword2(): CharSequence? {
        return mEditTextAsRepeatPassword.text
    }

    override fun setPasswords(pw1: CharSequence, pw2: CharSequence) {

    }

    override fun cleanUp() {
        ViewUtil.clearText(mEditTextAsEnterPassword)
        ViewUtil.clearText(mEditTextAsRepeatPassword)

        mEditTextAsEnterPassword.clearComposingText()
        mEditTextAsRepeatPassword.clearComposingText()
    }
}
