package com.ezhuang.model;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/4/28 0028.
 */
public interface IPcMt {
    String getCompanyName();
    String getBigTypeId();
    String getBigTypeName();
    String getMtId();
    String getMtName();
    String getPrice();
    String getSTypeId();
    String getSTypeName();
    String getSpec();
    String getUnitName();
    String getMtImg();
    String getCount();
    void toLoadData(JSONObject jsonObject);
}
