package com.ezhuang.project;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.PhotoData;
import com.ezhuang.model.SpMaterial;
import com.ezhuang.project.detail.SetProjectInfo_;
import com.loopj.android.http.RequestParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/21 0021.
 */
@EActivity(R.layout.activity_view_bill_detail)
public class ViewBillDetailActivity extends BaseActivity {
    @Extra
    String roleId;
    @Extra
    String staffId;
    @Extra
    String pjBillId;
    @Extra
    int billState;

    View action_pass;
    View action_reject;

    boolean isOperatOk = false;

    public final static int CHECK_BILL_REJEC = 100;
    public final static int CHECK_BILL_PASS = 101;

    ViewAndSubmitBillFragment fragment;

    String QUERY_BILL_DETAIL = Global.HOST + "/app/project/queryBillingDetail.do?pjBillId=%s";

    String OPARE_BILL = Global.HOST + "/app/order/billExamine.do";

    List<SpMaterial> mData = new LinkedList<>();

    @AfterViews
    void init(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        if(billState == 0 && Global.CEHCK.equals(roleId)){
            actionBar.setCustomView(R.layout.chcek_bill_actionbar);
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

        fragment = ViewAndSubmitBillFragment_.builder().build();
        fragment.readOnly = true;
        getSupportFragmentManager().beginTransaction().add(R.id.container,fragment).commit();
        showDialogLoading();

        getNetwork(String.format(QUERY_BILL_DETAIL,pjBillId),QUERY_BILL_DETAIL);
    }
    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(QUERY_BILL_DETAIL.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                Log.i("json",respanse.getString("data"));
                jsonToObject(respanse);

            }else{
                showButtomToast("错误码 "+code);
            }
        }
        if(OPARE_BILL.equals(tag)){
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

    @Background
    public void jsonToObject(JSONObject respanse){
        try {
            JSONArray jsonArray = respanse.getJSONArray("data");

            if(jsonArray.length()==0){
                noMore();
            }
            for (int i=0 ; i<jsonArray.length() ; i++){

                JSONArray jsonBillDetails = jsonArray.getJSONObject(i).getJSONArray("billDetails");
                for(int k=0; k<jsonBillDetails.length() ;k++){
                    JSONObject jsonObject = jsonBillDetails.getJSONObject(k);
                    SpMaterial spMaterial = JsonUtil.Json2Object(jsonObject.getString("matl"),SpMaterial.class);
                    spMaterial.item_count = String.valueOf(jsonObject.getInt("dosage"));
                    spMaterial.item_remark = jsonObject.getString("remark");
                    spMaterial.mgBillId = jsonObject.getString("mgBillId");
                    spMaterial.itemImages = new LinkedList();
                    String img = jsonObject.getString("img");
                    if(!img.isEmpty()){
                        String[] imgs = img.split("&");
                        for (String url:imgs){
                            PhotoData photoData = new PhotoData(url);
                            Log.i("图片路径",url);
                            spMaterial.itemImages.add(photoData);
                        }
                    }

                    mData.add(spMaterial);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        toUI();
    }

    @UiThread
    public void toUI(){
        fragment.updateData(mData);
        hideProgressDialog();
    }

    @UiThread
    public void noMore(){
        showButtomToast("没有更多内容");
    }

    @OptionsItem(android.R.id.home)
    void home() {
        if(isOperatOk){
            Intent intent = getIntent();
            intent.putExtra("state",billState);
            intent.putExtra("pjBillId",pjBillId);
            setResult(RESULT_OK,intent);
            finish();
        }else{
            onBackPressed();
        }
    }



    void action_pass(){
        SetProjectInfo_.intent(this).row(CHECK_BILL_PASS).title("审核意见").startForResult(CHECK_BILL_PASS);
    }


    void action_reject(){
        SetProjectInfo_.intent(this).row(CHECK_BILL_REJEC).title("驳回意见").startForResult(CHECK_BILL_REJEC);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            int row = data.getIntExtra("row",100);
            String value = data.getStringExtra("itemValue");
            RequestParams params = new RequestParams();
            params.put("pjBillId",pjBillId);
            params.put("result",row-100);
            params.put("remark",value);
            postNetwork(OPARE_BILL,params,OPARE_BILL);
            showProgressBar(true,"提交操作");
            billState = ((row-100)==0?5:1);
        }
    }
}
