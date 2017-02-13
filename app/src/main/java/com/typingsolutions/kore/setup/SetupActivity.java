package com.typingsolutions.kore.setup;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import org.jetbrains.annotations.NotNull;
import ui.NotSwipeableViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import com.typingsolutions.kore.R;
import com.typingsolutions.kore.common.*;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.StringJoiner;

// entropy = length * log2(numberofavailablechars)
// avg is 40.54 bit
// lower mid (yellow) -> everything below is bad, and +20 is good?

public class SetupActivity extends AppCompatActivity {
  private NotSwipeableViewPager mViewPagerAsContentWrapper;
  private TextView mTextViewAsHint;
  private SimplePagerAdapter mSetupPageAdapter;
  private AppCompatButton mButtonAsExtended;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setup_layout);

    if (Build.VERSION.SDK_INT >= 21) {
      getWindow().setStatusBarColor(0x44000000);
    }

    KoreApplication app = (KoreApplication) getApplicationContext();
    app.setOnDatabaseOpened((sender, e) -> {
      Log.d(getClass().getSimpleName(), "" + e.getData());
      // todo: start overview activity
    });

    mViewPagerAsContentWrapper = (NotSwipeableViewPager) findViewById(R.id.setuplayout_viewpager_contenthost);
    mTextViewAsHint = (TextView) findViewById(R.id.setuplayout_textview_hint);
    mButtonAsExtended = (AppCompatButton) findViewById(R.id.setuplayout_button_extended);

    mSetupPageAdapter = new SimplePagerAdapter(getSupportFragmentManager(), new Fragment[]
        {
            SimpleViewFragment.create(R.layout.setup_fragment_1),
            SimpleViewFragment.create(R.layout.setup_fragment_2),
            new ExtendSetupFragment()
        });

    mViewPagerAsContentWrapper.canSwipe(false);
    mViewPagerAsContentWrapper.setAdapter(mSetupPageAdapter);
    mViewPagerAsContentWrapper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        if (position > 0) {
          mTextViewAsHint.animate()
              .alpha(0)
              .setDuration(150)
              .setInterpolator(new AccelerateInterpolator())
              .setStartDelay(50)
              .setListener(new SetGoneOnEndAnimationListener(mTextViewAsHint))
              .start();

          mButtonAsExtended.animate()
              .alpha(1)
              .setDuration(150)
              .setInterpolator(new DecelerateInterpolator())
              .setStartDelay(50)
              .setListener(new SetVisibleOnStartAnimationListener(mButtonAsExtended))
              .start();
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    AppBarLayout header = (AppBarLayout) findViewById(R.id.setuplayout_appbarlayout_header);
    setAppbarElevation(header);

    AppCompatButton next = (AppCompatButton) findViewById(R.id.setuplayout_button_next);
    next.setOnClickListener(v -> {
      int currentItem = mViewPagerAsContentWrapper.getCurrentItem();
      if(currentItem == 0) {
        mViewPagerAsContentWrapper.setCurrentItem(1, true);
        next.setText("Setup");
      } else {
        String pw = null;
        CharSequence rp = null;
        int pim = 0;

        if(currentItem == 2) {
          ExtendSetupFragment fragment = (ExtendSetupFragment) mSetupPageAdapter.getItem(currentItem);

          pw = fragment.getPassword1().toString();
          rp = fragment.getPassword2();

          String pim1 = fragment.getPIM1().toString();
          String pim2 = fragment.getPIM2().toString();

          if(!pim1.equals(pim2)) {
            AlertBuilder.create(this)
                .setMessage("The entered PIMs don't match!")
                .setPositiveButton("OK", null)
                .show();

            return;
          }

          if(!pim1.isEmpty()) {
            pim = Integer.parseInt(pim1);
          }
        } else {
          Fragment fragment = mSetupPageAdapter.getItem(currentItem);

          pw = ((TextInputEditText)fragment.getView().findViewById(R.id.setuplayout_edittext_passwordenter)).getText().toString();
          rp = ((TextInputEditText)fragment.getView().findViewById(R.id.setuplayout_edittext_passwordrepeat)).getText();
        }

        if(!pw.equals(rp.toString())) {
          AlertBuilder.create(this)
              .setMessage("Passwords don't match!")
              .setPositiveButton("OK", null)
              .show();

          return;
        }

        if(pim == 0) {
          int calcPim = calcPim(pw);
          if (calcPim <= 0) {
            AlertBuilder.create(this)
                .setMessage("Error during setup. Please try again.\nIf it still fails, please select another password.")
                .setPositiveButton("OK", null)
                .show();

            return;
          }

          pim = calcPim;
        }

        // selected pim at least 50 (=> 20000 iterations)
        // calculated pim at least 50 (=> 20000 iterations) and 150 (=> 30000 iterations)
        app.openDatabaseConnection(pw, pim);
      }
    });

    mButtonAsExtended.setOnClickListener(v -> {
      AlertBuilder.create(this)
          .setMessage("Uhh, an expert coming along!")
          .setPositiveButton("Yes, continue", (dialog, which) -> {
            Fragment password = mSetupPageAdapter.getItem(1);
            ExtendSetupFragment extended = (ExtendSetupFragment) mSetupPageAdapter.getItem(2);

            TextInputEditText passwordInputText = (TextInputEditText) password.getView().findViewById(R.id.setuplayout_edittext_passwordenter);
            CharSequence pw = passwordInputText.getText();
            CharSequence rp = ((TextInputEditText)password.getView().findViewById(R.id.setuplayout_edittext_passwordrepeat)).getText();

            extended.setEnteredPasswords(pw, rp);

            mViewPagerAsContentWrapper.setCurrentItem(2, true);
            mButtonAsExtended.animate()
                .alpha(0)
                .setDuration(150)
                .setInterpolator(new AccelerateInterpolator())
                .setStartDelay(50)
                .setListener(new SetGoneOnEndAnimationListener(mButtonAsExtended))
                .start();
          })
          .setNegativeButton("No, get me out!", null)
          .show();
    });


  }

  @TargetApi(21)
  private void setAppbarElevation(AppBarLayout appbar) {
    StateListAnimator stateListAnimator = new StateListAnimator();
    stateListAnimator.addState(new int[0],
        ObjectAnimator.ofFloat(appbar,
            "elevation",
            getResources().getDimensionPixelSize(R.dimen.xs)));
    appbar.setStateListAnimator(stateListAnimator);
  }

  int calcPim(String password) {
    try {
      MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
      shaDigest.update(password.getBytes());

      BigInteger integer = new BigInteger(shaDigest.digest());
      Random r = new Random(integer.longValue());
      int pim = r.nextInt(10000) + 20000;
      Log.d(getClass().getSimpleName(), "SETUPPIM: " + pim);

      shaDigest.reset();

      return pim;
    } catch (NoSuchAlgorithmException e) {
      Log.d(getClass().getSimpleName(), "FAIL! " + e.getMessage());

      return -1;
    }
  }
}

