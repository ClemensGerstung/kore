package com.typingsolutions.kore.setup

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.typingsolutions.kore.R


class ExtendSetupFragment : Fragment(), IPasswordProvider {

    private lateinit var mEditTextAsEnterPassword: TextInputEditText
    private lateinit var mEditTextAsRepeatPassword: TextInputEditText
    private lateinit var mEditTextAsEnterPIM: TextInputEditText
    private lateinit var mEditTextAsRepeatPIM: TextInputEditText
    private var mTextViewAsPIMHint: TextView? = null

    private var mBackupText: String? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater!!.inflate(R.layout.setup_fragment_3, container, false)
        mEditTextAsEnterPassword = root.findViewById(R.id.setuplayout_edittext_passwordenter) as TextInputEditText
        mEditTextAsRepeatPassword = root.findViewById(R.id.setuplayout_edittext_passwordrepeat) as TextInputEditText

        mEditTextAsEnterPIM = root.findViewById(R.id.setuplayout_edittext_pimenterextended) as TextInputEditText
        mEditTextAsRepeatPIM = root.findViewById(R.id.setuplayout_edittext_pimrepeatextended) as TextInputEditText

        mTextViewAsPIMHint = root.findViewById(R.id.setuplayout_textview_currentpim) as TextView

        mEditTextAsEnterPassword.addTextChangedListener(EnableSetupTextWatcher(activity as SetupActivity, this, mEditTextAsRepeatPassword, true))
        mEditTextAsRepeatPassword.addTextChangedListener(EnableSetupTextWatcher(activity as SetupActivity, this, mEditTextAsEnterPassword))

        mEditTextAsEnterPIM.addTextChangedListener(EnableSetupTextWatcher(activity as SetupActivity, mEditTextAsRepeatPIM))
        mEditTextAsRepeatPIM.addTextChangedListener(EnableSetupTextWatcher(activity as SetupActivity, mEditTextAsEnterPIM))

        return root
    }

    override fun setPasswords(pw1: CharSequence, pw2: CharSequence) {
        mEditTextAsEnterPassword.setText(pw1)
        mEditTextAsRepeatPassword.setText(pw2)
    }

    override fun cleanUp() {
        val activity = activity as SetupActivity

        activity.clearText(mEditTextAsEnterPassword)
        activity.clearText(mEditTextAsRepeatPassword)

        mEditTextAsEnterPassword.clearComposingText()
        mEditTextAsRepeatPassword.clearComposingText()

        activity.clearText(mEditTextAsEnterPIM)
        activity.clearText(mEditTextAsRepeatPIM)

        mEditTextAsEnterPIM.clearComposingText()
        mEditTextAsRepeatPIM.clearComposingText()
    }

    override fun getPassword1(): CharSequence? {
        return mEditTextAsEnterPassword.text
    }

    override fun getPassword2(): CharSequence? {
        return mEditTextAsRepeatPassword.text
    }

    internal val pim1: CharSequence
        get() = mEditTextAsEnterPIM.text

    internal val pim2: CharSequence
        get() = mEditTextAsRepeatPIM.text

    internal fun setCurrentPIM() {
        if (mBackupText == null) {
            mBackupText = mTextViewAsPIMHint?.text.toString()
        }

        val activity = activity as SetupActivity

        val calcPim = activity.mKoreApplication.calculatePIM(mEditTextAsEnterPassword.text.toString())
        mTextViewAsPIMHint?.text = mBackupText?.replace("\${_pim_}", Integer.toString(calcPim))
    }
}
