package com.ezhuang.project.detail;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.MyApp;
import com.ezhuang.R;
import com.ezhuang.ImagePagerActivity;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.common.network.RefreshBaseFragment;
import com.ezhuang.model.Project;
import com.ezhuang.model.StaffUser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/9 0009.
 */
@EFragment(R.layout.fragment_project_list)
public class FragmentProjectList extends BaseFragment {

    @FragmentArg
    String roleId; //类型 1：客服 2：项目经理 3：审核员 4：质检员

    @ViewById
    PullToRefreshListView listView;



    List<Project> listProject;


    String PROJECT_BY_ROLE = Global.HOST + "/app/project/queryMyProject.do?roleId=%s&global_key=%s";

    String PROJECT_BY_ROLE_MORE = Global.HOST + "/app/project/queryMyProject.do?roleId=%s&global_key=%s&lastPjId=%s";

    @AfterViews
    void init(){
        showDialogLoading();

        mFootUpdate.init(listView, mInflater, this);

        listProject = new LinkedList<>();

        listView.setAdapter(adapter);
        listView.setOnRefreshListener(onRefreshListener);

        getNetwork(String.format(PROJECT_BY_ROLE, roleId, MyApp.currentUser.getGlobal_key(), ""), PROJECT_BY_ROLE);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

        if(tag.equals(PROJECT_BY_ROLE)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                listProject.clear();
                JSONArray jsonArray = respanse.getJSONArray("data");
                for(int i=0; i<jsonArray.length() ;i++){
                    Project project = getProject(jsonArray.getString(i), jsonArray.getJSONObject(i));
                    listProject.add(project);
                }
            }
        }

        if(tag.equals(PROJECT_BY_ROLE_MORE)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                JSONArray jsonArray = respanse.getJSONArray("data");
                for(int i=0; i<jsonArray.length() ;i++){
                    Project project = getProject(jsonArray.getString(i),jsonArray.getJSONObject(i));
                    listProject.add(project);
                }
            }
        }

        listView.onRefreshComplete();
        adapter.notifyDataSetChanged();
        hideProgressDialog();
    }

    Project getProject(String sProject,JSONObject jsonObject) throws  JSONException{
        Project project = JsonUtil.Json2Object(sProject,Project.class);
        project.setPjM(JsonUtil.Json2Object(jsonObject.getString("pjM"), StaffUser.class));
        project.setPjChecker(JsonUtil.Json2Object(jsonObject.getString("pjChecker"), StaffUser.class));
        project.setPjBuyer(JsonUtil.Json2Object(jsonObject.getString("pjBuyer"), StaffUser.class));
        project.setPjQuality(JsonUtil.Json2Object(jsonObject.getString("pjQuality"), StaffUser.class));

        return project;
    }



    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return listProject.size();
        }

        @Override
        public Object getItem(int position) {
            return listProject.get(position);
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
                view = mInflater.inflate(R.layout.item_project_detail,parent,false);

                viewHolder.pjName = (TextView) view.findViewById(R.id.pj_name);
                viewHolder.pjAddress = (TextView) view.findViewById(R.id.pj_address);
                viewHolder.ownerName = (TextView) view.findViewById(R.id.owner_name);
                viewHolder.ownerPhone = (TextView) view.findViewById(R.id.owner_phone);
                viewHolder.pmName = (TextView) view.findViewById(R.id.pm_name);
                viewHolder.checkName = (TextView) view.findViewById(R.id.check_name);
                viewHolder.buyerName = (TextView) view.findViewById(R.id.buyer_name);
                viewHolder.qualityName = (TextView) view.findViewById(R.id.quality_name);
                viewHolder.pjCreateTime = (TextView) view.findViewById(R.id.pj_create_time);
                viewHolder.pjState = (TextView) view.findViewById(R.id.pj_state);
                viewHolder.iconPjState = (ImageView) view.findViewById(R.id.icon_pj_state);

                convertView = view;
                convertView.setTag(viewHolder);
            }else{
                viewHolder  = (ViewHolder) convertView.getTag();
            }

            Project project = (Project) getItem(position);
            viewHolder.pjName.setText(project.getPjName());
            viewHolder.pjAddress.setText(project.getPjAddress());
            viewHolder.ownerName.setText(project.getRealName());
            viewHolder.ownerPhone.setText(project.getUserName());
            viewHolder.pmName.setText(project.getPjM().getName());
            viewHolder.checkName.setText(project.getPjChecker().getName());
            viewHolder.buyerName.setText(project.getPjBuyer().getName());
            viewHolder.qualityName.setText(project.getPjQuality().getName());
            viewHolder.pjCreateTime.setText(project.getPjCreateTime());
            viewHolder.pjState.setText(Global.PJ_STATE[project.getPjState()]);

            return convertView;
        }
    };

    class ViewHolder{
        TextView pjName;
        TextView pjAddress;
        TextView ownerName;
        TextView ownerPhone;
        TextView pmName;
        TextView checkName;
        TextView buyerName;
        TextView qualityName;
        TextView pjCreateTime;
        TextView pjState;
        ImageView iconPjState;
    }


    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            getNetwork(String.format(PROJECT_BY_ROLE, roleId, MyApp.currentUser.getGlobal_key(), ""), PROJECT_BY_ROLE);

        }
    };
}
