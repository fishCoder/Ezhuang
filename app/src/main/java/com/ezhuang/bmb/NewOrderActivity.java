package com.ezhuang.bmb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.ezhuang.ActivityFragmentInterface.BmbMtFragmentInterface;
import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.model.SpMaterial;
import com.gc.materialdesign.widgets.Dialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/6/19 0019.
 */
@EActivity(R.layout.activity_new_bmb_order)
public class NewOrderActivity extends BaseActivity implements BmbMtFragmentInterface {

    BmbMtFragment bmbMtFragment;
    SearchBmbMtFragment searchBmbMtFragment;
    FillBillItemFragment fillBillItemFragment;
    ViewNewOrderFragment viewNewOrderFragment;

    ActionBar actionBar;
    Spinner spinner;

    String lastKeyWord = "";

    public SpMaterial spMaterial;

    boolean needBack = true;

    @AfterViews
    void init(){

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        bmbMtFragment = BmbMtFragment_.builder().build();
        searchBmbMtFragment = SearchBmbMtFragment_.builder().build();
        fillBillItemFragment = new FillBillItemFragment();
        viewNewOrderFragment = ViewNewOrderFragment_.builder().build();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, bmbMtFragment).commit();
        changeToMaterialActionBar();
    }

    void changeToSubmitActionBar(){

        needBack = false;

        actionBar.setCustomView(R.layout.submit_bill_actionbar);
        findViewById(R.id.action_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewNewOrderFragment.newBmbOrder();
            }
        });
        findViewById(R.id.action_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToMaterialActionBar();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, bmbMtFragment).commit();

            }
        });

    }


    void changeToMaterialActionBar(){

        needBack = true;

        actionBar.setCustomView(R.layout.activity_add_material_to_bill_actionbar);

        EditText editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(watcher);
        spinner  = (Spinner) findViewById(R.id.spinner);
        View view     = findViewById(R.id.action_view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToSubmitActionBar();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, viewNewOrderFragment).commit();
            }
        });

        Map<String,Object> base = new HashMap<>();
        base.put("icon",R.mipmap.ic_spinner_maopao_time);
        base.put("name","基础");

        Map<String,Object> main = new HashMap<>();
        main.put("icon",R.mipmap.ic_spinner_maopao_friend);
        main.put("name","主材");


        List<Map<String,Object>> list = new LinkedList<>();
        list.add(base);
        list.add(main);

        spinner.setAdapter(new SimpleAdapter(this, list, R.layout.spinner_sp_material, new String[]{"icon", "name"}, new int[]{R.id.imageView, R.id.textView}));
        spinner.setOnItemSelectedListener(onItemSelectedListener);

    }

    public void setSpinnerSelection(int position){
        spinner.setSelection(position);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] bigType = new String[]{"1","2"};
            bmbMtFragment.setSelectBigType(bigType[position]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(s.length()!=0){
                if(lastKeyWord.isEmpty())
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, searchBmbMtFragment).commit();
                lastKeyWord = s.toString();

                searchBmbMtFragment.searchMaterial(lastKeyWord);
            }else{
                lastKeyWord = s.toString();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, bmbMtFragment).commit();
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void showAddDailog(SpMaterial spMaterial){
        this.spMaterial = spMaterial;
        fillBillItemFragment.show(getFragmentManager(), "");
    }

    @Override
    public boolean isSelect(SpMaterial spMaterial) {
        return viewNewOrderFragment.containData(spMaterial);
    }

    @Override
    public void addData() {
        viewNewOrderFragment.addData(spMaterial);
        searchBmbMtFragment.notifyDataSetChanged();
        bmbMtFragment.notifyDataSetChanged();
    }

    @OptionsItem(android.R.id.home)
    void home() {
        if(needBack) {

            if(viewNewOrderFragment.mData.size()!=0){
                final Dialog dialog = new Dialog(this, "退出", "确定退出开单吗？");
                dialog.show();
                dialog.getButtonAccept().setText("确定");
                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                        finish();
                    }
                });
            }else{
                onBackPressed();
            }

        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.container, bmbMtFragment).commit();
            changeToMaterialActionBar();
        }
    }
}
