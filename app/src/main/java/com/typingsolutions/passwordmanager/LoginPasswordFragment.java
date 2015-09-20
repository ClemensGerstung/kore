package com.typingsolutions.passwordmanager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.EditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.typingsolutions.passwordmanager.callbacks.LoginCallback;
import com.typingsolutions.passwordmanager.callbacks.ShowEnterPasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.textwatcher.SimpleSwitchTextWatcher;
import core.UserProvider;


public class LoginPasswordFragment extends Fragment {

    private EditText password;
    private CheckBox safeLogin;
    private LinearLayout notUser;
    private ImageView background;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity();
        LoginActivity loginActivity = (LoginActivity) getActivity();

        try {
            password.addTextChangedListener(new SimpleSwitchTextWatcher(context, loginActivity, LoginCallback.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_password_layout, container, false);

        background = (ImageView) view.findViewById(R.id.loginpasswordlayout_imageview_background);
        notUser = (LinearLayout) view.findViewById(R.id.loginpasswordlayout_linearlayout_notuser);
        password = (EditText) view.findViewById(R.id.loginpasswordlayout_edittext_password);
        safeLogin = (CheckBox) view.findViewById(R.id.loginpasswordlayout_checkbox_safelogin);

        final TextView username = (TextView) view.findViewById(R.id.loginpasswordlayout_textview_bonjourname);
        final TextView notUserName = (TextView) notUser.findViewById(R.id.loginpasswordlayout_textview_notuser);
        replaceTemplate(username);
        replaceTemplate(notUserName);


        return view;
    }

    private void replaceTemplate(TextView textView){
        String text = textView.getText().toString();
        text = text.replace("{User.Name}", UserProvider.getInstance(getActivity()).getUsername());
        textView.setText(text);
    }

}
