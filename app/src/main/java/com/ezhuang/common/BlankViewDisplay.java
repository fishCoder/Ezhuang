package com.ezhuang.common;

import android.view.View;
import android.widget.TextView;

import com.ezhuang.R;


/**
 * Created by chaochen on 14-10-24.
 */
public class BlankViewDisplay {

    public static void setBlank(int itemSize, Object fragment, boolean request, View v, View.OnClickListener onClick) {
        setBlank(itemSize, fragment, request, v, onClick, "");
    }


    public static void setBlank(int itemSize, Object fragment, boolean request, View v, View.OnClickListener onClick, String blankMessage) {
        View btn = v.findViewById(R.id.btnRetry);
        if (request) {
            btn.setVisibility(View.INVISIBLE);
        } else {
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(onClick);
        }

        setBlank(itemSize, fragment, request, v);
    }

    private static void setBlank(int itemSize, Object fragment, boolean request, View v) {
        boolean show = (itemSize == 0);
        if (!show) {
            v.setVisibility(View.GONE);
            return;
        }
        v.setVisibility(View.VISIBLE);

        int iconId = R.mipmap.ic_exception_no_network;
        String text = "";

        if (request) {
//            if (fragment instanceof ProjectListFragment) {
//                iconId = R.drawable.ic_exception_blank_task;
//                text = "您还没有项目\n快去coding网站创建吧";
//
//            }

        } else {
            iconId = R.mipmap.ic_exception_no_network;
            text = "获取数据失败\n请检查下网络是否通畅";
        }

        v.findViewById(R.id.icon).setBackgroundResource(iconId);
        ((TextView) v.findViewById(R.id.message)).setText(text);
    }

}
