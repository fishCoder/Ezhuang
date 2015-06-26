package com.ezhuang.model;


import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/4/14 0014.
 */
@Table(name="SpMaterial")
public class SpMaterial extends Model implements Serializable,IPcMt{


    public SpMaterial(){

    }

    public SpMaterial(JSONObject jsonObject) throws JSONException {
        bigTypeId = jsonObject.getString("bigTypeId");
        bigTypeName = jsonObject.getString("bigTypeName");
        mtId = jsonObject.getString("mtId");
        mtName = jsonObject.getString("mtName");
        sTypeId = jsonObject.getString("sTypeId");
        sTypeName = jsonObject.getString("sTypeName");
        spec = jsonObject.getString("spec");
        unitId = jsonObject.getString("unitId");
        unitName = jsonObject.getString("unitName");
        try{
            bmb_price = jsonObject.getString("price");
        }catch (JSONException e){

        }

    }

    @Column(name = "bigTypeId")
    public String bigTypeId;

    @Column(name = "bigTypeName")
    public String bigTypeName;

    @Column(name = "mtId",index = true)
    public String mtId;

    @Column(name = "mtName")
    public String mtName;

    @Column(name = "sTypeId",index = true)
    public String sTypeId;

    @Column(name = "sTypeName")
    public String sTypeName;

    @Column(name = "spec")
    public String spec;

    @Column(name = "unitId")
    public String unitId;

    @Column(name = "unitName")
    public String unitName;

    public String mgBillId;

    public String item_id;

    public String item_count;

    public String item_remark;

    public int state;

    public List   itemImages;

    public String bmb_m_id = "";

    public String bmb_name = "";

    public String bmb_m_name = "";

    @Column(name = "bmb_price")
    public String bmb_price = "";

    public String bmb_m_spec = "";

    public String bmb_m_img = "";

    public String bmb_id = "";

    public int    bmb_m_type = 0;

    @Override
    public String getBigTypeId() {
        return bigTypeId;
    }

    @Override
    public String getBigTypeName() {
        return bigTypeName;
    }

    @Override
    public String getMtId() {
        return bmb_m_id;
    }

    @Override
    public String getMtName() {
        return bmb_m_name;
    }

    @Override
    public String getPrice() {
        return String.format("%.2f",Float.parseFloat(bmb_price));
    }

    @Override
    public String getSTypeId() {
        return sTypeId;
    }

    @Override
    public String getSTypeName() {
        return sTypeName;
    }

    @Override
    public String getSpec() {
        return bmb_m_spec;
    }

    @Override
    public String getUnitName() {
        return unitName;
    }

    @Override
    public String getMtImg() {
        return bmb_m_img;
    }

    @Override
    public String getCount() {
        return item_count;
    }

    @Override
    public String getCompanyName() {
        return bmb_name;
    }

    public String getCompanyId() { return bmb_id;}

    @Override
    public int getMtType() {
        return bmb_m_type;
    }

    @Override
    public int getMtState() {
        return state;
    }

    @Override
    public void toLoadData(JSONObject jsonObject) {
        try {
            bigTypeId = jsonObject.getString("bigTypeId");
            bigTypeName = jsonObject.getString("bigTypeName");
            bmb_m_id = jsonObject.getString("mtId");
            bmb_m_name = jsonObject.getString("mtName");
            bmb_price = jsonObject.getString("price");
            bmb_m_spec = jsonObject.getString("spec");
            sTypeId = jsonObject.getString("sTypeId");
            sTypeName = jsonObject.getString("sTypeName");
            unitName = jsonObject.getString("unitName");
            bmb_id = jsonObject.optString("companyId");
            bmb_m_img = jsonObject.optString("img");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    static public void clear(){
        new Delete().from(SpMaterial.class).execute();
    }

    static public List<SpMaterial> getAll(){
        return new Select().from(SpMaterial.class).execute();
    }

    static public List<SpMaterial> getListByType(String spMtType){
        return new Select().from(SpMaterial.class).where("sTypeId=?",spMtType).execute();
    }

    static public void saveAll(List<SpMaterial> spMaterials){
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < spMaterials.size(); i++) {
                spMaterials.get(i).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    static public List<SpMaterial> search(String keyword){
        List<SpMaterial> list = new Select().from(SpMaterial.class).where("mtName like '%"+keyword+"%'").execute();
        return  list;
    }
}
