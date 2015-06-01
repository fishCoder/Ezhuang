package com.ezhuang.project;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.MyApp;
import com.ezhuang.R;
import com.ezhuang.SelectActivity;
import com.ezhuang.SelectActivity_;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.AccountInfo;
import com.ezhuang.model.Project;
import com.ezhuang.model.ProjectBill;
import com.ezhuang.model.StaffUser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/20 0020.
 */
@EActivity(R.layout.activity_view_billing)
public class ViewBillingActivity extends BaseActivity {

    EditText editText;

    @Extra
    String roleId;

    @Extra
    boolean isRecord = false;

    @ViewById
    PullToRefreshExpandableListView listView;

    @StringArrayRes
    String[] bill_state;

    String QUERY_BILL = Global.HOST + "/app/project/queryBillings.do?global_key=%s&roleId=%s&state=%s&keyword=%s&beginTime=%s&endTime=%s";

    String QUERY_BILL_MORE = Global.HOST + "/app/project/queryBillings.do?global_key=%s&roleId=%s&state=%s&lastId=%s&keyword=%s&beginTime=%s&endTime=%s";

    List<Project> mType = new LinkedList<>();

    List<List<ProjectBill>> mData = new LinkedList<>();

    String state = "";

    String startTime = "";
    String endTime = "";

    @AfterViews
    void init(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        if(Global.CEHCK.equals(roleId)){
            if(!isRecord){
                state = "0";
            }else{
                state = "1,2,3,4,5";
            }
        }else if(Global.BUYER.equals(roleId)){
            if(!isRecord){
                state = "1,2";
            }else{
                state = "3,4";
            }
        }

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(onRefreshListener);
        listView.getRefreshableView().setAdapter(adapter);
        listView.getRefreshableView().setOnChildClickListener(onChildClickListener);
        listView.getRefreshableView().setGroupIndicator(null);
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
                StaffUser staffUser = MyApp.currentUser;
                showDialogLoading();

                getNetwork(String.format(QUERY_BILL,staffUser.getGlobal_key(),roleId,state,s.toString(),startTime,endTime),QUERY_BILL);
                showDialogLoading();
            }
        });
        findViewById(R.id.action_calender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectActivity_.intent(ViewBillingActivity.this).start(startTime).end(endTime).startForResult(SelectActivity.REQUEST_CODE);
            }
        });
        StaffUser staffUser = AccountInfo.loadAccount(this);
        showDialogLoading();
        getNetwork(String.format(QUERY_BILL,staffUser.getGlobal_key(),roleId,state,"",startTime,endTime),QUERY_BILL);
    }

    BaseExpandableListAdapter adapter = new BaseExpandableListAdapter() {

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            super.registerDataSetObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            return mType.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mData.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mType.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mData.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View view;
            ViewHolderforHead viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolderforHead();
                view = mInflater.inflate(R.layout.item_bill_project,parent,false);

                viewHolder.pj_name = (TextView) view.findViewById(R.id.pj_name);
                viewHolder.pm_name = (TextView) view.findViewById(R.id.pm_name);
                viewHolder.check_name = (TextView) view.findViewById(R.id.check_name);
                viewHolder.buyer_name = (TextView) view.findViewById(R.id.buyer_name);
                viewHolder.quality_name = (TextView) view.findViewById(R.id.quality_name);

                view.setTag(viewHolder);
                convertView = view;

            }else{
                viewHolder = (ViewHolderforHead) convertView.getTag();
            }
            Project project = (Project) getGroup(groupPosition);

            viewHolder.pj_name.setText(project.getPjName());
            viewHolder.pm_name.setText(project.getPjM().getName());
            viewHolder.check_name.setText(project.getPjChecker().getName());
            viewHolder.buyer_name.setText(project.getPjBuyer().getName());
            viewHolder.quality_name.setText(project.getPjQuality().getName());


            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;

            if(convertView == null){
                viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.item_bill,parent,false);

                viewHolder.bill_no = (TextView) view.findViewById(R.id.bill_no);
                viewHolder.bill_create_time = (TextView) view.findViewById(R.id.bill_create_time);
                viewHolder.bill_state = (TextView) view.findViewById(R.id.bill_state);
                viewHolder.item_count = (TextView) view.findViewById(R.id.item_count);
                viewHolder.item_remark = (TextView) view.findViewById(R.id.item_remark);
                convertView = view;
                convertView.setTag(viewHolder);

            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ProjectBill bill = (ProjectBill) getChild(groupPosition,childPosition);

            viewHolder.bill_no.setText(bill.getBillCode());
            viewHolder.bill_create_time.setText(bill.getBillTime());
            viewHolder.bill_state.setText(bill_state[bill.getState()]);
            viewHolder.item_count.setText(""+bill.getBdCount());
            viewHolder.item_remark.setText(bill.getRemark());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {

        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }

        class ViewHolderforHead{
            TextView pj_name;
            TextView pm_name;
            TextView check_name;
            TextView buyer_name;
            TextView quality_name;
        }

        class ViewHolder{
            TextView bill_no;
            TextView bill_create_time;
            TextView bill_state;
            TextView item_count;
            TextView item_remark;
        }

    };

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        hideProgressDialog();

        if(QUERY_BILL.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                mType.clear();
                mData.clear();
                try{
                    Log.i("json",respanse.getString("data"));
                }catch (Exception e){
                    showButtomToast("没有记录");
                    return;
                }
                toObject(respanse);
            }else{
                showButtomToast("错误码:" + code);
            }
        }

        if(QUERY_BILL_MORE.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                try{
                    Log.i("json",respanse.getString("data"));
                }catch (Exception e){
                    showButtomToast("没有更多");
                    listView.onRefreshComplete();
                    return;
                }

                toObject(respanse);

            }else{
                showButtomToast("错误码:" + code);
            }
        }
    }

    @Background
    void toObject(JSONObject respanse){

        JSONArray jsonArray = null;
        try {
            jsonArray = respanse.getJSONArray("data");
            for (int i=0 ; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                mType.add(JsonUtil.Json2Object(jsonObject.getString("project"),Project.class));

                List<ProjectBill> projectBills = new LinkedList<>();
                JSONArray jsonBills = jsonObject.getJSONArray("bills");
                for (int k=0; k < jsonBills.length();k++){
                    projectBills.add(JsonUtil.Json2Object(jsonBills.getString(k),ProjectBill.class));
                }
                mData.add(projectBills);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        toUI();
    }

    @UiThread
    void toUI(){
        listView.onRefreshComplete();
        hideProgressDialog();
        adapter.notifyDataSetChanged();
        expandGroup();
    }

    ExpandableListView.OnChildClickListener onChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            ProjectBill bill = mData.get(groupPosition).get(childPosition);
            ViewBillDetailActivity_.intent(ViewBillingActivity.this)
                    .pjId(mType.get(groupPosition).getPjId())
                    .roleId(roleId)
                    .staffId(MyApp.currentUser.getGlobal_key())
                    .pjBillId(bill.getId())
                    .billState(bill.getState())
                    .isRecord(isRecord)
                    .startForResult(0);
            return false;
        }
    };

    @OnActivityResult(0)
    void changeBillState(int resultCode,Intent data){
        if(resultCode == RESULT_OK){
            String pjBillId = data.getStringExtra("pjBillId");
            int state = data.getIntExtra("state",0);

            for(List<ProjectBill> list:mData){
                for (ProjectBill bill:list){
                    if(bill.getId().equals(pjBillId)){
                        bill.setState(state);
                        adapter.notifyDataSetChanged();
                        return ;
                    }

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == SelectActivity.REQUEST_CODE ){
            if(resultCode == RESULT_OK){
                startTime = data.getStringExtra("start");
                endTime = data.getStringExtra("end");
                getNetwork(String.format(QUERY_BILL,MyApp.currentUser.getGlobal_key(),roleId,state,editText.getText().toString(),startTime,endTime),QUERY_BILL);
                showDialogLoading();
            }
        }
    }

    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            StaffUser staffUser = MyApp.currentUser;
            if(PullToRefreshBase.Mode.PULL_FROM_START == listView.getCurrentMode()){
                getNetwork(String.format(QUERY_BILL,staffUser.getGlobal_key(),roleId,state,editText.getText().toString(),startTime,endTime),QUERY_BILL);
            }else{
                int i = mData.size()-1;
                int index = mData.get(i).size()-1;
                String lastId = mData.get(i).get(index).getId();
                getNetwork(String.format(QUERY_BILL_MORE,staffUser.getGlobal_key(),roleId,state,lastId,editText.getText().toString(),startTime,endTime),QUERY_BILL_MORE);
            }
        }
    };


    void expandGroup(){
        for (int i=0; i<mData.size(); i++){
            listView.getRefreshableView().expandGroup(i);
        }
    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }

}
