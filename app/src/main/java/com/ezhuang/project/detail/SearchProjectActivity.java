package com.ezhuang.project.detail;

import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.Project;
import com.ezhuang.model.StaffUser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/11 0011.
 */
@EActivity(R.layout.activity_search_project)
public class SearchProjectActivity extends BaseActivity{

    @Extra("roleId")
    public String roleId;

    @Extra("staffId")
    public String staffId;

    List<Project> listProject;

    @ViewById
    View emptyView, container;

    EditText editText;

    String PROJECT_BY_SEARCH = Global.HOST + "/app/project/queryMyProject.do?roleId=%s&global_key=%s&keyword=%s";

    String PROJECT_BY_SEARCH_MORE = Global.HOST + "/app/project/queryMyProject.do?roleId=%s&global_key=%s&keyword=%s&lastPjId=%s";

    String keyword = "";

    FragmentProjectList fragment;

    @AfterViews
    void init(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setCustomView(R.layout.activity_search_project_actionbar);
        editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(watcher);

        if(listProject == null){
            listProject = new LinkedList<>();
        }

        fragment = FragmentProjectList_.builder().arg("roleId","roleId").build();
        fragment.roleId = roleId;
        fragment.setProjectListListener(new ListListener() {
            @Override
            public void refresh() {
                SearchProjectActivity.this
                        .getNetwork(String.format(PROJECT_BY_SEARCH, roleId, staffId, keyword), PROJECT_BY_SEARCH);
            }

            @Override
            public void loadMore() {
                SearchProjectActivity.this
                        .getNetwork(String.format(PROJECT_BY_SEARCH_MORE, roleId, staffId ,keyword ,listProject.get(listProject.size() - 1).getPjId()), PROJECT_BY_SEARCH_MORE);
            }
        });


        getSupportFragmentManager().beginTransaction().add(R.id.container,fragment).commit();
    }

    TextWatcher watcher = new TextWatcher(){

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0 && !keyword.equals(s.toString())) {
                keyword = s.toString();

                getNetwork(String.format(PROJECT_BY_SEARCH, roleId, staffId, keyword), PROJECT_BY_SEARCH);

                showDialogLoading();
                emptyView.setVisibility(View.INVISIBLE);
                container.setVisibility(View.VISIBLE);
            }

            getSupportActionBar().setTitle(R.string.title_activity_search_project);
        }

    };

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(tag.equals(PROJECT_BY_SEARCH)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                listProject.clear();
                JSONArray jsonArray = respanse.getJSONArray("data");
                for(int i=0; i<jsonArray.length() ;i++){
                    Project project = getProject(jsonArray.getString(i), jsonArray.getJSONObject(i));
                    listProject.add(project);
                }
            }
        }

        if(tag.equals(PROJECT_BY_SEARCH_MORE)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                JSONArray jsonArray = respanse.getJSONArray("data");
                int len = jsonArray.length();
                if(len==0){
                    showButtomToast("没有更多了");
                }
                else{
                    for(int i=0; i< len ;i++){
                        Project project = getProject(jsonArray.getString(i),jsonArray.getJSONObject(i));
                        listProject.add(project);
                    }
                }

            }
        }

        hideProgressDialog();

        if (listProject.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            container.setVisibility(View.INVISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
            container.setVisibility(View.VISIBLE);
        }

        updateSearchResult();
    }

    Project getProject(String sProject,JSONObject jsonObject) throws  JSONException{
        Project project = JsonUtil.Json2Object(sProject, Project.class);
        project.setPjM(JsonUtil.Json2Object(jsonObject.getString("pjM"), StaffUser.class));
        project.setPjChecker(JsonUtil.Json2Object(jsonObject.getString("pjChecker"), StaffUser.class));
        project.setPjBuyer(JsonUtil.Json2Object(jsonObject.getString("pjBuyer"), StaffUser.class));
        project.setPjQuality(JsonUtil.Json2Object(jsonObject.getString("pjQuality"), StaffUser.class));

        return project;
    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }

    private void updateSearchResult() {
        fragment.updateData(listProject);
    }
}
