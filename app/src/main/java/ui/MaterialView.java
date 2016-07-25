package ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class MaterialView extends View {
  private String mText = "";
  private int mOverlayColor = 0xFFFFFFFF;
  private Random mRandom = new Random();
  private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  public MaterialView(Context context) {
    super(context);
  }

  public MaterialView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MaterialView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @TargetApi(21)
  public MaterialView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawColor(mOverlayColor);
  }

  private int overlay(int a, int b) {
    return ((b < 0x80) ? (2 * a * b / 0xFF) : (0xFF - 2 * (0xFF - a) * (0xFF - b) / 0xFF)) & 0xFF;
  }

  private int overlay(int grey) {
    grey &= 0x000000FF;
    int red = (0x00FF0000 & mOverlayColor) >> 16;
    int green = (0x0000FF00 & mOverlayColor) >> 8;
    int blue = 0x000000FF & mOverlayColor;

    return Color.argb(0xFF, overlay(red, grey), overlay(green, grey), overlay(blue, grey));
  }

  private void generate() {
    int grey = Math.abs(mRandom.nextInt(0xDB - 0x20) + 0x30);

    int drawColor = overlay(grey);

    Log.d(getClass().getCanonicalName(), "0x" + Integer.toHexString(grey) + " 0x" + Integer.toHexString(drawColor));

    int contrast = (0xFFFFFF - mOverlayColor) & 0xFF000000;
  }

  public String getText() {
    return mText;
  }

  public void setText(String text) {
    this.mText = text;
    invalidate();
  }

  public int getOverlayColor() {
    return mOverlayColor;
  }

  /**
   * Sets the color
   *
   * @param overlayColor color in ARGB format!
   */
  public void setOverlayColor(int overlayColor) {
    this.mOverlayColor = overlayColor;
    invalidate();
  }
}
