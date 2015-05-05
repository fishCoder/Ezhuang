package com.ezhuang;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezhuang.common.BlankViewDisplay;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.BillState;
import com.ezhuang.model.Message;
import com.ezhuang.model.NewsTypeEnum;
import com.ezhuang.project.ViewBillDetailActivity;
import com.ezhuang.project.ViewBillDetailActivity_;
import com.ezhuang.project.detail.ViewProjectActivity_;
import com.ezhuang.quality.ViewProgressActivity_;
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
 * Created by Administrator on 2015/4/7 0007.
 */
@EFragment(R.layout.tab_bar_message)
public class FragmentMessage extends BaseFragment {

    @ViewById
    PullToRefreshListView listView;

    @ViewById
    View blankLayout;

    List<Message> mData = new LinkedList<>();

    String MESSAGE_LIST = Global.HOST + "/app/news/queryNews.do";

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
        if(MESSAGE_LIST.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                mData.clear();
                JSONArray jsonArray = respanse.getJSONArray("data");
                Log.d("data",jsonArray.toString());
                for (int i=0 ; i<jsonArray.length() ; i++){
                    mData.add(JsonUtil.Json2Object(jsonArray.getString(i),Message.class));
                }
                adapter.notifyDataSetChanged();
            }else{

            }
        }
        listView.onRefreshComplete();
        BlankViewDisplay.setBlank(mData.size(), this, true, blankLayout, null);
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
                convertView = view;
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Message msg = (Message) getItem(position);
            viewHolder.msg_title.setText(msg.getTitle());
            viewHolder.msg_content.setText(msg.getContent());
            viewHolder.msg_time.setText(msg.getTime());

            return convertView;
        }

        class ViewHolder{
            ImageView msg_img;
            TextView msg_title;
            TextView msg_content;
            TextView msg_time;
        }
    };


    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            if(PullToRefreshBase.Mode.PULL_FROM_START == listView.getCurrentMode()){
                getNetwork(MESSAGE_LIST,MESSAGE_LIST);
            }else{

            }
        }
    };

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("position",""+position);
            jumpToDealActivity(mData.get(position-1));
        }
    };

    void jumpToDealActivity(Message msg){
        int newsType = msg.newsType;
        if(newsType==NewsTypeEnum.NewPrijectNoticeToManager.newsType){
            ViewProjectActivity_
                    .intent(getActivity())
                    .roleId(Global.PROJECT_MANAGER)
                    .staffId(MyApp.currentUser.getGlobal_key())
                    .start();
        }else
        if(newsType==NewsTypeEnum.ProjectOrderCheckResultNoticeToManager.newsType){
            ViewBillDetailActivity_.intent(getActivity())
                    .pjBillId(msg.sourceId)
                    .roleId(Global.PROJECT_MANAGER)
                    .billState(BillState.UNBUY.state)
                    .isRecord(true)
                    .start();
        }else
        if(newsType==NewsTypeEnum.ProjectOrderCheckResultNoticeToBuyer.newsType){
            ViewBillDetailActivity_.intent(getActivity())
                    .pjBillId(msg.sourceId)
                    .roleId(Global.BUYER)
                    .billState(1)
                    .start();
        }else
        if(newsType==NewsTypeEnum.NewPurchaseOrderNotice.newsType){
            ViewBillDetailActivity_.intent(getActivity())
                    .pjBillId(msg.sourceId)
                    .roleId(Global.PROJECT_MANAGER)
                    .billState(2)
                    .isRecord(true)
                    .start();
        }else
        if(newsType==NewsTypeEnum.NewPrijectProgressNoticeToQuality.newsType){
            ViewProgressActivity_.intent(getActivity()).start();
        }



    }
}
