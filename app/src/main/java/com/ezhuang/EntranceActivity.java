package com.ezhuang;


import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;


import com.ezhuang.common.LoginBackground;
import com.ezhuang.model.AccountInfo;
import com.ezhuang.model.StaffUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

import java.io.File;

/**
 * Created by Administrator on 2015/4/2 0002.
 */
@EActivity(R.layout.entrance_image)
public class EntranceActivity extends BaseActivity {

    @ViewById
    ImageView image;

    @ViewById
    TextView title;

    @ViewById
    View mask;

    @ViewById
    View logo;

    @AnimationRes
    Animation entrance;

    Uri background = null;

    @AfterViews
    void init() {

        LoginBackground.PhotoItem photoItem = new LoginBackground(this).getPhoto();
        File file = photoItem.getCacheFile(this);
        if (file.exists()) {
            background = Uri.fromFile(file);
            image.setImageURI(background);
            title.setText(photoItem.getTitle());

            if (photoItem.isGuoguo()) {
                hideLogo();
            }
        }

        if(AccountInfo.isLogin(this)){
            StaffUser staffUser = AccountInfo.loadAccount(this);
            //TODO 免登陆
        }

        entrance.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                next();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        image.startAnimation(entrance);
    }

    private void hideLogo() {
        mask.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        logo.setVisibility(View.GONE);
    }

    void next(){
        Intent intent;
        intent = new Intent(this,LoginActivity_.class);
        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }
}
