package ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NotSwipeableViewPager extends android.support.v4.view.ViewPager {
  private boolean mCanSwipe = true;

  public NotSwipeableViewPager(Context context) {
    super(context);

  }

  public NotSwipeableViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public boolean canSwipe() {
    return mCanSwipe;
  }

  public void canSwipe(boolean mCanSwipe) {
    this.mCanSwipe = mCanSwipe;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    return mCanSwipe && super.onInterceptTouchEvent(event);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return mCanSwipe && super.onTouchEvent(event);
  }
}
