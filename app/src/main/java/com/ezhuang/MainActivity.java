package com.ezhuang;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.LoginBackground;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.AccountInfo;
import com.ezhuang.model.StaffUser;
import com.loopj.android.http.RequestParams;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.readystatesoftware.viewbadger.BadgeView;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;


public class MainActivity extends BaseActivity {


    View layout_home;
    View layout_message;
    View layout_settings;

    BadgeView badgeView;

    View[] layouts;

    ImageView btn_home;
    ImageView btn_message;
    ImageView btn_settings;

    ImageView[] btns;

    int[] icon = new int[]{R.mipmap.icon_home,R.mipmap.icon_message,R.mipmap.icon_settings};
    int[] icon_active = new int[]{R.mipmap.icon_home_active,R.mipmap.icon_message_active,R.mipmap.icon_settings_active};

    FragmentHome     fragmentHome;
    FragmentMessage  fragmentMessage;
    FragmentSettings fragmentSettings;

    Fragment[] fragments;

    String HOST_LOGIN = Global.HOST + "/app/stf/login.do";

    String QUERY_BADGE = Global.HOST + "/app/res/queryBadge.do";


    //Tab选项卡的文字
    private String mTextviewArray[] = {"首页", "消息", "设置"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_);

        initView();
        updateVersion();
    }

    void updateVersion(){
        String appId ="b87e159131abde328237831cd3902aad"; //蒲公英注册或上传应用获取的AppId
        PgyUpdateManager.register(this, appId);
    }

    /**
     * 初始化组件
     */
    private void initView(){
        registerBoradcastReceiver();

        Intent intent = new Intent(this, UpdateService.class);
        intent.putExtra(UpdateService.EXTRA_BACKGROUND, true);
        intent.putExtra(UpdateService.EXTRA_WIFI, true);
        intent.putExtra(UpdateService.EXTRA_DEL_OLD_APK, true);
        startService(intent);

        StaffUser staffUser = AccountInfo.loadAccount(this);
        MyApp.currentUser = staffUser;
        if(!getIntent().getBooleanExtra("isLogin",false)){

            RequestParams params = new RequestParams();
            params.put("phone", staffUser.getPhone());
            params.put("password", staffUser.getPassword());
            params.put("registerId", JPushInterface.getRegistrationID(this));
            params.put("equType","2");
            postNetwork(HOST_LOGIN, params, HOST_LOGIN);

        }



        fragmentHome = FragmentHome_.builder().build();
        fragmentMessage = FragmentMessage_.builder().build();
        fragmentSettings = FragmentSettings_.builder().build();

        layout_home = findViewById(R.id.layout_home);
        layout_message = findViewById(R.id.layout_message);
        layout_settings = findViewById(R.id.layout_settings);

        layout_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFragment(0);
            }
        });

        layout_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFragment(1);
            }
        });

        layout_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActiveFragment(2);
            }
        });

        btn_home = (ImageView) findViewById(R.id.btn_home);
        btn_message = (ImageView) findViewById(R.id.btn_message);
        btn_settings = (ImageView) findViewById(R.id.btn_settings);

        badgeView = (BadgeView) findViewById(R.id.badge);
        badgeView.setVisibility(View.GONE);
        layouts = new View[]{layout_home,layout_message,layout_settings};
        btns = new ImageView[]{btn_home,btn_message,btn_settings};
        fragments = new Fragment[]{fragmentHome,fragmentMessage,fragmentSettings};

        if(getIntent().getBooleanExtra("from_notify",false)){
            setActiveFragment(1);
        }else{
            setActiveFragment(0);
        }

    }


    void setActiveFragment(int index){

        for (int i=0;i<layouts.length;i++){
            layouts[i].setBackgroundColor(getResources().getColor(R.color.white));
            btns[i].setImageResource(icon[i]);
        }

        layouts[index].setBackgroundColor(getResources().getColor(R.color.white_pressed));
        btns[index].setImageResource(icon_active[index]);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragments[index]).commit();
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
        if(QUERY_BADGE.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                int count = respanse.getInt("data");
                Log.i("data",respanse.toString());
                if(count==0){
                    badgeView.setVisibility(View.GONE);
                }else{
                    badgeView.setVisibility(View.VISIBLE);
                    badgeView.setText(""+count);
                }
            }else{

            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
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

    boolean isActive =  false;

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
        getNetwork(QUERY_BADGE,QUERY_BADGE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    public void registerBoradcastReceiver(){
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Global.PUSH_BROADCAST);
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Global.PUSH_BROADCAST)){
                if(isActive){
                    setActiveFragment(1);
                    fragmentMessage.refreshData();
                }
            }
        }

    };

}
