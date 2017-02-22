package ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

public class OutlinedImageView extends android.support.v7.widget.AppCompatImageView {

  private static final Paint BLUE_ANTI_ALIASED;
  private static final Paint GREY_ANTI_ALIASED;

  static {
    BLUE_ANTI_ALIASED = new Paint(Paint.ANTI_ALIAS_FLAG);
    BLUE_ANTI_ALIASED.setStyle(Paint.Style.STROKE);
    BLUE_ANTI_ALIASED.setColor(0xff1976D2);
    BLUE_ANTI_ALIASED.setStrokeWidth(10.f);
    GREY_ANTI_ALIASED = new Paint(Paint.ANTI_ALIAS_FLAG);
    GREY_ANTI_ALIASED.setColor(0xffe0e0e0);
  }

  private RectF bound;
  private boolean blocked;
  private int maxBlockTime;
  private int remainingBlockTime;

  public OutlinedImageView(Context context) {
    super(context);
  }

  public OutlinedImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public OutlinedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onDraw(@NonNull Canvas canvas) {
    if (bound == null) {
      bound = new RectF(5, 5, getWidth() - 5, getHeight() - 5);
    }

    if (blocked) {
      canvas.drawArc(bound, 270, 360.f * remainingBlockTime / maxBlockTime, false, BLUE_ANTI_ALIASED);
    }

    canvas.drawCircle(getWidth() / 2.f, getHeight() / 2.f, getWidth() / 2.f - 7.5f, GREY_ANTI_ALIASED);

    super.onDraw(canvas);
  }

  private void reset() {
    maxBlockTime = 0;
    remainingBlockTime = Integer.MAX_VALUE;
    blocked = false;
  }

  public void update(int timeRemaining, int completeTime) {
    maxBlockTime = completeTime;
    remainingBlockTime = timeRemaining;
    blocked = remainingBlockTime > 0;
    if (!blocked) {
      reset();
    }
  }
}
