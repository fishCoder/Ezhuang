package com.ezhuang.bmb;

import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.BmbOrder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

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
 * Created by Administrator on 2015/6/13 0013.
 */
@EActivity(R.layout.activity_orders)
public class OrdersActivity extends BaseActivity {

    @StringArrayRes
    String[] bmb_order_state;

    String QUERY_ORDERS = Global.HOST + "/app/bmb/queryOrders.do?roleId=%s";
    String QUERY_ORDERS_MORE = Global.HOST + "/app/bmb/queryOrders.do?roleId=%s&lastId=%s";

    @Extra
    String roleId;

    @ViewById
    PullToRefreshListView listView;



    List<BmbOrder>  mData = new LinkedList<>();

    @AfterViews
    void init(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        if(mData.size()==0){
            getNetwork(String.format(QUERY_ORDERS,roleId),QUERY_ORDERS);
            showDialogLoading();
        }

        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (PullToRefreshBase.Mode.PULL_FROM_START == listView.getCurrentMode()) {
                    getNetwork(String.format(QUERY_ORDERS, roleId), QUERY_ORDERS);
                } else {
                    String lastId = mData.get(mData.size() - 1).spOrderId;
                    getNetwork(String.format(QUERY_ORDERS_MORE, roleId, lastId), QUERY_ORDERS_MORE);
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BmbOrder bmbOrder = mData.get(position-1);
                OrderDetailActivity_.intent(OrdersActivity.this).bmbPcId(bmbOrder.spOrderId).roleId(roleId).start();
            }
        });
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        hideProgressDialog();
        listView.onRefreshComplete();
        if (QUERY_ORDERS.equals(tag)||QUERY_ORDERS_MORE.equals(QUERY_ORDERS_MORE)){
            if(NetworkImpl.REQ_SUCCESSS==code){
                JSONArray jsonArray = respanse.getJSONArray("data");
                if(jsonArray==null){
                    return;
                }
                if(QUERY_ORDERS.equals(tag)){
                    mData.clear();
                }
                int len = jsonArray.length();
                if(len<Global.PAGE_SIZE){
                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
                for (int i=0;i<len;i++){
                    mData.add(JsonUtil.Json2Object(jsonArray.getString(i),BmbOrder.class));
                }
                adapter.notifyDataSetChanged();
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
            ViewHolder viewHolder ;
            if(convertView==null){
                viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.item_bmb_order,parent,false);
                viewHolder.bmb_order_no = (TextView) view.findViewById(R.id.bmb_order_no);
                viewHolder.sp_name = (TextView) view.findViewById(R.id.sp_name);
                viewHolder.staff_name = (TextView) view.findViewById(R.id.staff_name);
                viewHolder.bmb_order_time = (TextView) view.findViewById(R.id.bmb_order_time);
                viewHolder.bmb_order_state = (TextView) view.findViewById(R.id.bmb_order_state);
                viewHolder.details_count = (TextView) view.findViewById(R.id.details_count);
                view.setTag(viewHolder);
                convertView = view;
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BmbOrder bmbOrder = mData.get(position);

            viewHolder.bmb_order_no.setText(bmbOrder.spOrderNo);
            viewHolder.sp_name.setText(bmbOrder.spName);
            if(bmbOrder.bmbStaffName==null||bmbOrder.bmbStaffName.isEmpty()){
                bmbOrder.bmbStaffName = getResources().getString(R.string.undispatch);
            }
            viewHolder.staff_name.setText(bmbOrder.bmbStaffName);
            viewHolder.bmb_order_time.setText(bmbOrder.spOrderTime);
            viewHolder.bmb_order_state.setText(bmb_order_state[bmbOrder.spOrderState]);
            viewHolder.details_count.setText(bmbOrder.detailsCount);

            return convertView;
        }

        class ViewHolder{
            TextView bmb_order_no;
            TextView sp_name;
            TextView staff_name;
            TextView bmb_order_time;
            TextView bmb_order_state;
            TextView details_count;
        }
    };

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }
}
