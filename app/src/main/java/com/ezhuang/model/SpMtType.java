package com.ezhuang.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * Created by Administrator on 2015/4/14 0014.
 */
public class SpMtType extends Model {

    @Column(name = "bigTypeId")
    String bigTypeId;

    @Column(name = "bigTypeName")
    String bigTypeName;

    @Column(name = "typeId")
    String typeId;

    @Column(name = "typeName")
    String typeName;

}
