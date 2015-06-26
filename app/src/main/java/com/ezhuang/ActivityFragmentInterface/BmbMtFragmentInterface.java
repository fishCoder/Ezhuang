package com.ezhuang.ActivityFragmentInterface;

import com.ezhuang.model.SpMaterial;

/**
 * Created by Administrator on 2015/6/23 0023.
 */
public interface BmbMtFragmentInterface {

    void setSpinnerSelection(int position);

    void showAddDailog(SpMaterial spMaterial);

    boolean isSelect(SpMaterial spMaterial);

    void addData();
}
