package com.ezhuang.project;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.MyApp;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.AccountInfo;
import com.ezhuang.model.BillDetailState;
import com.ezhuang.model.BmbPurchase;
import com.ezhuang.model.IPcMt;
import com.ezhuang.model.PhotoData;
import com.ezhuang.model.Project;
import com.ezhuang.model.PurchaseDetail;
import com.ezhuang.model.SpMaterial;
import com.ezhuang.model.SpOdDetail;
import com.ezhuang.model.SpOnlineOrder;
import com.ezhuang.project.detail.SetProjectInfo_;
import com.ezhuang.purchase.SelectPcMtFragment;
import com.ezhuang.purchase.SelectPcMtFragment_;
import com.ezhuang.quality.ProgressDetailActivity;
import com.gc.materialdesign.widgets.Dialog;
import com.loopj.android.http.RequestParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    String pjId;
    @Extra
    int billState;
    @Extra
    boolean isRecord = false;

    @Extra
    Project project;

    View action_pass;
    View action_reject;
    View action_cancel;
    View action_remind;

    boolean action_remind_network = false;

    boolean isOperatOk = false;

    public final static int CHECK_BILL_REJEC = 100;
    public final static int CHECK_BILL_PASS = 101;

    ViewAndSubmitBillFragment fragment;
    SelectPcMtFragment viewOrderFragment;

    String QUERY_BILL_DETAIL = Global.HOST + "/app/project/queryBillingDetail.do?pjBillId=%s";

    String OPARE_BILL = Global.HOST + "/app/order/billExamine.do";

    String ADD_PURCHASE_ORDER = Global.HOST + "/app/order/addPurchaseOrder.do";

    String CANCEL_BILL = Global.HOST + "/app/project/deleteBill.do";

    String ORDER_REMIND = Global.HOST + "/app/order/reminderOrder.do";

    @ViewById
    TextView total_price;
    @ViewById
    TextView purchase;

    List<SpMaterial> mData = new LinkedList<>();

    int msg_state = 1;

    @AfterViews
    void init(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        if(Global.PROJECT_MANAGER.equals(roleId)){
            actionBar.setCustomView(R.layout.cancel_bill_actionbar);
            action_cancel = findViewById(R.id.action_cancel);
            action_remind = findViewById(R.id.action_remind);

            if(billState == 0){
                action_cancel.setVisibility(View.VISIBLE);
            }else{
                action_cancel.setVisibility(View.GONE);
            }

            if(billState == 0||billState == 1||billState == 2){
                action_remind.setVisibility(View.VISIBLE);
            }else{
                action_remind.setVisibility(View.GONE);
            }
            action_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = new Dialog(ViewBillDetailActivity.this,"撤销", "撤销后所有开单都将删除");
                    dialog.show();
                    dialog.getButtonAccept().setText("确定");
                    dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RequestParams params = new RequestParams();
                            params.add("billId",pjBillId);
                            postNetwork(CANCEL_BILL,params,CANCEL_BILL);
                            showButtomToast("正在撤销");
                        }
                    });

                }
            });
            action_remind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!action_remind_network){
                        RequestParams params = new RequestParams();
                        params.put("billId",pjBillId);
                        postNetwork(ORDER_REMIND,params,ORDER_REMIND);
                        action_remind_network = true;
                    }
                }
            });
        }
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


        if((billState == 1||billState == 2) && Global.BUYER.equals(roleId)){
            actionBar.setCustomView(R.layout.purchase_actionbar);
            findViewById(R.id.layout_parchase).setVisibility(View.VISIBLE);
            findViewById(R.id.action_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<IPcMt> list = new LinkedList<IPcMt>();

                    for(IPcMt pcMt : mData){
                        if(!pcMt.getCompanyName().isEmpty()){
                            list.add(pcMt);
                        }
                    }
                    if(list.size() == 0){
                        showButtomToast("还没有采购任何东西无法预料哦");
                        return;
                    }
                    viewOrderFragment.updateData(list);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, viewOrderFragment).commit();
                }
            });
            purchase.setOnClickListener(submitClick);
            reflashBottom();
        }

        viewOrderFragment = SelectPcMtFragment_.builder().build();
        viewOrderFragment.viewOrder = true;

        fragment = ViewAndSubmitBillFragment_.builder().build();
        fragment.readOnly = true;
        fragment.isRecord = isRecord;
        fragment.roleId = roleId;
        fragment.project = project;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        showDialogLoading();

        getNetwork(String.format(QUERY_BILL_DETAIL,pjBillId),QUERY_BILL_DETAIL);
    }
    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(QUERY_BILL_DETAIL.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                try{
                    Log.i("json",respanse.getString("data"));
                }catch (Exception e){
                    fragment.updateData(mData);
                    return;
                }

                jsonToObject(respanse);

            }else{
                hideProgressDialog();
                showButtomToast("错误码 "+code);
            }
        }
        if(OPARE_BILL.equals(tag)){
            showProgressBar(false);
            if(code == NetworkImpl.REQ_SUCCESSS){
                isOperatOk = true;
                action_pass.setVisibility(View.GONE);
                action_reject.setVisibility(View.GONE);

                msg_state = 4;
            }else{
                showButtomToast("错误码 "+code);
            }
        }
        if(ADD_PURCHASE_ORDER.equals(tag)){
            showProgressBar(false);
            if(code == NetworkImpl.REQ_SUCCESSS){
                isOperatOk = true;
                billState = respanse.getInt("data");

                for (SpMaterial spMaterial : mData){
                    if(purchaseItemIdSet.contains(spMaterial.item_id)){
                        if(spMaterial.getMtType()==0)
                            spMaterial.state = BillDetailState.BUY.state;
                        else
                            spMaterial.state = BillDetailState.SELF.state;
                    }
                }

                fragment.updateData(mData);
                reflashBottom();
                showButtomToast("提交订单成功");
            }else{
                showButtomToast("提交订单失败 "+code);
            }
        }
        if(CANCEL_BILL.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                Intent intent = new Intent();
                intent.putExtra("pjBillId",pjBillId);
                intent.putExtra("state",6);
                setResult(RESULT_OK,intent);
                finish();
                showButtomToast("成功");
            }else{
                showButtomToast("失败");
            }
        }
        if(ORDER_REMIND.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                action_remind.setVisibility(View.GONE);
                showButtomToast("催单成功");
            }else{
                action_remind_network = false;
                showButtomToast("催单失败");
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
                    spMaterial.state = jsonObject.getInt("state");
                    spMaterial.itemImages = new LinkedList();
                    spMaterial.item_id =  jsonObject.getString("id");

                    String img = jsonObject.optString("img");
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
            intent.putExtra("msg_state",msg_state);
            setResult(RESULT_OK,intent);
            finish();
        }else{
            if(viewOrderFragment.isVisible()){
                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            }else {
                onBackPressed();
            }
        }
    }


    void action_pass(){
        SetProjectInfo_.intent(this).row(CHECK_BILL_PASS).title("审核意见").startForResult(CHECK_BILL_PASS);
    }


    void action_reject(){
        SetProjectInfo_.intent(this).row(CHECK_BILL_REJEC).title("驳回意见").startForResult(CHECK_BILL_REJEC);
    }

    //存放采购 bill detail id
    Set<String> purchaseItemIdSet = new HashSet<>();

    View.OnClickListener submitClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i("submitClick","点击了一下采购");
            Map<String,List<SpOdDetail>> mSpOd = new HashMap<>();
            Map<String,List<PurchaseDetail>> purchaseDetails = new HashMap<>();
            purchaseItemIdSet.clear();
            for (SpMaterial spMaterial : mData){
                if(spMaterial.getCompanyName().isEmpty()||spMaterial.state!= BillDetailState.UNBUY.state)continue;
                purchaseItemIdSet.add(spMaterial.item_id);

                if(spMaterial.getMtType()==0){
                    PurchaseDetail detail = new PurchaseDetail();

                    detail.materialId = spMaterial.getMtId();
                    detail.pcPrice = spMaterial.getPrice();
                    detail.pjBillId = spMaterial.item_id;

                    if(purchaseDetails.get(spMaterial.getCompanyId())==null){
                        purchaseDetails.put(spMaterial.getCompanyId(),new LinkedList<PurchaseDetail>());
                    }
                    purchaseDetails.get(spMaterial.getCompanyId()).add(detail);

                }else{
                    SpOdDetail detail = new SpOdDetail();
                    detail.spOdMtName = spMaterial.getMtName();
                    detail.spOdMtNum  = spMaterial.getCount();
                    detail.spOdMtUnit = spMaterial.unitId;
                    detail.spOdSpec = spMaterial.bmb_m_spec;

                    detail.pjdId = spMaterial.item_id;
                    detail.spOdPrice  = spMaterial.getPrice();
                    detail.spOdTotalPrice = String.format("%.2f",Float.parseFloat(spMaterial.getPrice())*Float.parseFloat(spMaterial.getCount()));
                    String companyName = spMaterial.getCompanyName();
                    if(mSpOd.get(companyName) == null){
                        mSpOd.put(companyName,new LinkedList<SpOdDetail>());
                    }
                    mSpOd.get(companyName).add(detail);
                }


            }

            if(mSpOd.size()==0&&purchaseDetails.size()==0){
                showButtomToast("您还没有采购哦");
                return;
            }

            List<BmbPurchase> bmbPurchases = new LinkedList<>();

            for(String key:purchaseDetails.keySet()){
                BmbPurchase bmbPurchase = new BmbPurchase();
                bmbPurchase.bmbId = key;
                bmbPurchase.purchaseDetails = purchaseDetails.get(key);
                bmbPurchases.add(bmbPurchase);
            }



            List<Map<String,Object>> upSpOd = new LinkedList<>();
            for (String key : mSpOd.keySet()){
                List<SpOdDetail> details = mSpOd.get(key);
                Map<String,Object> item = new HashMap<>();
                item.put("spPjId",pjId);
                item.put("spBmbName",key.substring(4,key.length()));
                item.put("details",details);
                upSpOd.add(item);

            }


            RequestParams params = new RequestParams();
            if(upSpOd.size()!=0){
                String sUpSpOd = JsonUtil.Object2Json(upSpOd);
                params.put("purchaseOrder",sUpSpOd);
                Log.d("purchaseOrder",sUpSpOd);
            }
            if(bmbPurchases.size()!=0){
                SpOnlineOrder spOnlineOrder = new SpOnlineOrder();
                spOnlineOrder.bmbPurchases = bmbPurchases;
                spOnlineOrder.spId = MyApp.currentUser.getCompanyId();
                spOnlineOrder.spStaffId = MyApp.currentUser.getGlobal_key();
                spOnlineOrder.spPjId = pjId;
                String sSpOnlineOrder = JsonUtil.Object2Json(spOnlineOrder);
                params.put("bmbOrders",sSpOnlineOrder);
                Log.d("bmbOrders",sSpOnlineOrder);
            }
            params.put("pjBillId",pjBillId);

            postNetwork(ADD_PURCHASE_ORDER, params, ADD_PURCHASE_ORDER);
            showProgressBar(true, "提交订单");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHECK_BILL_PASS || requestCode == CHECK_BILL_REJEC){
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

    @OnActivityResult(16)
    void bindBillItem(int resultCode,Intent data){
        if(resultCode == RESULT_OK){
            SpMaterial spMaterial = (SpMaterial) data.getSerializableExtra("sp");
            for(int i=0 ; i< mData.size();i++){

                SpMaterial sp = mData.get(i);
                if(sp.item_id.equals(spMaterial.item_id)){
                    sp.bmb_name = spMaterial.bmb_name;
                    sp.bmb_m_name = spMaterial.bmb_m_name;
                    sp.bmb_price = spMaterial.bmb_price;
                    sp.bmb_m_spec = spMaterial.bmb_m_spec;
                    sp.bmb_m_id = spMaterial.bmb_m_id;
                    sp.bmb_id = spMaterial.bmb_id;
                    sp.bmb_m_type = spMaterial.bmb_m_type;
                }

            }
            fragment.updateData(mData);
            reflashBottom();
        }
    }


    public void reflashBottom(){
        int count = 0;
        float fTotalPrice = 0f;
        for (SpMaterial pcMt : mData){
            if(!pcMt.getCompanyName().isEmpty()&&pcMt.state == BillDetailState.UNBUY.state){
                count ++;
                fTotalPrice += Float.parseFloat(pcMt.getPrice())*Float.parseFloat(pcMt.item_count);
            }

        }
        if(billState!=1&&billState!=2){
            findViewById(R.id.action_view).setVisibility(View.GONE);
            findViewById(R.id.layout_parchase).setVisibility(View.GONE);

            msg_state = 4;
        }
        total_price.setText(String.format("￥%.2f",fTotalPrice));
        purchase.setText(String.format("购买 (%d)",count));

    }
}
