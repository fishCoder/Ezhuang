package com.ezhuang;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.enter.SimpleTextWatcher;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.AccountInfo;
import com.ezhuang.model.StaffUser;
import com.loopj.android.http.RequestParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/5/20 0020.
 */
@EActivity(R.layout.activity_alter_password)
public class AlterPasswordActivity extends BaseActivity {

    @ViewById
    EditText edit_password;
    @ViewById
    EditText edit_repassword;
    @ViewById
    Button confirm_button;
    @Extra
    String phone;
    @Extra
    String oldPwd;

    String UPDATE_USER_PWD = Global.HOST + "/app/stf/updateUserPwd.do";

    @AfterViews
    void init(){
        edit_password.addTextChangedListener(textWatcher);
        edit_repassword.addTextChangedListener(textWatcher);

        upateLoginButton();

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edit_password.getText().toString();
                String repassword = edit_repassword.getText().toString();

                if(!password.equals(repassword)){
                    showButtomToast("密码不一致");
                    return;
                }

                RequestParams params = new RequestParams();
                params.put("phone",phone);
                params.put("oldPwd",oldPwd);
                params.put("newPwd",password);

                postNetwork(UPDATE_USER_PWD,params,UPDATE_USER_PWD);
            }
        });
    }

    TextWatcher textWatcher = new SimpleTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            upateLoginButton();
        }
    };

    private void upateLoginButton() {

        if (edit_password.getText().length() < 6) {
            confirm_button.setEnabled(false);
            return;
        }

        if (edit_repassword.getText().length() < 6) {
            confirm_button.setEnabled(false);
            return;
        }

        confirm_button.setEnabled(true);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(UPDATE_USER_PWD.equals(UPDATE_USER_PWD)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                StaffUser currentUser = JsonUtil.Json2Object(respanse.getString("data"), StaffUser.class);
                MyApp.currentUser = currentUser;
                AccountInfo.saveAccount(AlterPasswordActivity.this, currentUser);
                showButtomToast("更改密码成功");
                Intent intent = new Intent(AlterPasswordActivity.this,MainActivity.class);
                intent.putExtra("isLogin",true);
                startActivity(intent);
                finish();
            }else{
                showButtomToast("失败");
            }
        }
    }
}
