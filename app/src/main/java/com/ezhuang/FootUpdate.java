package com.ezhuang;

import android.view.LayoutInflater;
import android.view.View;

import com.ezhuang.common.Global;

import java.lang.reflect.Method;

/**
 * Created by chaochen on 14-10-22.
 */
public class FootUpdate {

    View mLayout;
    View mClick;
    View mLoading;

    public FootUpdate() {
    }

    public int getHigh() {
        if (mLayout == null) {
            return 0;
        }

        return mLayout.getHeight();
    }

    public void initToHead(Object listView, LayoutInflater inflater, final LoadMore loadMore) {
        View v = inflater.inflate(R.layout.listview_foot, null);

        // 为了防止触发listview的onListItemClick事件
        mLayout = v.findViewById(R.id.layout);
        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mClick = v.findViewById(R.id.textView);
        mClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore.loadMore();
                showLoading();
            }
        });

        mLoading = v.findViewById(R.id.progressBar);

        try {
            Method method = listView.getClass().getMethod("addHeaderView", View.class);
            method.invoke(listView, v);
        } catch (Exception e) {
            Global.errorLog(e);
        }

        mLayout.setVisibility(View.GONE);
    }

    public void init(Object listView, LayoutInflater inflater, final LoadMore loadMore) {
        View v = inflater.inflate(R.layout.listview_foot, null, false);

        // 为了防止触发listview的onListItemClick事件
        mLayout = v.findViewById(R.id.layout);
        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mClick = v.findViewById(R.id.textView);
        mClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore.loadMore();
                showLoading();
            }
        });

        mLoading = v.findViewById(R.id.progressBar);

        try {
            Method method = listView.getClass().getMethod("addFooterView", View.class);
            method.invoke(listView, v);
        } catch (Exception e) {
            Global.errorLog(e);
        }

        mLayout.setVisibility(View.GONE);
    }

    public void showLoading() {
        show(true, true);
    }

    public void showFail() {
        show(true, false);
    }

    public void dismiss() {
        show(false, true);
    }

    private void show(boolean show, boolean loading) {
        if (mLayout == null) {
            return;
        }

        if (show) {
            mLayout.setVisibility(View.VISIBLE);
            mLayout.setPadding(0, 0, 0, 0);
            if (loading) {
                mClick.setVisibility(View.INVISIBLE);
                mLoading.setVisibility(View.VISIBLE);
            } else {
                mClick.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.INVISIBLE);
            }
        } else {
            mLayout.setVisibility(View.INVISIBLE);
            mLayout.setPadding(0, -mLayout.getHeight(), 0, 0);
        }
    }

    public static interface LoadMore {
        public void loadMore();
    }
}
