package com.ezhuang.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/5/3 0003.
 */
public class SpOrder {
    public String  spOrderNo;
    public int     spOrderState;
    public String  spBmbName;
    public String  spPjName;
    public int     detailsCount;
    public String  spOrderId;
    public String  spOrderTime;
    public String  spPjId;

    public SpOrder(){

    }

    public SpOrder(JSONObject data){
        try {
            spOrderId = data.getString("spOrderId");
            spOrderNo = data.getString("spOrderNo");
            spOrderState = data.getInt("spOrderState");
            spOrderTime = data.getString("spOrderTime");
            spBmbName = data.getString("spBmbName");
            detailsCount = data.getInt("detailsCount");
            spPjName = data.getString("spPjName");
            spPjId = data.getString("spPjId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
