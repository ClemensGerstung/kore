package com.typingsolutions.passwordmanager.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.EditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.typingsolutions.passwordmanager.R;
import com.typingsolutions.passwordmanager.activities.LoginActivity;
import com.typingsolutions.passwordmanager.callbacks.ShowEnterPasswordCallback;
import com.typingsolutions.passwordmanager.callbacks.textwatcher.SimpleSwitchTextWatcher;

public class LoginUsernameFragment extends Fragment {

    public final static String REMEMBERED_USERNAME = "REMEMBERED_USERNAME";
    public final static String REMEMBER = "REMEMBER";

    private ImageView background;
    private EditText username;
    private TextInputLayout usernameWrapper;
    private CheckBox remember;


    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();

            editor.putBoolean(REMEMBER, isChecked);
            editor.putString(REMEMBERED_USERNAME, isChecked ? username.getText().toString() : "");

            editor.apply();
        }
    };


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getActivity();
        LoginActivity loginActivity = (LoginActivity) getActivity();

        final SharedPreferences preferences = loginActivity.getPreferences(Context.MODE_PRIVATE);
        final boolean checked = preferences.getBoolean(REMEMBER, false);
        final String rememberedName = preferences.getString(REMEMBERED_USERNAME, "");

        remember.setChecked(checked);
        remember.setOnCheckedChangeListener(checkedChangeListener);


        try {
            username.addTextChangedListener(new SimpleSwitchTextWatcher(context, loginActivity, ShowEnterPasswordCallback.class));
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), String.format("%s: %s", e.getClass().getSimpleName(), e.getMessage()));
        }

        username.setText(checked ? rememberedName : "");
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
}