package com.typingsolutions.passwordmanager.activities;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import com.typingsolutions.passwordmanager.BaseActivity;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.adapter.SetupPagerAdapter;
import com.typingsolutions.passwordmanager.utils.ViewUtils;

public class SetupActivity extends BaseActivity {

  private int mPim;
  private String mPassword;
  private int mScrollWidth = -1;

  private CoordinatorLayout mCoordinatorLayoutAsRootLayout;
  private ui.ViewPager mViewPagerAsFragmentHost;
  private ImageView mImageViewAsBackground;
  private ImageSwitcher mImageSwitcherAsIcon;

  private SetupPagerAdapter mSetupPagerAdapter;

  private final int[] imageResources = {R.drawable.android, R.mipmap.security, R.mipmap.pim, R.mipmap.setup_done};

  private final ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
    @Override
    public boolean onPreDraw() {
      float[] matrix = new float[9];
      mImageViewAsBackground.getImageMatrix().getValues(matrix);
      float entryPoint = matrix[2];

      mImageViewAsBackground.setScrollX((int) entryPoint);
      mScrollWidth = (int) ((entryPoint * -2) / (mSetupPagerAdapter.getCount() - 1));

      mImageViewAsBackground.getViewTreeObserver().removeOnPreDrawListener(this);
      return true;
    }
  };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setup_main_layout);

    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    if(Build.VERSION.SDK_INT >= 21) getWindow().setStatusBarColor(0x00FFFFFF);

    mCoordinatorLayoutAsRootLayout = findCastedViewById(R.id.setuplayout_coordinatorlayout_root);
    mViewPagerAsFragmentHost = findCastedViewById(R.id.setuplayout_viewpager_content);
    mImageViewAsBackground = findCastedViewById(R.id.setuplayout_image_background);
    mImageSwitcherAsIcon = findCastedViewById(R.id.setuplayout_imageswitcher_wrapper);

    mSetupPagerAdapter = new SetupPagerAdapter(getSupportFragmentManager());

    mViewPagerAsFragmentHost.setAdapter(mSetupPagerAdapter);
    mViewPagerAsFragmentHost.setOffscreenPageLimit(3);
    mViewPagerAsFragmentHost.canSwipe(false);

    mImageViewAsBackground.getViewTreeObserver().addOnPreDrawListener(onPreDrawListener);

    mImageSwitcherAsIcon.setFactory(ViewUtils.getSimpleViewFactory(this));
    mImageSwitcherAsIcon.setImageResource(imageResources[0]);
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

      mImageSwitcherAsIcon.setOutAnimation(this, R.anim.zoom_slide_out_left);
      mImageSwitcherAsIcon.setInAnimation(this, R.anim.zoom_slide_in_right);
      mImageSwitcherAsIcon.setImageResource(imageResources[index + 1]);

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

      mImageSwitcherAsIcon.setOutAnimation(this, R.anim.zoom_slide_out_right);
      mImageSwitcherAsIcon.setInAnimation(this, R.anim.zoom_slide_in_left);
      mImageSwitcherAsIcon.setImageResource(imageResources[index - 1]);

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
