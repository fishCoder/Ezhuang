package com.ezhuang.purchase;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.SpMaterial;
import com.ezhuang.model.SpOdDetail;
import com.gc.materialdesign.widgets.SnackBar;
import com.loopj.android.http.RequestParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Administrator on 2015/6/19 0019.
 */
@EActivity(R.layout.item_bill_detail)
public class OrderDetailItemActivity extends BaseActivity{
    @ViewById
    TextView sp_m_name;
    @ViewById
    TextView sp_m_spec;
    @ViewById
    TextView item_count;
    @ViewById
    TextView item_state;
    @ViewById
    TextView item_remark;
    @ViewById
    TextView sp_m_unit_name;


    @ViewById
    GridView gridView;
    @ViewById
    TextView bmb_name;
    @ViewById
    TextView bmb_m_name;
    @ViewById
    TextView bmb_m_spec;
    @ViewById
    TextView bmb_m_price;
    @ViewById
    TextView bmb_m_total;

    @ViewById
    View bmb;
    @ViewById
    View layout_btn;
    @ViewById
    View btnSure;
    @ViewById
    View layout_bill_item;

    String QUERY_DETAIL = Global.HOST + "/app/project/queryBillDetail.do?billDId=%s";

    String RECEIVE = Global.HOST + "/app/order/goodsReceipt.do";
    @Extra
    String billDId;
    @Extra
    String roleId;

    int msg_state;

    SpMaterial spMaterial;
    SpOdDetail spOdDetail;

    @StringArrayRes
    String[] bill_detail_state;

    @AfterViews
    void init(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        showDialogLoading();

        getNetwork(String.format(QUERY_DETAIL,billDId),QUERY_DETAIL);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(QUERY_DETAIL.equals(tag)){
            hideProgressDialog();
            if(code == NetworkImpl.REQ_SUCCESSS){
                Log.i("json",respanse.getString("data"));

                spOdDetail = new SpOdDetail();
                spOdDetail.toLoadData(respanse.getJSONObject("data").getJSONObject("buyerBillDetail"));

                if(Global.STORAGE.equals(roleId)){
                    layout_bill_item.setVisibility(View.GONE);
                }else{
                    JSONObject jsonObject = respanse.getJSONObject("data").getJSONObject("billDetail").getJSONArray("billDetails").getJSONObject(0);
                    spMaterial = JsonUtil.Json2Object(jsonObject.getString("matl"), SpMaterial.class);
                    spMaterial.item_count = String.valueOf(jsonObject.getInt("dosage"));
                    spMaterial.item_remark = jsonObject.getString("remark");
                    spMaterial.mgBillId = jsonObject.getString("mgBillId");
                    spMaterial.state = jsonObject.getInt("state");
                    spMaterial.itemImages = new LinkedList();
                    spMaterial.item_id =  jsonObject.getString("id");

                    sp_m_name.setText(spMaterial.mtName);
                    sp_m_spec.setText(spMaterial.spec);
                    item_count.setText(spMaterial.getCount());
                    item_state.setText(bill_detail_state[spMaterial.getMtState()]);
                    item_remark.setText(spMaterial.item_remark);
                    sp_m_unit_name.setText(spMaterial.unitName);
                }


                bmb_name.setText(spOdDetail.getCompanyName());
                bmb_m_name.setText(spOdDetail.getMtName());
                bmb_m_spec.setText(spOdDetail.getSpec());
                bmb_m_price.setText(spOdDetail.getPrice());
                bmb_m_total.setText(spOdDetail.getCount());

                if(Global.PROJECT_MANAGER.equals(roleId)&&(spMaterial.state==3||spMaterial.state==5)){
                    layout_btn.setVisibility(View.VISIBLE);
                }

                btnSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SnackBar snackbar = new SnackBar(OrderDetailItemActivity.this, "确定收到货物吗？" , "确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestParams requestParams = new RequestParams();
                                requestParams.put("billDId",spMaterial.item_id);
                                requestParams.put("type",spMaterial.state==3?0:1);
                                postNetwork(RECEIVE,requestParams,RECEIVE);
                            }
                        });
                        snackbar.show();
                    }
                });
            }
        }else
        if(RECEIVE.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                btnSure.setVisibility(View.GONE);
                msg_state = 4;
            }
        }
    }

    @OptionsItem(android.R.id.home)
    void home() {
        Intent intent = getIntent();
        intent.putExtra("msg_state",msg_state);
        setResult(RESULT_OK,intent);
        onBackPressed();
    }
}
