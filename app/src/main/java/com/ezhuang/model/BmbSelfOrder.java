package com.ezhuang.model;

/**
 * Created by Administrator on 2015/6/29 0029.
 */
public class BmbSelfOrder {
    public String orderId;
    public String orderState;
    public String bmbStaffName;
    public String orderCode;
    public String bmbOrderRespStaffId;
    public String bmbName;
    public String orderTime;
    public String orderCount;
    public String totalPrice;
    public Owner  sOwner;
    public Company sCompany;

    public class  Owner{
        public String phoneNumber;
        public String ownerAddress;
        public String ownerName;
    }

    public class Company{
        public String responsiblePerson;
        public String reviceAddress;
        public String provideName;
        public String telphone;
        public String provideAddress;
    }
}
