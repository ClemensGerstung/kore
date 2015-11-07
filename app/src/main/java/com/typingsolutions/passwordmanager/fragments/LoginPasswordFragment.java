package com.typingsolutions.passwordmanager.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.EditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.callbacks.LoginCallback;
import com.typingsolutions.passwordmanager.callbacks.ShowEnterUsernameCallback;
import com.typingsolutions.passwordmanager.callbacks.textwatcher.SimpleSwitchTextWatcher;
import core.data.UserProvider;
import ui.OutlinedImageView;


public class LoginPasswordFragment extends Fragment {

    public static final long FAST_ANIMATION_DURATION = 150;
    public static final String SAFELOGIN = "com.typingsolutions.passwordmanager.fragments.LoginPasswordFragment.SAFELOGIN";

    protected EditText password;
    private CheckBox safeLogin;
    private OutlinedImageView background;
    protected LoginActivity loginActivity;
    private TextView notUser;
    private CardView notUserBackground;

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();

            editor.putBoolean(SAFELOGIN, isChecked);

            editor.apply();
        }
    };

    private final Runnable invalidateBackgroundRunnable = new Runnable() {
        @Override
        public void run() {
            background.invalidate();
        }
    };
    protected SimpleSwitchTextWatcher watcher;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();

        if (activity instanceof LoginActivity) {
            loginActivity = (LoginActivity) activity;

            notUserBackground.setOnClickListener(new ShowEnterUsernameCallback(activity, loginActivity));

            try {
                watcher = new SimpleSwitchTextWatcher(activity, loginActivity, LoginCallback.class);
                password.addTextChangedListener(watcher);
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), e.getMessage());
            }
        }

        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final boolean checked = preferences.getBoolean(SAFELOGIN, false);

        safeLogin.setChecked(checked);
        safeLogin.setOnCheckedChangeListener(checkedChangeListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_password_layout, container, false);

        background = (OutlinedImageView) view.findViewById(R.id.loginpasswordlayout_imageview_background);
        password = (EditText) view.findViewById(R.id.loginpasswordlayout_edittext_password);
        safeLogin = (CheckBox) view.findViewById(R.id.loginpasswordlayout_checkbox_safelogin);
        notUser = (TextView) view.findViewById(R.id.loginpasswordlayout_textview_notuser);
        notUserBackground = (CardView) view.findViewById(R.id.loginpasswordlayout_cardview_notuser);

        safeLogin.setTag(R.string.hidden, false);

        final TextView username = (TextView) view.findViewById(R.id.loginpasswordlayout_textview_bonjourname);

        replaceTemplate(username);
        replaceTemplate(notUser);

        return view;
    }

    public void hideAllInputs() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hide(safeLogin, R.anim.checkbox_hide);
                password.hide();
                background.invalidate();
            }
        });
    }

    public void showAllInputs() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                show(safeLogin, R.anim.checkbox_show);
                password.show();
                background.invalidate();
            }
        });

    }

    private void replaceTemplate(TextView textView) {
        String text = textView.getText().toString();
        text = text.replace("{User.Name}", UserProvider.getInstance(getActivity()).getUsername());
        textView.setText(text);
    }

    public void retypePassword() {
        clearEditText();
        password.requestFocus();
    }

    public void clearEditText() {
        if(password == null) return;
        password.setText("");
    }

    public OutlinedImageView getBackground() {
        return background;
    }

    public void redrawBlockedbackground() {
        loginActivity.runOnUiThread(invalidateBackgroundRunnable);
    }

    public synchronized void show(final View view, @AnimRes int animation) {
        boolean hiding = (boolean) view.getTag(R.string.hidden);
        if (hiding || view.getVisibility() != View.VISIBLE) {
            view.clearAnimation();
            view.setVisibility(View.VISIBLE);
            Animation anim = android.view.animation.AnimationUtils.loadAnimation(getActivity(), animation);
            anim.setDuration(FAST_ANIMATION_DURATION);
            anim.setInterpolator(new DecelerateInterpolator());

            view.startAnimation(anim);
        }
    }

    public synchronized void hide(final View view, @AnimRes int animation) {
        boolean hiding = (boolean) view.getTag(R.string.hidden);
        if (hiding || view.getVisibility() != View.VISIBLE) return;

        Animation anim = android.view.animation.AnimationUtils.loadAnimation(getActivity(), animation);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(FAST_ANIMATION_DURATION);
        anim.setAnimationListener(new Animation.AnimationListener() {
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
        });
        view.startAnimation(anim);
    }
}
