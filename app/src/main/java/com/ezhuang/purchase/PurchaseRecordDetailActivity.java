package com.ezhuang.purchase;

import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.BillDetailState;
import com.ezhuang.model.IPcMt;
import com.ezhuang.model.SpMaterial;
import com.ezhuang.model.SpOdDetail;

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
 * Created by Administrator on 2015/5/3 0003.
 */
@EActivity(R.layout.activity_purchase_record_detail)
public class PurchaseRecordDetailActivity extends BaseActivity {

    SelectPcMtFragment fragment;

    String PURCHASE_RECORD_DETAIL = Global.HOST + "/app/order/queryPurchaseOrderDetails.do?spOrderId=%s";

    @Extra
    String spOrderId;
    @ViewById
    TextView total_price;
    @ViewById
    TextView total_count;

    List<IPcMt> mData = new LinkedList<>();

    @AfterViews
    void init(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        fragment = SelectPcMtFragment_.builder().build();
        fragment.viewOrder = true;
        getSupportFragmentManager().beginTransaction().add(R.id.container,fragment).commit();

        getNetwork(String.format(PURCHASE_RECORD_DETAIL,spOrderId),PURCHASE_RECORD_DETAIL);
        showDialogLoading();
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        hideProgressDialog();

        if(tag.equals(PURCHASE_RECORD_DETAIL)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                JSONArray jsonArray = respanse.getJSONArray("data");
                Log.d("data",jsonArray.toString());
                for(int i=0;i<jsonArray.length();i++){
                    SpOdDetail detail = new SpOdDetail();
                    detail.toLoadData(jsonArray.getJSONObject(i));
                    mData.add(detail);
                }
                reflashBottom();
                fragment.updateData(mData);
            }else{

            }
        }
    }

    public void reflashBottom(){
        int count = 0;
        float fTotalPrice = 0f;
        for (IPcMt pcMt : mData){
            count++;
            fTotalPrice += Float.parseFloat(pcMt.getPrice())*Float.parseFloat(pcMt.getCount());
        }

        total_price.setText(String.format("￥%.2f",fTotalPrice));
        total_count.setText(String.valueOf(count));
    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }
}
