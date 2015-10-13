package com.typingsolutions.passwordmanager;

import android.content.Context;
import android.support.annotation.AnimRes;
import android.view.View;
import android.view.animation.Animation;

public final class ViewUtils {

    public synchronized static void show(Context context, View view, @AnimRes final int animation) {
        Object tag = view.getTag(R.string.hidden);
        boolean hiding = tag != null && (boolean) tag;
        if (hiding || view.getVisibility() != View.VISIBLE) {
            view.clearAnimation();
            view.setVisibility(View.VISIBLE);
            Animation anim = android.view.animation.AnimationUtils.loadAnimation(context, animation);

            view.startAnimation(anim);
        }
    }

    public synchronized void hide(Context context, final View view, @AnimRes int animation) {
        boolean hiding = (boolean) view.getTag(R.string.hidden);
        if (hiding || view.getVisibility() != View.VISIBLE) return;

        Animation anim = android.view.animation.AnimationUtils.loadAnimation(context, animation);
        anim.setAnimationListener(new LocalAnimationListener(view));
        view.startAnimation(anim);
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
