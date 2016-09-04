package com.typingsolutions.passwordmanager.utils;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.AnimRes;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.typingsolutions.passwordmanager.R;
import core.Utils;
import org.w3c.dom.Text;

import java.util.Arrays;

public final class ViewUtils {

  public static final long FAST_ANIMATION_DURATION = 250;

  public static synchronized void show(final Context context, final View view, @AnimRes int animation) {
    if (view == null) return;

    boolean hiding = true;
    try {
      hiding = (boolean) view.getTag(R.string.hidden);
    } catch (Exception e) {
      view.setTag(R.string.hidden, false);
    }

    if (hiding || view.getVisibility() != View.VISIBLE) {
      view.clearAnimation();
      view.setVisibility(View.VISIBLE);
      Animation anim = android.view.animation.AnimationUtils.loadAnimation(context, animation);
      anim.setDuration(FAST_ANIMATION_DURATION);
      anim.setInterpolator(new DecelerateInterpolator());

      view.startAnimation(anim);
    }
  }

  public static synchronized void hide(final Context context, final View view, @AnimRes int animation) {
    if (view == null) return;

    boolean hiding = false;
    try {
      hiding = (boolean) view.getTag(R.string.hidden);
    } catch (Exception e) {
      view.setTag(R.string.hidden, true);
    }

    if (hiding || view.getVisibility() != View.VISIBLE) return;

    Animation anim = android.view.animation.AnimationUtils.loadAnimation(context, animation);
    anim.setInterpolator(new AccelerateInterpolator());
    anim.setDuration(FAST_ANIMATION_DURATION);
    anim.setAnimationListener(new LocalAnimationListener(view));
    view.startAnimation(anim);
  }

  public static int setColor(@Nullable View view, String first, String second) {
    int hexColor = 0;
    try {
      String programHash = Utils.getHashedString(first).substring(0, 6);
      String passwordHash = Utils.getHashedString(second).substring(0, 6);

      hexColor = Integer.parseInt(programHash, 16)
          & Integer.parseInt(passwordHash, 16);

      hexColor += 0x0000FF;

      if(view !=null) {
        view.getBackground()
            .setColorFilter(hexColor | 0xFF000000, PorterDuff.Mode.SRC);

        if (view instanceof TextView) {
          ((TextView) view).setText(first.toCharArray(), 0, 1);
        }
      }
    } catch (Exception e) {
      Log.e(ViewUtils.class.getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
    }
    return hexColor;
  }

  public static ViewSwitcher.ViewFactory getSimpleViewFactory(final Context context) {
    return new ViewSwitcher.ViewFactory() {
      @Override
      public View makeView() {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return imageView;
      }
    };
  }

  private static class LocalAnimationListener implements Animation.AnimationListener {

    private View view;

    public LocalAnimationListener(View view) {
      this.view = view;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
      view.setTag(R.string.hidden, true);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
      view.setTag(R.string.hidden, false);
      view.setVisibility(View.GONE);
    }
  }
}
