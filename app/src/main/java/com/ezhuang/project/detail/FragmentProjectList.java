package com.ezhuang.project.detail;

import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.Project;
import com.ezhuang.project.AddMaterialToBillActivity_;
import com.ezhuang.project.ProjectBillActivity_;
import com.ezhuang.quality.AddProjectProgressActivity_;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/9 0009.
 */
@EFragment(R.layout.fragment_project_list)
public class FragmentProjectList extends BaseFragment {


    @ViewById
    PullToRefreshListView listView;

    List<Project> listProject;

    ListListener listListener;


    public String roleId;

    @ViewById
    View blankLayout;

    @AfterViews
    void init(){

        if(listProject==null){
            listProject = new LinkedList<>();
            listView.setAdapter(adapter);
            listView.setMode(PullToRefreshBase.Mode.BOTH);
            listView.setOnRefreshListener(onRefreshListener);
        }

    }

    void updateData(List<Project> list){
        this.listProject = list;
        adapter.notifyDataSetChanged();
        listView.onRefreshComplete();
//        BlankViewDisplay.setBlank(1, this, false, blankLayout, null);
    }

    void setProjectListListener(ListListener listListener){
        this.listListener = listListener;
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
                viewHolder.pjBillCount = (TextView) view.findViewById(R.id.pj_bill_count);
                viewHolder.pjBillCount.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                viewHolder.pjPgCount = (TextView) view.findViewById(R.id.pj_pg_count);
                viewHolder.pjPgCount.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

                viewHolder.layoutBillCount = view.findViewById(R.id.layout_bill_count);
                viewHolder.layoutPgCount = view .findViewById(R.id.pj_pg_count);
                viewHolder.layoutPjBtn = view.findViewById(R.id.layout_pj_btn);

                convertView = view;
                convertView.setTag(viewHolder);
            }else{
                viewHolder  = (ViewHolder) convertView.getTag();
            }

            final Project project = (Project) getItem(position);
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
            viewHolder.pjBillCount.setText(""+project.getBillCount());
            viewHolder.pjPgCount.setText(""+project.getPgCount());

            if(Global.PROJECT_MANAGER.equals(roleId)){
                viewHolder.layoutPjBtn.setVisibility(View.VISIBLE);
                viewHolder.layoutPjBtn.findViewById(R.id.add_bill).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        AddMaterialToBillActivity_.intent(getActivity()).projectId(project.getPjId()).start();
                    }
                });
                viewHolder.layoutPjBtn.findViewById(R.id.add_pg).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        AddProjectProgressActivity_.intent(getActivity()).projectId(project.getPjId()).start();
                    }
                });
            }

            viewHolder.layoutBillCount.setOnClickListener(new BillListener(project.getPjId()));

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
        TextView pjBillCount;
        TextView pjPgCount;

        View layoutBillCount;
        View layoutPgCount;

        View layoutPjBtn;
    }

    class BillListener implements   View.OnClickListener {

        String pjId = null;

        public BillListener(String pjId){
            this.pjId = pjId;
        }

        @Override
        public void onClick(View v) {
            ProjectBillActivity_.intent(getActivity()).pjId(pjId).start();
            getActivity().overridePendingTransition(R.anim.alpha_in,R.anim.left_slide_out);
        }
    }



    PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {

            if(PullToRefreshBase.Mode.PULL_FROM_START == listView.getCurrentMode()){
                listListener.refresh();
            }else{
                listListener.loadMore();

            }
        }
    };
}
