package com.ezhuang;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.ezhuang.adapter.GridImageAdapter;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.PhotoData;
import com.ezhuang.model.Problem;
import com.ezhuang.project.FillBillItemFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/5 0005.
 */
@EActivity(R.layout.activity_problem)
public class ProblemActicity extends BaseActivity {

    @ViewById
    PullToRefreshListView listView;

    String QUERY_PROBLEM = Global.HOST + "/app/problem/queryProblem.do";
    String QUERY_PROBLEM_MORE = Global.HOST + "/app/problem/queryProblem.do?lastId=%s";

    List<Problem> mData = new LinkedList<>();

    @AfterViews
    void init(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getNetwork(QUERY_PROBLEM,QUERY_PROBLEM);
        showDialogLoading();

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnRefreshListener(onRefreshListener);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        hideProgressDialog();
        if(QUERY_PROBLEM.equals(tag)||QUERY_PROBLEM_MORE.equals(QUERY_PROBLEM_MORE)){
            if(code== NetworkImpl.REQ_SUCCESSS){

                if(QUERY_PROBLEM.equals(tag))mData.clear();

                JSONArray jsonArray = respanse.getJSONArray("data");
                int len = jsonArray.length();
                for(int i=0;i<len;i++){
                    mData.add(JsonUtil.Json2Object(jsonArray.getString(i),Problem.class));
                }
                if(len==0){
                    showButtomToast("没有更多");
                }
                if(len==Global.PAGE_SIZE){
                    listView.setMode(PullToRefreshBase.Mode.BOTH);
                }else{
                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            }else{

            }
        }

        adapter.notifyDataSetChanged();
        listView.onRefreshComplete();
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
            if(convertView==null){
                viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.item_problem,parent,false);
                viewHolder.pj_name = (TextView) view.findViewById(R.id.pj_name);
                viewHolder.owner_name = (TextView) view.findViewById(R.id.owner_name);
                viewHolder.owner_phone = (TextView) view.findViewById(R.id.owner_phone);
                viewHolder.problem_time = (TextView) view.findViewById(R.id.problem_time);
                viewHolder.problem_desc = (TextView) view.findViewById(R.id.problem_desc);
                viewHolder.grid_view = (GridView) view.findViewById(R.id.gridView);
                viewHolder.grid_view.setAdapter(new GridImageAdapter());

                convertView = view;
                convertView.setTag(viewHolder);

            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Problem problem = (Problem) getItem(position);
            viewHolder.pj_name.setText(problem.pjName);
            viewHolder.owner_name.setText(problem.realName);
            viewHolder.owner_phone.setText(problem.ownerPhone);
            viewHolder.problem_desc.setText(problem.problemDesc);
            viewHolder.problem_time.setText(problem.problemTime);
            if(problem.problemImages!=null&&problem.problemImages.length!=0){
                viewHolder.grid_view.setVisibility(View.VISIBLE);
                final List<PhotoData> list = new LinkedList<>();
                for (String url:problem.problemImages){
                    PhotoData photoData = new PhotoData(url);
                    list.add(photoData);
                }
                GridImageAdapter myAdapter = (GridImageAdapter) viewHolder.grid_view.getAdapter();
                myAdapter.setData(list,mInflater);
                viewHolder.grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ProblemActicity.this, ImagePagerActivity_.class);
                        ArrayList<String> arrayUri = new ArrayList<String>();
                        for (PhotoData item : list) {
                            arrayUri.add(item.uri.toString());
                        }
                        intent.putExtra("mArrayUri", arrayUri);
                        intent.putExtra("mPagerPosition", position);
                        intent.putExtra("needEdit", false);
                        startActivityForResult(intent, FillBillItemFragment.RESULT_REQUEST_IMAGE);
                    }
                });
            }else{
                viewHolder.grid_view.setVisibility(View.GONE);
            }
            return convertView;
        }

        class ViewHolder{
            TextView pj_name;
            TextView owner_name;
            TextView owner_phone;
            TextView problem_desc;
            TextView problem_time;
            GridView grid_view;
        }
    };

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            if(PullToRefreshBase.Mode.PULL_FROM_START == listView.getCurrentMode()){
                getNetwork(QUERY_PROBLEM,QUERY_PROBLEM);
            }else{
                getNetwork(String.format(QUERY_PROBLEM_MORE,mData.get(mData.size()-1).problemId),QUERY_PROBLEM_MORE);
            }
        }
    };

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }
}
