package com.ezhuang.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2015/4/14 0014.
 */
@Table(name="SpMaterial")
public class SpMaterial extends Model{


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

    public String item_count;

    public String item_remark;

    public List   itemImages;


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
