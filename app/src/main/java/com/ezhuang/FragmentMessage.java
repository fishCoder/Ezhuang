package com.ezhuang;


import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezhuang.bmb.OrderDetailActivity_;
import com.ezhuang.common.BlankViewDisplay;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.BillState;
import com.ezhuang.model.Message;
import com.ezhuang.model.NewsTypeEnum;

import com.ezhuang.model.Project;
import com.ezhuang.project.AddMaterialToBillActivity_;
import com.ezhuang.project.ProjectDetailActivity_;
import com.ezhuang.project.ViewBillDetailActivity_;
import com.ezhuang.purchase.PurchaseRecordDetailActivity_;
import com.ezhuang.quality.ProgressDetailActivity_;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/7 0007.
 */
@EFragment(R.layout.tab_bar_message)
public class FragmentMessage extends BaseFragment {

    @ViewById
    PullToRefreshListView listView;

    @ViewById
    View blankLayout;

    List<Message> mData = new LinkedList<>();

    Map<String,Project> mProject = new HashMap<>();

    String MESSAGE_LIST = Global.HOST + "/app/news/queryNews.do";
    String MESSAGE_LIST_MORE = Global.HOST + "/app/news/queryNews.do?lastId=%s";

    @AfterViews
    void init(){

        if(mData.size()==0){
            getNetwork(MESSAGE_LIST,MESSAGE_LIST);
            showDialogLoading();
        }

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnRefreshListener(onRefreshListener);

    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        hideProgressDialog();
        listView.onRefreshComplete();
        if(MESSAGE_LIST.equals(tag)||MESSAGE_LIST_MORE.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                if(MESSAGE_LIST.equals(tag))
                    mData.clear();

                JSONArray jsonArray = respanse.getJSONObject("data").getJSONArray("news");
                int len = jsonArray.length();

                for (int i=0 ; i< len ; i++){
                    mData.add(JsonUtil.Json2Object(jsonArray.getString(i),Message.class));
                }

                if(len==Global.PAGE_SIZE){
                    listView.setMode(PullToRefreshBase.Mode.BOTH);
                }else{
                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
                try{
                    jsonArray = respanse.getJSONObject("data").getJSONArray("projects");
                }catch(Exception e){
                    BlankViewDisplay.setBlank(mData.size(), this, true, blankLayout, null);
                    return;
                }

                for (int i=0 ; i<jsonArray.length() ; i++){
                    Project project = JsonUtil.Json2Object(jsonArray.getString(i),Project.class);
                    if(mProject.get(project.getPjId())==null){
                        mProject.put(project.getPjId(),project);
                    }
                }
                adapter.notifyDataSetChanged();
            }else{

            }
        }

        BlankViewDisplay.setBlank(mData.size(), this, true, blankLayout, null);
    }


    void refreshData(){
        if(networkImpl!=null){
            getNetwork(MESSAGE_LIST,MESSAGE_LIST);
        }
    }

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
                view = mInflater.inflate(R.layout.item_message,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.msg_img = (ImageView) view.findViewById(R.id.msg_img);
                viewHolder.msg_title = (TextView) view.findViewById(R.id.msg_title);
                viewHolder.msg_content = (TextView) view.findViewById(R.id.msg_content);
                viewHolder.msg_time = (TextView) view.findViewById(R.id.msg_time);
                viewHolder.msg_badge = view.findViewById(R.id.badge);
                convertView = view;
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Message msg = (Message) getItem(position);
            viewHolder.msg_title.setText(msg.getTitle());
            viewHolder.msg_content.setText(msg.getContent());
            viewHolder.msg_time.setText(Global.dataToNow(msg.getTime()));
            viewHolder.msg_img.setImageResource(getMsgIcomRes(msg.newsType));
            if(msg.state==2 || msg.state==4){
                viewHolder.msg_badge.setVisibility(View.GONE);
            }else{
                viewHolder.msg_badge.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        class ViewHolder{
            ImageView msg_img;
            TextView msg_title;
            TextView msg_content;
            TextView msg_time;
            View     msg_badge;
        }
    };


    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            if(PullToRefreshBase.Mode.PULL_FROM_START == listView.getCurrentMode()){
                getNetwork(MESSAGE_LIST,MESSAGE_LIST);
            }else{
                getNetwork(String.format(MESSAGE_LIST_MORE,mData.get(mData.size()-1).newsId),MESSAGE_LIST_MORE);
            }
        }
    };

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("position",""+position);
            int i = position-1;
            jumpToDealActivity(mData.get(i),i);
        }
    };

    int getMsgIcomRes(int newsType){

        if(newsType==NewsTypeEnum.NewPrijectNoticeToManager.newsType){
            return NewsTypeEnum.NewPrijectNoticeToManager.newIcon;
        }else
        if(newsType==NewsTypeEnum.NewPrijectNoticeToBuyer.newsType){
            return NewsTypeEnum.NewPrijectNoticeToBuyer.newIcon;
        }else
        if(newsType==NewsTypeEnum.NewPrijectNoticeToChecker.newsType){
            return NewsTypeEnum.NewPrijectNoticeToChecker.newIcon;
        }else
        if(newsType==NewsTypeEnum.NewPrijectOrderNotice.newsType){
            return NewsTypeEnum.NewPrijectOrderNotice.newIcon;

        }else
        if(newsType==NewsTypeEnum.NewPrijectNoticeToQuality.newsType){
            return NewsTypeEnum.NewPrijectNoticeToQuality.newIcon;
        }else
        if(newsType==NewsTypeEnum.ProjectOrderCheckPassNoticeToManager.newsType){
            return NewsTypeEnum.ProjectOrderCheckPassNoticeToManager.newIcon;
        }else
        if(newsType==NewsTypeEnum.ProjectOrderCheckNotPassNoticeToManager.newsType){
            return NewsTypeEnum.ProjectOrderCheckNotPassNoticeToManager.newIcon;
        }else
        if(newsType==NewsTypeEnum.ProjectOrderCheckResultNoticeToBuyer.newsType){
            return NewsTypeEnum.ProjectOrderCheckResultNoticeToBuyer.newIcon;
        }else
        if(newsType==NewsTypeEnum.NewPurchaseOrderNotice.newsType){
            return NewsTypeEnum.NewPurchaseOrderNotice.newIcon;
        }else
        if(newsType==NewsTypeEnum.NewPrijectProgressNoticeToQuality.newsType){
            return NewsTypeEnum.NewPrijectProgressNoticeToQuality.newIcon;
        }else
        if(newsType==NewsTypeEnum.QualityCheckPrijectProgressNotice.newsType){
            return  NewsTypeEnum.QualityCheckPrijectProgressNotice.newIcon;
        }

        return R.mipmap.ic_default_image;
    }

    void jumpToDealActivity(Message msg,int index){

        int newsType = msg.newsType;
        if(newsType==NewsTypeEnum.NewPrijectNoticeToManager.newsType){
            ProjectDetailActivity_
                    .intent(this)
                    .projectId(msg.source)
                    .roleId(Global.PROJECT_MANAGER)
                    .global_key(MyApp.currentUser.getGlobal_key())
                    .start();
            msg.state = 2;
        }else
        if(newsType==NewsTypeEnum.NewPrijectNoticeToBuyer.newsType){
            ProjectDetailActivity_
                    .intent(this)
                    .projectId(msg.source)
                    .roleId(Global.BUYER)
                    .global_key(MyApp.currentUser.getGlobal_key())
                    .start();
            msg.state = 2;
        }else
        if(newsType==NewsTypeEnum.NewPrijectNoticeToChecker.newsType){
            ProjectDetailActivity_
                    .intent(this)
                    .projectId(msg.source)
                    .roleId(Global.CEHCK)
                    .global_key(MyApp.currentUser.getGlobal_key())
                    .start();
            msg.state = 2;
        }else
        if(newsType==NewsTypeEnum.NewPrijectOrderNotice.newsType){
            //通知审核员审核
            int billState = BillState.UNCHECK.state;
            if(msg.state == 4){
                billState = BillState.UNBUY.state;
            }
            ViewBillDetailActivity_.intent(getActivity())
                    .pjId(msg.newsPjId)
                    .roleId(Global.CEHCK)
                    .staffId(MyApp.currentUser.getGlobal_key())
                    .pjBillId(msg.source)
                    .billState(billState)
                    .project(mProject.get(msg.newsPjId))
                    .isRecord(true)
                    .startForResult(index);

        }else
        if(newsType==NewsTypeEnum.NewPrijectNoticeToQuality.newsType){
            ProjectDetailActivity_
                    .intent(this)
                    .projectId(msg.source)
                    .roleId(Global.QUALITY)
                    .global_key(MyApp.currentUser.getGlobal_key())
                    .start();
            msg.state = 2;
        }else
        if(newsType==NewsTypeEnum.ProjectOrderCheckPassNoticeToManager.newsType){
            //开单通过
            ViewBillDetailActivity_.intent(getActivity())
                    .project(mProject.get(msg.newsPjId))
                    .pjBillId(msg.source)
                    .roleId(Global.PROJECT_MANAGER)
                    .billState(BillState.UNBUY.state)
                    .isRecord(true)
                    .start();
            msg.state = 2;
        }else
        if(newsType==NewsTypeEnum.ProjectOrderCheckNotPassNoticeToManager.newsType){
            //开单驳回
            AddMaterialToBillActivity_.intent(getActivity())
                    .project(mProject.get(msg.newsPjId))
                    .newsId(msg.newsId)
                    .pjBillId(msg.source)
                    .startForResult(index);
        }else
        if(newsType==NewsTypeEnum.ProjectOrderCheckResultNoticeToBuyer.newsType){

            int billState = BillState.UNBUY.state;
            if(msg.state == 4){
                billState = BillState.BUYALL.state;
            }

            ViewBillDetailActivity_.intent(getActivity())
                    .pjBillId(msg.source)
                    .roleId(Global.BUYER)
                    .pjId(msg.newsPjId)
                    .billState(billState)
                    .project(mProject.get(msg.newsPjId))
                    .startForResult(index);

        }else
        if(newsType==NewsTypeEnum.NewPurchaseOrderNotice.newsType){
            int billState = BillState.BUYALL.state;

            ViewBillDetailActivity_.intent(getActivity())
                    .pjId(msg.newsPjId)
                    .pjBillId(msg.source)
                    .roleId(Global.PROJECT_MANAGER)
                    .project(mProject.get(msg.newsPjId))
                    .billState(billState)
                    .isRecord(true)
                    .start();
            msg.state = 2;
        }else
        if(newsType==NewsTypeEnum.NewPrijectProgressNoticeToQuality.newsType){
            ProgressDetailActivity_
                    .intent(getActivity())
                    .project(mProject.get(msg.newsPjId))
                    .pgId(msg.source)
                    .roleId(Global.QUALITY)
                    .startForResult(index);
        }else
        if(newsType==NewsTypeEnum.QualityCheckPrijectProgressNotice.newsType){
            ProgressDetailActivity_.intent(getActivity()).project(mProject.get(msg.newsPjId)).pgId(msg.source).roleId(Global.PROJECT_MANAGER).start();
            msg.state = 2;
        }else
        if(newsType==NewsTypeEnum.BmbOrderDispatch.newsType){
            OrderDetailActivity_.intent(getActivity()).roleId(Global.DISPATCH).bmbPcId(msg.source).startForResult(index);
        }else
        if(newsType==NewsTypeEnum.BmbOrderStorage.newsType){
            OrderDetailActivity_.intent(getActivity()).roleId(Global.STORAGE).bmbPcId(msg.source).startForResult(index);
        }else
        if(newsType==NewsTypeEnum.ProjectOrderHasSendOutToManager.newsType){
            int billState = BillState.BUYALL.state;

            ViewBillDetailActivity_.intent(getActivity())
                    .pjId(msg.newsPjId)
                    .pjBillId(msg.source)
                    .roleId(Global.PROJECT_MANAGER)
                    .project(mProject.get(msg.newsPjId))
                    .billState(billState)
                    .isRecord(true)
                    .start();

        }else
        if(newsType==NewsTypeEnum.ProjectOrderHasSendOutToBuyer.newsType){
            PurchaseRecordDetailActivity_.intent(getActivity()).spOrderId(msg.source).type(0).start();
            msg.state = 2;
        }else
        if(newsType==NewsTypeEnum.ConfirmReceiptGoodsToDingdy.newsType){
            OrderDetailActivity_.intent(getActivity()).roleId(Global.STORAGE).bmbPcId(msg.source).startForResult(index);
            msg.state = 2;
        }else
        if(newsType==NewsTypeEnum.ConfirmReceiptGoodsToBuyer.newsType){
            PurchaseRecordDetailActivity_.intent(getActivity()).spOrderId(msg.source).type(0).start();
            msg.state = 2;
        }

        adapter.notifyDataSetChanged();
    }


    public void updateMsgState(int index,int msg_state){
        mData.get(index).state = msg_state;
        adapter.notifyDataSetChanged();

    }


}
