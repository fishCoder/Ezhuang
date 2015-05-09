package com.ezhuang.project;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.model.Project;
import com.ezhuang.user.UserDetailActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/5/6 0006.
 */
@EActivity(R.layout.activity_project_detail)
public class ProjectDetailActivity extends BaseActivity {

    String QUERY_PROJECT_DETAIL = Global.HOST + "/app/project/queryProjectDetail.do?pjId=%s&roleId=%s&global_key=%s";

    @StringArrayRes
    String[] project_item_name;
    String[] project_item_value;

    @Extra
    String projectId;
    @Extra
    String roleId;
    @Extra
    String global_key;

    @ViewById
    ListView listView;

    @Extra
    Project project;

    @AfterViews
    void init(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(project == null){
            getNetwork(String.format(QUERY_PROJECT_DETAIL,projectId,roleId,global_key),QUERY_PROJECT_DETAIL);
            getProjectRows();
        }else{
            fillProjectRows();
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 13:
                        UserDetailActivity_.intent(ProjectDetailActivity.this)
                                .globalKey(project.getPjQuality().getGlobal_key())
                                .companyType(0)
                                .start();
                        break;
                    case 12:
                        UserDetailActivity_.intent(ProjectDetailActivity.this)
                                .globalKey(project.getPjBuyer().getGlobal_key())
                                .companyType(0)
                                .start();
                        break;
                    case 11:
                        UserDetailActivity_.intent(ProjectDetailActivity.this)
                                .globalKey(project.getPjChecker().getGlobal_key())
                                .companyType(0)
                                .start();
                        break;
                    case 10:
                        UserDetailActivity_.intent(ProjectDetailActivity.this)
                                .globalKey(project.getPjM().getGlobal_key())
                                .companyType(0)
                                .start();
                        break;
                }
            }
        });
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

    void fillProjectRows(){
        project_item_value = new String[]{
                project.getPjName(),
                project.getPjAddress(),
                project.getPjHousetype(),
                project.getPjArea(),
                project.getPjContractnum(),
                project.getPjRemark(),
                project.getRealName(),
                "",
                project.getUserName(),
                project.getPjDesigner(),
                project.getPjM().getName(),
                project.getPjChecker().getName(),
                project.getPjBuyer().getName(),
                project.getPjQuality().getName()
        };
        adapter.notifyDataSetChanged();
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

        class ViewHolder {
            TextView first;
            TextView second;
        }
    };

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(QUERY_PROJECT_DETAIL.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                Log.d("project",respanse.getString("data"));
                project = JsonUtil.Json2Object(respanse.getString("data"), Project.class);
                fillProjectRows();
            }else{

            }
        }
    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }

}
