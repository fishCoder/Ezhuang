package com.ezhuang.project.detail;


import android.os.AsyncTask;

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
import org.androidannotations.annotations.OptionsMenu;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/9 0009.
 */

@EActivity(R.layout.activity_view_project)
@OptionsMenu(R.menu.menu_fragment_project)
public class ViewProjectActivity extends BaseActivity {


    @Extra("roleId")
    public String roleId;

    @Extra("staffId")
    public String staffId;

    List<Project> listProject;

    String PROJECT_BY_ROLE = Global.HOST + "/app/project/queryMyProject.do?roleId=%s&global_key=%s";

    String PROJECT_BY_ROLE_MORE = Global.HOST + "/app/project/queryMyProject.do?roleId=%s&global_key=%s&lastPjId=%s";

    FragmentProjectList fragment;

    @AfterViews
    void init(){


        if(listProject == null){
            listProject = new LinkedList<>();
            showDialogLoading();
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragment = FragmentProjectList_.builder().arg("roleId",roleId).build();
        fragment.roleId = roleId;

        fragment.setProjectListListener(new ListListener() {
            @Override
            public void refresh() {
                ViewProjectActivity.this.getNetwork(String.format(PROJECT_BY_ROLE, roleId, staffId), PROJECT_BY_ROLE);
            }

            @Override
            public void loadMore() {
                ViewProjectActivity.this.getNetwork(String.format(PROJECT_BY_ROLE_MORE, roleId, staffId, listProject.get(listProject.size() - 1).getPjId()), PROJECT_BY_ROLE_MORE);
            }
        });
        getNetwork(String.format(PROJECT_BY_ROLE, roleId, staffId), PROJECT_BY_ROLE);
        getSupportFragmentManager().beginTransaction().add(R.id.container,fragment).commit();
    }

    @OptionsItem
    void action_search() {
        SearchProjectActivity_.intent(this).roleId(roleId).staffId(staffId).start();
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

        if(tag.equals(PROJECT_BY_ROLE)){
            if(code == NetworkImpl.REQ_SUCCESSS){

                new AsyncTask<JSONObject,Void,Void>(){
                    @Override
                    protected Void doInBackground(JSONObject... params) {
                        listProject.clear();
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = params[0].getJSONArray("data");
                            for(int i=0; i<jsonArray.length() ;i++){
                                Project project = getProject(jsonArray.getString(i), jsonArray.getJSONObject(i));
                                listProject.add(project);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        publishProgress();
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {

                        super.onProgressUpdate(values);
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        hideProgressDialog();
                        updateSearchResult();
                        super.onPostExecute(aVoid);
                    }
                }.execute(respanse);


            }else{


            }
        }

        if(tag.equals(PROJECT_BY_ROLE_MORE)){
            if(code == NetworkImpl.REQ_SUCCESSS){


                new AsyncTask<JSONObject,Void,Boolean>(){
                    @Override
                    protected Boolean doInBackground(JSONObject... params) {

                        JSONArray jsonArray = null;
                        try {
                            jsonArray = params[0].getJSONArray("data");
                            int len = jsonArray.length();
                            if(len==0){
                                publishProgress();
                            }
                            else{
                                for(int i=0; i< len ;i++){
                                    Project project = getProject(jsonArray.getString(i),jsonArray.getJSONObject(i));
                                    listProject.add(project);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            return  false;
                        }

                        return true;
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                        showButtomToast("没有更多了");
                        super.onProgressUpdate(values);
                    }

                    @Override
                    protected void onPostExecute(Boolean reslut) {
                        hideProgressDialog();
                        updateSearchResult();
                        super.onPostExecute(reslut);
                    }
                }.execute(respanse);



            }



        }

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
