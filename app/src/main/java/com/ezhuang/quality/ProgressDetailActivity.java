package com.ezhuang.quality;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.ImagePagerActivity_;
import com.ezhuang.R;
import com.ezhuang.adapter.GridImageAdapter;
import com.ezhuang.common.Global;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.PhotoData;
import com.ezhuang.model.ProjectProgress;
import com.ezhuang.project.FillBillItemFragment;
import com.ezhuang.project.detail.SetProjectInfo_;
import com.loopj.android.http.RequestParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/24 0024.
 */
@EActivity(R.layout.activity_progress_detail)
public class ProgressDetailActivity extends BaseActivity {

    @ViewById
    TextView pg_time;
    @ViewById
    View     layout_quality;
    @ViewById
    View     layout_owner;
    @ViewById
    TextView quality_state;
    @ViewById
    TextView owner_state;
    @ViewById
    TextView pg_remark;
    @ViewById
    TextView pg_name;
    @ViewById
    TextView pgQtEmeExplain;
    @ViewById
    TextView pgOwnerEmeExplain;
    @ViewById
    GridView gridView;

    @StringArrayRes
    String[] pg_state;
    int[] pg_state_color = {R.color.undo,R.color.undo,R.color.pass,R.color.reject};

    @Extra("projectProgress")
    ProjectProgress pg;

    @Extra("roleId")
    String roleId;
    @Extra("pjId")
    String pjId;
    @Extra("pgId")
    String pgId;


    boolean isOperatOk = false;
    int state;

    View action_pass;
    View action_reject;

    String PG_NODE_EXAMIE = Global.HOST + "/app/progress/addNodeExamine.do";

    @AfterViews
    void init() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        if (Global.QUALITY.equals(roleId)&&pg.quoCheckResult==1) {
            actionBar.setCustomView(R.layout.chcek_bill_actionbar);
            TextView textView = (TextView) findViewById(R.id.title);
            textView.setText("进度审核");
            action_pass = findViewById(R.id.action_pass);
            action_pass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action_pass();
                }
            });
            action_reject = findViewById(R.id.action_reject);
            action_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action_reject();
                }
            });
        }

        pg_name.setText(pg.nodeName);
        pg_time.setText(pg.time);
        pg_remark.setText(pg.pgRemark);
        if (pg.isNeedOwnerCheck()) {
            layout_owner.setVisibility(View.VISIBLE);
            owner_state.setText(pg_state[pg.owerCheckResult]);
            owner_state.setTextColor(pg_state_color[pg.owerCheckResult]);
            pgOwnerEmeExplain.setText(pg.owerRemark==null?"":pg.owerRemark);
        } else {
            layout_owner.setVisibility(View.GONE);
        }

        if (pg.isNeedQualityCheck()) {
            layout_quality.setVisibility(View.VISIBLE);
            quality_state.setText(pg_state[pg.quoCheckResult]);
            quality_state.setTextColor(pg_state_color[pg.quoCheckResult]);
            pgQtEmeExplain.setText(pg.quoRemark==null?"":pg.quoRemark);
        } else {
            layout_quality.setVisibility(View.GONE);
        }

        if (pg.imgUrls == null || pg.imgUrls.length == 0) {
            gridView.setVisibility(View.GONE);
        } else {
            final List<PhotoData> list = new LinkedList<>();

            for (String url : pg.imgUrls) {
                PhotoData photoData = new PhotoData(url);
                Log.i("图片路径", url);
                list.add(photoData);
            }

            gridView.setVisibility(View.VISIBLE);
            GridImageAdapter myAdapter = new GridImageAdapter();
            myAdapter.setData(list,mInflater);
            myAdapter.notifyDataSetChanged();
            gridView.setAdapter(myAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ProgressDetailActivity.this, ImagePagerActivity_.class);
                    ArrayList<String> arrayUri = new ArrayList<String>();
                    for (PhotoData item : list) {
                        arrayUri.add(item.uri.toString());
                    }
                    intent.putExtra("mArrayUri", arrayUri);
                    intent.putExtra("mPagerPosition", position);
                    intent.putExtra("needEdit", false);
                    startActivityForResult(intent, FillBillItemFragment.RESULT_REQUEST_IMAGE);
                }
            });
        }
    }


    @OptionsItem(android.R.id.home)
    void home() {
        if(isOperatOk){
            Intent intent = getIntent();
            intent.putExtra("state",state);
            intent.putExtra("roleId",roleId);
            intent.putExtra("pgId",pgId);
            setResult(RESULT_OK, intent);
            finish();
        }else{
            onBackPressed();
        }
    }


    public final static int PG_PASS = 2002;
    public final static int PG_REJECT = 2003;

    void action_pass(){
        SetProjectInfo_.intent(this).row(PG_PASS).title("审核意见").startForResult(PG_PASS);
    }


    void action_reject(){
        SetProjectInfo_.intent(this).row(PG_REJECT).title("驳回意见").startForResult(PG_REJECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            int row = data.getIntExtra("row",2003);
            String value = data.getStringExtra("itemValue");
            RequestParams params = new RequestParams();
            state = (row-2000);
            params.put("pgId",pg.pgId);
            params.put("pjId",pjId);
            params.put("quoCheckResult",state);
            params.put("quoRemark", value);
            pg.quoCheckResult = state;
            pgQtEmeExplain.setText(value);
            postNetwork(PG_NODE_EXAMIE, params, PG_NODE_EXAMIE);
            showProgressBar(true,"提交操作");
        }
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(PG_NODE_EXAMIE.equals(PG_NODE_EXAMIE)){
            showProgressBar(false);
            if(code == NetworkImpl.REQ_SUCCESSS){
                isOperatOk = true;
                action_pass.setVisibility(View.GONE);
                action_reject.setVisibility(View.GONE);
            }else{
                showButtomToast("错误码 "+code);
            }
        }
    }
}
