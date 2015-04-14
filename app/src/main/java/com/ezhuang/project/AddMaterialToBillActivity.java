package com.ezhuang.project;

import android.support.v7.app.ActionBar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.third.PinnedHeaderExpandableListView;
import com.ezhuang.common.third.StickyLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/14 0014.
 */
@EActivity(R.layout.activity_add_material_to_bill)
public class AddMaterialToBillActivity extends BaseActivity implements
        ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener,
        PinnedHeaderExpandableListView.OnHeaderUpdateListener,
        StickyLayout.OnGiveUpTouchEventListener {

    PinnedHeaderExpandableListView expandableListView;
    StickyLayout stickyLayout;

    @AfterViews
    void init(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setCustomView(R.layout.activity_add_material_to_bill_actionbar);

        EditText editText = (EditText) findViewById(R.id.editText);
        Spinner  spinner  = (Spinner) findViewById(R.id.spinner);

        Map<String,Object> base = new HashMap<>();
        base.put("icon",R.mipmap.ic_spinner_maopao_time);
        base.put("name","基础");

        Map<String,Object> main = new HashMap<>();
        main.put("icon",R.mipmap.ic_spinner_maopao_friend);
        main.put("name","主材");


        List<Map<String,Object>> list = new LinkedList<>();
        list.add(base);
        list.add(main);

        spinner.setAdapter(new SimpleAdapter(this,list,R.layout.spinner_sp_material,new String[]{"icon","name"},new int[]{R.id.imageView,R.id.textView}));
    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }

    @Override
    public View getPinnedHeader() {


        return null;
    }

    @Override
    public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {

    }

    @Override
    public boolean giveUpTouchEvent(MotionEvent event) {

        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }
}
