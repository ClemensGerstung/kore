package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import com.typingsolutions.passwordmanager.R;

public class EditText extends android.widget.EditText {

    private static final int FAST_ANIMATION_DURATION = 250;

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
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EditText, defStyleAttr, 0);

        final float elevation = a.getDimension(R.styleable.EditText_support_elevation, 2.f);
        final float translationZ = a.getDimension(R.styleable.EditText_support_translationZ, 3.f);

        a.recycle();

        editText = new EditTextImpl();
        editText.initShadow();
        editText.init(this, context, Color.WHITE, 8.f, elevation, elevation + translationZ);
    }

    public void setShadowPadding(int left, int top, int right, int bottom) {
        shadowPadding.set(left, top, right, bottom);
        super.setPadding(left + getPaddingLeft(), top + getPaddingTop(), right + getPaddingRight(), bottom + getPaddingBottom());
    }

    public synchronized void hide() {
        if (hiding || getVisibility() != View.VISIBLE) {
            return;
        }

        Animation anim = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.design_fab_out);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(FAST_ANIMATION_DURATION);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {
                hiding = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hiding = false;
                setVisibility(View.GONE);
            }
        });
        startAnimation(anim);

        animateParent(anim);
    }

    public void show() {
        if (getVisibility() != View.VISIBLE || hiding) {
            clearAnimation();
            setVisibility(View.VISIBLE);
            Animation anim = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.design_fab_in);
            anim.setDuration(FAST_ANIMATION_DURATION);
            anim.setInterpolator(new BounceInterpolator());

            startAnimation(anim);

            animateParent(anim);
        }
    }

    private void animateParent(Animation anim) {
        ViewParent parent = this.getParent();
        if (parent != null) {
            if (parent instanceof TextInputLayout) {
                TextInputLayout activeParent = (TextInputLayout) parent;
                activeParent.startAnimation(anim);
            }
        }
    }
}