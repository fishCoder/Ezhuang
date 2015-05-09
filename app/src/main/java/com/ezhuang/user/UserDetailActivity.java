package com.ezhuang.user;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezhuang.common.ClickSmallImage;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.Role;
import com.ezhuang.model.StaffUser;
import com.ezhuang.project.detail.ViewProjectActivity_;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import com.ezhuang.BaseActivity;
import com.ezhuang.MyApp;
import com.ezhuang.R;
import com.ezhuang.common.Global;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.json.JSONException;
import org.json.JSONObject;


@EActivity(R.layout.activity_user_detail)
public class UserDetailActivity extends BaseActivity {

    StaffUser staffUser;

    @Extra
    String globalKey;

    @Extra
    Integer companyType;

    @ViewById
    ImageView icon;

    @ViewById
    TextView name;

    @ViewById
    View sendMessage;

    @ViewById
    View icon_sharow;

    @ViewById
    CheckBox followCheckbox;

    @ViewById
    ImageView userBackground;

    @ViewById
    ImageView sex;

    @StringArrayRes
    String[] user_detail_activity_list_first;

    @StringArrayRes
    String[] user_detail_list_first;

    String[] user_detail_list_second;

    boolean isMe = false;

    @AfterViews
    void init() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.sendMessageLayout).setVisibility(View.GONE);

        initListFirst();

        if (globalKey != null) {
            if (globalKey.equals(MyApp.currentUser.getGlobal_key())) {
                isMe = true;
                setTitleMyPage();
                resizeHead();


            }

            getNetwork(HOST_USER_INFO + "?global_key=" + globalKey + "&companyType=" + companyType, HOST_USER_INFO);
        } else {
            String globalKey = getIntent().getData().getQueryParameter("globalKey");
            String companyType = getIntent().getData().getQueryParameter("companyType");
            if (name.equals(MyApp.currentUser.getName())) {
                setTitleMyPage();
                resizeHead();
            }

            getNetwork(HOST_USER_INFO + "?global_key=" + globalKey + "&companyType=" + companyType, HOST_USER_INFO);
        }

        userBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams lp = userBackground.getLayoutParams();
                if (lp.width > 0) {
                    lp.height = lp.width * 560 / 1080;
                    userBackground.setLayoutParams(lp);
                    userBackground.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            staffUser = (StaffUser) savedInstanceState.getSerializable("mUserObject");
            isMe = savedInstanceState.getBoolean("isMe", false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (staffUser != null) {
            outState.putSerializable("mUserObject", staffUser);
            outState.putBoolean("isMe", isMe);
        }
    }

    private void setTitleMyPage() {
        ((TextView) findViewById(R.id.titleProject)).setText("我的项目");
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        if (isMe) {
//            inflater.inflate(R.menu.user_detail_me, menu);
//        } else {
//            inflater.inflate(R.menu.user_detail, menu);
//        }
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @OptionsItem
    void action_edit() {
//        UserDetailEditActivity_
//                .intent(this)
//                .startForResult(RESULT_EDIT);
    }

    @OptionsItem
    public final void action_more_detail() {
//        UserDetailMoreActivity_.intent(this)
//                .mUserObject(mUserObject)
//                .start();
    }

    @OptionsItem
    public final void action_copy_link() {
//        String link = Global.HOST + mUserObject.path;
//        Global.copy(this, link);
//        showButtomToast("已复制链接 " + link);
    }

    public final int RESULT_EDIT = 0;

    @OnActivityResult(RESULT_EDIT)
    void onResult() {
        getNetwork(HOST_USER_INFO + staffUser.getGlobal_key(), HOST_USER_INFO);
    }

    private void resizeHead() {
        isMe = true;
        invalidateOptionsMenu();

        followCheckbox.setVisibility(View.GONE);

    }




    int sexs[] = new int[]{
            R.mipmap.ic_sex_boy,
            R.mipmap.ic_sex_girl,
            android.R.color.transparent
    };

    void displayUserinfo() {
        String dayToNow = staffUser.getCreateTime();
        user_detail_list_second = new String[]{
                dayToNow,
                dayToNow,
                staffUser.getGlobal_key(),
                staffUser.getCompanyName(),
                "项目经理",
                "",
                ""
        };

        if(staffUser.getAvatar()!=null){
            iconfromNetwork(icon, staffUser.getAvatar(), new AnimateFirstDisplayListener());
            icon.setOnClickListener(new ClickSmallImage(this));
        }else {
            icon.setImageResource(R.mipmap.ic_default_user);
        }

//        icon.setTag(new MaopaoListFragment.ClickImageParam(mUserObject.avatar));


        sex.setImageResource(sexs[0]);

        name.setText(staffUser.getName());



        TextView fans = (TextView) findViewById(R.id.companyTxt);
        fans.setText(staffUser.getCompanyName());



        setListData();
    }

    private void initListFirst() {
        for (int i = 0; i < items.length; ++i) {
            View parent = findViewById(items[i]);
            TextView first = (TextView) parent.findViewById(R.id.first);
            first.setText(user_detail_activity_list_first[i]);
        }
    }

    private final int[] items = new int[]{
            R.id.pos0,
            R.id.pos1,
            R.id.pos2
    };

    private void setListData() {
        String roles = "";
        if(staffUser.getRoles()!=null)
        for(Role role:staffUser.getRoles()){
            roles += role.getRoleName() + " ";
        }

        String[] secondContents = new String[]{
                roles,
                staffUser.getPhone(),
                staffUser.getCreateTime()
        };

        for (int i = 0; i < items.length; ++i) {
            View parent = findViewById(items[i]);
            TextView second = (TextView) parent.findViewById(R.id.second);
            String contentString = secondContents[i];
            if (contentString.isEmpty()) {
                contentString = "未填写";
                second.setTextColor(getResources().getColor(R.color.font_gray));
            }

            second.setText(contentString);
        }
    }


    private SpannableString createSpan(String s) {
        SpannableString itemContent = new SpannableString(s);
        final ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.font_green));
        itemContent.setSpan(colorSpan, 0, itemContent.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return itemContent;
    }



    final String HOST_USER_INFO = Global.HOST + "/app/stf/queryInfo.do";

    boolean mNeedUpdate = false;

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if (tag.equals(HOST_USER_INFO)) {
            if (code == NetworkImpl.REQ_SUCCESSS) {
                staffUser = JsonUtil.Json2Object(respanse.getString("data"),StaffUser.class);
                displayUserinfo();
            } else {
                showButtomToast("获取用户信息错误");
                onBackPressed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(mNeedUpdate ? RESULT_OK : RESULT_CANCELED);
        super.onBackPressed();
    }

    @OptionsItem(android.R.id.home)
    void close() {
        onBackPressed();
    }

    @Click
    public void clickProject() {
        if(isMe){
            ViewProjectActivity_.intent(UserDetailActivity.this).roleId(Global.PROJECT_MANAGER).staffId(MyApp.currentUser.getGlobal_key()).start();
        }else{
//            ViewProjectActivity_.intent(UserDetailActivity.this).roleId(Global.STAFF).staffId(MyApp.currentUser.getGlobal_key()).start();
        }
    }



    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                ((View) imageView.getParent()).setVisibility(View.VISIBLE);
                FadeInBitmapDisplayer.animate((View) imageView.getParent(), 300);
            }
        }
    }

}
