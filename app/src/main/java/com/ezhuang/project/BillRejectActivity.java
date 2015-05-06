package com.ezhuang.project;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.BillRejection;

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
 * Created by Administrator on 2015/5/6 0006.
 */
@EActivity(R.layout.activity_bill_reject)
public class BillRejectActivity extends BaseActivity {

    List<BillRejection> mData = new LinkedList<>();

    String QUERY_REJECT = Global.HOST + "";

    @ViewById
    ListView listView;

    @AfterViews
    void init(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(mData.size()==0){
            getNetwork(String.format(QUERY_REJECT),QUERY_REJECT);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_bill,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(QUERY_REJECT.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                Log.v("data",respanse.getString("data"));
                JSONArray jsonArray = respanse.getJSONArray("data");
                for( int i=0;i<jsonArray.length(); i++){
                    mData.add(JsonUtil.Json2Object(jsonArray.getString(i),BillRejection.class));
                }
                adapter.notifyDataSetChanged();
            }else{

            }
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
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            return convertView;
        }

        class ViewHolder{

        }
    };

    @OptionsItem
    void action_view() {

    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }
}
