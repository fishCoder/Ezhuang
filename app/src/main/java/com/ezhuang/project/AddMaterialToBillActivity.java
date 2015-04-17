package com.ezhuang.project;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.PhotoOperate;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.common.photopick.PhotoPickActivity;
import com.ezhuang.model.SpMaterial;
import com.ezhuang.model.SpMtType;
import com.ezhuang.project.detail.ListListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/14 0014.
 */
@EActivity(R.layout.activity_add_material_to_bill)
public class AddMaterialToBillActivity extends BaseActivity {

    static public int PHOTO_MAX_COUNT = 6;

    String lastKeyWord = "";

    boolean needToback = true;

    TotalSpMaterialFragment totalSpMaterialFragment;

    SearchSpMaterialFragment searchSpMaterialFragment;

    ViewAndSubmitBillFragment viewAndSubmitBillFragment;

    FillBillItemFragment fillBillItemFragment;

    String selectBigType = "1";

    String QUERY_MATERIAL = Global.HOST + "/app/project/queryProjectMaterialsByAndroid.do";

    Map<String,List<SpMaterial>> mData;
    List<SpMtType> mType;

    List<SpMaterial> searchData;

    List<SpMaterial> billData;

    ActionBar actionBar;

    PhotoOperate photoOperate = new PhotoOperate(this);

    public SpMaterial spMaterial;


    @AfterViews
    void init(){

        if(mData == null){
            mData = new HashMap<>();
            mType = new LinkedList<>();
            searchData = new LinkedList<>();
            billData = new LinkedList<>();
        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        changeToSubmitActionBar();

        fillBillItemFragment = new FillBillItemFragment();
        viewAndSubmitBillFragment = ViewAndSubmitBillFragment_.builder().build();
        viewAndSubmitBillFragment.setFillBillItem(fillBillItem);
        searchSpMaterialFragment = SearchSpMaterialFragment_.builder().build();
        searchSpMaterialFragment.setFillBillItem(fillBillItem);
        totalSpMaterialFragment = TotalSpMaterialFragment_.builder().build();
        totalSpMaterialFragment.setListListener(new ListListener() {
            @Override
            public void loadMore() {
            }

            @Override
            public void refresh() {
                getNetwork(QUERY_MATERIAL, QUERY_MATERIAL);
            }
        },fillBillItem);


        getSupportFragmentManager().beginTransaction().replace(R.id.container, viewAndSubmitBillFragment).commit();
    }

    void changeToSubmitActionBar(){

        needToback = true;

        actionBar.setCustomView(R.layout.submit_bill_actionbar);
        findViewById(R.id.action_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.action_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToMaterialActionBar();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, totalSpMaterialFragment).commit();
                if(mData.size()==0)
                    new LoadDataTask().execute();
            }
        });
    }

    void changeToMaterialActionBar(){

        needToback =  false;

        actionBar.setCustomView(R.layout.activity_add_material_to_bill_actionbar);

        EditText editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(watcher);
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

        spinner.setAdapter(new SimpleAdapter(this, list, R.layout.spinner_sp_material, new String[]{"icon", "name"}, new int[]{R.id.imageView, R.id.textView}));
        spinner.setOnItemSelectedListener(onItemSelectedListener);
    }

    @OptionsItem(android.R.id.home)
    void home() {
        if(needToback)
            onBackPressed();
        else{
            changeToSubmitActionBar();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, viewAndSubmitBillFragment).commit();
        }
    }


    private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            mType = SpMtType.getTypeByBigType(selectBigType);
            if(mType==null || mType.size()==0){
                return false;
            }else{
                publishProgress();
                for (SpMtType type:mType){
                    if(mData.get(type.typeId)==null)
                        mData.put(type.typeId, SpMaterial.getListByType(type.typeId));
                }
                return true;
            }


        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Boolean reslut) {
            super.onPostExecute(reslut);

            if(reslut){
                totalSpMaterialFragment.updateData(mData,mType);
            }else{
                showProgressBar(true,"从服务器加载材料数据\n时间较长 请稍等");
                getNetwork(QUERY_MATERIAL,QUERY_MATERIAL);
            }
        }
    }

    @Override
    public void parseJson(int code, JSONObject respanse, final String tag, int pos, Object data) throws JSONException {

        if(QUERY_MATERIAL.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                new AsyncTask<JSONObject,Void,Void>(){
                    @Override
                    protected Void doInBackground(JSONObject... params) {
                        long start = System.currentTimeMillis();

                        JSONObject resp = params[0];
                        JSONArray jsonArray = null;

                        List<SpMaterial> totalSpMaterial = new LinkedList<SpMaterial>();
                        try {
                            jsonArray = params[0].getJSONArray("data");
                            mType.clear();
                            for (int i=0;i<jsonArray.length();i++){
                                SpMtType type = JsonUtil.Json2Object(jsonArray.getString(i), SpMtType.class);
                                mType.add(type);

                                List<SpMaterial> spMaterials = new LinkedList<SpMaterial>();
                                JSONArray childList = jsonArray.getJSONObject(i).getJSONArray("childList");

                                for(int k=0; k<childList.length(); k++){
                                    SpMaterial spMaterial = new SpMaterial(childList.getJSONObject(k));
                                    spMaterials.add(spMaterial);
                                    totalSpMaterial.add(spMaterial);
                                }

                                mData.put(type.typeId, spMaterials);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            return null;
                        }

                        SpMtType.clear();
                        SpMtType.saveAll(mType);

                        List<SpMtType> list = new LinkedList<SpMtType>();
                        for (int i=0;i<mType.size();i++){
                            SpMtType type = mType.get(i);
                            if(type.bigTypeId.equals(selectBigType)){
                                list.add(type);
                            }
                        }

                        mType = list;
                        publishProgress();

                        long end = System.currentTimeMillis();
                        Log.i("Log","解析处理时间"+(end-start));
                        SpMaterial.clear();

                        SpMaterial.saveAll(totalSpMaterial);

                        long saveEnd = System.currentTimeMillis();
                        Log.i("Log","本地存储处理时间" + (saveEnd - end));
                        return  null;
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                        showProgressBar(false);
                        totalSpMaterialFragment.updateData(mData,mType);
                        super.onProgressUpdate(values);
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {

                        super.onPostExecute(aVoid);
                    }
                }.execute(respanse);
            }

        }

    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


            if(s.length()!=0){
                if(lastKeyWord.isEmpty())
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, searchSpMaterialFragment).commit();
                lastKeyWord = s.toString();

                new AsyncTask<String,Void,Void>(){
                    @Override
                    protected Void doInBackground(String... params) {
                        showDialogLoading();
                        searchData = SpMaterial.search(params[0].toString());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {

                        hideProgressDialog();
                        searchSpMaterialFragment.updateData(searchData);
                        super.onPostExecute(aVoid);
                    }
                }.execute(s.toString());
            }else{
                lastKeyWord = s.toString();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, totalSpMaterialFragment).commit();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] bigType = new String[]{"1","2"};
            selectBigType = bigType[position];
            new LoadDataTask().execute();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    interface FillBillItem{
        public void show(SpMaterial spMaterial);
    }

    FillBillItem fillBillItem = new FillBillItem() {
        @Override
        public void show(SpMaterial spMaterial) {
            AddMaterialToBillActivity.this.spMaterial = spMaterial;
            fillBillItemFragment.show(getFragmentManager(), "");

        }
    };

    void toPickUpPhotoActivity(){
        Intent intent = new Intent(this, PhotoPickActivity.class);
        intent.putExtra(PhotoPickActivity.EXTRA_MAX, 6);
        startActivityForResult(intent, FillBillItemFragment.RESULT_REQUEST_PICK_PHOTO);
    }

    @OnActivityResult(FillBillItemFragment.RESULT_REQUEST_PICK_PHOTO)
    void result_pick_photo(int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK) {
            try {
                List<FillBillItemFragment.PhotoData> photoDataList = new LinkedList<>();
                List<PhotoPickActivity.ImageInfo> pickPhots = (List<PhotoPickActivity.ImageInfo>) data.getSerializableExtra("data");
                for (PhotoPickActivity.ImageInfo item : pickPhots) {
                    Uri uri = Uri.parse(item.path);
                    File outputFile = photoOperate.scal(uri);

                    photoDataList.add(new FillBillItemFragment.PhotoData(outputFile));

                }
                fillBillItemFragment.updateData(photoDataList);
            } catch (Exception e) {
                showMiddleToast("缩放图片失败");
                Global.errorLog(e);
            }

        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        if (requestCode == FillBillItemFragment.RESULT_REQUEST_PICK_PHOTO) {
//            if (resultCode == Activity.RESULT_OK) {
//                try {
//                    fillBillItemFragment.show(getFragmentManager(), "");
//                    List<FillBillItemFragment.PhotoData> photoDataList = new LinkedList<>();
//                    ArrayList<PhotoPickActivity.ImageInfo> pickPhots = (ArrayList<PhotoPickActivity.ImageInfo>) data.getSerializableExtra("data");
//                    for (PhotoPickActivity.ImageInfo item : pickPhots) {
//                        Uri uri = Uri.parse(item.path);
//                        File outputFile = photoOperate.scal(uri);
//
//                        photoDataList.add(new FillBillItemFragment.PhotoData(outputFile));
//
//                    }
//                    fillBillItemFragment.updateData(photoDataList);
//                } catch (Exception e) {
//                    showMiddleToast("缩放图片失败");
//                    Global.errorLog(e);
//                }
//
//            }
//        }
//    }

    void addBillRow(){
        if(Global.isFloat(spMaterial.item_count)){

            boolean flag = true;
            for(SpMaterial m : billData){
                if(m.mtId.equals(spMaterial.mtId)){
                    m.item_count = spMaterial.item_count;
                    m.item_remark = spMaterial.item_remark;
                    flag = false;
                }
            }
            if(flag){
                billData.add(spMaterial);
            }

            viewAndSubmitBillFragment.updateData(billData);
            fillBillItemFragment.dismiss();
        }else{
            showMiddleToast("用量不正确");
        }
    }
}
