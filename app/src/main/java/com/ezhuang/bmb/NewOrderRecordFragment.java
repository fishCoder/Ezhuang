package com.ezhuang.bmb;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.BmbSelfOrder;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/29 0029.
 */
@EFragment(R.layout.fragment_new_order_record)
public class NewOrderRecordFragment extends BaseFragment {

    String QUERY_BMB_ORDER = Global.HOST + "/app/bmb/queryBmbOrder.do?type=1";

    String QUERY_BMB_ORDER_MORE = Global.HOST + "/app/bmb/queryBmbOrder.do?type=1&lastId=%s";


    String QUERY_BMB_ORDER_2 = Global.HOST + "/app/bmb/queryBmbOrder.do?type=2";

    String QUERY_BMB_ORDER_MORE_2 = Global.HOST + "/app/bmb/queryBmbOrder.do?type=2&lastId=%s";

    @ViewById
    PullToRefreshListView listView;
    @ViewById
    View blankLayout;

    List<BmbSelfOrder> personData = new LinkedList<>();

    List<BmbSelfOrder> companyData = new LinkedList<>();

    List<BmbSelfOrder> mData = new LinkedList<>();

    int type =1;

    @AfterViews
    void init(){
        getNetwork(QUERY_BMB_ORDER,QUERY_BMB_ORDER);
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                if (mData.size() == 0) {
                    listView.onRefreshComplete();
                    return;
                }

                String lastId = mData.get(mData.size() - 1).orderId;

                if (PullToRefreshBase.Mode.PULL_FROM_START == listView.getCurrentMode()) {
                    if (type == 1) {
                        getNetwork(QUERY_BMB_ORDER, QUERY_BMB_ORDER);
                    } else {
                        getNetwork(QUERY_BMB_ORDER_2, QUERY_BMB_ORDER_2);
                    }

                } else {

                    if (type == 1) {
                        getNetwork(String.format(QUERY_BMB_ORDER_MORE, lastId), QUERY_BMB_ORDER_MORE);
                    } else {
                        getNetwork(String.format(QUERY_BMB_ORDER_MORE_2, lastId), QUERY_BMB_ORDER_MORE_2);
                    }

                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderDetailRecActivity_.intent(getActivity()).orderId(mData.get(position-1).orderId).type(String.valueOf(type)).start();
            }
        });
        showDialogLoading();

    }

    void changeType(int type){
        if(networkImpl == null){
            return ;
        }
        this.type = type;
        if(type==1){
            if(personData.size()==0){
                showDialogLoading();
                getNetwork(QUERY_BMB_ORDER,QUERY_BMB_ORDER);
            }else{
                mData = personData;
                adapter.notifyDataSetChanged();
            }

        }else{
            if(companyData.size()==0){
                showDialogLoading();
                getNetwork(QUERY_BMB_ORDER_2,QUERY_BMB_ORDER_2);
            }else{
                mData = companyData;
                adapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        listView.onRefreshComplete();
        hideProgressDialog();
        if(QUERY_BMB_ORDER.equals(tag)||QUERY_BMB_ORDER_MORE.equals(tag)){
            Log.d("json",respanse.optString("data"));
            if(QUERY_BMB_ORDER.equals(tag)){
                personData.clear();
            }

            JSONArray jsonArray = respanse.optJSONArray("data");
            if(jsonArray.length()!=Global.PAGE_SIZE){
                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }else{
                listView.setMode(PullToRefreshBase.Mode.BOTH);
            }

            for(int i=0;i<jsonArray.length();i++){
                personData.add(JsonUtil.Json2Object(jsonArray.getString(i),BmbSelfOrder.class));
                mData = personData;
                adapter.notifyDataSetChanged();
            }

        }else
        if(QUERY_BMB_ORDER_2.equals(tag)||QUERY_BMB_ORDER_MORE_2.equals(tag)){
            Log.d("json2",respanse.optString("data"));
            if(QUERY_BMB_ORDER_2.equals(tag)){
                companyData.clear();
            }

            JSONArray jsonArray = respanse.optJSONArray("data");
            if(jsonArray.length()!=Global.PAGE_SIZE){
                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }else{
                listView.setMode(PullToRefreshBase.Mode.BOTH);
            }

            for(int i=0;i<jsonArray.length();i++){
                companyData.add(JsonUtil.Json2Object(jsonArray.getString(i),BmbSelfOrder.class));
                mData = companyData;
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
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_new_bmb_order_rec,parent,false);
                viewHolder.bmb_order_no = (TextView) convertView.findViewById(R.id.bmb_order_no);
                viewHolder.owner_name = (TextView) convertView.findViewById(R.id.owner_name);
                viewHolder.phoneNumber = (TextView) convertView.findViewById(R.id.phoneNumber);
                viewHolder.ownerAddress = (TextView) convertView.findViewById(R.id.ownerAddress);
                viewHolder.details_count = (TextView) convertView.findViewById(R.id.details_count);
                viewHolder.total_price = (TextView) convertView.findViewById(R.id.total_price);
                viewHolder.bmb_order_time = (TextView) convertView.findViewById(R.id.bmb_order_time);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BmbSelfOrder bmbSelfOrder = mData.get(position);
            viewHolder.bmb_order_no.setText(bmbSelfOrder.orderCode);
            if(bmbSelfOrder.sOwner!=null){
                viewHolder.owner_name.setText(bmbSelfOrder.sOwner.ownerName);
                viewHolder.phoneNumber.setText(bmbSelfOrder.sOwner.phoneNumber);
                viewHolder.ownerAddress.setText(bmbSelfOrder.sOwner.ownerAddress);
            }
            if(bmbSelfOrder.sCompany!=null){
                viewHolder.owner_name.setText(bmbSelfOrder.sCompany.responsiblePerson);
                viewHolder.phoneNumber.setText(bmbSelfOrder.sCompany.telphone);
                viewHolder.ownerAddress.setText(bmbSelfOrder.sCompany.provideAddress);
            }

            viewHolder.details_count.setText(bmbSelfOrder.orderCount);
            viewHolder.total_price.setText(bmbSelfOrder.totalPrice+" 元");
            viewHolder.bmb_order_time.setText(bmbSelfOrder.orderTime);
            return convertView;
        }

        class ViewHolder{
            TextView bmb_order_no;
            TextView owner_name;
            TextView phoneNumber;
            TextView ownerAddress;
            TextView details_count;
            TextView total_price;
            TextView bmb_order_time;
        }
    };
}
