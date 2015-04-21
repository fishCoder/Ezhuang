package com.ezhuang.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezhuang.ImagePagerActivity_;
import com.ezhuang.R;
import com.ezhuang.common.BlankViewDisplay;
import com.ezhuang.common.Global;
import com.ezhuang.common.network.BaseFragment;
import com.ezhuang.model.PhotoData;
import com.ezhuang.model.SpMaterial;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/16 0016.
 */
@EFragment(R.layout.fragment_view_submit_bill)
public class ViewAndSubmitBillFragment extends BaseFragment {

    String TAG = this.getClass().getSimpleName();

    List<SpMaterial> mData;

    AddMaterialToBillActivity.FillBillItem fillBillItem;

    public boolean readOnly = false;

    @ViewById
    SwipeListView listView;

    @ViewById
    View blankLayout;

    @StringArrayRes
    String[] bill_detail_state;

    public boolean dealblank = true;

    @AfterViews
    void init(){
        if(mData == null){
            mData = new LinkedList<>();
        }

        if(!readOnly&&dealblank)
            BlankViewDisplay.setBlank(mData.size(), this, true, blankLayout, null);

        listView.setAdapter(adapter);
        if(readOnly){
            listView.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
        }
        listView.setSwipeListViewListener(baseSwipeListViewListener);
    }



    void setFillBillItem(AddMaterialToBillActivity.FillBillItem fillBillItem){
        this.fillBillItem = fillBillItem;
    }

    void updateData(List<SpMaterial> mData){
        this.mData = mData;
//        listView.setAdapter(adapter);
        Log.i(this.getClass().getSimpleName()+""," listview 刷新");
        adapter.notifyDataSetChanged();
        BlankViewDisplay.setBlank(mData.size(), this, true, blankLayout, null);
    }


    BillDetailAdapter adapter = new BillDetailAdapter();

    class BillDetailAdapter extends  BaseAdapter{

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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            final ViewHolder viewHolder;
            if(convertView == null){
                viewHolder = new ViewHolder();
                view = mInflater.inflate(R.layout.item_bill_row,parent,false);

                if(readOnly){
                    view.findViewById(R.id.layout_state).setVisibility(View.VISIBLE);
                    viewHolder.item_state = (TextView) view.findViewById(R.id.item_state);
                }

                viewHolder.sp_m_name = (TextView) view.findViewById(R.id.sp_m_name);
                viewHolder.sp_m_spec = (TextView) view.findViewById(R.id.sp_m_spec);
                viewHolder.item_count = (TextView) view.findViewById(R.id.item_count);
                viewHolder.sp_m_unit_name = (TextView) view.findViewById(R.id.sp_m_unit_name);
                viewHolder.item_remark = (TextView) view.findViewById(R.id.item_remark);
                viewHolder.grid_view = (GridView) view.findViewById(R.id.gridView);
                viewHolder.btnDel =  view.findViewById(R.id.btnDel);
                viewHolder.grid_view.setAdapter(new MyAdapter());
                convertView = view;
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final SpMaterial spMaterial = (SpMaterial) getItem(position);

            if(readOnly){
                viewHolder.item_state.setText(bill_detail_state[spMaterial.state]);
            }

            viewHolder.sp_m_name.setText(spMaterial.mtName);
            viewHolder.sp_m_spec.setText(spMaterial.spec);
            viewHolder.sp_m_unit_name.setText(spMaterial.unitName);
            viewHolder.item_count.setText(spMaterial.item_count);
            viewHolder.item_remark.setText(spMaterial.item_remark);

            viewHolder.btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mData.remove(position);
                    notifyDataSetChanged();
                    listView.closeOpenedItems();
                    BlankViewDisplay.setBlank(mData.size(), ViewAndSubmitBillFragment.this, true, blankLayout, null);

                }
            });
            if(spMaterial.itemImages==null || spMaterial.itemImages.size()==0){
                viewHolder.grid_view.setVisibility(View.GONE);
            }else{
                viewHolder.grid_view.setVisibility(View.VISIBLE);
                MyAdapter myAdapter = (MyAdapter) viewHolder.grid_view.getAdapter();
                myAdapter.setData(spMaterial.itemImages);
                adapter.notifyDataSetChanged();
                viewHolder.grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), ImagePagerActivity_.class);
                        ArrayList<String> arrayUri = new ArrayList<String>();
                        for (PhotoData item : (List<PhotoData>)spMaterial.itemImages) {
                            arrayUri.add(item.uri.toString());
                        }
                        intent.putExtra("mArrayUri", arrayUri);
                        intent.putExtra("mPagerPosition", position);
                        intent.putExtra("needEdit", false);
                        startActivityForResult(intent, FillBillItemFragment.RESULT_REQUEST_IMAGE);
                    }
                });

            }


            return convertView;
        }
    };


    class ViewHolder{
        TextView sp_m_name;
        TextView sp_m_spec;
        TextView item_count;
        TextView sp_m_unit_name;
        TextView item_remark;
        TextView item_state;
        GridView grid_view;

        View   btnDel;
    }

    class MyAdapter extends  BaseAdapter {

        int imageWidthPx;
        ImageSize mSize;

        List<PhotoData> mData = new LinkedList<>();

        void setData(List<PhotoData> mData){
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
                holder.image = (ImageView)getActivity().getLayoutInflater().inflate(R.layout.image_display, parent, false);
                holderList.add(holder);
                holder.image.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.image.setImageResource(R.mipmap.ic_default_image);
            PhotoData photoData = mData.get(position);
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

            return holder.image;
        }

        class ViewHolder {
            ImageView image;
            String uri = "";
        }

    };

    BaseSwipeListViewListener baseSwipeListViewListener = new BaseSwipeListViewListener() {

        @Override
        public void onStartOpen(int position, int action, boolean right) {

            Log.d(TAG, "onStartOpen");
        }

        @Override
        public void onStartClose(int position, boolean right) {

            Log.d(TAG, "onStartClose");
        }

        @Override
        public void onClickFrontView(int position) {
            if(readOnly){

            }else{
                fillBillItem.show(mData.get(position));
            }
            Log.d(TAG, "onClickFrontView");
        }

        @Override
        public void onClickBackView(int position) {

            Log.d(TAG, "onClickBackView");
        }

        @Override
        public void onDismiss(int[] reverseSortedPositions) {

            Log.d(TAG, "onDismiss");
        }
    };
}
