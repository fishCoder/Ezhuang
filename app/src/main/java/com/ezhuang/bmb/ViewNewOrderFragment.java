package com.ezhuang.bmb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.ListModify;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.BmbOrderDetail;
import com.ezhuang.model.SpMaterial;
import com.ezhuang.project.detail.SetProjectInfo_;
import com.loopj.android.http.RequestParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/6/24 0024.
 */
@EFragment(R.layout.fragment_view_new_order)
public class ViewNewOrderFragment extends BaseFragment {

    View headerView;
    TextView txt_address;
    TextView txt_phone;
    TextView txt_name;

    TextView txt_cp_address;
    TextView txt_cp_phone;
    TextView txt_cp_name;
    TextView txt_sp_name;
    TextView txt_sp_address;

    View person;
    View person_bar;
    View company;
    View company_bar;

    View layout_person;
    View layout_company;

    String val_address="";
    String val_phone="";
    String val_name="";

    String val_sp_name="";
    String val_sp_address="";

    String ADD_BMB_ORDER = Global.HOST + "/app/bmb/addBmbOrder.do";

    final public static int SET_ADDRESS=18;
    final public static int SET_PHONE  =19;
    final public static int SET_NAME   =20;
    final public static int SET_SP_NAME   =21;
    final public static int SET_SP_ADDRESS   =22;

    boolean isPersonOrder = true;

    @ViewById
    ListView listView;

    @ViewById
    View blankLayout;

    public List<SpMaterial> mData = new LinkedList<>();

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

    }


    @AfterViews
    void init(){
        if (headerView == null){
            headerView = mInflater.inflate(R.layout.head_view_new_order,null);
            txt_address = (TextView) headerView.findViewById(R.id.txt_address);
            txt_phone = (TextView) headerView.findViewById(R.id.txt_phone);
            txt_name = (TextView) headerView.findViewById(R.id.txt_name);

            txt_cp_address = (TextView) headerView.findViewById(R.id.txt_cp_address);
            txt_cp_phone = (TextView) headerView.findViewById(R.id.txt_cp_phone);
            txt_cp_name = (TextView) headerView.findViewById(R.id.txt_cp_name);

            txt_sp_name = (TextView) headerView.findViewById(R.id.txt_sp_name);
            txt_sp_address = (TextView) headerView.findViewById(R.id.txt_sp_address);

            layout_person = headerView.findViewById(R.id.layout_person);
            layout_company = headerView.findViewById(R.id.layout_company);

            person = headerView.findViewById(R.id.person);
            person.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isPersonOrder){
                        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.left_slide);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                person_bar.setVisibility(View.VISIBLE);
                                company_bar.setVisibility(View.GONE);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        company_bar.startAnimation(animation);
                        person_bar.startAnimation(animation);
                        isPersonOrder = !isPersonOrder;

                        Animation list_top_up = AnimationUtils.loadAnimation(getActivity(), R.anim.listview_top_up);
                        list_top_up.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                layout_company.setVisibility(View.GONE);
                                layout_person.setVisibility(View.VISIBLE);
                                Animation list_top_down = AnimationUtils.loadAnimation(getActivity(), R.anim.listview_top_down);
                                layout_person.startAnimation(list_top_down);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        layout_company.setAnimation(list_top_up);

                    }

                }
            });
            person_bar = headerView.findViewById(R.id.person_bar);
            company = headerView.findViewById(R.id.company);
            company.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isPersonOrder){
                        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.right_slide);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                person_bar.setVisibility(View.GONE);
                                company_bar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        company_bar.startAnimation(animation);
                        person_bar.startAnimation(animation);
                        isPersonOrder = !isPersonOrder;

                        Animation list_top_up = AnimationUtils.loadAnimation(getActivity(), R.anim.listview_top_up);
                        list_top_up.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                layout_person.setVisibility(View.GONE);
                                layout_company.setVisibility(View.VISIBLE);
                                Animation list_top_down = AnimationUtils.loadAnimation(getActivity(), R.anim.listview_top_down);
                                layout_company.startAnimation(list_top_down);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        layout_person.setAnimation(list_top_up);
                    }

                }
            });
            company_bar = headerView.findViewById(R.id.company_bar);



            headerView.findViewById(R.id.layout_send_address).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_send_address();
                }
            });
            headerView.findViewById(R.id.layout_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_name();
                }
            });
            headerView.findViewById(R.id.layout_phone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_phone();
                }
            });

            headerView.findViewById(R.id.layout_cp_send_address).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_send_address();
                }
            });
            headerView.findViewById(R.id.layout_cp_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_name();
                }
            });
            headerView.findViewById(R.id.layout_cp_phone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_phone();
                }
            });
            headerView.findViewById(R.id.layout_sp_name).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetProjectInfo_
                            .intent(ViewNewOrderFragment.this)
                            .title("公司名称")
                            .row(SET_SP_NAME)
                            .rowValue(val_phone)
                            .startForResult(ListModify.Add);
                }
            });
            headerView.findViewById(R.id.layout_sp_address).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetProjectInfo_
                            .intent(ViewNewOrderFragment.this)
                            .title("公司地址")
                            .row(SET_SP_ADDRESS)
                            .rowValue(val_phone)
                            .startForResult(ListModify.Add);
                }
            });
        }else{
            listView.removeHeaderView(headerView);
        }

        listView.addHeaderView(headerView);

        listView.setAdapter(adapter);
    }


    void addData(SpMaterial spMaterial){
        mData.add(spMaterial);
    }

    boolean containData(SpMaterial spMaterial){
        for(SpMaterial sp : mData){
            if(sp.mtId.equals(spMaterial.mtId)){
                return true;
            }
        }
        return false;
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
            if(convertView==null){
                convertView = mInflater.inflate(R.layout.item_bmb_new_order,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.close = convertView.findViewById(R.id.action_close);
                viewHolder.laytout_remark = convertView.findViewById(R.id.layout_remark);
                viewHolder.mt_name = (TextView) convertView.findViewById(R.id.bmb_m_name);
                viewHolder.mt_spec = (TextView) convertView.findViewById(R.id.bmb_m_spec);
                viewHolder.mt_dosage = (TextView) convertView.findViewById(R.id.item_count);
                viewHolder.mt_unit_name = (TextView) convertView.findViewById(R.id.unit_name);
                viewHolder.mt_remark = (TextView) convertView.findViewById(R.id.item_remark);
                viewHolder.mt_price = (TextView) convertView.findViewById(R.id.bmb_m_price);
                viewHolder.mt_total = (TextView) convertView.findViewById(R.id.bmb_m_total);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            SpMaterial spMaterial = mData.get(position);
            viewHolder.close.setOnClickListener(new ItemClose(position));
            if(spMaterial.item_remark==null||spMaterial.item_remark.isEmpty()){
                viewHolder.mt_remark.setVisibility(View.GONE);
            }else{
                viewHolder.mt_remark.setVisibility(View.VISIBLE);
                viewHolder.mt_remark.setText(spMaterial.item_remark);
            }

            viewHolder.mt_name.setText(spMaterial.mtName);
            viewHolder.mt_spec.setText(spMaterial.spec);
            viewHolder.mt_dosage.setText(spMaterial.item_count);
            viewHolder.mt_unit_name.setText(spMaterial.unitName);
            viewHolder.mt_price.setText(spMaterial.bmb_price);
            viewHolder.mt_total.setText(String.format("%.2f",Float.parseFloat(spMaterial.bmb_price)*Float.parseFloat(spMaterial.item_count)));

            return convertView;
        }

        class ViewHolder{
            View close;
            View laytout_remark;
            TextView mt_name;
            TextView mt_spec;
            TextView mt_unit_name;
            TextView mt_dosage;
            TextView mt_remark;
            TextView mt_price;
            TextView mt_total;
        }
    };

    class ItemClose implements View.OnClickListener{

        int index;
        public ItemClose(int index){
            this.index = index;
        }
        @Override
        public void onClick(View v) {
            mData.get(index).item_count = "";
            mData.remove(index);
            adapter.notifyDataSetChanged();
        }
    }



    void layout_send_address(){
        SetProjectInfo_
                .intent(ViewNewOrderFragment.this)
                .title("地址")
                .row(SET_ADDRESS)
                .rowValue(val_address)
                .startForResult(ListModify.Add);
    }

    void layout_name(){
        SetProjectInfo_
                .intent(ViewNewOrderFragment.this)
                .title("姓名")
                .row(SET_NAME)
                .rowValue(val_name)
                .startForResult(ListModify.Add);
    }


    void layout_phone(){
        SetProjectInfo_
                .intent(ViewNewOrderFragment.this)
                .title("电话")
                .row(SET_PHONE)
                .rowValue(val_phone)
                .startForResult(ListModify.Add);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ListModify.Add){
            if(resultCode == Activity.RESULT_OK){
                int row = data.getIntExtra("row",0);
                String value = data.getStringExtra("itemValue");
                if(row == SET_ADDRESS){
                    val_address = value;
                    txt_address.setText(val_address);
                    txt_cp_address.setText(val_address);
                }else
                if(row == SET_NAME){
                    val_name = value;
                    txt_name.setText(val_name);
                    txt_cp_name.setText(val_name);
                }else
                if(row == SET_PHONE){
                    val_phone = value;
                    txt_phone.setText(val_phone);
                    txt_cp_phone.setText(val_phone);
                }else
                if(row == SET_SP_NAME){
                    val_sp_name = value;
                    txt_sp_name.setText(val_sp_name);
                }else
                if(row == SET_SP_ADDRESS){
                    val_sp_address = value;
                    txt_sp_address.setText(val_sp_name);
                }
            }
        }
    }

    void setHeadInfo(){
        txt_address.setText(val_address);
        txt_cp_address.setText(val_address);

        txt_name.setText(val_name);
        txt_cp_name.setText(val_name);

        txt_phone.setText(val_phone);
        txt_cp_phone.setText(val_phone);

        txt_sp_name.setText(val_sp_name);

        txt_sp_address.setText(val_sp_name);
    }

    void newBmbOrder(){
        List<BmbOrderDetail> details = new LinkedList<>();
        for(SpMaterial spMaterial : mData){
            BmbOrderDetail detail = new BmbOrderDetail();
            detail.bmbODMId = spMaterial.mtId;
            detail.bmbODNum = spMaterial.item_count;
            detail.bmbODPrice = spMaterial.bmb_price;
            details.add(detail);
        }

        RequestParams requestParams = new RequestParams();
        requestParams.put("sOrder", JsonUtil.Object2Json(details));
        if(isPersonOrder){
            if(val_address.isEmpty()){
                showButtomToast("地址不能为空");
                return;
            }
            if(val_phone.isEmpty()){
                showButtomToast("电话不能为空");
                return;
            }
            if(val_name.isEmpty()){
                showButtomToast("姓名不能为空");
                return;
            }

            Map<String,Object> data = new HashMap<>();
            data.put("ownerAddress",val_address);
            data.put("phoneNumber",val_phone);
            data.put("ownerName",val_name);
            requestParams.put("sOwner", JsonUtil.Object2Json(data));
        }else{
            if(val_address.isEmpty()){
                showButtomToast("地址不能为空");
                return;
            }
            if(val_phone.isEmpty()){
                showButtomToast("电话不能为空");
                return;
            }
            if(val_name.isEmpty()){
                showButtomToast("姓名不能为空");
                return;
            }
            if(val_sp_address.isEmpty()){
                showButtomToast("公司地址不能为空");
                return;
            }
            if(val_sp_name.isEmpty()){
                showButtomToast("公司不能为空");
                return;
            }

            Map<String,Object> data = new HashMap<>();
            data.put("reviceAddress",val_address);
            data.put("telphone",val_phone);
            data.put("responsiblePerson",val_name);
            data.put("providerName",val_sp_name);
            data.put("address", val_sp_address);
            requestParams.put("sCompany", JsonUtil.Object2Json(data));
        }
        postNetwork(ADD_BMB_ORDER, requestParams, ADD_BMB_ORDER);
        showProgressBar(true,"提交中");
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        super.parseJson(code, respanse, tag, pos, data);
        showProgressBar(false);
        if(ADD_BMB_ORDER.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                val_address = "";
                val_phone = "";
                val_name = "";
                val_sp_name = "";
                val_sp_address = "";
                mData.clear();
                setHeadInfo();
                adapter.notifyDataSetChanged();
                showButtomToast("提交成功");
            }else{
                showButtomToast("错误代码:"+code);
            }
        }
    }
}
