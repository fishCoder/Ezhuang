package com.ezhuang.bmb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.ActionBar;
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
public class OrderDetailActivity extends BaseActivity {

    String QUERY_ORDER_DETAIL = Global.HOST + "/app/bmb/queryOrderDetail.do?bmbPcId=%s";
    String QUERY_STAFF = Global.HOST + "/app/bmb/queryOrderStaffs.do";
    String POINT_STORAGE = Global.HOST + "/app/bmb/dispatchOrder.do";
    String OUT_STORAGE = Global.HOST + "/app/bmb/deliverGood.do";

    @StringArrayRes
    String[] bmb_order_detail_state;

    @Extra
    String bmbPcId;
    @Extra
    String roleId;
    @ViewById
    ListView listView;

    String selectId;

    List<BmbOdItem> mData = new LinkedList<>();

    List<StaffUser> staffList = new LinkedList<>();

    View headView;

    int msg_state = 1;

    @AfterViews
    void init(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        if(headView==null){
            headView = mInflater.inflate(R.layout.layout_order_info,null);
            listView.addHeaderView(headView);
        }

        if(mData.size()==0){
            showDialogLoading();
            getNetwork(String.format(QUERY_ORDER_DETAIL,bmbPcId),QUERY_ORDER_DETAIL);
        }
        listView.setAdapter(adapter);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        hideProgressDialog();

        if(tag.equals(QUERY_ORDER_DETAIL)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                JSONObject jsonObject = respanse.getJSONObject("data");
                if(jsonObject==null)return;
                fillHeadView(jsonObject.getJSONObject("orderDetail"));

                JSONArray jsonArray = jsonObject.getJSONArray("orderDetailList");
                for (int i=0;i<jsonArray.length();i++){
                    mData.add(JsonUtil.Json2Object(jsonArray.getString(i),BmbOdItem.class));
                    adapter.notifyDataSetChanged();
                }
            }
        }else if(QUERY_STAFF.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                JSONArray jsonArray = respanse.getJSONArray("data");
                for(int i=0;i<jsonArray.length();i++){
                    StaffUser staff = JsonUtil.Json2Object(jsonArray.getString(i),StaffUser.class);
                    staffList.add(staff);
                }
                showStaffList();
            }
        }else if(POINT_STORAGE.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                headView.findViewById(R.id.layout_point).setVisibility(View.GONE);
                msg_state = 4;
            }else{
                showButtomToast(String.format("错误码：%d",code));
            }
        }else if(OUT_STORAGE.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                for (BmbOdItem item : mData){
                    if(item.spOdId.equals(selectId)){
                        selectId = null;
                        item.spOdState = item.spOdState + 1;
                        break;
                    }
                }

                boolean flag = true;
                for (BmbOdItem item : mData){
                    if(item.spOdState<2){
                        flag = false;
                    }
                }
                if(flag){
                    msg_state = 4;
                }

                adapter.notifyDataSetChanged();
            }else{
                selectId = null;
                showButtomToast(String.format("错误码：%d",code));
            }
        }

    }

    void fillHeadView(JSONObject jsonObject){
        TextView sp_name = (TextView) headView.findViewById(R.id.sp_name);
        sp_name.setText(jsonObject.optString("spName"));
        TextView pj_address = (TextView) headView.findViewById(R.id.pjAddress);
        pj_address.setText(jsonObject.optString("pjAddress"));
        TextView managerName = (TextView) headView.findViewById(R.id.managerName);
        managerName.setText(jsonObject.optString("managerName"));
        TextView managerPhone = (TextView) headView.findViewById(R.id.managerPhone);
        final String sManagerPhone = jsonObject.optString("managerPhone");
        managerPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        managerPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sManagerPhone));
                startActivity(intent);
            }
        });
        managerPhone.setText(sManagerPhone);
        TextView buyerName = (TextView) headView.findViewById(R.id.buyerName);
        buyerName.setText(jsonObject.optString("buyerName"));
        TextView buyerPhone = (TextView) headView.findViewById(R.id.buyerPhone);
        final String sBuyerPhone = jsonObject.optString("buyerPhone");
        buyerPhone.setText(sBuyerPhone);
        buyerPhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        buyerPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + sBuyerPhone));
                startActivity(intent);
            }
        });

        TextView storage = (TextView) findViewById(R.id.storage);
        storage.setTextColor(getResources().getColor(R.color.action_blue));


        String bmbStaffName = jsonObject.optString("bmbStaffName");

        if(bmbStaffName==null||bmbStaffName.isEmpty()){
            headView.findViewById(R.id.layout_point).setVisibility(View.VISIBLE);
            headView.findViewById(R.id.layout_point).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(staffList.size()==0){
                        showDialogLoading();
                        getNetwork(QUERY_STAFF,QUERY_STAFF);
                    }else{
                        showStaffList();
                    }

                }
            });
        }else{
            final String bmbPhone = jsonObject.optString("bmbPhone");
            headView.findViewById(R.id.layout_show).setVisibility(View.VISIBLE);
            TextView storageName = (TextView) headView.findViewById(R.id.bmbStaffName);
            storageName.setText(bmbStaffName);
            TextView storagePhone = (TextView) headView.findViewById(R.id.bmbPhone);
            storagePhone.setText(bmbPhone);
            storagePhone.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            storagePhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bmbPhone));
                    startActivity(intent);
                }
            });
        }

    }

    void showStaffList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
        builder.setTitle(getResources().getString(R.string.point));
        builder.setAdapter(staffListAdapter, new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

            final StaffUser user = staffList.get(which);

            SnackBar snackbar = new SnackBar(OrderDetailActivity.this, String.format("确定指派 %s 吗？", user.getName()), "确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                RequestParams requestParams = new RequestParams();
                requestParams.put("bmbPcId",bmbPcId);
                requestParams.put("bmbStaffId",user.getGlobal_key());
                postNetwork(POINT_STORAGE, requestParams, POINT_STORAGE);
                }
            });
            snackbar.show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
        dialog.getWindow().setAttributes(p);
        dialogTitleLineColor(dialog);
    }

    BaseAdapter staffListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return staffList.size();
        }

        @Override
        public Object getItem(int position) {
            return staffList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            StaffHolder staffHolder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.item_staff_role, parent, false);
                staffHolder = new StaffHolder();
                staffHolder.imageView = (ImageView) convertView.findViewById(R.id.staff_avator);
                staffHolder.textView = (TextView) convertView.findViewById(R.id.staff_name);
                convertView.setTag(staffHolder);
            }else{
                staffHolder = (StaffHolder) convertView.getTag();
            }
            StaffUser staff = (StaffUser) getItem(position);
            if(staff.getAvatar()!=null&&!staff.getAvatar().isEmpty())
                iconfromNetwork(staffHolder.imageView,staff.getAvatar());
            staffHolder.textView.setText(staff.getName());
            return convertView;
        }

        class StaffHolder{
            ImageView imageView;
            TextView  textView;
        }
    };

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
            final BmbOdItem item = mData.get(position);
            viewHolder.bmb_m_name.setText(item.spOdMtName);
            viewHolder.bmb_m_spec.setText(item.mtSpec);
            viewHolder.item_state.setText(bmb_order_detail_state[item.spOdState]);
            viewHolder.bmb_m_price.setText(item.spOdPrice);
            viewHolder.item_count.setText(item.spOdMtNum);
            viewHolder.bmb_m_unit.setText(item.spOdMtUnitName);
            if(Global.STORAGE.equals(roleId)){
                viewHolder.bmb_m_price_txt.setVisibility(View.GONE);
            }
            if(Global.STORAGE.equals(roleId)&&item.spOdState==1){
                viewHolder.layout_btn.setVisibility(View.VISIBLE);
                viewHolder.out_storage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(selectId!=null)return;
                        SnackBar snackbar = new SnackBar(OrderDetailActivity.this, "确认发货吗？", "确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestParams requestParams = new RequestParams();
                                requestParams.put("pcId",item.spOdId);
                                selectId = item.spOdId;
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
