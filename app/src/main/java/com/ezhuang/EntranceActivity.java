package com.ezhuang;


import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

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


    @AfterViews
    void init() {


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

    void next(){
        Intent intent;
        intent = new Intent(this,LoginActivity_.class);
        startActivity(intent);
        finish();

        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }
}
