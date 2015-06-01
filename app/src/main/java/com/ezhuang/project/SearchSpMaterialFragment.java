package com.ezhuang.project;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ezhuang.R;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.SpMaterial;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/15 0015.
 */
@EFragment(R.layout.fragment_search_sp_material)
public class SearchSpMaterialFragment extends BaseFragment {

    @ViewById
    PullToRefreshListView listView;

    AddMaterialToBillActivity.FillBillItem fillBillItem;

    List<SpMaterial> mData;

    void setFillBillItem(AddMaterialToBillActivity.FillBillItem fillBillItem){
        this.fillBillItem = fillBillItem;
    }

    @AfterViews
    void init(){
        if(mData == null){
            mData = new LinkedList<>();

            listView.setMode(PullToRefreshBase.Mode.DISABLED);

        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fillBillItem.show(mData.get(position-1));
            }
        });
    }

    void updateData(List<SpMaterial> mData){
        this.mData = mData;
        adapter.notifyDataSetChanged();
    }

    public void refreshListView(){
        adapter.notifyDataSetChanged();
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
                view = mInflater.inflate(R.layout.item_sp_material,parent,false);
                viewHolder.sp_m_name = (TextView) view.findViewById(R.id.sp_m_name);
                viewHolder.sp_m_spec = (TextView) view.findViewById(R.id.sp_m_spec);
                viewHolder.sp_m_unit_name = (TextView) view.findViewById(R.id.sp_m_unit_name);
                viewHolder.icon_mark = view.findViewById(R.id.icon_mark);
                view.findViewById(R.id.divider).setVisibility(View.GONE);
                view.setTag(viewHolder);
                convertView = view;

            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SpMaterial spMaterial = (SpMaterial) getItem(position);

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
    };

    class ViewHolder{
        TextView sp_m_name;
        TextView sp_m_spec;
        TextView sp_m_unit_name;
        View     icon_mark;
    }
}
