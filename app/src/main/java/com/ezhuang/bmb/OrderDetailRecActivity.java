package com.ezhuang.bmb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.BmbOdItem;
import com.ezhuang.model.BmbOrderDetail;
import com.ezhuang.model.StaffUser;
import com.gc.materialdesign.widgets.SnackBar;
import com.loopj.android.http.RequestParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/15 0015.
 */
@EActivity(R.layout.activity_order_detail)
public class OrderDetailRecActivity extends BaseActivity {

    String QUERY_ORDER_DETAIL = Global.HOST + "/app/bmb/queryBmbOrderDetail.do?orderId=%s&type=%s";
    String QUERY_STAFF = Global.HOST + "/app/bmb/queryOrderStaffs.do";
    String POINT_STORAGE = Global.HOST + "/app/bmb/dispatchOrder.do";
    String OUT_STORAGE = Global.HOST + "/app/bmb/sendSelfGoods.do";

    @StringArrayRes
    String[] bmb_order_detail_state;

    @Extra
    String orderId;
    @Extra
    String type;
    @Extra
    String roleId = Global.STORAGE;
    @ViewById
    ListView listView;

    String selectId;

    List<BmbOrderDetail> mData = new LinkedList<>();



    int msg_state = 1;

    @AfterViews
    void init(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        if(mData.size()==0){
            showDialogLoading();
            getNetwork(String.format(QUERY_ORDER_DETAIL,orderId,type),QUERY_ORDER_DETAIL);
        }
        listView.setAdapter(adapter);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        hideProgressDialog();

        if(tag.equals(QUERY_ORDER_DETAIL)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                JSONArray jsonArray = respanse.optJSONArray("data");
                for (int i=0;i<jsonArray.length();i++){
                    mData.add(JsonUtil.Json2Object(jsonArray.getString(i),BmbOrderDetail.class));
                    adapter.notifyDataSetChanged();
                }
            }
        }else if(OUT_STORAGE.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                for (BmbOrderDetail item : mData){
                    if(item.bmbODId.equals(selectId)){
                        selectId = null;
                        item.bmbODstate = "2";
                        break;
                    }
                }

                adapter.notifyDataSetChanged();
            }else{
                selectId = null;
                showButtomToast(String.format("错误码：%d",code));
            }
        }

    }







    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
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
                view = mInflater.inflate(R.layout.item_bmb_order_detail,parent,false);
                viewHolder.bmb_m_name = (TextView) view.findViewById(R.id.bmb_m_name);
                viewHolder.bmb_m_spec = (TextView) view.findViewById(R.id.bmb_m_spec);
                viewHolder.item_state = (TextView) view.findViewById(R.id.item_state);
                viewHolder.bmb_m_price = (TextView) view.findViewById(R.id.bmb_m_price);
                viewHolder.item_count = (TextView) view.findViewById(R.id.item_count);
                viewHolder.bmb_m_unit = (TextView) view.findViewById(R.id.bmb_m_unit);
                viewHolder.layout_btn = view.findViewById(R.id.layout_btn);
                viewHolder.out_storage = view.findViewById(R.id.out_storage);
                viewHolder.bmb_m_price_txt = view.findViewById(R.id.bmb_m_price_txt);
                convertView = view;
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final BmbOrderDetail item = mData.get(position);
            viewHolder.bmb_m_name.setText(item.material.mtName);
            viewHolder.bmb_m_spec.setText(item.material.spec);
            viewHolder.item_state.setText(bmb_order_detail_state[Integer.parseInt(item.bmbODstate)]);
            viewHolder.bmb_m_price.setText(item.bmbODPrice);
            viewHolder.item_count.setText(item.bmbODNum);
            viewHolder.bmb_m_unit.setText(item.material.unitName);

            if(item.bmbODstate.equals("1")){
                viewHolder.layout_btn.setVisibility(View.VISIBLE);
                viewHolder.out_storage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(selectId!=null)return;
                        SnackBar snackbar = new SnackBar(OrderDetailRecActivity.this, "确认发货吗？", "确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestParams requestParams = new RequestParams();
                                requestParams.put("orderId",orderId);
                                requestParams.put("orderDetailId",item.bmbODId);
                                requestParams.put("type",type);
                                selectId = item.bmbODId;
                                postNetwork(OUT_STORAGE, requestParams, OUT_STORAGE);
                            }
                        });
                        snackbar.show();
                    }
                });
            }else {
                viewHolder.layout_btn.setVisibility(View.GONE);
            }

            return convertView;
        }

        class ViewHolder{
            TextView bmb_m_name;
            TextView bmb_m_spec;
            TextView item_state;
            TextView bmb_m_price;
            View bmb_m_price_txt;
            TextView item_count;
            TextView bmb_m_unit;
            View out_storage;
            View layout_btn;
        }
    };


    @OptionsItem(android.R.id.home)
    void home() {
        Intent intent = getIntent();
        intent.putExtra("msg_state",msg_state);
        setResult(RESULT_OK,intent);
        onBackPressed();
    }
}
