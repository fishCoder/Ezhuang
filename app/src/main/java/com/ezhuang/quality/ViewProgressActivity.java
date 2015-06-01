package com.ezhuang.quality;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
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
import com.ezhuang.model.PhotoData;
import com.ezhuang.model.Project;
import com.ezhuang.model.ProjectProgress;
import com.ezhuang.project.FillBillItemFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/23 0023.
 */
@EActivity(R.layout.activity_view_progress)
public class ViewProgressActivity extends BaseActivity {

    String QUERY_PROGRESS = Global.HOST + "/app/progress/queryQtProgress.do";

    String QUERY_PROGRESS_MORE = Global.HOST + "/app/progress/queryQtProgress.do?lastId=%s";

    @StringArrayRes
    String[] pg_state;
    int[] pg_state_color = {R.color.undo,R.color.undo,R.color.pass,R.color.reject};

    @ViewById
    PullToRefreshExpandableListView listView;

    @ViewById
    View blankLayout;

    List<Project> mType = new LinkedList<>();

    List<List<ProjectProgress>> mData = new LinkedList<>();

    @Extra
    String roleId;

    @AfterViews
    void init(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        showDialogLoading();

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(onRefreshListener);
        listView.getRefreshableView().setAdapter(adapter);
        listView.getRefreshableView().setOnChildClickListener(onChildClickListener);
        listView.getRefreshableView().setGroupIndicator(null);

        getNetwork(QUERY_PROGRESS,QUERY_PROGRESS);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(QUERY_PROGRESS.equals(tag)||QUERY_PROGRESS_MORE.equals(tag)){
            if(tag.equals(QUERY_PROGRESS)){
                mType.clear();
                mData.clear();
            }

            toObject(respanse);

        }
    }

    @Background
    void toObject(JSONObject respanse){
        //记录条数
        int count = 0;
        JSONArray jsonArray = null;
        try {
            jsonArray = respanse.getJSONArray("data");
            for (int i=0 ; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                mType.add(JsonUtil.Json2Object(jsonObject.getString("project"), Project.class));

                List<ProjectProgress> projectProgressList = new LinkedList<>();
                JSONArray jsonProgress = jsonObject.getJSONArray("progress");
                for (int k=0; k < jsonProgress.length();k++){
                    projectProgressList.add(JsonUtil.Json2Object(jsonProgress.getString(k),ProjectProgress.class));
                    count++;
                }
                mData.add(projectProgressList);
            }
        } catch (JSONException e) {
            toUI(0);
        }
        toUI(count);
    }

    @UiThread
    void toUI(int count){
        if(count<Global.PAGE_SIZE){
            listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
        listView.onRefreshComplete();
        hideProgressDialog();
        adapter.notifyDataSetChanged();
        BlankViewDisplay.setBlank(mData.size(), this, true, blankLayout, null);
        expandGroup();
    }

    void expandGroup(){
        for (int i=0; i<mData.size(); i++){
            listView.getRefreshableView().expandGroup(i);
        }
    }

    ExpandableListView.OnChildClickListener onChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Log.i("groupPosition",String.valueOf(groupPosition));
            Log.i("childPosition",String.valueOf(childPosition));

            ProjectProgress pg = mData.get(groupPosition).get(childPosition);
            ProgressDetailActivity_.intent(ViewProgressActivity.this).pg(pg).pjId(mType.get(groupPosition).getPjId()).roleId(Global.QUALITY).startForResult(0);
            return false;
        }
    };

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
                view = mInflater.inflate(R.layout.item_progress_row,parent,false);

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

            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ProjectProgress pg = (ProjectProgress) getChild(groupPosition,childPosition);
            viewHolder.pg_name.setText(pg.nodeName);
            viewHolder.pg_time.setText(pg.time);
            viewHolder.pg_remark.setText(pg.pgRemark);
            if(pg.isNeedOwnerCheck()){
                viewHolder.layout_owner.setVisibility(View.VISIBLE);
                if(pg.owerScore==0){
                    viewHolder.owner_state.setText(pg_state[pg.owerCheckResult]);
                    viewHolder.owner_state.setTextColor(getResources().getColor(pg_state_color[pg.owerCheckResult]));
                }else{
                    int i=0;
                    StringBuffer stars = new StringBuffer();
                    for(;i<pg.owerScore;i++){
                        stars.append("★");
                    }
                    for (;i<ProjectProgress.SCORE;i++){
                        stars.append("☆");
                    }
                    viewHolder.owner_state.setText(stars.toString());
                    viewHolder.owner_state.setTextColor(getResources().getColor(R.color.yellow));
                }
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

//            if(pg.imgUrls==null || pg.imgUrls.length==0){
//                viewHolder.grid_view.setVisibility(View.GONE);
//            }else{
//                final List<PhotoData> list = new LinkedList<>();
//
//                for (String url:pg.imgUrls){
//                    PhotoData photoData = new PhotoData(url);
//                    Log.i("图片路径",url);
//                    list.add(photoData);
//                }
//
//                viewHolder.grid_view.setVisibility(View.VISIBLE);
//                MyAdapter myAdapter = (MyAdapter) viewHolder.grid_view.getAdapter();
//                myAdapter.setData(list);
//                myAdapter.notifyDataSetChanged();
//                viewHolder.grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Intent intent = new Intent(ViewProgressActivity.this, ImagePagerActivity_.class);
//                        ArrayList<String> arrayUri = new ArrayList<String>();
//                        for (PhotoData item : list) {
//                            arrayUri.add(item.uri.toString());
//                        }
//                        intent.putExtra("mArrayUri", arrayUri);
//                        intent.putExtra("mPagerPosition", position);
//                        intent.putExtra("needEdit", false);
//                        startActivityForResult(intent, FillBillItemFragment.RESULT_REQUEST_IMAGE);
//                    }
//                });
//
//            }

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


    class MyAdapter extends BaseAdapter {

        int imageWidthPx;
        ImageSize mSize;

        List<PhotoData> mData = new LinkedList<>();

        void setData(List<PhotoData> mData){
            imageWidthPx = Global.dpToPx(120);
            mSize = new ImageSize(imageWidthPx, imageWidthPx);

            this.mData = mData;
        }

        public int getCount() {
            return mData.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        ArrayList<ViewHolder> holderList = new ArrayList<ViewHolder>();

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                holder.image = (ImageView) mInflater.inflate(R.layout.image_display, parent, false);
                holderList.add(holder);
                holder.image.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.image.setImageResource(R.mipmap.ic_default_image);
            PhotoData photoData = mData.get(position);
            Uri data = photoData.uri;
            holder.uri = data.toString();

            ImageLoader.getInstance().loadImage(data.toString(), mSize, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    for (ViewHolder item : holderList) {
                        if (item.uri.equals(imageUri)) {
                            item.image.setImageBitmap(loadedImage);
                        }
                    }
                }
            });

            return holder.image;
        }

        class ViewHolder {
            ImageView image;
            String uri = "";
        }

    };


    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            if(PullToRefreshBase.Mode.PULL_FROM_START == listView.getCurrentMode()){
                getNetwork(QUERY_PROGRESS,QUERY_PROGRESS);
            }else{
                int i = mData.size()-1;
                int index = mData.get(i).size()-1;
                String lastId = mData.get(i).get(index).pgId;
                getNetwork(String.format(QUERY_PROGRESS_MORE,lastId),QUERY_PROGRESS_MORE);
            }
        }
    };

    @OnActivityResult(0)
    void changeBillState(int resultCode,Intent data){
        if(resultCode == RESULT_OK){
            String pgId = data.getStringExtra("pgId");
            int state = data.getIntExtra("state",0);
            for (List<ProjectProgress> pglist : mData){
                for(ProjectProgress pg : pglist){
                    if(pg.pgId.equals(pgId)){
                        pg.quoCheckResult = state;
                        adapter.notifyDataSetChanged();
                    }
                }

            }
        }
    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }

}
