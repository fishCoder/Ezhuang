package com.ezhuang.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Administrator on 2015/4/14 0014.
 */
@Table(name="SpMaterial")
public class SpMaterial extends Model{

    @Column(name = "bigTypeId")
    public String bigTypeId;

    @Column(name = "bigTypeName")
    public String bigTypeName;

    @Column(name = "mtId")
    public String mtId;

    @Column(name = "mtName")
    public String mtName;

    @Column(name = "sTypeId")
    public String sTypeId;

    @Column(name = "sTypeName")
    public String sTypeName;

    @Column(name = "spec")
    public String spec;

    @Column(name = "unitId")
    public String unitId;

    @Column(name = "unitName")
    public String unitName;
}
