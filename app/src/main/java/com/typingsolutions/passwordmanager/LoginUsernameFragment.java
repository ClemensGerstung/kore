package com.typingsolutions.passwordmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.EditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import com.typingsolutions.passwordmanager.callbacks.ShowEnterPasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.textwatcher.SimpleSwitchTextWatcher;

import java.lang.reflect.InvocationTargetException;

public class LoginUsernameFragment extends Fragment {

    private ImageView background;
    private EditText username;
    private TextInputLayout usernameWrapper;
    private CheckBox remember;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity();
        LoginActivity loginActivity = (LoginActivity) getActivity();

        try {
            username.addTextChangedListener(new SimpleSwitchTextWatcher(context, loginActivity, ShowEnterPasswordCallback.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.login_username_layout, container, false);

        background = (ImageView) view.findViewById(R.id.loginusernamelayout_imageview_background);
        username = (EditText) view.findViewById(R.id.loginusernamelayout_edittext_username);
        usernameWrapper = (TextInputLayout) view.findViewById(R.id.loginusernamelayout_textinputlayout_wrapper);
        remember = (CheckBox) view.findViewById(R.id.loginusernamelayout_checkbox_remember);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


}