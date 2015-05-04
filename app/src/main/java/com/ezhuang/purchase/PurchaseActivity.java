package com.ezhuang.purchase;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.ListModify;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.IPcMt;
import com.ezhuang.model.SpMaterial;
import com.loopj.android.http.RequestParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/24 0024.
 */
@EActivity(R.layout.activity_sp_purchase)
public class PurchaseActivity extends BaseActivity {

    @Extra
    SpMaterial spMaterial;

    SelfBuyFragment selfBuyFragment;
    SelectPcMtFragment selectPcMtFragment;

    ActionBar actionBar;

    String QUERY_MT_REOCORD = Global.HOST + "/app/pcmt/queryPcMtRecord.do?smallTypeId=%s";

    Map<String,List<IPcMt>> mData = new HashMap<>();

    Spinner spinner;
    @AfterViews
    void init(){

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        selfBuyFragment = SelfBuyFragment_.builder().build();
        selfBuyFragment.setMaterial(spMaterial);

        selectPcMtFragment = SelectPcMtFragment_.builder().build();

        getNetwork(String.format(QUERY_MT_REOCORD,spMaterial.sTypeId),QUERY_MT_REOCORD);
        showDialogLoading();
    }

    String[] companyNames;

    void changeToSelectBmbMaterial(){
        getSupportFragmentManager().beginTransaction().add(R.id.container,selectPcMtFragment).commit();

        actionBar.setCustomView(R.layout.spinner_actionbar);
        spinner = (Spinner) findViewById(R.id.spinner);
        companyNames = new String[mData.size()];
        int i = 0;
        for(String name : mData.keySet()){
            companyNames[i] = name;
            i++;
        }

        if(i!=0){
            selectPcMtFragment.updateData(mData.get(companyNames[0]));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_textview,companyNames);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(onItemSelectedListener);

        findViewById(R.id.action_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToSelfBuy();
            }
        });
    }

    String ADD_PC_RECORD = Global.HOST + "/app/pcmt/addPcMtRecord.do";


    void changeToSelfBuy(){

        getSupportFragmentManager().beginTransaction().add(R.id.container,selfBuyFragment).commit();

        actionBar.setCustomView(R.layout.self_buy_actionbar);
        findViewById(R.id.action_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(!selfBuyFragment.fill(spMaterial)){
                return;
            }

            RequestParams params = new RequestParams();
            params.put("pcMtName",spMaterial.bmb_m_name);
            params.put("pcMtSmallType",spMaterial.sTypeId);
            params.put("pcMtUnitId",spMaterial.unitId);
            params.put("pcMtPrice",spMaterial.bmb_price);
            params.put("pcCompanyName",spMaterial.bmb_name);
            params.put("pcMtSpec",spMaterial.bmb_m_spec);
            postNetwork(ADD_PC_RECORD, params, ADD_PC_RECORD);

            Intent intent = new Intent();
            intent.putExtra("sp", spMaterial);
            setResult(RESULT_OK, intent);
            finish();

            }
        });
    }

    void selectRecord(IPcMt pcMt){

        spMaterial.bmb_name = companyNames[spinner.getSelectedItemPosition()];
        spMaterial.bmb_m_name = pcMt.getMtName();
        spMaterial.bmb_price = pcMt.getPrice();
        spMaterial.bmb_m_spec = pcMt.getSpec();

        Log.d("mData", JsonUtil.Object2Json(spMaterial));

        Intent intent = new Intent();
        intent.putExtra("sp", spMaterial);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(QUERY_MT_REOCORD.equals(tag)){
            hideProgressDialog();
            if(code == NetworkImpl.REQ_SUCCESSS){
                JSONArray jsonArray = respanse.getJSONArray("data");
                Log.d("data",jsonArray.toString());
                for(int i=0;i<jsonArray.length();i++){
                    String companyName = jsonArray.getJSONObject(i).getString("companyName");
                    JSONArray jsonPcMts = jsonArray.getJSONObject(i).getJSONArray("materials");
                    List<IPcMt> iPcMts = new LinkedList<>();
                    for(int k=0;k<jsonPcMts.length();k++){
                        SpMaterial pcmtImpl = new SpMaterial();
                        pcmtImpl.bmb_name = companyName;
                        pcmtImpl.toLoadData(jsonPcMts.getJSONObject(k));
                        iPcMts.add(pcmtImpl);
                    }
                    if(iPcMts.size()!=0)
                        mData.put(companyName,iPcMts);
                }

                if(mData.size()==0){
                    changeToSelfBuy();
                }else{
                    changeToSelectBmbMaterial();
                }
            }else{
                Log.i("错误码",""+code);
            }
        }

        if(ADD_PC_RECORD.equals(tag))
            Log.i(ADD_PC_RECORD,respanse.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ListModify.Add){
            if(resultCode == Activity.RESULT_OK){
                int row = data.getIntExtra("row",0);
                String value = data.getStringExtra("itemValue");
                selfBuyFragment.setRowValue(row,value);
            }
        }
    }


    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectPcMtFragment.updateData(mData.get(companyNames[position]));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }
}
