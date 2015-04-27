package com.ezhuang.model;


import java.io.Serializable;

public class ProjectBill implements Serializable{

    private String id;
    private String billCode;
    private String billTime;
    private String pjId;
    private String pjName;
    private String remark;
    private int    bdCount;
    private int    state;

    public int getBdCount() {
        return bdCount;
    }

    public void setBdCount(int bdCount) {
        this.bdCount = bdCount;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getBillTime() {
        return billTime;
    }

    public void setBillTime(String billTime) {
        this.billTime = billTime;
    }

    public String getPjId() {
        return pjId;
    }

    public void setPjId(String pjId) {
        this.pjId = pjId;
    }

    public String getPjName() {
        return pjName;
    }

    public void setPjName(String pjName) {
        this.pjName = pjName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
