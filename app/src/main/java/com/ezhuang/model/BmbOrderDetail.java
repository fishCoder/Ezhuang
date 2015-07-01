package com.ezhuang.model;

/**
 * Created by Administrator on 2015/6/26 0026.
 */
public class BmbOrderDetail {
    public String bmbODId;
    public String bmbODMId;
    public String bmbODNum;
    public String bmbODPrice;
    public String bmbOorderId;
    public String bmbODstate;
    public Material material;
    public class Material {
        public String spec;
        public String bigTypeName;
        public String sTypeName;
        public String mtName;
        public String unitName;
    }
}
