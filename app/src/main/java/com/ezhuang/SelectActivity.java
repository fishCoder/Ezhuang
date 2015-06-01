package com.ezhuang;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2015/5/22 0022.
 */
@EActivity(R.layout.activity_select)
public class SelectActivity extends BaseActivity {

    @ViewById
    View layout_start;
    @ViewById
    TextView txt_start;
    @ViewById
    View layout_end;
    @ViewById
    TextView txt_end;

    DatePickerDialog dateStartDialog;

    DatePickerDialog dateEndDialog;

    @Extra
    String start = "";
    @Extra
    String end = "";

    @AfterViews
    void init() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        if(start.isEmpty()){
            calendar.setTime(new Date());
        }else{
            txt_start.setText(start);
            try {
                calendar.setTime(sdf.parse(start));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        dateStartDialog = new DatePickerDialog(this,dateStartListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

        if(end.isEmpty()){
            calendar.setTime(new Date());
        }else{
            txt_end.setText(end);
            try {
                calendar.setTime(sdf.parse(end));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        dateEndDialog = new DatePickerDialog(this,dateEndListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        layout_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateStartDialog.show();
            }
        });

        layout_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateEndDialog.show();
            }
        });
    }

    DatePickerDialog.OnDateSetListener dateStartListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear++;
            txt_start.setText(String.format("%d-%d-%d",year,monthOfYear,dayOfMonth));
        }
    };

    DatePickerDialog.OnDateSetListener dateEndListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear++;
            txt_end.setText(String.format("%d-%d-%d",year,monthOfYear,dayOfMonth));
        }
    };

    public static int REQUEST_CODE = 522;

    @OptionsItem(android.R.id.home)
    void home() {
        Intent intent = new Intent();
        intent.putExtra("start",txt_start.getText().toString());
        intent.putExtra("end",txt_end.getText().toString());
        setResult(RESULT_OK,intent);
        finish();
    }


}
