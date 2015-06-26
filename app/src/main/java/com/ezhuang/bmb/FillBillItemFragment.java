package com.ezhuang.bmb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezhuang.ActivityFragmentInterface.BmbMtFragmentInterface;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.model.PhotoData;
import com.ezhuang.model.SpMaterial;
import com.ezhuang.project.AddMaterialToBillActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/16 0016.
 */

public class FillBillItemFragment extends DialogFragment {

    TextView spMName;

    TextView spMUnitName;

    TextView spMSpec;

    EditText itemCount;

    EditText itemRemark;

    GridView gridView;

    int imageWidthPx;
    ImageSize mSize;

    public static final int RESULT_REQUEST_IMAGE = 100;
    public static final int RESULT_REQUEST_FOLLOW = 1002;
    public static final int RESULT_REQUEST_PICK_PHOTO = 1003;
    public static final int RESULT_REQUEST_PHOTO = 1005;
    public static final int RESULT_REQUEST_LOCATION = 1006;

    void initView(){

        gridView.setAdapter(adapter);

        imageWidthPx = Global.dpToPx(120);
        mSize = new ImageSize(imageWidthPx, imageWidthPx);

        String count = itemCount.getText().toString();

        if(!count.isEmpty()){
            return;
        }

        SpMaterial spMaterial = ((NewOrderActivity)getActivity()).spMaterial;

        spMName.setText(spMaterial.mtName);
        spMUnitName.setText(spMaterial.bmb_price+"元/"+spMaterial.unitName);

        if(spMaterial.spec.isEmpty()){
            spMSpec.setVisibility(View.GONE);
        }else{
            spMSpec.setVisibility(View.VISIBLE);
        }

        spMSpec.setText(spMaterial.spec);

        itemCount.setText(getContent(spMaterial.item_count));
        itemRemark.setText(getContent(spMaterial.item_remark));

        if(spMaterial.itemImages != null){
            mData = spMaterial.itemImages;
        }else{
            mData = new LinkedList<>();
        }
        adapter.notifyDataSetChanged();
    }

    String getContent(String value){
        if(value == null){
            Log.v(" value "," is null ");
            return "";
        }else{
            Log.v(" value ",value);
            return value;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_fill_bmb_order, null);

        spMName = (TextView) view.findViewById(R.id.spMName);
        spMSpec = (TextView) view.findViewById(R.id.spMSpec);
        spMUnitName = (TextView) view.findViewById(R.id.spMUnitName);

        itemCount = (EditText) view.findViewById(R.id.fill_item_count);
        itemRemark = (EditText) view.findViewById(R.id.fill_item_remark);

        gridView = (GridView) view.findViewById(R.id.gridView);

        view.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpMaterial spMaterial = ((NewOrderActivity)getActivity()).spMaterial;
                spMaterial.item_count = new String(itemCount.getText().toString());
                spMaterial.item_remark = new String(itemRemark.getText().toString());
                ((BmbMtFragmentInterface)getActivity()).addData();
                dismiss();
                Log.v(" spMaterial.item_count ", spMaterial.item_count);

            }
        });
        view.findViewById(R.id.btnUpPic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        initView();
        builder.setView(view);
        return builder.create();
    }




    List<PhotoData> mData = new LinkedList<>();

    public void updateData(List<PhotoData> mData){
        this.mData = mData;

        adapter.notifyDataSetChanged();
    }

    BaseAdapter adapter = new BaseAdapter() {

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

            holder.image.setImageResource(R.mipmap.ic_default_image);

            holder.image.setVisibility(View.VISIBLE);
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

}
