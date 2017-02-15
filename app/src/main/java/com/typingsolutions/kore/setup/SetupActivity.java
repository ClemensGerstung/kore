package com.typingsolutions.kore.setup;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
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

// entropy = length * log2(numberofavailablechars)
// avg is 40.54 bit
// lower mid (yellow) -> everything below is bad, and +20 is good?

public class SetupActivity extends AppCompatActivity {
  private NotSwipeableViewPager mViewPagerAsContentWrapper;
  private TextView mTextViewAsHint;
  private SimplePagerAdapter mSetupPageAdapter;
  private AppCompatButton mButtonAsExtended;
  private AppCompatButton mButtonAsNextOrSetup;
  private KoreApplication mKoreApplication;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setup_layout);

    if (Build.VERSION.SDK_INT >= 21) {
      getWindow().setStatusBarColor(0x44000000);
    }

    mKoreApplication = (KoreApplication) getApplicationContext();
    mKoreApplication.setOnDatabaseOpened((sender, e) -> {
      Log.d(getClass().getSimpleName(), "" + e.getData());
      // todo: start overview activity
    });

    mViewPagerAsContentWrapper = (NotSwipeableViewPager) findViewById(R.id.setuplayout_viewpager_contenthost);
    mTextViewAsHint = (TextView) findViewById(R.id.setuplayout_textview_hint);
    mButtonAsExtended = (AppCompatButton) findViewById(R.id.setuplayout_button_extended);

    mSetupPageAdapter = new SimplePagerAdapter(getSupportFragmentManager(), new Fragment[]
        {
            SimpleViewFragment.create(R.layout.setup_fragment_1),
            new SimpleSetupFragment(),
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

    mButtonAsNextOrSetup = (AppCompatButton) findViewById(R.id.setuplayout_button_next);
    mButtonAsNextOrSetup.setOnClickListener(v -> {
      int currentItem = mViewPagerAsContentWrapper.getCurrentItem();
      if(currentItem == 0) {
        mViewPagerAsContentWrapper.setCurrentItem(1, true);
        mButtonAsNextOrSetup.setText("Setup");
        mButtonAsNextOrSetup.setEnabled(false);
      } else {
        String pw = null;
        CharSequence rp = null;
        int pim = 0;

        IPasswordProvider password = (IPasswordProvider) mSetupPageAdapter.getItem(currentItem);
        pw = password.getPassword1().toString();
        rp = password.getPassword2();

        if(currentItem == 2) {
          ExtendSetupFragment fragment = (ExtendSetupFragment) mSetupPageAdapter.getItem(currentItem);

          String pim1 = fragment.getPIM1().toString();
          String pim2 = fragment.getPIM2().toString();

          pim = checkPim(pw, rp, pim1, pim2);

          if(pim < 0)
            return;
        }

        checkPassword(pw, rp, pim);

        setup(pw, pim);
      }
    });

    mButtonAsExtended.setOnClickListener(v -> {
      AlertBuilder.create(this)
          .setMessage("Uhh, an expert coming along!")
          .setPositiveButton("Yes, continue", (dialog, which) -> {
            IPasswordProvider password = (IPasswordProvider) mSetupPageAdapter.getItem(1);
            IPasswordProvider extended = (IPasswordProvider) mSetupPageAdapter.getItem(2);

            extended.setPasswords(password.getPassword1(), password.getPassword2());

            mViewPagerAsContentWrapper.setCurrentItem(2, true);
            mButtonAsExtended.animate()
                .alpha(0)
                .setDuration(150)
                .setInterpolator(new AccelerateInterpolator())
                .setStartDelay(50)
                .setListener(new SetGoneOnEndAnimationListener(mButtonAsExtended))
                .start();
            mButtonAsNextOrSetup.setEnabled(false);
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

  void enableSetupButton(boolean enable) {
    mButtonAsNextOrSetup.setEnabled(enable);
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

  private int checkPim(final String pw, final CharSequence rp, String pim1, String pim2) {
    int pim = 0;
    if(!pim1.equals(pim2)) {
      AlertBuilder.create(this)
          .setMessage("The entered PIMs don't match!")
          .setPositiveButton("OK", null)
          .show();

      return pim;
    }

    if(!pim1.isEmpty()) {
      pim = Integer.parseInt(pim1);
      final int finalPim = pim;

      if (pim < 20000) {
        AlertBuilder.create(this)
            .setMessage("Your entered PIM seems to be a bit low! You should use a 5 digit number between 20000 and 30000.\nCheck help if you're not sure.")
            .setPositiveButton("Change", null)
            .setNegativeButton("Keep PIM", (dialog, which) -> checkPassword(pw, rp, finalPim))
            .show();

        return -1;
      }

      if (pim > 30000) {
        AlertBuilder.create(this)
            .setMessage("Your entered PIM seems to be a bit high! If you choose a high PIM the mKoreApplication may slow down significantly.\nCheck help if you're not sure.")
            .setPositiveButton("Change", null)
            .setNegativeButton("Keep PIM", (dialog, which) -> checkPassword(pw, rp, finalPim))
            .show();

        return -1;
      }
    }

    return pim;
  }

  private void checkPassword(String pw, CharSequence rp, int pim) {
    if(!pw.equals(rp.toString())) {
      AlertBuilder.create(this)
          .setMessage("Passwords don't match!")
          .setPositiveButton("OK", null)
          .show();

      return;
    }

    if(!pw.matches(Constants.REGEX_PASSWORD_SAFETY)) {
      AlertBuilder.create(this)
          .setMessage("Your password doesn't seem to be safe enough! Check help for more information.")
          .setPositiveButton("Change", null)
          .setNegativeButton("Continue anyway", (dialog, which) -> setup(pw, pim))
          .show();
    }
  }

  private void setup(String pw, int pim) {
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
    mKoreApplication.openDatabaseConnection(pw, pim);
  }
}

