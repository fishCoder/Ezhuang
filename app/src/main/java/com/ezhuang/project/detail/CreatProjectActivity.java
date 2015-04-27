package com.ezhuang.project.detail;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.MyApp;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.ListModify;
import com.ezhuang.model.StaffUser;
import com.loopj.android.http.RequestParams;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/7 0007.
 */
@EActivity(R.layout.activity_create_project)
public class CreatProjectActivity extends BaseActivity {

    @StringArrayRes
    String[] project_item_name;
    String[] project_item_value;

    String[] project_staff_ids = null;

    List<StaffUser> pm = null;
    List<StaffUser> buyer = null;
    List<StaffUser> check = null;
    List<StaffUser> quality = null;

    @ViewById
    ListView listView;

    String QUERY_STAFF_BY_ROLE =   Global.HOST + "/app/stf/queryStaffs.do?";

    @AfterViews
    void init(){

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(pm==null){
            pm = new LinkedList<>();
            buyer = new LinkedList<>();
            check = new LinkedList<>();
            quality =  new LinkedList<>();

            String companyId = MyApp.currentUser.getCompanyId();
            int companyType = MyApp.currentUser.getCompanyType();

            getNetwork(QUERY_STAFF_BY_ROLE+"companyId="+companyId+"&companyType="+companyType+"&roleId="+Global.PROJECT_MANAGER,Global.PROJECT_MANAGER);
            getNetwork(QUERY_STAFF_BY_ROLE+"companyId="+companyId+"&companyType="+companyType+"&roleId="+Global.CEHCK,Global.CEHCK);
            getNetwork(QUERY_STAFF_BY_ROLE+"companyId="+companyId+"&companyType="+companyType+"&roleId="+Global.BUYER,Global.BUYER);
            getNetwork(QUERY_STAFF_BY_ROLE+"companyId="+companyId+"&companyType="+companyType+"&roleId="+Global.QUALITY,Global.QUALITY);

            getProjectRows();
        }

        if(project_staff_ids==null){
            project_staff_ids =  new String[]{"","","",""};
        }

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(itemClickListener);
    }

    void getProjectRows(){
        project_item_value = new String[]{
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        };
    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return project_item_name.length;
        }

        @Override
        public Object getItem(int position) {
            return project_item_value[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_2_text_align_right, parent, false);
                holder = new ViewHolder();
                holder.first = (TextView) convertView.findViewById(R.id.first);
                holder.second = (TextView) convertView.findViewById(R.id.second);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.first.setText(project_item_name[position]);

            String seondString = project_item_value[position];
            if (seondString==null||seondString.isEmpty()) {
                seondString = "未填写";
            }
            holder.second.setText(seondString);

            return convertView;
        }
    };
    class ViewHolder {
        TextView first;
        TextView second;
    }


    String CREATE_PROJECT = Global.HOST + "/app/project/addProject.do";

    @OptionsItem
    void action_add() {
        RequestParams params = new RequestParams();

        //不需要检查的字段
        List<Integer> unCheckFeild = new ArrayList<>();
        unCheckFeild.add(PJ_REMARK);
        unCheckFeild.add(O_ADDRESS);

        for (int i=0 ;i<project_item_value.length;i++){
            if(unCheckFeild.contains(i))continue;

            if(project_item_value[i].isEmpty()){
                showMiddleToast(project_item_name[i]+"需要填写");
                return;
            }
        }

        params.put("pjName",project_item_value[0]);
        params.put("pjAddress",project_item_value[1]);
        params.put("pjHousetype",project_item_value[2]);
        params.put("pjArea",project_item_value[3]);
        params.put("pjContractnum",project_item_value[4]);
        params.put("pjRemark",project_item_value[5]);

        params.put("realName",project_item_value[6]);
        params.put("address",project_item_value[7]);
        params.put("userName",project_item_value[8]);

        params.put("pjDesigner",project_item_value[9]);
        params.put("pjMId",project_staff_ids[0]);
        params.put("pjCheckerId",project_staff_ids[1]);
        params.put("pjBuyerId",project_staff_ids[2]);
        params.put("pjQualityId",project_staff_ids[3]);

        postNetwork(CREATE_PROJECT, params, CREATE_PROJECT);
        showProgressBar(false);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

        if(tag.equals(Global.PROJECT_MANAGER)){
            if(code==10001){
                JSONArray jsonArray = respanse.getJSONArray("data");
                for(int i=0;i<jsonArray.length();i++){
                    StaffUser staff = JsonUtil.Json2Object(jsonArray.getString(i),StaffUser.class);
                    pm.add(staff);
                }
            }else{

            }
        }else if(tag.equals(Global.CEHCK)){
            if(code==10001){
                JSONArray jsonArray = respanse.getJSONArray("data");
                for(int i=0;i<jsonArray.length();i++){
                    StaffUser staff = JsonUtil.Json2Object(jsonArray.getString(i),StaffUser.class);
                    check.add(staff);
                }
            }else{

            }
        }else if(tag.equals(Global.BUYER)){
            if(code==10001){
                JSONArray jsonArray = respanse.getJSONArray("data");
                for(int i=0;i<jsonArray.length();i++){
                    StaffUser staff = JsonUtil.Json2Object(jsonArray.getString(i),StaffUser.class);
                    buyer.add(staff);
                }
            }else{

            }
        }else if(tag.equals(Global.QUALITY)){
            if(code==10001){
                JSONArray jsonArray = respanse.getJSONArray("data");
                for(int i=0;i<jsonArray.length();i++){
                    StaffUser staff = JsonUtil.Json2Object(jsonArray.getString(i),StaffUser.class);
                    quality.add(staff);
                }
            }else{

            }
        }else if(tag.equals(CREATE_PROJECT)){
            if(code==10001){
                showMiddleToast("创建成功");
                getProjectRows();
                adapter.notifyDataSetChanged();
                showProgressBar(false);
            }else{

            }
        }

    }


    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_project, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public final static int PJ_NAME = 0;
    public final static int PJ_ADDRESS = 1;
    public final static int HOUSE_TYPE = 2;
    public final static int AREA = 3;
    public final static int CONTRACT_NUM = 4;
    public final static int PJ_REMARK = 5;
    public final static int O_NAME = 6;
    public final static int O_ADDRESS = 7;
    public final static int O_PHONE = 8;
    public final static int PJ_DESINER = 9;
    public final static int PJ_M = 10;
    public final static int PJ_C = 11;
    public final static int PJ_B = 12;
    public final static int PJ_Q = 13;


    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            String title = project_item_name[(int)id];
            String rowValue = project_item_value[(int)id];
            switch ((int) id){
                case PJ_NAME:
                    SetProjectInfo_.intent(CreatProjectActivity.this)
                            .title(title)
                            .row(PJ_NAME)
                            .rowValue(rowValue)
                            .startForResult(ListModify.Add);
                    break;
                case PJ_ADDRESS:
                    SetProjectInfo_
                            .intent(CreatProjectActivity.this)
                            .title(title)
                            .row(PJ_ADDRESS)
                            .rowValue(rowValue)
                            .startForResult(ListModify.Add);
                    break;
                case HOUSE_TYPE:
                    SetProjectInfo_
                            .intent(CreatProjectActivity.this)
                            .title(title)
                            .row(HOUSE_TYPE)
                            .rowValue(rowValue)
                            .startForResult(ListModify.Add);
                    break;
                case AREA:
                    SetProjectInfo_
                            .intent(CreatProjectActivity.this)
                            .title(title)
                            .row(AREA)
                            .rowValue(rowValue)
                            .startForResult(ListModify.Add);
                    break;
                case CONTRACT_NUM:
                    SetProjectInfo_
                            .intent(CreatProjectActivity.this)
                            .title(title)
                            .row(CONTRACT_NUM)
                            .rowValue(rowValue)
                            .startForResult(ListModify.Add);
                    break;
                case PJ_REMARK:
                    SetProjectInfo_
                            .intent(CreatProjectActivity.this)
                            .title(title)
                            .row(PJ_REMARK)
                            .rowValue(rowValue)
                            .startForResult(ListModify.Add);
                    break;
                case O_NAME:
                    SetProjectInfo_
                            .intent(CreatProjectActivity.this)
                            .title(title)
                            .row(O_NAME)
                            .rowValue(rowValue)
                            .startForResult(ListModify.Add);
                    break;
                case O_ADDRESS:
                    SetProjectInfo_
                            .intent(CreatProjectActivity.this)
                            .title(title)
                            .row(O_ADDRESS)
                            .rowValue(rowValue)
                            .startForResult(ListModify.Add);
                    break;
                case O_PHONE:
                    SetProjectInfo_
                            .intent(CreatProjectActivity.this)
                            .title(title)
                            .row(O_PHONE)
                            .rowValue(rowValue)
                            .startForResult(ListModify.Add);
                    break;
                case PJ_DESINER:
                    SetProjectInfo_
                            .intent(CreatProjectActivity.this)
                            .title(title)
                            .row(PJ_DESINER)
                            .rowValue(rowValue)
                            .startForResult(ListModify.Add);
                    break;
                case PJ_M:
                    showStaff(PJ_M);
                    break;
                case PJ_C:
                    showStaff(PJ_C);
                    break;
                case PJ_B:
                    showStaff(PJ_B);
                    break;
                case PJ_Q:
                    showStaff(PJ_Q);
                    break;


            }
        }
    };


    List<StaffUser> staffList = null;

    void showStaff(final int row){
        String title = "";
        if(row == PJ_M){
            title = "项目经理";
            staffList = pm;
        }else if(row == PJ_C){
            title = "审核员";
            staffList = check;
        }else if(row == PJ_B){
            title = "采购员";
            staffList = buyer;
        }else if(row == PJ_Q){
            title = "质检员";
            staffList = quality;
        }

        if(staffList.size()==0){
            showMiddleToast("请稍后 正在网络请求");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setAdapter(staffListAdapter,new Dialog.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                project_item_value[row] = staffList.get(which).getName();
                project_staff_ids[row-PJ_M] = staffList.get(which).getGlobal_key();

                adapter.notifyDataSetChanged();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
        dialog.getWindow().setAttributes(p);
        dialogTitleLineColor(dialog);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ListModify.Add){
            if(resultCode == RESULT_OK){
                int row = data.getIntExtra("row",0);
                String value = data.getStringExtra("itemValue");
                project_item_value[row] = value;

                adapter.notifyDataSetChanged();
            }
        }
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
    };

    class StaffHolder{
        ImageView imageView;
        TextView  textView;
    }
}
