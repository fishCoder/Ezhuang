package com.ezhuang.project;

import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.ProjectBill;
import com.ezhuang.project.detail.ListListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/13 0013.
 */
@EActivity(R.layout.activity_project_bill)

public class ProjectBillActivity extends BaseActivity {

    EditText editText;

    ProjectBillFragment_ fragment;

    @Extra("pjId")
    String pjId;

    String QUERY_BILL = Global.HOST + "/app/project/queryProjectBillings.do?pjId=%s";

    String QUERY_BILL_MORE = Global.HOST + "/app/project/queryProjectBillings.do?pjId=%s&last_pj_bill_id=%s";

    List<ProjectBill> bills;

    @AfterViews
    void init(){
        bills = new LinkedList<>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setCustomView(R.layout.activity_search_project_actionbar);
        editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO 检索
            }
        });

        fragment = new ProjectBillFragment_();
        fragment.setUpDownListListener(new ListListener() {
            @Override
            public void loadMore() {

            }

            @Override
            public void refresh() {

            }
        });
        showDialogLoading();
        getNetwork(String.format(QUERY_BILL,pjId),QUERY_BILL);
        getSupportFragmentManager().beginTransaction().add(R.id.container,fragment).commit();
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

        if(tag.equals(QUERY_BILL)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                bills.clear();
                JSONArray jsonArray = respanse.getJSONArray("data");
                for (int i=0;i<jsonArray.length();i++){
                    String json = jsonArray.getString(i);
                    bills.add(JsonUtil.Json2Object(json,ProjectBill.class));
                }
                fragment.updateDate(bills);
            }
        }else
        if(tag.equals(QUERY_BILL_MORE)){
            if(code == NetworkImpl.REQ_SUCCESSS){

            }

        }
        hideProgressDialog();
    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }
}
