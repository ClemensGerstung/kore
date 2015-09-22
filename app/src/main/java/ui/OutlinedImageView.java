package ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

public class OutlinedImageView extends ImageView {
    private static final Paint BLUE_ANTIALAISED;
    private static final Paint GREY_ANTIALAISED;

    private RectF bound;

    static {
        BLUE_ANTIALAISED = new Paint(Paint.ANTI_ALIAS_FLAG);
        BLUE_ANTIALAISED.setColor(0xff1976D2);
        BLUE_ANTIALAISED.setStrokeWidth(5.f);
        GREY_ANTIALAISED = new Paint(Paint.ANTI_ALIAS_FLAG);
        GREY_ANTIALAISED.setColor(0xffe0e0e0);
    }

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
            bound = new RectF(0, 0, getWidth(), getHeight());
        }

        canvas.drawArc(bound, 270, 90, false, BLUE_ANTIALAISED);

        canvas.drawCircle(getWidth() / 2.f, getHeight() / 2.f, getWidth() / 2.f - 7.5f, GREY_ANTIALAISED);

        super.onDraw(canvas);
    }


}
