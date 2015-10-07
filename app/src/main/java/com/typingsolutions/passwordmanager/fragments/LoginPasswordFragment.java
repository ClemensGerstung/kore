package com.typingsolutions.passwordmanager.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.EditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.callbacks.LoginCallback;
import com.typingsolutions.passwordmanager.callbacks.service.GetLockTimeServiceCallback;
import com.typingsolutions.passwordmanager.callbacks.textwatcher.SimpleSwitchTextWatcher;
import core.UserProvider;
import ui.OutlinedImageView;


public class LoginPasswordFragment extends Fragment {


    public static final long FAST_ANIMATION_DURATION = 150;
    private EditText password;
    private CheckBox safeLogin;
    private LinearLayout notUser;
    private OutlinedImageView background;
    private LoginActivity loginActivity;

    private GetLockTimeServiceCallback serviceCallback;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity();
        Activity activity = getActivity();

        if (activity instanceof LoginActivity) {
            loginActivity = (LoginActivity) activity;
        }

        try {
            password.addTextChangedListener(new SimpleSwitchTextWatcher(context, loginActivity, LoginCallback.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: show BlockedView if blocked
        View view = inflater.inflate(R.layout.login_password_layout, container, false);

        background = (OutlinedImageView) view.findViewById(R.id.loginpasswordlayout_imageview_background);
        notUser = (LinearLayout) view.findViewById(R.id.loginpasswordlayout_linearlayout_notuser);
        password = (EditText) view.findViewById(R.id.loginpasswordlayout_edittext_password);
        safeLogin = (CheckBox) view.findViewById(R.id.loginpasswordlayout_checkbox_safelogin);

        serviceCallback = new GetLockTimeServiceCallback(background);

        final TextView username = (TextView) view.findViewById(R.id.loginpasswordlayout_textview_bonjourname);
        final TextView notUserName = (TextView) notUser.findViewById(R.id.loginpasswordlayout_textview_notuser);
        replaceTemplate(username);
        replaceTemplate(notUserName);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            loginActivity.getLoginServiceRemote().registerCallback(serviceCallback);
        } catch (RemoteException ignored) {
        }
    }

    @Override
    public void onPause() {
        try {
            loginActivity.getLoginServiceRemote().unregisterCallback(serviceCallback);
        } catch (RemoteException ignored) {
        }
        super.onPause();
    }

    private void replaceTemplate(TextView textView) {
        String text = textView.getText().toString();
        text = text.replace("{User.Name}", UserProvider.getInstance(getActivity()).getUsername());
        textView.setText(text);
    }

    public void retypePassword() {
        password.setText("");
        password.requestFocus();
    }

    public OutlinedImageView getBackground() {
        return background;
    }

    public synchronized void showHide(final View view) {
        if (view.getVisibility() != View.VISIBLE) {
            view.clearAnimation();
            view.setVisibility(View.VISIBLE);
            Animation anim = android.view.animation.AnimationUtils.loadAnimation(getActivity(), R.anim.checkbox_show);
            anim.setDuration(FAST_ANIMATION_DURATION);
            anim.setInterpolator(new DecelerateInterpolator());

            view.startAnimation(anim);
            return;
        }

        Animation anim = android.view.animation.AnimationUtils.loadAnimation(getActivity(), R.anim.checkbox_hide);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.setDuration(FAST_ANIMATION_DURATION);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }
        });
        view.startAnimation(anim);
    }
}
