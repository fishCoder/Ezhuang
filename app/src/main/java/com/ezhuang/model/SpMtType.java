package com.ezhuang.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Administrator on 2015/4/14 0014.
 */
public class SpMtType extends Model {

    @Column(name = "bigTypeId")
    public String bigTypeId;

    @Column(name = "bigTypeName")
    public String bigTypeName;

    @Column(name = "typeId")
    public String typeId;

    @Column(name = "typeName")
    public String typeName;

    static public List<SpMtType> getAll() {
        return new Select().from(SpMtType.class).execute();
    }

    static public List<SpMtType> getTypeByBigType(String bigType){
        return new Select().from(SpMtType.class).where("bigTypeId=?",bigType).execute();
    }

    static public void saveAll(List<SpMtType> spMtTypes) {
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < spMtTypes.size(); i++) {
                spMtTypes.get(i).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }
    static public void clear(){
        new Delete().from(SpMtType.class).execute();
    }
}