package com.ezhuang.model;

/**
 * Created by Administrator on 2015/5/5 0005.
 */
public enum BillState {
    UNCHECK(0),UNBUY(1),PARTBUY(2),BUYALL(3),DONE(4),REJECT(5);
    public int state;
    private BillState(int _state){
        state = _state;
    }
}
