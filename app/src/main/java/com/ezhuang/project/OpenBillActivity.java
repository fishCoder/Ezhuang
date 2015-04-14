package com.ezhuang.project;

import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.model.BillDetail;
import com.inqbarna.tablefixheaders.TableFixHeaders;
import com.inqbarna.tablefixheaders.adapters.BaseTableAdapter;
import com.inqbarna.tablefixheaders.adapters.TableAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/4/13 0013.
 */
@EActivity(R.layout.activity_open_bill)
public class OpenBillActivity extends BaseActivity {

    @ViewById
    TableFixHeaders table;

    List<BillDetail> billDetails = null;

    @AfterViews
    void init(){

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        billDetails = new LinkedList<>();
//        table.setAdapter(adapter);


    }


    TableAdapter adapter = new BaseTableAdapter(){
        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public View getView(int row, int column, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public int getWidth(int column) {
            return 0;
        }

        @Override
        public int getHeight(int row) {
            return 0;
        }

        @Override
        public int getItemViewType(int row, int column) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_open_bill, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @OptionsItem
    void action_add(){
        AddMaterialToBillActivity_.intent(this).start();
    }

    @OptionsItem
    void action_submit(){
        //TODO 提交订单
    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }
}
