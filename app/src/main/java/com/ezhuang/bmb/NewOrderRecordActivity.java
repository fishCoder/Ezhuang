package com.ezhuang.bmb;



import android.support.v7.app.ActionBar;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by Administrator on 2015/6/27 0027.
 */
@EActivity(R.layout.activity_new_order_record)
public class NewOrderRecordActivity extends BaseActivity {


    NewOrderRecordFragment newOrderRecordFragment;

    @AfterViews
    void init(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        newOrderRecordFragment = NewOrderRecordFragment_.builder().build();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, newOrderRecordFragment).commit();

        ActionBar.Tab tab_person = actionBar
                .newTab()
                .setCustomView(R.layout.tab_textview)
                .setTabListener(new ActionBar.TabListener() {
                    @Override
                    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
                        newOrderRecordFragment.changeType(1);
                    }

                    @Override
                    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

                    }

                    @Override
                    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

                    }
                 });

        ActionBar.Tab tab_company = actionBar
                .newTab()
                .setCustomView(R.layout.tab_textview)
                .setTabListener(new ActionBar.TabListener() {
                    @Override
                    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
                        newOrderRecordFragment.changeType(2);
                    }

                    @Override
                    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

                    }

                    @Override
                    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

                    }
                });

        actionBar.addTab(tab_person);
        actionBar.addTab(tab_company);


        TextView title_person = (TextView)tab_person.getCustomView().findViewById(R.id.tab_title);
        title_person.setText("个人");

        TextView title_company = (TextView)tab_company.getCustomView().findViewById(R.id.tab_title);
        title_company.setText("公司");
    }



}
