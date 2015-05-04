package com.ezhuang.project.detail;

import android.content.Intent;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.MyApp;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.model.StaffUser;
import com.ezhuang.project.ViewBillDetailActivity;
import com.ezhuang.purchase.SelfBuyFragment;
import com.ezhuang.quality.ProgressDetailActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Administrator on 2015/4/7 0007.
 */
@EActivity(R.layout.activity_project_edit)
public class SetProjectInfo extends BaseActivity {

    @Extra("title")
    String title;

    @Extra("row")
    int row;

    @Extra("rowValue")
    String rowValue;

    StaffUser user;

    @ViewById
    TextView value;


    @AfterViews
    void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        user = MyApp.currentUser;
        final String hintFormat = "请输入%s";
        value.setHint(String.format(hintFormat, title));
        value.setText(getRowValue());
        value.requestFocus();

        if(row==CreatProjectActivity.O_PHONE){
            value.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    private String getRowValue() {
        String returnValue = rowValue;
        if(rowValue==null){
            returnValue = "";
        }
        return returnValue;
    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }

    @OptionsItem
    void action_submit(){
        String itemValue = value.getText().toString();

        if(!verifyValue(itemValue)){
            return;
        }

        Intent intent = getIntent();
        intent.putExtra("row",row);
        intent.putExtra("itemValue",itemValue);


        setResult(RESULT_OK,intent);
        finish();
    }

    boolean verifyValue(String itemValue){
        boolean flag = false;

        switch (row){
                case CreatProjectActivity.PJ_NAME:

                case CreatProjectActivity.PJ_ADDRESS:

                case CreatProjectActivity.HOUSE_TYPE:

                case CreatProjectActivity.PJ_DESINER:

                case CreatProjectActivity.CONTRACT_NUM:

                case CreatProjectActivity.O_NAME:
                    flag = emptyValue(itemValue);
                    break;
                case CreatProjectActivity.AREA:
                    if(Global.isFloat(itemValue)){
                        flag = true;
                    }else{
                        flag = false;
                        showMiddleToast("请输入数字");
                    }
                    break;

                case CreatProjectActivity.PJ_REMARK:
                    flag = true;
                    break;

                case CreatProjectActivity.O_ADDRESS:
                    flag = true;
                    break;
                case CreatProjectActivity.O_PHONE:
                    if(Global.isMoblie(itemValue)){
                        flag = true;
                    }else{
                        flag = false;
                        showMiddleToast("手机号不正确");
                    }

                    break;
                case ViewBillDetailActivity.CHECK_BILL_PASS:
                    flag = true;
                    break;

                case ViewBillDetailActivity.CHECK_BILL_REJEC:
                    flag = emptyValue(itemValue);
                    break;

                case ProgressDetailActivity.PG_PASS:
                    flag = emptyValue(itemValue);
                    break;

                case ProgressDetailActivity.PG_REJECT:
                    flag = emptyValue(itemValue);
                    break;
                case SelfBuyFragment.BMB_NAME:
                    flag = emptyValue(itemValue);
                    break;
                case SelfBuyFragment.BMB_TITLE:
                    flag = emptyValue(itemValue);
                    break;
                case SelfBuyFragment.BMB_PRICE:
                    if(Global.isFloat(itemValue)){
                        flag = true;
                    }else{
                        flag = false;
                        showMiddleToast("请输入数字");
                    }
                case SelfBuyFragment.BMB_SPEC:
                    flag = true;
                    break;
        }


        return flag;
    }

    boolean emptyValue(String itemValue){
        if(itemValue.isEmpty()){
            showMiddleToast("不能为空");
            return false;
        }else{
            return true;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_project, menu);


        return super.onCreateOptionsMenu(menu);
    }
}
