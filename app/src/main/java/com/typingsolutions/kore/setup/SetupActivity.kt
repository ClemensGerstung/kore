package com.typingsolutions.kore.setup

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.typingsolutions.kore.R
import com.typingsolutions.kore.common.*
import com.typingsolutions.kore.login.LoginActivity
import ui.NotSwipeableViewPager
import java.util.*

// entropy = length * log2(numberofavailablechars)
// avg is 40.54 bit
// lower mid (yellow) -> everything below is bad, and +20 is good?

class SetupActivity : AppCompatActivity() {

    private var mViewPagerAsContentWrapper: NotSwipeableViewPager? = null
    private var mTextViewAsHint: TextView? = null
    private var mSetupPageAdapter: SimplePagerAdapter? = null
    private var mButtonAsExtended: AppCompatButton? = null
    private var mButtonAsNextOrSetup: AppCompatButton? = null
    internal lateinit var mKoreApplication: KoreApplication

    private val mHelpViews = intArrayOf(R.layout.setup_help_1, R.layout.setup_help_2, R.layout.setup_help_3)

    override fun onCreate(savedInstanceState: Bundle?) {
        mKoreApplication = applicationContext as KoreApplication

        if (mKoreApplication.wasSetup()) {
            val intent = Intent(mKoreApplication, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup_layout)

        if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = 0x44000000
        }


        mViewPagerAsContentWrapper = findViewById(R.id.setuplayout_viewpager_contenthost) as NotSwipeableViewPager
        mTextViewAsHint = findViewById(R.id.setuplayout_textview_hint) as TextView
        mButtonAsExtended = findViewById(R.id.setuplayout_button_extended) as AppCompatButton
        mButtonAsNextOrSetup = findViewById(R.id.setuplayout_button_next) as AppCompatButton
        val fab = findViewById(R.id.setuplayout_fab_expandBottom) as FloatingActionButton
        val header = findViewById(R.id.setuplayout_appbarlayout_header) as AppBarLayout

        mSetupPageAdapter = SimplePagerAdapter(supportFragmentManager, arrayOf(SimpleViewFragment.create(R.layout.setup_fragment_1), SimpleSetupFragment(), ExtendSetupFragment()))

        mViewPagerAsContentWrapper!!.canSwipe(false)
        mViewPagerAsContentWrapper!!.adapter = mSetupPageAdapter

        setAppbarElevation(header)

        mKoreApplication.setOnDatabaseOpened(IEvent<Int> { _, e -> onDatabaseOpened(e) })
        mButtonAsNextOrSetup!!.setOnClickListener({ onNextOrSetupClicked() })
        mButtonAsExtended!!.setOnClickListener({ onExtendedClicked() })
        fab.setOnClickListener({ onHelpButtonClicked() })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    private fun onDatabaseOpened(e: EventArgs<Int>) {
        val i = e.data

        Log.d(javaClass.simpleName, "" + i)

        if (i == 0) {
            (mSetupPageAdapter!!.getItem(1) as IPasswordProvider).cleanUp()
            (mSetupPageAdapter!!.getItem(2) as IPasswordProvider).cleanUp()


            // todo: start overview activity
        }
    }

    private fun onNextOrSetupClicked() {
        val currentItem = mViewPagerAsContentWrapper!!.currentItem
        if (currentItem == 0) {
            mViewPagerAsContentWrapper?.setCurrentItem(1, true)
            mButtonAsNextOrSetup?.setText(R.string.setuplayout_string_setuptext)
            mButtonAsNextOrSetup?.isEnabled = false

            mTextViewAsHint!!.animate()
                    .alpha(0f)
                    .setDuration(150)
                    .setInterpolator(AccelerateInterpolator())
                    .setStartDelay(50)
                    .setListener(SetGoneOnEndAnimationListener(mTextViewAsHint))
                    .start()

            mButtonAsExtended!!.animate()
                    .alpha(1f)
                    .setDuration(150)
                    .setInterpolator(DecelerateInterpolator())
                    .setStartDelay(50)
                    .setListener(SetVisibleOnStartAnimationListener(mButtonAsExtended))
                    .start()
        } else {
            val pw: String
            val rp: CharSequence
            var pim = 0

            val password = mSetupPageAdapter?.getItem(currentItem) as IPasswordProvider
            pw = password.password1.toString()
            rp = password.password2

            if (currentItem == 2) {
                val fragment = mSetupPageAdapter?.getItem(currentItem) as ExtendSetupFragment

                val pim1 = fragment.pim1.toString()
                val pim2 = fragment.pim2.toString()

                pim = checkPim(pw, rp, pim1, pim2)

                if (pim < 0)
                    return
            }

            checkPassword(pw, rp, pim)

            setup(pw, pim)
        }
    }

    private fun onExtendedClicked() {
        AlertBuilder.create(this)
                .setMessage(R.string.setuplayout_string_hintextended)
                .setPositiveButton(getString(R.string.setuplayout_string_extenedcontinue)) { _, _ -> this.onExtendedDialogClicked() }
                .setNegativeButton(getString(R.string.setuplayout_string_cancelextended))
                .show()
    }

    private fun onExtendedDialogClicked() {
        val password = mSetupPageAdapter!!.getItem(1) as IPasswordProvider
        val extended = mSetupPageAdapter!!.getItem(2) as IPasswordProvider

        extended.setPasswords(password.password1, password.password2)

        mViewPagerAsContentWrapper!!.setCurrentItem(2, true)
        mButtonAsExtended!!.animate()
                .alpha(0f)
                .setDuration(150)
                .setInterpolator(AccelerateInterpolator())
                .setStartDelay(50)
                .setListener(SetGoneOnEndAnimationListener(mButtonAsExtended))
                .start()
        mButtonAsNextOrSetup!!.isEnabled = false
    }

    private fun onHelpButtonClicked() {
        val layout = mHelpViews[mViewPagerAsContentWrapper!!.currentItem]

        if (resources.getBoolean(R.bool.common_bool_istablet)) {
            AlertBuilder.create(this)
                    .setView(layout)
                    .setPositiveButton(getString(R.string.common_string_close))
                    .show()
        } else {
            val fragment = BottomSheetViewerFragment.create(layout)
            fragment.show(supportFragmentManager, fragment.tag)
        }
    }


    private fun setAppbarElevation(appbar: AppBarLayout) {
        if (Build.VERSION.SDK_INT < 21) return

        val stateListAnimator = StateListAnimator()
        stateListAnimator.addState(IntArray(0),
                ObjectAnimator.ofFloat(appbar,
                        "elevation",
                        resources.getDimensionPixelSize(R.dimen.xs).toFloat()))
        appbar.stateListAnimator = stateListAnimator
    }

    internal fun enableSetupButton(enable: Boolean) {
        mButtonAsNextOrSetup?.isEnabled = enable
    }

    private fun checkPim(pw: String, rp: CharSequence, pim1: String, pim2: String): Int {
        var pim = 0
        if (pim1 != pim2) {
            AlertBuilder.create(this)
                    .setMessage(R.string.setuplayout_string_enteredpimsdontmatch)
                    .setPositiveButton(getString(R.string.common_string_ok))
                    .show()

            return pim
        }

        if (!pim1.isEmpty()) {
            pim = Integer.parseInt(pim1)
            val finalPim = pim

            mKoreApplication.hasCustomPIM(true)

            if (pim < 20000) {
                AlertBuilder.create(this)
                        .setMessage(R.string.setuplayout_string_pimhint)
                        .setPositiveButton(getString(R.string.common_string_change))
                        .setNegativeButton(getString(R.string.setuplayout_string_keeppim)) { _, _ -> checkPassword(pw, rp, finalPim) }
                        .show()

                return -1
            }

            if (pim > 30000) {
                AlertBuilder.create(this)
                        .setMessage(R.string.setuplayout_string_pimhinthigh)
                        .setPositiveButton(getString(R.string.common_string_change))
                        .setNegativeButton(getString(R.string.setuplayout_string_keeppim)) { _, _ -> checkPassword(pw, rp, finalPim) }
                        .show()

                return -1
            }
        }

        return pim
    }

    private fun checkPassword(pw: String, rp: CharSequence, pim: Int) {
        if (pw != rp.toString()) {
            AlertBuilder.create(this)
                    .setMessage(R.string.setuplayout_string_passwordsdontmatch)
                    .setPositiveButton(getString(R.string.common_string_ok))
                    .show()

            return
        }

        if (!pw.matches(Constants.REGEX_PASSWORD_SAFETY.toRegex())) {
            AlertBuilder.create(this)
                    .setMessage(R.string.setuplayout_string_weakpasswordhint)
                    .setPositiveButton(getString(R.string.common_string_change))
                    .setNegativeButton(getString(R.string.setuplayout_string_continueanyway)) { _, _ -> setup(pw, pim) }
                    .show()
        }
    }

    private fun setup(pw: String, pim: Int) {
        var calcPim: Int = 0
        if (pim == 0) {
            calcPim = mKoreApplication.calculatePIM(pw)
            if (calcPim <= 0) {
                AlertBuilder.create(this)
                        .setMessage(R.string.setuplayout_string_setuperror)
                        .setPositiveButton(getString(R.string.common_string_ok))
                        .show()

                return
            }
        }

        // selected pim at least 50 (=> 20000 iterations)
        // calculated pim at least 50 (=> 20000 iterations) and 150 (=> 30000 iterations)
        mKoreApplication.openDatabaseConnection(pw, calcPim)
    }

    internal fun <T : TextView> clearText(v: T?) {
        if (v == null) return
        try {
            val text = v.text
            val field = text.javaClass.getDeclaredField("mText")
            field.isAccessible = true
            var arr = field.get(text) as CharArray
            arr = Arrays.copyOf(Constants.CHARS, arr.size)
            field.set(text, arr)
            v.text = ""
        } catch (e: IllegalAccessException) {
            Log.d(javaClass.simpleName, e.message)
        } catch (e: NoSuchFieldException) {
            Log.d(javaClass.simpleName, e.message)
        }

    }

    companion object {
        val NAME: String = SetupActivity::class.java.name
    }
}

