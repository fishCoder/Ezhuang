package com.ezhuang.quality;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

/**
 * Created by Administrator on 2015/4/22 0022.
 */
@EActivity(R.layout.activity_node_user_activity)
@OptionsMenu(R.menu.create_project)
public class SelectNodeAndUserActivity extends BaseActivity {

    @StringArrayRes
    String[] progress_node;

    String[] progress_deal = {"业主","质检员"};

    int select_user = 0;

    @Extra
    String select_type;

    @ViewById
    ListView listView;

    @AfterViews
    void init(){

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(select_type.equals("node")){
            listView.setAdapter(new SelectAdapter(progress_node));
        }else{
            listView.setAdapter(new SelectAdapter(progress_deal));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(view.findViewById(R.id.imageView).getClass().getSimpleName(),""+position);
                View imageView = view.findViewById(R.id.imageView);
                if(imageView.getVisibility() == View.VISIBLE){
                    select_user -= (position+1);
                    imageView.setVisibility(View.GONE);
                }else{
                    imageView.setVisibility(View.VISIBLE);
                    select_user += (position+1);
                }

                if(select_type.equals("node")){
                    Intent intent=new Intent();
                    intent.putExtra("node_name",progress_node[position]);
                    intent.putExtra("pg_node",""+(position+1));
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }


    class SelectAdapter extends  BaseAdapter{

        String[] mData;

        public SelectAdapter(String[] mData){
            this.mData = mData;
        }

        @Override
        public int getCount() {
            return mData.length;
        }

        @Override
        public Object getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_select,parent,false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
                viewHolder.imageView = convertView.findViewById(R.id.imageView);
                viewHolder.imageView.setVisibility(View.GONE);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String item_name = (String) getItem(position);
            viewHolder.textView.setText(item_name);
            return convertView;
        }

        class ViewHolder{
            View imageView;
            TextView textView;
        }
    }



    @OptionsItem
    void action_submit(){

        String node_name = "";
        String pg_node = "";

        switch (select_user){
            case 1:
                node_name = "业主";
                pg_node = "10";
                break;
            case 2:
                node_name = "质检员";
                pg_node = "01";
                break;
            case 3:
                node_name = "业主 质检员";
                pg_node = "11";
                break;
        }

        Intent intent=new Intent();
        intent.putExtra("deal_name",node_name);
        intent.putExtra("pg_deal",pg_node);
        setResult(RESULT_OK,intent);
        finish();
    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }

}
