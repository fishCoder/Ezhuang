package com.ezhuang.quality;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.ImagePagerActivity_;
import com.ezhuang.R;
import com.ezhuang.common.BlankViewDisplay;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.PhotoData;
import com.ezhuang.model.ProjectProgress;
import com.ezhuang.project.FillBillItemFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
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
 * Created by Administrator on 2015/4/23 0023.
 */
@EActivity(R.layout.activity_view_project_progress)
public class ViewProjectProgressActivity extends BaseActivity {

    String QUERY_PROJECT_PROGRESS = Global.HOST + "/app/progress/queryProgress.do?pjId=%s&roleId=%s";

    String QUERY_PROJECT_PROGRESS_MORE = Global.HOST + "/app/progress/queryProgress.do?pjId=%s&roleId=%s&lastId=%s";

    @Extra
    String roleId;
    @Extra
    String pjId;

    @StringArrayRes
    String[] pg_state;
    int[] pg_state_color = {R.color.undo,R.color.undo,R.color.pass,R.color.reject};

    @ViewById
    View blankLayout;

    @ViewById
    PullToRefreshListView listView;

    List<ProjectProgress> mData = new LinkedList<>();

    @AfterViews
    void init(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getNetwork(String.format(QUERY_PROJECT_PROGRESS,pjId,roleId),QUERY_PROJECT_PROGRESS);

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(onRefreshListener);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("position",String.valueOf(position));
                ProjectProgress pg = mData.get(position-1);
                ProgressDetailActivity_.intent(ViewProjectProgressActivity.this).roleId(roleId).pjId(pjId).pg(pg).start();
            }
        });

        showDialogLoading();
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

        if(QUERY_PROJECT_PROGRESS.equals(tag)||QUERY_PROJECT_PROGRESS_MORE.equals(tag)){
            hideProgressDialog();
            Log.i("加载更多",respanse.getString("data"));
            JSONArray jsonArray = respanse.getJSONArray("data");
            if(code == NetworkImpl.REQ_SUCCESSS){
                int array_size = jsonArray.length();

                if(QUERY_PROJECT_PROGRESS.equals(tag)){
                    mData.clear();
                    if(array_size==0){
                        BlankViewDisplay.setBlank(mData.size(), this, true, blankLayout, null);
                    }
                }
                if(array_size==0){
                    showButtomToast("没有更多");
                }
                if(array_size<Global.PAGE_SIZE){
                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
                for( int i=0;i<array_size;i++){
                    ProjectProgress projectProgress = JsonUtil.Json2Object(jsonArray.getString(i),ProjectProgress.class);
                    mData.add(projectProgress);
                    adapter.notifyDataSetChanged();
                }
            }else{
                showButtomToast("错误码 " + code);
            }
            listView.onRefreshComplete();
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
                viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.item_progress_row, parent, false);
                viewHolder.layout_owner = view.findViewById(R.id.layout_owner);
                viewHolder.layout_quality = view.findViewById(R.id.layout_quality);
                viewHolder.pg_time = (TextView) view.findViewById(R.id.pg_time);
                viewHolder.quality_state = (TextView) view.findViewById(R.id.quality_state);
                viewHolder.owner_state = (TextView) view.findViewById(R.id.owner_state);
                viewHolder.pg_remark = (TextView) view.findViewById(R.id.pg_remark);
//                viewHolder.grid_view = (GridView) view.findViewById(R.id.gridView);
                viewHolder.pg_name = (TextView) view.findViewById(R.id.pg_name);
//                viewHolder.grid_view.setAdapter(new MyAdapter());
                convertView = view;
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ProjectProgress pg = (ProjectProgress) getItem(position);

            viewHolder.pg_name.setText(pg.nodeName);
            viewHolder.pg_time.setText(pg.time);
            viewHolder.pg_remark.setText(pg.pgRemark);
            if(pg.isNeedOwnerCheck()){
                viewHolder.layout_owner.setVisibility(View.VISIBLE);
                viewHolder.owner_state.setText(pg_state[pg.owerCheckResult]);
                viewHolder.owner_state.setTextColor(pg_state_color[pg.owerCheckResult]);
            }else{
                viewHolder.layout_owner.setVisibility(View.GONE);
            }

            if(pg.isNeedQualityCheck()){
                viewHolder.layout_quality.setVisibility(View.VISIBLE);
                viewHolder.quality_state.setText(pg_state[pg.quoCheckResult]);
                viewHolder.quality_state.setTextColor(pg_state_color[pg.quoCheckResult]);
            }else{
                viewHolder.layout_quality.setVisibility(View.GONE);

            }



            return convertView;
        }

        class ViewHolder{
            TextView pg_time;
            View     layout_quality;
            View     layout_owner;
            TextView quality_state;
            TextView owner_state;
            TextView pg_remark;
            TextView pg_name;
            GridView grid_view;
        }
    };

    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {

            if(PullToRefreshBase.Mode.PULL_FROM_START == listView.getCurrentMode()){
                getNetwork(String.format(QUERY_PROJECT_PROGRESS,pjId,roleId),QUERY_PROJECT_PROGRESS);
            }else{
                getNetwork(String.format(QUERY_PROJECT_PROGRESS_MORE,pjId,roleId,mData.get(mData.size()-1).pgId),QUERY_PROJECT_PROGRESS_MORE);
            }
        }
    };





    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }
}
