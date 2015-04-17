package com.ezhuang.project;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.SpMaterial;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/16 0016.
 */
@EFragment(R.layout.fragment_view_submit_bill)
public class ViewAndSubmitBillFragment extends BaseFragment {

    List<SpMaterial> mData;

    AddMaterialToBillActivity.FillBillItem fillBillItem;

    @ViewById
    PullToRefreshListView listView;

    @AfterViews
    void init(){
        if(mData == null){
            mData = new LinkedList<>();
        }

        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fillBillItem.show(mData.get(position-1));
            }
        });
    }

    void setFillBillItem(AddMaterialToBillActivity.FillBillItem fillBillItem){
        this.fillBillItem = fillBillItem;
    }

    void updateData(List<SpMaterial> mData){
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
                viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.item_bill_row,parent,false);

                viewHolder.sp_m_name = (TextView) view.findViewById(R.id.sp_m_name);
                viewHolder.sp_m_spec = (TextView) view.findViewById(R.id.sp_m_spec);
                viewHolder.item_count = (TextView) view.findViewById(R.id.item_count);
                viewHolder.sp_m_unit_name = (TextView) view.findViewById(R.id.sp_m_unit_name);
                viewHolder.item_remark = (TextView) view.findViewById(R.id.item_remark);
                viewHolder.grid_view = (GridView) view.findViewById(R.id.gridView);

                convertView = view;
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SpMaterial spMaterial = (SpMaterial) getItem(position);
            viewHolder.sp_m_name.setText(spMaterial.mtName);
            viewHolder.sp_m_spec.setText(spMaterial.spec);
            viewHolder.sp_m_unit_name.setText(spMaterial.unitName);
            viewHolder.item_count.setText(spMaterial.item_count);
            viewHolder.item_remark.setText(spMaterial.item_remark);
            viewHolder.grid_view.setAdapter(new MyAdapter(spMaterial.itemImages));

            return convertView;
        }
    };

    class ViewHolder{
        TextView sp_m_name;
        TextView sp_m_spec;
        TextView item_count;
        TextView sp_m_unit_name;
        TextView item_remark;
        GridView grid_view;
    }

    class MyAdapter extends  BaseAdapter {

        int imageWidthPx;
        ImageSize mSize;

        List<FillBillItemFragment.PhotoData> mData;

        public MyAdapter(List<FillBillItemFragment.PhotoData> mData){
            imageWidthPx = Global.dpToPx(120);
            mSize = new ImageSize(imageWidthPx, imageWidthPx);

            this.mData = mData;
        }

        public int getCount() {
            return mData.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        ArrayList<ViewHolder> holderList = new ArrayList<ViewHolder>();

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                holder.image = (ImageView)getActivity().getLayoutInflater().inflate(R.layout.image_make_maopao, parent, false);
                holderList.add(holder);
                holder.image.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


//            if (position == getCount() - 1) {
//                if (getCount() == (PHOTO_MAX_COUNT + 1)) {
//                    holder.image.setVisibility(View.INVISIBLE);
//
//                } else {
//                    holder.image.setVisibility(View.VISIBLE);
//                    holder.image.setImageResource(R.mipmap.make_maopao_add);
//                    holder.uri = "";
//                }
//
//            } else {
            holder.image.setVisibility(View.VISIBLE);
            FillBillItemFragment.PhotoData photoData = mData.get(position);
            Uri data = photoData.uri;
            holder.uri = data.toString();

            ImageLoader.getInstance().loadImage(data.toString(), mSize, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    for (ViewHolder item : holderList) {
                        if (item.uri.equals(imageUri)) {
                            item.image.setImageBitmap(loadedImage);
                        }
                    }
                }
            });
//            }

            return holder.image;
        }

        class ViewHolder {
            ImageView image;
            String uri = "";
        }

    };
}
