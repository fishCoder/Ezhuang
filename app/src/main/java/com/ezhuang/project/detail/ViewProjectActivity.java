package com.ezhuang.project.detail;


import android.os.Bundle;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/4/9 0009.
 */

@EActivity(R.layout.activity_view_project)
public class ViewProjectActivity extends BaseActivity {


    @Extra("roleId")
    public String roleId;

    @AfterViews
    void init(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentProjectList_ fragment = new FragmentProjectList_();

        Bundle bundle = new Bundle();
        bundle.putString("roleId", roleId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.container,fragment).commit();
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }


}
