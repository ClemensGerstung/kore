package ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class ViewPager extends android.support.v4.view.ViewPager {

  private class Specs {
    int mHeight;
    int mWidthMeasureSpec;
    int mHeightMeasureSpec;

    public Specs(int mHeight, int mWidthMeasureSpec, int mHeightMeasureSpec) {
      this.mHeight = mHeight;
      this.mWidthMeasureSpec = mWidthMeasureSpec;
      this.mHeightMeasureSpec = mHeightMeasureSpec;
    }
  }

  private boolean mCanSwipe = true;
  private int mCurrentIndex = 0;

  private Specs[] mHeights = null;

  public ViewPager(Context context) {
    super(context);

  }

  public ViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public boolean canSwipe() {
    return mCanSwipe;
  }

  public void canSwipe(boolean mCanSwipe) {
    this.mCanSwipe = mCanSwipe;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    if (mHeights == null && getChildCount() > 0) {
      mHeights = new Specs[getChildCount()];

      for (int i = 0; i < mHeights.length; i++) {
        View child = getChildAt(i);
        child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int h = child.getMeasuredHeight();
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
        mHeights[i] = new Specs(h, widthMeasureSpec, heightMeasureSpec);
      }
    }

    if (mHeights != null && mHeights.length > 0) {
      heightMeasureSpec = mHeights[mCurrentIndex].mHeightMeasureSpec;
      widthMeasureSpec = mHeights[mCurrentIndex].mWidthMeasureSpec;
    }

    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  public void setCurrentItem(int item, boolean smoothScroll) {
    super.setCurrentItem(item, smoothScroll);
    this.mCurrentIndex = item;

    //because fuck you..OK! FUCK! YOU!...OK! FUCK!
    //#VaasForPresident
    requestLayout();
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    return super.onInterceptTouchEvent(event) && this.mCanSwipe;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return super.onTouchEvent(event) && this.mCanSwipe;
  }
}
