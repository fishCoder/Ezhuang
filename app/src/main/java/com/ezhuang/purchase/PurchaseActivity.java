package com.ezhuang.purchase;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.ListModify;
import com.ezhuang.model.SpMaterial;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;

/**
 * Created by Administrator on 2015/4/24 0024.
 */
@EActivity(R.layout.activity_sp_purchase)
public class PurchaseActivity extends BaseActivity {

    @Extra
    SpMaterial spMaterial;

    SelfBuyFragment selfBuyFragment;

    ActionBar actionBar;

    @AfterViews
    void init(){

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        changeToSelfBuy();

        selfBuyFragment = SelfBuyFragment_.builder().build();
        selfBuyFragment.setMaterial(spMaterial);

        getSupportFragmentManager().beginTransaction().add(R.id.container,selfBuyFragment).commit();
    }

    void changeToSelectBmbMaterial(){
        actionBar.setCustomView(R.layout.spinner_actionbar);
    }

    void changeToSelfBuy(){
        actionBar.setCustomView(R.layout.self_buy_actionbar);
        findViewById(R.id.action_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent();
            if(!selfBuyFragment.fill(spMaterial)){
                return;
            }
            Log.i("spMaterial", JsonUtil.Object2Json(spMaterial));
            intent.putExtra("sp",spMaterial);
            setResult(RESULT_OK, intent);
            finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ListModify.Add){
            if(resultCode == Activity.RESULT_OK){
                int row = data.getIntExtra("row",0);
                String value = data.getStringExtra("itemValue");
                selfBuyFragment.setRowValue(row,value);
            }
        }
    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }
}
