package com.typingsolutions.passwordmanager.activities;

import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.adapter.SetupPagerAdapter;

public class SetupActivity extends BaseActivity {

  private int mPim;
  private String mPassword;
  private int mScrollWidth = -1;

  private CoordinatorLayout mCoordinatorLayoutAsRootLayout;
  private ui.ViewPager mViewPagerAsFragmentHost;
  private ImageView mImageViewAsBackground;

  private SetupPagerAdapter mSetupPagerAdapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setup_main_layout);

    mCoordinatorLayoutAsRootLayout = findCastedViewById(R.id.setuplayout_coordinatorlayout_root);
    mViewPagerAsFragmentHost = findCastedViewById(R.id.setuplayout_viewpager_content);
    mImageViewAsBackground = findCastedViewById(R.id.setuplayout_image_background);

    mSetupPagerAdapter = new SetupPagerAdapter(getSupportFragmentManager());

    mViewPagerAsFragmentHost.setAdapter(mSetupPagerAdapter);
    mViewPagerAsFragmentHost.setOffscreenPageLimit(3);
    mViewPagerAsFragmentHost.canSwipe(false);

    int imageWidth = mImageViewAsBackground.getDrawable().getMinimumWidth();

    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int windowWidth = size.x;

    mScrollWidth = (imageWidth - windowWidth * 2) / mSetupPagerAdapter.getCount();

    mImageViewAsBackground.setScrollX(((imageWidth / 2) - windowWidth) * -1);
  }

  @Override
  public void onBackPressed() {
    if (mViewPagerAsFragmentHost.getCurrentItem() == 0) {
      super.onBackPressed();
    } else {
      moveToPreviousPage();
    }
  }

  public void moveToNextPage() {
    int index = mViewPagerAsFragmentHost.getCurrentItem();
    if (index < mSetupPagerAdapter.getCount()) {
      mViewPagerAsFragmentHost.setCurrentItem(index + 1, true);

      int x = mImageViewAsBackground.getScrollX();

      ObjectAnimator anim = ObjectAnimator.ofInt(mImageViewAsBackground, "scrollX", x, x + mScrollWidth);
      anim.setDuration(250);
      anim.start();

    }
  }

  public void moveToPreviousPage() {
    int index = mViewPagerAsFragmentHost.getCurrentItem();
    if (index > 0) {
      mViewPagerAsFragmentHost.setCurrentItem(index - 1, true);

      ObjectAnimator anim = ObjectAnimator.ofInt(mImageViewAsBackground, "scrollX", mImageViewAsBackground.getScrollX(), mImageViewAsBackground.getScrollX() - mScrollWidth);
      anim.setDuration(250);
      anim.start();
    }
  }

  @Override
  protected View getSnackbarRelatedView() {
    return mCoordinatorLayoutAsRootLayout;
  }

  public void setPim(int mPim) {
    this.mPim = mPim;
  }

  public void setPassword(String mPassword) {
    this.mPassword = mPassword;
  }
}
