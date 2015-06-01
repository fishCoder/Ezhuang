package com.ezhuang.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2015/4/7 0007.
 */
public class Project implements Serializable {
    private String pjId;
    private String pjName; // 项目名称
    private String pjCpId; // 所属公司

    private String userName;
    private String realName;

    private String pjRemark; // 备注
    private String pjCreateTime; // 创建时间
    private Float pjBudget; // 项目预算
    private Integer pjProgress; // 项目进度
    private String pjAddress; // 施工地址
    private Integer pjState; // 项目状态 1:启动 2:进行中 3:竣工

    private String pj_no; // 项目编号
    private String pjHousetype; // 户型
    private String pjArea; // 面积
    private String pjContractnum; // 合同编号
    private String pjDesigner; // 设计师

    private StaffUser pjCreator; // 创建人id
    private StaffUser pjChecker;
    private StaffUser pjBuyer;
    private StaffUser pjM;
    private StaffUser pjQuality;

    private Integer pgCount;
    private Integer billCount;

    private Integer nodeId;
    private String nodeName;

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getPgCount() {
        return pgCount;
    }

    public void setPgCount(Integer pgCount) {
        this.pgCount = pgCount;
    }

    public Integer getBillCount() {
        return billCount;
    }

    public void setBillCount(Integer billCount) {
        this.billCount = billCount;
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

    public String getPjCpId() {
        return pjCpId;
    }

    public void setPjCpId(String pjCpId) {
        this.pjCpId = pjCpId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPjRemark() {
        return pjRemark;
    }

    public void setPjRemark(String pjRemark) {
        this.pjRemark = pjRemark;
    }

    public String getPjCreateTime() {
        return pjCreateTime;
    }

    public void setPjCreateTime(String pjCreateTime) {
        this.pjCreateTime = pjCreateTime;
    }

    public Float getPjBudget() {
        return pjBudget;
    }

    public void setPjBudget(Float pjBudget) {
        this.pjBudget = pjBudget;
    }

    public Integer getPjProgress() {
        return pjProgress;
    }

    public void setPjProgress(Integer pjProgress) {
        this.pjProgress = pjProgress;
    }

    public String getPjAddress() {
        return pjAddress;
    }

    public void setPjAddress(String pjAddress) {
        this.pjAddress = pjAddress;
    }

    public Integer getPjState() {
        return pjState;
    }

    public void setPjState(Integer pjState) {
        this.pjState = pjState;
    }

    public String getPj_no() {
        return pj_no;
    }

    public void setPj_no(String pj_no) {
        this.pj_no = pj_no;
    }

    public String getPjHousetype() {
        return pjHousetype;
    }

    public void setPjHousetype(String pjHousetype) {
        this.pjHousetype = pjHousetype;
    }

    public String getPjArea() {
        return pjArea;
    }

    public void setPjArea(String pjArea) {
        this.pjArea = pjArea;
    }

    public String getPjContractnum() {
        return pjContractnum;
    }

    public void setPjContractnum(String pjContractnum) {
        this.pjContractnum = pjContractnum;
    }

    public String getPjDesigner() {
        return pjDesigner;
    }

    public void setPjDesigner(String pjDesigner) {
        this.pjDesigner = pjDesigner;
    }

    public StaffUser getPjCreator() {
        return pjCreator;
    }

    public void setPjCreator(StaffUser pjCreator) {
        this.pjCreator = pjCreator;
    }

    public StaffUser getPjChecker() {
        return pjChecker;
    }

    public void setPjChecker(StaffUser pjChecker) {
        this.pjChecker = pjChecker;
    }

    public StaffUser getPjBuyer() {
        return pjBuyer;
    }

    public void setPjBuyer(StaffUser pjBuyer) {
        this.pjBuyer = pjBuyer;
    }

    public StaffUser getPjM() {
        return pjM;
    }

    public void setPjM(StaffUser pjM) {
        this.pjM = pjM;
    }

    public StaffUser getPjQuality() {
        return pjQuality;
    }

    public void setPjQuality(StaffUser pjQuality) {
        this.pjQuality = pjQuality;
    }
}
