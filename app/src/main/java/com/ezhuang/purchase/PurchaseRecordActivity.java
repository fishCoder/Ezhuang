package com.ezhuang.purchase;

import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.SpOrder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/3 0003.
 */
@EActivity(R.layout.activity_view_sp_order)
public class PurchaseRecordActivity extends BaseActivity {

    String QUERY_PC_ORDER = Global.HOST + "/app/order/queryPurchaseOrder.do?keyword=%s";
    String QUERY_PC_ORDER_MORE =  Global.HOST + "/app/order/queryPurchaseOrder.do?keyword=%s&lastId=%s";



    List<SpOrder> mData = new LinkedList<>();

    @ViewById
    PullToRefreshListView listView;
    @StringArrayRes
    String[] self_order_state;

    EditText editText;

    @AfterViews
    void init(){
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
                getNetwork(String.format(QUERY_PC_ORDER,s.toString()),QUERY_PC_ORDER);
                showDialogLoading();
            }
        });
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(onRefreshListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PurchaseRecordDetailActivity_.intent(PurchaseRecordActivity.this).spOrderId(mData.get(position-1).spOrderId).start();
            }
        });
        getNetwork(String.format(QUERY_PC_ORDER, ""), QUERY_PC_ORDER);
        showDialogLoading();
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        hideProgressDialog();

        if(QUERY_PC_ORDER.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                mData.clear();
                Log.i("json", respanse.getString("data"));
                toObject(respanse);
            }else{
                showButtomToast("错误码:" + code);
            }
        }

        if(QUERY_PC_ORDER_MORE.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                try{
                    Log.i("json",respanse.getString("data"));
                }catch (Exception e){
                    showButtomToast("没有更多");
                    return;
                }

                toObject(respanse);

            }else{
                showButtomToast("错误码:" + code);
            }
        }
    }


    void toObject(JSONObject respanse){

        JSONArray jsonArray = null;
        try {
            jsonArray = respanse.getJSONArray("data");
            if(jsonArray.length()==0){
                showButtomToast("没有更多");
            }
            for (int i=0 ; i<jsonArray.length();i++){
                mData.add(JsonUtil.Json2Object(jsonArray.getString(i), SpOrder.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        toUI();
    }


    void toUI(){
        listView.onRefreshComplete();
        adapter.notifyDataSetChanged();

    }

    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {

            if(PullToRefreshBase.Mode.PULL_FROM_START == listView.getCurrentMode()){
                getNetwork(String.format(QUERY_PC_ORDER,""),QUERY_PC_ORDER);
            }else{
                int i = mData.size()-1;
                String lastId = mData.get(i).spOrderId;
                getNetwork(String.format(QUERY_PC_ORDER_MORE,editText.getText().toString(),lastId),QUERY_PC_ORDER_MORE);
            }
        }
    };


    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;

            if(convertView == null){
                viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.item_sp_order,parent,false);

                viewHolder.sp_order_no = (TextView) view.findViewById(R.id.sp_order_no);
                viewHolder.sp_bmb_name = (TextView) view.findViewById(R.id.sp_bmb_name);
                viewHolder.sp_order_time = (TextView) view.findViewById(R.id.sp_order_time);
                viewHolder.sp_order_state = (TextView) view.findViewById(R.id.sp_order_state);
                viewHolder.details_count = (TextView) view.findViewById(R.id.details_count);
                viewHolder.pj_name = (TextView) view.findViewById(R.id.pj_name);

                convertView = view;
                convertView.setTag(viewHolder);

            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            SpOrder spOrder = (SpOrder) getItem(position);
            viewHolder.sp_order_no.setText(spOrder.spOrderNo);
            viewHolder.sp_bmb_name.setText(spOrder.spBmbName);
            viewHolder.sp_order_time.setText(spOrder.spOrderTime);
            viewHolder.sp_order_state.setText(self_order_state[spOrder.spOrderState]);
            viewHolder.details_count.setText(String.valueOf(spOrder.detailsCount));
            viewHolder.pj_name.setText(spOrder.spPjName);
            return convertView;
        }
        class ViewHolder{
            TextView pj_name;
            TextView sp_order_no;
            TextView sp_order_state;
            TextView sp_bmb_name;
            TextView details_count;
            TextView sp_order_time;
        }
    };

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }

}
