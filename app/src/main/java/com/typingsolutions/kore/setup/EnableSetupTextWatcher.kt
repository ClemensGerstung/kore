package com.typingsolutions.kore.setup

import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher


internal class EnableSetupTextWatcher
    @JvmOverloads
    constructor(private val mActivity: SetupActivity,
                private val mFragment: Fragment?,
                private val mCounterpart: TextInputEditText,
                private val mRefreshPim: Boolean = false)
    : TextWatcher {

    constructor(activity: SetupActivity, counterpart: TextInputEditText) : this(activity, null, counterpart)

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {
        if (mFragment is ExtendSetupFragment && mRefreshPim) {
            mFragment.setCurrentPIM()
        }

        mActivity.enableSetupButton(s.isNotEmpty() && mCounterpart.length() > 0)
    }
}
