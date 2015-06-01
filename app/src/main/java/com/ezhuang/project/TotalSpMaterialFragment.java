package com.ezhuang.project;

import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.R;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.SpMaterial;
import com.ezhuang.model.SpMtType;
import com.ezhuang.project.detail.ListListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/14 0014.
 */
@EFragment(R.layout.fragement_total_sp_material)
public class TotalSpMaterialFragment  extends BaseFragment {

    Map<String,List<SpMaterial>> mData;
    List<SpMtType> mType;

    ListListener listListener;
    AddMaterialToBillActivity.FillBillItem fillBillItem;

    @ViewById
    PullToRefreshExpandableListView listView;

    public void setListListener(ListListener listListener,AddMaterialToBillActivity.FillBillItem fillBillItem) {
        this.listListener = listListener;
        this.fillBillItem = fillBillItem;
    }

    public void refreshListView(){
        adapter.notifyDataSetChanged();
    }

    void updateData(Map<String,List<SpMaterial>> mData,List<SpMtType> mType){
        this.mData = mData;
        this.mType = mType;
        adapter.notifyDataSetChanged();
        listView.onRefreshComplete();
    }

    @AfterViews
    void init(){
        if(mData == null){
            mData = new HashMap<>();
            mType = new LinkedList<>();

        }

        listView.getRefreshableView().setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setOnRefreshListener(onRefreshListener);
        listView.getRefreshableView().setOnChildClickListener(onChildClickListener);

    }

    ExpandableListView.OnChildClickListener onChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            fillBillItem.show(mData.get(mType.get(groupPosition).typeId).get(childPosition));
            Log.i(TotalSpMaterialFragment.class.getSimpleName(),"groupPosition:"+groupPosition+" childPosition:"+childPosition);
            return false;
        }
    };

    BaseExpandableListAdapter   adapter = new BaseExpandableListAdapter() {

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
            return mData.get(mType.get(groupPosition).typeId).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mType.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {

            return mData.get(mType.get(groupPosition).typeId).get(childPosition);
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
                view = mInflater.inflate(R.layout.item_material_type,parent,false);
                viewHolder.type_icon =  (ImageView) view.findViewById(R.id.type_icon);
                viewHolder.material_type = (TextView) view.findViewById(R.id.material_type);

                view.setTag(viewHolder);
                convertView = view;

            }else{
                viewHolder = (ViewHolderforHead) convertView.getTag();
            }
            SpMtType spMtType = (SpMtType) getGroup(groupPosition);
            viewHolder.material_type.setText(spMtType.typeName);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.item_sp_material,parent,false);
                viewHolder.sp_m_name = (TextView) view.findViewById(R.id.sp_m_name);
                viewHolder.sp_m_spec = (TextView) view.findViewById(R.id.sp_m_spec);
                viewHolder.sp_m_unit_name = (TextView) view.findViewById(R.id.sp_m_unit_name);
                viewHolder.icon_mark = view.findViewById(R.id.icon_mark);
                view.setTag(viewHolder);
                convertView = view;

            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SpMaterial spMaterial = (SpMaterial) getChild(groupPosition,childPosition);

            viewHolder.sp_m_name.setText(spMaterial.mtName);
            viewHolder.sp_m_spec.setText(spMaterial.spec);
            viewHolder.sp_m_unit_name.setText(spMaterial.unitName);

            if(((AddMaterialToBillActivity)getActivity()).isSelect(spMaterial.mtId)){
                viewHolder.icon_mark.setVisibility(View.VISIBLE);
            }else{
                viewHolder.icon_mark.setVisibility(View.GONE);
            }

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


    };

    class ViewHolderforHead{
        ImageView type_icon;
        TextView  material_type;
    }

    class ViewHolder{
        TextView sp_m_name;
        TextView sp_m_spec;
        TextView sp_m_unit_name;
        View     icon_mark;
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
