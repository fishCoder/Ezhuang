package com.ezhuang;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.enter.SimpleTextWatcher;
import com.ezhuang.model.CurrentUser;
import com.loopj.android.http.RequestParams;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/4/3 0003.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @Extra
    Uri background;

    @ViewById
    ImageView userIcon;

    @ViewById
    ImageView backgroundImage;

    @ViewById
    EditText editName;

    @ViewById
    EditText editPassword;

    @ViewById
    ImageView imageValify;

    @ViewById
    EditText editValify;

    @ViewById
    View captchaLayout;

    @ViewById
    View loginButton;

    final float radius = 8;
    final double scaleFactor = 16;

    String HOST_LOGIN = Global.HOST + "/app/stf/login.do";

    @AfterViews
    void init(){
        editName.addTextChangedListener(textWatcher);
        editPassword.addTextChangedListener(textWatcher);
        editValify.addTextChangedListener(textWatcher);
        upateLoginButton();

        editName.addTextChangedListener(textWatcherName);
    }

    final private int RESULT_CLOSE = 100;
    @OnActivityResult(RESULT_CLOSE)
    void resultRegiter(int result) {
        if (result == Activity.RESULT_OK) {
            finish();
        }
    }

    @Click
    protected final void loginButton() {
        try {
            String name = editName.getText().toString();
            String password = editPassword.getText().toString();
            String captcha = editValify.getText().toString();

            if (name.isEmpty()) {
                showMiddleToast("用户名不能为空");
                return;
            }

            if (password.isEmpty()) {
                showMiddleToast("密码不能为空");
                return;
            }

            RequestParams params = new RequestParams();
            params.put("userName", name);
            params.put("passWord", password);
            if (captchaLayout.getVisibility() == View.VISIBLE) {
                params.put("j_captcha", captcha);
            }
            //params.put("remember_me", true);

            postNetwork(HOST_LOGIN, params, HOST_LOGIN);
            showProgressBar(true);

        } catch (Exception e) {
            Global.errorLog(e);
        }
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(tag.equals(HOST_LOGIN)){
            if(code==0){
                CurrentUser currentUser = JsonUtil.Json2Object(respanse.getString("data"), CurrentUser.class);
                currentUser.save();
                showProgressBar(true);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }else{
                showProgressBar(false);
                showErrorMsg(code, respanse);
            }

        }

    }


    @FocusChange
    void editName(boolean hasFocus) {
        if (hasFocus) {
            return;
        }

        String name = editName.getText().toString();
        if (name.isEmpty()) {
            return;
        }

//        String global = AccountInfo.loadRelogininfo(this, name);
//        if (global.isEmpty()) {
//            return;
//        }
//
//        getNetwork(String.format(HOST_USER, global), HOST_USER_RELOGIN);
    }

    TextWatcher textWatcher = new SimpleTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            upateLoginButton();
        }
    };

    TextWatcher textWatcherName = new SimpleTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            userIcon.setImageResource(R.mipmap.icon_user_monkey_circle);
        }
    };

    private void upateLoginButton() {
        if (editName.getText().length() == 0) {
            loginButton.setEnabled(false);
            return;
        }

        if (editPassword.getText().length() == 0) {
            loginButton.setEnabled(false);
            return;
        }

        if (captchaLayout.getVisibility() == View.VISIBLE &&
                editValify.getText().length() == 0) {
            loginButton.setEnabled(false);
            return;
        }

        loginButton.setEnabled(true);
    }
}

