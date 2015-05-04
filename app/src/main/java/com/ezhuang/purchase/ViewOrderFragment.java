package com.ezhuang.purchase;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.R;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.IPcMt;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/29 0029.
 */
@EFragment(R.layout.fragment_list_view)
public class ViewOrderFragment extends BaseFragment {
    @ViewById
    ListView listView;
    List<IPcMt> mData = new LinkedList<>();

    @AfterViews
    void init(){
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((PurchaseActivity)getActivity()).selectRecord(mData.get(position));
            }
        });
    }

    public void updateData(List<IPcMt> mData){
        this.mData = mData;
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
                view = mInflater.inflate(R.layout.item_view_order,parent,false);
                viewHolder = new ViewHolder();

                viewHolder.bmb_m_img = (ImageView) view.findViewById(R.id.bmb_m_img);
                viewHolder.bmb_m_name = (TextView) view.findViewById(R.id.bmb_m_name);
                viewHolder.bmb_m_price = (TextView) view.findViewById(R.id.bmb_m_price);
                viewHolder.bmb_m_spec = (TextView) view.findViewById(R.id.bmb_m_spec);
                viewHolder.bmb_m_unit = (TextView) view.findViewById(R.id.bmb_m_unit);
                viewHolder.count = (TextView) view.findViewById(R.id.item_count);
                convertView = view;
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            IPcMt pcMt = mData.get(position);

            if(pcMt.getMtImg().isEmpty()){
                viewHolder.bmb_m_img.setVisibility(View.GONE);
            }else{
                viewHolder.bmb_m_img.setVisibility(View.VISIBLE);
                iconfromNetwork(viewHolder.bmb_m_img,pcMt.getMtImg());
            }


            viewHolder.bmb_m_name.setText(pcMt.getMtName());
            viewHolder.bmb_m_spec.setText(pcMt.getSpec());
            viewHolder.bmb_m_price.setText(pcMt.getPrice());
            viewHolder.bmb_m_unit.setText(pcMt.getUnitName());
            viewHolder.count.setText(pcMt.getCount());
            return convertView;
        }

        class ViewHolder{
            ImageView bmb_m_img;
            TextView bmb_m_name;
            TextView bmb_m_price;
            TextView bmb_m_spec;
            TextView bmb_m_unit;
            TextView count;
        }
    };

}
