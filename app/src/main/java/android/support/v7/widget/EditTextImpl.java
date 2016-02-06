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

  /**
   * Sets the shadow for Android API >= 17
   */
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

  /**
   * Initializes the shadow with all needed information
   *
   * @param view to set shadow at
   * @param context of the app
   * @param backgroundColor of the shadow
   * @param radius of the shadow
   * @param elevation of the shadow in rest state
   * @param translationZ added to {@see elevation} if activated
   */
  public void init(View view, Context context, int backgroundColor, float radius, float elevation, float translationZ) {
    RoundRectDrawableWithShadow background = new RoundRectDrawableWithShadow(context.getResources(), backgroundColor, radius, elevation, elevation + translationZ);
    background.setAddPaddingForCorners(false);
    view.setBackgroundDrawable(background);
    updatePadding(view);
  }

  /**
   * Updates the padding if view state changed
   *
   * @param view to update padding
   */
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

  /**
   * Gets the minimum width needed for the view
   *
   * @param view to get width from
   * @return the minimum width
   */
  public float getMinWidth(View view) {
    return getShadowBackground(view).getMinWidth();
  }

  /**
   * Gets the minimum height needed for the view
   *
   * @param view to get height from
   * @return the minimum height
   */
  public float getMinHeight(View view) {
    return getShadowBackground(view).getMinHeight();
  }

  /**
   * Gets the actual shadow background from the given view
   *
   * @param view to get shadow from
   * @return the shadow
   */
  /*package*/ RoundRectDrawableWithShadow getShadowBackground(View view) {
    return ((RoundRectDrawableWithShadow) view.getBackground());
  }
}
