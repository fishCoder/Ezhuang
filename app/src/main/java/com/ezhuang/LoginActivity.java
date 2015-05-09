package com.ezhuang;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.LoginBackground;
import com.ezhuang.common.enter.SimpleTextWatcher;
import com.ezhuang.common.third.FastBlur;
import com.ezhuang.model.AccountInfo;
import com.ezhuang.model.StaffUser;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.jpush.android.api.JPushInterface;

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

        if (background == null) {
            LoginBackground.PhotoItem photoItem = new LoginBackground(this).getPhoto();
            File file = photoItem.getCacheFile(this);
            if (file.exists()) {
                background = Uri.fromFile(file);
            }
        }

        try { // TODO 图片载入可能失败，因为图片没有下载完
            BitmapDrawable bitmapDrawable;
            if (background == null) {
                bitmapDrawable = createBlur();
            } else {
                bitmapDrawable = createBlur(background);
            }
            backgroundImage.setImageDrawable(bitmapDrawable);
        } catch (Exception e) {}

        editName.addTextChangedListener(textWatcher);
        editPassword.addTextChangedListener(textWatcher);
        editValify.addTextChangedListener(textWatcher);
        upateLoginButton();
        editName.addTextChangedListener(textWatcherName);

    }

    private BitmapDrawable createBlur() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.mipmap.entrance1, options);
        int height = options.outHeight;
        int width = options.outWidth;

        options.outHeight = (int) (height / scaleFactor);
        options.outWidth = (int) (width / scaleFactor);
        options.inSampleSize = (int) (scaleFactor + 0.5);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inMutable = true;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.entrance1, options);
        Bitmap blurBitmap = FastBlur.doBlur(bitmap, (int) radius, true);

        return new BitmapDrawable(getResources(), blurBitmap);
    }

    private BitmapDrawable createBlur(Uri uri) {
        String path = Global.getPath(this, uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int height = options.outHeight;
        int width = options.outWidth;

        options.outHeight = (int) (height / scaleFactor);
        options.outWidth = (int) (width / scaleFactor);
        options.inSampleSize = (int) (scaleFactor + 0.5);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inMutable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        Bitmap blurBitmap = FastBlur.doBlur(bitmap, (int) radius, true);

        return new BitmapDrawable(getResources(), blurBitmap);
    }

    @Override
    public void onBackPressed() {
        finish();
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
            params.put("phone", name);
            params.put("password", password);
            params.put("registerid",JPushInterface.getRegistrationID(this));
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

    final int RESULT_REQUEST_USERINFO = 21;
    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

        if(tag.equals(HOST_LOGIN)){
            if(code==10001){
                StaffUser currentUser = JsonUtil.Json2Object(respanse.getString("data"), StaffUser.class);
                //TODO 保存登陆用户
                MyApp.currentUser = currentUser;
                showProgressBar(false);
                AccountInfo.saveAccount(LoginActivity.this,currentUser);
//                UserDetailActivity_
//                        .intent(this)
//                        .globalKey(MyApp.currentUser.getGlobal_key())
//                        .companyType(MyApp.currentUser.getCompanyType())
//                        .startForResult(RESULT_REQUEST_USERINFO);

                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                showMiddleToast("密码或账号不正确");
                showProgressBar(false);
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

