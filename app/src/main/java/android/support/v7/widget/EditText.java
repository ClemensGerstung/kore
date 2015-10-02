package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import com.typingsolutions.passwordmanager.R;

public class EditText extends android.widget.EditText {

    private EditTextImpl editText;

    private boolean hiding;

    private final Rect shadowPadding;

    public EditText(Context context) {
        super(context);
        shadowPadding = new Rect();
        hiding = false;

        initEditText(context, null, 0);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        shadowPadding = new Rect();

        initEditText(context, attrs, 0);
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        shadowPadding = new Rect();

        initEditText(context, attrs, defStyleAttr);
    }

    private void initEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.EditText, defStyleAttr, 0);

        final float elevation = a.getDimension(R.styleable.EditText_support_elevation, 2.f);
        final float translationZ = a.getDimension(R.styleable.EditText_support_translationZ, 3.f);

        a.recycle();

        editText = new EditTextImpl();
        editText.initShadow();
        editText.init(this, context, Color.WHITE, 8.f, elevation, elevation + translationZ);
    }

    public void setShadowPadding(int left, int top, int right, int bottom) {
        shadowPadding.set(left, top, right, bottom);
        super.setPadding(left + getPaddingLeft(), top + getPaddingTop(),
                right + getPaddingRight(), bottom + getPaddingBottom());
    }

    public void hide() {
        if(hiding) {
            return;
        }



        hiding = true;
    }

    public void show() {
        if(!hiding){
            return;
        }

        hiding = false;
    }
}
