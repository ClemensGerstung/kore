package ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.*;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MaterialView extends View {
  private static Integer[] GREYS = {0xFFfafafa, 0xFFf5f5f5, 0xFFeeeeee, 0xFFe0e0e0, 0xFFbdbdbd, 0xFF9e9e9e, 0xFF757575, 0xFF616161, 0xFF424242, 0xFF212121};
  private String mText = "";
  private int mOverlayColor = 0xFFBBDEFB;
  private Random mRandom = new Random();
  private Rect mBounds = new Rect();
  private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private float[] mHsvOverlayColor = new float[3];

  {
    init();
  }

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

  void init() {
    setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    float min = Math.min(canvas.getWidth(), canvas.getHeight());

    if (min >= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112, getResources().getDisplayMetrics())) {
      Color.colorToHSV(mOverlayColor, mHsvOverlayColor);
      mHsvOverlayColor[2] = mHsvOverlayColor[2] + 0.33f;

      Log.d(getClass().getSimpleName(), "L: " + mHsvOverlayColor[2]);

      if (mHsvOverlayColor[2] > 1.0f) {
        mHsvOverlayColor[2] = mHsvOverlayColor[2] - 1;
      }

      canvas.drawColor(Color.HSVToColor(mHsvOverlayColor));
      int i = mRandom.nextInt(2);
      switch (i) {
        case 0:
          generateType1(canvas);
          break;
        case 1:
          generateType2(canvas);
          break;
      }
    } else {
      canvas.drawColor(mOverlayColor);
    }

    if (mText != null) {
      mPaint.setShadowLayer(0, 0, 0, 0);
      mPaint.setColor(0xFF000000);
      mPaint.setTextSize(canvas.getHeight() * 0.1f);
      mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
      canvas.drawText(mText, canvas.getWidth() / 2.f - mBounds.width() / 2.f, canvas.getHeight() / 2.f - mBounds.height() / 2.f, mPaint);
    }
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

  private int generate() {
    int grey = Math.abs(mRandom.nextInt(0xDB - 0x20) + 0x30);
    int drawColor = overlay(grey);
    int contrast = (0xFFFFFF - mOverlayColor) | 0xFF000000;
    return contrast;
  }

  private void generateType1(Canvas canvas) {
    mPaint.setShadowLayer(50, 10, 10, 0xFF000000);
    float pivotX = canvas.getWidth() / 2.0f;
    float pivotY = canvas.getHeight() / 2.0f;
    float yOffset = -100;
    Log.d(getClass().getSimpleName(), "Offset: " + yOffset);
    List<Integer> greys = new ArrayList<>(Arrays.asList(GREYS));
    float posAngle = mRandom.nextFloat() * 20.f + 20;
    double posRad = Math.toRadians(posAngle);
    float negAngle = 360.f - posAngle;
    double negRad = Math.toRadians(negAngle);
    Path path;
    generateGrey(greys, 3);
    path = getType1Path(canvas, pivotX, yOffset + (canvas.getHeight() * 0.22f) / 2, negAngle, negRad);
    canvas.drawPath(path, mPaint);
    path.reset();
    generateGrey(greys, 2);
    path = getType1Path(canvas, pivotX, yOffset + (canvas.getHeight() * 0.22f) / 2, posAngle, posRad);
    canvas.drawPath(path, mPaint);
    path.reset();
    generateGrey(greys, 1);
    path = getType1Path(canvas, pivotX, yOffset, negAngle, negRad);
    canvas.drawPath(path, mPaint);
    path.reset();
    generateGrey(greys, 0);
    path = getType1Path(canvas, pivotX, yOffset, posAngle, posRad);
    canvas.drawPath(path, mPaint);
    path.reset();
  }

  private void generateType2(Canvas canvas) {
    mPaint.setShadowLayer(25, 5, 5, 0xFF000000);
    float posAngle = mRandom.nextFloat() * 5.f + 5;
    double posRad = Math.toRadians(posAngle);
    float negAngle = 360.f - posAngle;
    double negRad = Math.toRadians(negAngle);
    float height = (float) (0.50 * canvas.getHeight());
    float offset = (float) (0.50 * canvas.getHeight());
    PointF point = rotatePoint(canvas.getWidth(), canvas.getHeight(), canvas.getWidth() / 2.f, offset + height / 2.f, posAngle);
    point.y = (float) (point.y + Math.sin(posRad) * (canvas.getWidth() - point.x) / Math.cos(posRad));
    point.x = canvas.getWidth();
    Path p = new Path();
    p.moveTo(0, canvas.getHeight());
    p.lineTo(canvas.getWidth(), canvas.getHeight());
    lineTo(p, point);
    lineTo(p, rotatePoint(0, canvas.getHeight(), canvas.getWidth() / 2.f, offset + height / 2.f, posAngle));
    p.close();
    mPaint.setColor(mOverlayColor);
    canvas.drawPath(p, mPaint);
    int grey = GREYS[mRandom.nextInt(3) + 7];
    int darkOverlay = overlay(grey);
    mPaint.setColor(darkOverlay);
    canvas.drawCircle(canvas.getWidth() - 150, 150, point.y - 120, mPaint);
    float[] hsv = new float[3];
    Color.colorToHSV(mOverlayColor, hsv);
    hsv[0] = hsv[0] + 30;
    int color = Color.HSVToColor(hsv);
    mPaint.setColor(color);
    float angle = 180 - posAngle;
    double radians = Math.toRadians(angle);
    float upperHeight = (float) (0.15 * canvas.getHeight());
    PointF first = rotatePoint(canvas.getWidth(), upperHeight, canvas.getWidth() / 2.f, upperHeight / 2.f, angle);
    first.y = (float) (first.y + Math.sin(radians) * (0 - first.x) / Math.cos(radians));
    first.x = 0;
    PointF second = rotatePoint(0, upperHeight, canvas.getWidth() / 2.f, upperHeight / 2.f, angle);
    second.y = (float) (second.y + Math.sin(radians) * (canvas.getWidth() - second.x) / Math.cos(radians));
    second.x = canvas.getWidth();
    Path upper = new Path();
    upper.moveTo(canvas.getWidth(), 0);
    upper.lineTo(0, 0);
    lineTo(upper, first);
    lineTo(upper, second);
    upper.close();
    canvas.drawPath(upper, mPaint);
    float width = canvas.getWidth() * 0.33f;
    first = rotatePoint(width, 0, width / 2.f, canvas.getHeight() / 2.f, negAngle);
    first.x = (float) (first.x + Math.cos(negRad) * (canvas.getHeight() - first.y) / Math.sin(negRad));
    first.y = canvas.getHeight();
    Path left = new Path();
    left.moveTo(0, canvas.getHeight());
    left.lineTo(0, 0);
    lineTo(left, rotatePoint(width, canvas.getHeight(), width / 2.f, canvas.getHeight() / 2.f, negAngle));
    lineTo(left, first);
    left.close();
    hsv[2] = hsv[2] - 55.f / 255.f;
    color = Color.HSVToColor(hsv);
    mPaint.setColor(color);
    canvas.drawPath(left, mPaint);
  }

  @NonNull
  private Path getType1Path(Canvas canvas, float pivotX, float yOffset, float posAngle, double radians) {
    Path path = new Path();
    PointF first = rotatePoint(0, yOffset, pivotX, yOffset + (yOffset + canvas.getHeight() * 0.22f) / 2.f, posAngle);
    PointF second = rotatePoint(canvas.getWidth(), yOffset, pivotX, yOffset + (yOffset + canvas.getHeight() * 0.22f) / 2.f, posAngle);
    first.y = (float) (first.y + Math.sin(radians) * (0 - first.x) / Math.cos(radians));
    first.x = 0;
    second.y = (float) (second.y + Math.sin(radians) * (canvas.getWidth() - second.x) / Math.cos(radians));
    second.x = canvas.getWidth();
    moveTo(path, first);
    lineTo(path, second);
    path.lineTo(second.x, second.y + canvas.getHeight() * 0.22f);
    path.lineTo(first.x, first.y + canvas.getHeight() * 0.22f);
    path.close();
    return path;
  }

  private void moveTo(Path path, PointF pointF) {
    path.moveTo(pointF.x, pointF.y);
  }

  private void generateGrey(List<Integer> greys, int i) {
    int index = 4 - i + mRandom.nextInt(greys.size() - 4);
    int grey = greys.get(index);
    greys.remove(index);
    int color = overlay(grey);
    mPaint.setColor(color);
  }

  private void lineTo(Path path, PointF point) {
    path.lineTo(point.x, point.y);
  }

  private PointF rotatePoint(float x, float y, float pivotX, float pivotY, float angle) {
    double rad = Math.toRadians(angle);
    float nx = (float) (Math.cos(rad) * (x - pivotX) - Math.sin(rad) * (y - pivotY) + pivotX);
    float ny = (float) (Math.sin(rad) * (x - pivotX) - Math.cos(rad) * (y - pivotY) + pivotY);
    return new PointF(nx, ny);
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