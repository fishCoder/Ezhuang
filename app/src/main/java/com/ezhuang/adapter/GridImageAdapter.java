package com.ezhuang.adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.model.PhotoData;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/24 0024.
 */
public class GridImageAdapter extends BaseAdapter {

    int imageWidthPx;
    ImageSize mSize;

    List<PhotoData> mData = new LinkedList<>();
    LayoutInflater mInflater;

    public void setData(List<PhotoData> mData,LayoutInflater mInflater) {
        imageWidthPx = Global.dpToPx(120);
        mSize = new ImageSize(imageWidthPx, imageWidthPx);
        this.mInflater = mInflater;
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
            holder.image = (ImageView) mInflater.inflate(R.layout.image_display, parent, false);
            holderList.add(holder);
            holder.image.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setImageResource(R.mipmap.ic_default_image);
        PhotoData photoData = mData.get(position);
        Uri data = photoData.uri;
        holder.uri = data.toString();

        ImageLoader.getInstance().loadImage(data.toString()+"?imageView2/1/w/200/h/200", mSize, new SimpleImageLoadingListener() {
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

}