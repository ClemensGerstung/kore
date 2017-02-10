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
import android.widget.Button;
import android.widget.EditText;
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setup_layout);

    if (Build.VERSION.SDK_INT >= 21) {
      getWindow().setStatusBarColor(0x44000000);
    }

    mViewPagerAsContentWrapper = (NotSwipeableViewPager) findViewById(R.id.setuplayout_viewpager_contenthost);
    mTextViewAsHint = (TextView) findViewById(R.id.setuplayout_textview_hint);
    mButtonAsExtended = (AppCompatButton) findViewById(R.id.setuplayout_button_extended);

    mSetupPageAdapter = new SimplePagerAdapter(getSupportFragmentManager(), new Fragment[]
        {
            SimpleViewFragment.create(R.layout.setup_fragment_1),
            SimpleViewFragment.create(R.layout.setup_fragment_2),
            SimpleViewFragment.create(R.layout.setup_fragment_3)
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
        Fragment fragment = mSetupPageAdapter.getItem(currentItem);

        CharSequence pw = ((TextInputEditText)fragment.getView().findViewById(R.id.setuplayout_edittext_passwordenter)).getText();
        CharSequence rp = ((TextInputEditText)fragment.getView().findViewById(R.id.setuplayout_edittext_passwordrepeat)).getText();

        // Todo: get entered PIM!

        if(!pw.equals(rp)) {
          AlertBuilder.create(this)
              .setMessage("Passwords don't match!")
              .setPositiveButton("OK", (dialog, which) -> {})
              .show();

          return;
        }


        int calcPim = calcPim(pw.toString());
        if(calcPim == -1) {
          AlertBuilder.create(this)
              .setMessage("Error during setup. Please try again.\nIf it still fails, please select another password.")
              .setPositiveButton("OK", (dialog, which) -> {})
              .show();

          return;
        }

        // selected pim at least 50 (=> 20000 iterations)
        // calculated pim at least 50 (=> 20000 iterations) and 150 (=> 30000 iterations)
        int pim = calcPim * 100 + 15000;


      }
    });

    mButtonAsExtended.setOnClickListener(v -> {
      AlertBuilder.create(this)
          .setMessage("Uhh, an expert coming along!")
          .setPositiveButton("Yes, continue", (dialog, which) -> {
            Fragment password = mSetupPageAdapter.getItem(1);
            Fragment extended = mSetupPageAdapter.getItem(2);

            CharSequence pw = ((TextInputEditText)password.getView().findViewById(R.id.setuplayout_edittext_passwordenter)).getText();
            CharSequence rp = ((TextInputEditText)password.getView().findViewById(R.id.setuplayout_edittext_passwordrepeat)).getText();

            ((TextInputEditText)extended.getView().findViewById(R.id.setuplayout_edittext_passwordenter)).setText(pw);
            ((TextInputEditText)extended.getView().findViewById(R.id.setuplayout_edittext_passwordrepeat)).setText(rp);

            // TODO: calc pim and set to textview

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

    KoreApplication app = (KoreApplication) getApplicationContext();
    app.setOnDatabaseOpened((sender, e) -> Log.d(getClass().getSimpleName(), "" + e.getData()));
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

  private int calcPim(String password) {
    try {
      MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
      shaDigest.update(password.getBytes());

      BigInteger integer = new BigInteger(shaDigest.digest());
      Random r = new Random(integer.longValue());
      int pim = r.nextInt(100) + 50;
      Log.d(getClass().getSimpleName(), "SETUPPIM: " + pim);

      shaDigest.reset();

      return pim;
    } catch (NoSuchAlgorithmException e) {
      Log.d(getClass().getSimpleName(), "FAIL! " + e.getMessage());

      return -1;
    }
  }
}

