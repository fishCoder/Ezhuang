package com.ezhuang.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/4/29 0029.
 */
public class SpOdDetail implements IPcMt {
    public String spOdMtName;
    public String spOdMtNum;
    public String spOdMtUnit;
    public String pjdId;
    public String spOdPrice;
    public String spOdTotalPrice;

    public String spOdSpec;

    public String spOdId;
    public String spOdMtTypeName;

    public String spOrderId;
    public int spOdState;
    @Override
    public String getCompanyName() {
        return "";
    }

    @Override
    public String getBigTypeId() {
        return "";
    }

    @Override
    public String getBigTypeName() {
        return "";
    }

    @Override
    public String getMtId() {
        return "";
    }

    @Override
    public String getMtName() {
        return spOdMtName;
    }

    @Override
    public String getPrice() {
        return spOdPrice;
    }

    @Override
    public String getSTypeId() {
        return "";
    }

    @Override
    public String getSTypeName() {
        return spOdMtTypeName;
    }

    @Override
    public String getSpec() {
        return spOdSpec;
    }

    @Override
    public String getUnitName() {
        return spOdMtUnit;
    }

    @Override
    public String getMtImg() {
        return "";
    }

    @Override
    public String getCount() {
        return spOdMtNum;
    }

    @Override
    public String getCompanyId() {
        return "";
    }

    @Override
    public int getMtState() {
        return spOdState;
    }

    @Override
    public int getMtType() {
        return 0;
    }

    @Override
    public void toLoadData(JSONObject jsonObject) {
        try {
            spOdMtNum = jsonObject.getString("spOdMtNum");
            spOdMtTypeName = jsonObject.getString("spOdMtTypeName");
            spOdId = jsonObject.getString("spOdId");
            spOdPrice = jsonObject.getString("spOdPrice");
            spOrderId = jsonObject.getString("spOrderId");
            spOdState = jsonObject.getInt("spOdState");
            spOdTotalPrice = jsonObject.getString("spOdTotalPrice");
            spOdMtName = jsonObject.getString("spOdMtName");
            spOdMtUnit = jsonObject.getString("spOdMtUnitName");
            spOdSpec = jsonObject.getString("spOdSpec");
            pjdId = jsonObject.getString("pjdId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
