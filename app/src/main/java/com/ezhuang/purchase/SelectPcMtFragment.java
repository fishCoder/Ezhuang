package com.ezhuang.purchase;


import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ezhuang.ImagePagerActivity_;
import com.ezhuang.R;
import com.ezhuang.common.ImageLoadTool;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.IPcMt;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/28 0028.
 */
@EFragment(R.layout.fragment_list_view)
public class SelectPcMtFragment extends BaseFragment {

    @ViewById
    ListView listView;
    List<IPcMt> mData = new LinkedList<>();

    public boolean viewOrder = false;

    public boolean viewState = false;

    @StringArrayRes
    String[] bmb_order_detail_state;

    @AfterViews
    void init(){
        listView.setAdapter(adapter);
        if(!viewOrder){
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ((PurchaseActivity)getActivity()).selectRecord(mData.get(position));
                }
            });
        }

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
                view = mInflater.inflate(R.layout.item_pc_mt,parent,false);
                viewHolder = new ViewHolder();

                viewHolder.bmb_m_img = (ImageView) view.findViewById(R.id.bmb_m_img);
                viewHolder.bmb_m_name = (TextView) view.findViewById(R.id.bmb_m_name);
                viewHolder.bmb_m_price= (TextView) view.findViewById(R.id.bmb_m_price);
                viewHolder.bmb_m_spec = (TextView) view.findViewById(R.id.bmb_m_spec);
                viewHolder.bmb_m_unit = (TextView) view.findViewById(R.id.bmb_m_unit);
                viewHolder.item_count = (TextView) view.findViewById(R.id.item_count);
                viewHolder.mt_state   = (TextView) view.findViewById(R.id.mt_state);
                viewHolder.layout_state = view.findViewById(R.id.layout_state);

                if(viewOrder){
                    TextView item_name = (TextView) view.findViewById(R.id.item_count_name);
                    item_name.setText("用量");
                    viewHolder.item_count.setVisibility(View.VISIBLE);
                }

                convertView = view;
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final IPcMt pcMt = mData.get(position);

            if(pcMt.getMtImg()==null || pcMt.getMtImg().isEmpty()){
               viewHolder.bmb_m_img.setVisibility(View.GONE);
            }else{
                viewHolder.bmb_m_img.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(pcMt.getMtImg(), viewHolder.bmb_m_img, ImageLoadTool.optionsImage);
                viewHolder.bmb_m_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImagePagerActivity_.intent(getActivity()).mSingleUri(pcMt.getMtImg());
                    }
                });
            }

            viewHolder.bmb_m_name.setText(pcMt.getMtName());
            viewHolder.bmb_m_spec.setText(pcMt.getSpec());
            viewHolder.bmb_m_price.setText(pcMt.getPrice());
            viewHolder.bmb_m_unit.setText(pcMt.getUnitName());
            viewHolder.item_count.setText(pcMt.getCount());

            if(viewState){
                viewHolder.layout_state.setVisibility(View.VISIBLE);
                viewHolder.mt_state.setText(bmb_order_detail_state[pcMt.getMtState()]);
            }

            return convertView;
        }

        class ViewHolder{
            ImageView bmb_m_img;
            TextView bmb_m_name;
            TextView bmb_m_price;
            TextView bmb_m_spec;
            TextView bmb_m_unit;
            TextView item_count;
            TextView mt_state;
            View     layout_state;
        }
    };
}
