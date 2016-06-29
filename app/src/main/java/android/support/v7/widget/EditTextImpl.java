package android.support.v7.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.*;

public class EditTextImpl {

  public void initShadow() {
    RoundRectDrawableWithShadow.sRoundRectHelper
        = new RoundRectDrawableWithShadow.RoundRectHelper() {
      @Override
      public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius,
                                Paint paint) {
        canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint);
      }
    };
  }

  public void init(View view, Context context, int backgroundColor, float radius, float elevation, float translationZ) {
    RoundRectDrawableWithShadow background = new RoundRectDrawableWithShadow(context.getResources(), backgroundColor, radius, elevation, elevation + translationZ);
    background.setAddPaddingForCorners(false);
    view.setBackgroundDrawable(background);
    updatePadding(view);
  }

  public void updatePadding(View view) {
    Rect shadowPadding = new Rect();
    getShadowBackground(view).getMaxShadowAndCornerPadding(shadowPadding);
    view.setMinimumHeight((int) Math.ceil(getMinHeight(view)));
    view.setMinimumWidth((int) Math.ceil(getMinWidth(view)));

    if(!(view instanceof EditText))
      return;

    ((EditText) view).setShadowPadding(shadowPadding.left, shadowPadding.top,
        shadowPadding.right, shadowPadding.bottom);
  }

  public float getMinWidth(View view) {
    return getShadowBackground(view).getMinWidth();
  }

  public float getMinHeight(View view) {
    return getShadowBackground(view).getMinHeight();
  }

  RoundRectDrawableWithShadow getShadowBackground(View view) {
    return ((RoundRectDrawableWithShadow) view.getBackground());
  }
}
