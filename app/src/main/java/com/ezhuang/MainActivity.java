package com.ezhuang;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.LoginBackground;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.AccountInfo;
import com.ezhuang.model.StaffUser;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends BaseActivity {
    //定义FragmentTabHost对象
    private FragmentTabHost mTabHost;

    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {FragmentHome_.class,FragmentMessage_.class,FragmentSettings_.class};

    //定义数组来存放按钮图片
    private int mImageViewArray[] = {
            R.drawable.tab_home_btn,
            R.drawable.tab_msg_btn,
            R.drawable.tab_set_btn
    };

    String HOST_LOGIN = Global.HOST + "/app/stf/login.do";

    //Tab选项卡的文字
    private String mTextviewArray[] = {"首页", "消息", "设置"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }




    /**
     * 初始化组件
     */
    private void initView(){

        StaffUser staffUser = AccountInfo.loadAccount(this);
        MyApp.currentUser = staffUser;
        RequestParams params = new RequestParams();
        params.put("phone", staffUser.getPhone());
        params.put("password", staffUser.getPassword());
        postNetwork(HOST_LOGIN, params, HOST_LOGIN);

        LoginBackground loginBackground = new LoginBackground(this);
        loginBackground.update();

        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        //得到fragment的个数
        int count = fragmentArray.length;

        for(int i = 0; i < count; i++){
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);

            //设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index){
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

        if(tag.equals(HOST_LOGIN)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                StaffUser currentUser = JsonUtil.Json2Object(respanse.getString("data"), StaffUser.class);
                MyApp.currentUser = currentUser;
                AccountInfo.saveAccount(MainActivity.this, currentUser);

            }else{
                toLoginActivity();
            }

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            FragmentMessage fragmentMessage = (FragmentMessage) getSupportFragmentManager()
                    .findFragmentByTag("消息");
            fragmentMessage.updateMsgState(requestCode,data.getIntExtra("msg_state",1));
        }
    }

    void toLoginActivity(){
        Intent intent;
        intent = new Intent(this,LoginActivity_.class);
        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    private long exitTime = 0;

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            showButtomToast("再按一次退出");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
