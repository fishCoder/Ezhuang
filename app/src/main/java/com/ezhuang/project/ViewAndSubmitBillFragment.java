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
import com.ezhuang.model.ProjectBill;
import com.ezhuang.model.SpMaterial;
import com.ezhuang.purchase.PurchaseActivity_;
import com.ezhuang.quality.ProgressDetailActivity_;
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

    public String roleId = "";

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

        if (readOnly){

        }else{
            listView.setSwipeListViewListener(baseSwipeListViewListener);
        }

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
                viewHolder.layout_bill_item = view.findViewById(R.id.layout_bill_item);
                viewHolder.sp_m_name = (TextView) view.findViewById(R.id.sp_m_name);
                viewHolder.sp_m_spec = (TextView) view.findViewById(R.id.sp_m_spec);
                viewHolder.item_count = (TextView) view.findViewById(R.id.item_count);
                viewHolder.sp_m_unit_name = (TextView) view.findViewById(R.id.sp_m_unit_name);
                viewHolder.item_remark = (TextView) view.findViewById(R.id.item_remark);
                viewHolder.grid_view = (GridView) view.findViewById(R.id.gridView);
                viewHolder.btnDel =  view.findViewById(R.id.btnDel);
                viewHolder.grid_view.setAdapter(new MyAdapter());

                viewHolder.bmb = view.findViewById(R.id.bmb);
                viewHolder.bmb_name = (TextView) view.findViewById(R.id.bmb_name);
                viewHolder.bmb_m_name = (TextView) view.findViewById(R.id.bmb_m_name);
                viewHolder.bmb_m_price = (TextView) view.findViewById(R.id.bmb_m_price);
                viewHolder.bmb_m_total = (TextView) view.findViewById(R.id.bmb_m_total);

                convertView = view;
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final SpMaterial spMaterial = (SpMaterial) getItem(position);

            if(readOnly){
                viewHolder.item_state.setText(bill_detail_state[spMaterial.state]);
            }

            viewHolder.layout_bill_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Global.BUYER.equals(roleId)){
                        SpMaterial sp = mData.get(position);
                        SpMaterial tmp = new SpMaterial();
                        tmp.bigTypeId = sp.bigTypeId;
                        tmp.bigTypeName = sp.bigTypeName;
                        tmp.mtId = sp.mtId;
                        tmp.mtName = sp.mtName;
                        tmp.sTypeId = sp.sTypeId;
                        tmp.sTypeName = sp.sTypeName;
                        tmp.spec = sp.spec;
                        tmp.unitId = sp.unitId;
                        tmp.unitName = sp.unitName;
                        tmp.item_count = sp.item_count;
                        tmp.item_id = sp.item_id;
                        tmp.bmb_name = sp.bmb_name;
                        tmp.bmb_m_name = sp.bmb_m_name;
                        tmp.bmb_price = sp.bmb_price;

                        PurchaseActivity_.intent(getActivity()).spMaterial(tmp).startForResult(16);
                    }
                }
            });
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
                myAdapter.notifyDataSetChanged();
            }

            if(!spMaterial.bmb_name.isEmpty()) {
                viewHolder.bmb.setVisibility(View.VISIBLE);
                viewHolder.bmb_name.setText(spMaterial.bmb_name);
                viewHolder.bmb_m_name.setText(spMaterial.bmb_m_name);
                viewHolder.bmb_m_price.setText(spMaterial.bmb_price);
                viewHolder.bmb_m_total.setText(""+(Float.parseFloat(spMaterial.bmb_price)*Float.parseFloat(spMaterial.item_count)));
            }else{
                viewHolder.bmb.setVisibility(View.GONE);
            }

            return convertView;
        }
    };


    class ViewHolder{
        View layout_bill_item;
        TextView sp_m_name;
        TextView sp_m_spec;
        TextView item_count;
        TextView sp_m_unit_name;
        TextView item_remark;
        TextView item_state;
        GridView grid_view;
        View   btnDel;

        View   bmb;
        TextView bmb_name;
        TextView bmb_m_name;
        TextView bmb_m_price;
        TextView bmb_m_total;
    }

    class ImageClick implements View.OnClickListener{

        int position;
        List<PhotoData> mData;
        void setData(int position,List<PhotoData> mData){
            this.position = position;
            this.mData = mData;
        }

        @Override
        public void onClick(View v) {
            Log.i("position",""+position);
            Intent intent = new Intent(getActivity(), ImagePagerActivity_.class);
            ArrayList<String> arrayUri = new ArrayList<String>();
            for (PhotoData item : mData) {
                arrayUri.add(item.uri.toString());
            }
            intent.putExtra("mArrayUri", arrayUri);
            intent.putExtra("mPagerPosition", position);
            intent.putExtra("needEdit", false);
            startActivityForResult(intent, FillBillItemFragment.RESULT_REQUEST_IMAGE);
        }
    };


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
            ImageClick imageClick = new ImageClick();
            imageClick.setData(position,mData);
            holder.image.setOnClickListener(imageClick);
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

            fillBillItem.show(mData.get(position));
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
