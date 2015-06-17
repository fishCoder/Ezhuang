package com.ezhuang;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.common.BlankViewDisplay;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.Scheme;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/8 0008.
 */
@EActivity(R.layout.activity_scheme_list)
public class SchemeListActivity extends BaseActivity {

    @ViewById
    PullToRefreshListView listView;
    @ViewById
    View blankLayout;

    String QUERY_SCHEME = Global.HOST+"/app/scheme/queryScheme.do";
    String QUERY_SCHEME_MORE = Global.HOST+"/app/scheme/queryScheme.do?lastId=%s";

    List<Scheme> mData = new LinkedList<>();

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showStubImage(R.mipmap.ic_default_image)
            .showImageForEmptyUri(R.mipmap.ic_default_image)
            .showImageOnFail(R.mipmap.ic_default_image)
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .build();

    @AfterViews
    void init(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(mData.size()==0){
            listView.setAdapter(adapter);
            listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                @Override
                public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                    if (PullToRefreshBase.Mode.PULL_FROM_START == listView.getCurrentMode()) {
                        getNetwork(QUERY_SCHEME, QUERY_SCHEME);
                    } else {
                        String lastId = mData.get(mData.size() - 1).schemeId;
                        getNetwork(String.format(QUERY_SCHEME_MORE, lastId), QUERY_SCHEME_MORE);
                    }
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = Global.HOST+"/mobile/toSchemeDetail.do?cp_id=%s&schemeId=%s";
                    position++;
                    WebViewActivity_.intent(SchemeListActivity.this).scheme(mData.get(position)).url(String.format(url, MyApp.currentUser.getCompanyId(), mData.get(position).schemeId)).start();

                }
            });
            getNetwork(QUERY_SCHEME, QUERY_SCHEME);
            showDialogLoading();
        }

    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        hideProgressDialog();
        if(QUERY_SCHEME.equals(tag)||QUERY_SCHEME_MORE.equals(QUERY_SCHEME_MORE)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                JSONArray jsonArray = respanse.getJSONArray("data");
                if(QUERY_SCHEME.equals(tag)){
                    mData.clear();
                }
                if(jsonArray!=null){
                    for (int i=0;i<jsonArray.length();i++){
                        Scheme scheme = JsonUtil.Json2Object(jsonArray.getString(i),Scheme.class);
                        mData.add(scheme);
                    }
                    if(jsonArray.length()<Global.PAGE_SIZE){
                        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                }
                adapter.notifyDataSetChanged();
                BlankViewDisplay.setBlank(mData.size(), this, true, blankLayout, null);
            }
        }
        listView.onRefreshComplete();
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
            View view;
            ViewHolder viewHolder;

            if(convertView == null){
                view = mInflater.inflate(R.layout.item_scheme,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.scheme_img = (ImageView) view.findViewById(R.id.scheme_img);
                viewHolder.scheme_title = (TextView) view.findViewById(R.id.scheme_name);
                viewHolder.scheme_content = (TextView) view.findViewById(R.id.scheme_desc);
                viewHolder.scheme_time = (TextView) view.findViewById(R.id.scheme_time);
                convertView = view;
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Scheme scheme = (Scheme) mData.get(position);
            viewHolder.scheme_title.setText(scheme.schemeName);
            viewHolder.scheme_content.setText(scheme.introduction);
            viewHolder.scheme_time.setText(Global.dataToNow(scheme.createTime));
            ImageLoader.getInstance().displayImage(scheme.cover,viewHolder.scheme_img);


            return convertView;
        }

        class ViewHolder{
            ImageView scheme_img;
            TextView scheme_title;
            TextView scheme_content;
            TextView scheme_time;
            View     scheme_badge;
        }
    };
    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }



}
