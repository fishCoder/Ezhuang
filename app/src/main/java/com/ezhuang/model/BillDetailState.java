package com.ezhuang.model;

/**
 * Created by Administrator on 2015/4/29 0029.
 */
public enum BillDetailState {
    UNBUY(1),BUY(2),SEND(3),OK(4),SELF(5);
    public int state;
    private  BillDetailState(int state){
        this.state = state;
    }

}
