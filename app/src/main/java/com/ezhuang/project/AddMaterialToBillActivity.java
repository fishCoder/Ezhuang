package com.ezhuang.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.ezhuang.MyApp;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.PhotoOperate;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.common.photopick.PhotoPickActivity;
import com.ezhuang.model.BillDetail;
import com.ezhuang.model.Billing;
import com.ezhuang.model.BillingDetail;
import com.ezhuang.model.PhotoData;
import com.ezhuang.model.SpMaterial;
import com.ezhuang.model.SpMtType;
import com.ezhuang.project.detail.ListListener;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    String SUBMIT_BILLING = Global.HOST + "/app/project/addProjectBilling.do";

    Map<String,List<SpMaterial>> mData;
    List<SpMtType> mType;

    List<SpMaterial> searchData;

    List<SpMaterial> billData;

    ActionBar actionBar;

    PhotoOperate photoOperate = new PhotoOperate(this);

    public SpMaterial spMaterial;

    EditText billRemarkEdit;

    String QINIU_TOKEN = Global.HOST + "/app/qiniu/appToken.do";

    Billing projectBilling;

    @Extra
    String projectId;

    @Extra
    String pjBillId;

    String QUERY_BILL_DETAIL = Global.HOST + "/app/project/queryBillingDetail.do?pjBillId=%s";

    @AfterViews
    void init(){

        if(mData == null){
            mData = new HashMap<>();
            mType = new LinkedList<>();
            searchData = new LinkedList<>();
            billData = new LinkedList<>();
            projectBilling = new Billing();
            projectBilling.pj_id = projectId;
            projectBilling.pj_bill_details = new LinkedList<>();
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

        if(pjBillId != null){
            viewAndSubmitBillFragment.dealblank = false;
            getNetwork(String.format(QUERY_BILL_DETAIL,pjBillId),QUERY_BILL_DETAIL);
            showDialogLoading();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, viewAndSubmitBillFragment).commit();
    }

    void changeToSubmitActionBar(){

        needToback = true;
        actionBar.setCustomView(R.layout.submit_bill_actionbar);
        findViewById(R.id.action_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pjBillId != null){
                    reSubmitBill();
                    return;
                }

                if(billData.size()==0){
                    showButtomToast("没有可提交的内容");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(AddMaterialToBillActivity.this);

                billRemarkEdit = (EditText) mInflater.inflate(R.layout.my_edit_text,null);

                builder.setTitle("提交");
                builder.setView(billRemarkEdit);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isNeedUploadPic = false;
                        int imageCount = 0;
                        for(SpMaterial item : billData){
                            if(item.itemImages.size()>0){
                                isNeedUploadPic = true;
                                imageCount += item.itemImages.size();
                            }
                        }

                        if(imageCount==hasUpPic.size()){
                            //刚才已经上传过
                            isNeedUploadPic = false;
                        }

                        if(isNeedUploadPic){
                            getNetwork(QINIU_TOKEN,QINIU_TOKEN);
                            showProgressBar(true,"请求TOKEN");
                        }else{
                            for(SpMaterial item : billData){
                                BillingDetail detail = new BillingDetail();
                                detail.bill_d_dosage = item.item_count;
                                detail.bill_d_m_id = item.mtId;
                                detail.bill_d_remark = item.item_remark;
                                detail.bill_d_img = "";
                                projectBilling.pj_bill_details.add(detail);
                            }
                            submitBilling();
                        }


                    }
                });
                builder.setNegativeButton("取消",null);
                AlertDialog dialog = builder.create();
                dialog.show();
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
        spinner.setSelection(Integer.parseInt(selectBigType)-1);

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
    //待上传图片总数
    int imgCount = 0;
    //已上传图片数量
    int hasUpImgCount = 0;
    //本地路径 服务器路径
    Map<String,String> hasUpPic = new HashMap<>();

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
        if(QINIU_TOKEN.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){

                imgCount = 0;
                hasUpImgCount = 0;

                String token = respanse.getString("data");
                Log.i("七牛上传凭证", token);
                projectBilling.pj_bill_remark = billRemarkEdit.getText().toString();
                UploadManager uploadManager = new UploadManager();
                for (SpMaterial bill_item : billData){


                    BillingDetail detail = new BillingDetail();
                    detail.bill_d_dosage = bill_item.item_count;
                    detail.bill_d_m_id = bill_item.mtId;
                    detail.bill_d_remark = bill_item.item_remark;
                    detail.bill_d_img = "";


                    for (PhotoData photoData : (List<PhotoData>) bill_item.itemImages){
                        Log.i("上传图片本地路径",photoData.uri.toString());
                        String url = photoData.uri.toString();

                        String fileType =  url.substring(url.lastIndexOf("."),url.length());
                        String key = new StringBuffer(MyApp.currentUser.getCompanyId())
                                .append("/bill/")
                                .append(UUID.randomUUID().toString())
                                .append(fileType).toString();
                        if(hasUpPic.get(url)==null||hasUpPic.get(url).isEmpty()){
                            uploadManager.put(new File(Global.getPath(this, photoData.uri)),key, token,new BillUpCompletionHandler(detail,url),null);
                            imgCount++;
                        }

                    }



                    projectBilling.pj_bill_details.add(detail);
                }

                showProgressBar(true,String.format("上传图片[%d/%d]",hasUpImgCount,imgCount));
            }else{
                showButtomToast("请求TOKEN失败");
                showProgressBar(false);
            }
        }
        if(SUBMIT_BILLING.equals(tag)){
            Log.i("提交开单",""+code);
            showProgressBar(false);
            if(code == NetworkImpl.REQ_SUCCESSS){
                billData.clear();
                viewAndSubmitBillFragment.updateData(billData);
                showButtomToast("提交成功");
            }else{
                showButtomToast("错误码:"+code);
            }
        }
        if(QUERY_BILL_DETAIL.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                Log.i("json",respanse.getString("data"));
                jsonToObject(respanse);

            }else{
                showButtomToast("错误码 "+code);
            }
        }
    }


    @Background
    public void jsonToObject(JSONObject respanse){
        try {
            JSONArray jsonArray = respanse.getJSONArray("data");

            for (int i=0 ; i<jsonArray.length() ; i++){

                JSONArray jsonBillDetails = jsonArray.getJSONObject(i).getJSONArray("billDetails");
                for(int k=0; k<jsonBillDetails.length() ;k++){
                    JSONObject jsonObject = jsonBillDetails.getJSONObject(k);
                    SpMaterial spMaterial = JsonUtil.Json2Object(jsonObject.getString("matl"),SpMaterial.class);
                    spMaterial.item_count = String.valueOf(jsonObject.getInt("dosage"));
                    spMaterial.item_remark = jsonObject.getString("remark");
                    spMaterial.mgBillId = jsonObject.getString("mgBillId");
                    spMaterial.itemImages = new LinkedList();
                    String img = jsonObject.getString("img");
                    if(!img.isEmpty()){
                        String[] imgs = img.split("&");
                        for (String url:imgs){
                            PhotoData photoData = new PhotoData(url);
                            Log.i("图片路径",url);
                            spMaterial.itemImages.add(photoData);
                        }
                    }
                    billData.add(spMaterial);

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        toUI();
    }

    @UiThread
    public void toUI(){
        hideProgressDialog();
        viewAndSubmitBillFragment.updateData(billData);
    }

    class BillUpCompletionHandler implements UpCompletionHandler{

        BillingDetail detail;
        String localUrl;
        public BillUpCompletionHandler(BillingDetail detail,String localUrl){
            this.detail = detail;
            this.localUrl = localUrl;
        }

        @Override
        public void complete(String key, ResponseInfo info, JSONObject response) {
            Log.v("info.statusCode",""+info.statusCode);
            Log.v("上传一张图片",key);

            if(info.statusCode==200){

                hasUpPic.put(localUrl,key);

                if(detail.bill_d_img.isEmpty()){
                    detail.bill_d_img = key;
                }else {
                    detail.bill_d_img += "&" + key;
                }

                hasUpImgCount++;
                showProgressBar(true,String.format("上传图片[%d/%d]",hasUpImgCount,imgCount));

                if(hasUpImgCount==imgCount){
                    submitBilling();
                }

            }else{
                showProgressBar(false);
                showMiddleToast("图片上传出错");
            }
        }
    }


    void submitBilling(){
        showProgressBar(true,"提交开单数据");
        String json = JsonUtil.Object2Json(projectBilling);
        Log.i("sProjectBill",json);
        RequestParams params = new RequestParams();
        params.put("sProjectBill",json);

        postNetwork(SUBMIT_BILLING,params,SUBMIT_BILLING);
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
        Log.v(this.getClass().getSimpleName()+" 准备跳转 ",""+System.currentTimeMillis());
        Intent intent = new Intent(this, PhotoPickActivity.class);
        intent.putExtra(PhotoPickActivity.EXTRA_MAX, 6);
        startActivityForResult(intent, FillBillItemFragment.RESULT_REQUEST_PICK_PHOTO);
    }

    @OnActivityResult(FillBillItemFragment.RESULT_REQUEST_PICK_PHOTO)
    void result_pick_photo(int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK) {
            try {
                List<PhotoData> photoDataList = new LinkedList<>();
                List<PhotoPickActivity.ImageInfo> pickPhots = (List<PhotoPickActivity.ImageInfo>) data.getSerializableExtra("data");
                for (PhotoPickActivity.ImageInfo item : pickPhots) {
                    Uri uri = Uri.parse(item.path);
                    File outputFile = photoOperate.scal(uri);

                    photoDataList.add(new PhotoData(outputFile));

                }
                fillBillItemFragment.updateData(photoDataList);
            } catch (Exception e) {
                showMiddleToast("缩放图片失败");
                Global.errorLog(e);
            }

        }
    }

//    @OnActivityResult(FillBillItemFragment.RESULT_REQUEST_IMAGE)
//    void result_view_alter_photo(int resultCode, Intent data){
//        if (resultCode == RESULT_OK) {
//            ArrayList<String> delUris = data.getStringArrayListExtra("mDelUrls");
//            for (String item : delUris) {
//                for (int i = 0; i < spMaterial.itemImages.size(); ++i) {
//                    if (((List<FillBillItemFragment.PhotoData>)spMaterial.itemImages).get(i).uri.toString().equals(item)) {
//                        spMaterial.itemImages.remove(i);
//                    }
//                }
//            }
//
//        }
//    }

    void reSubmitBill(){
        //TODO 重新提交订单
    }

    void addBillRow(){
        if(Global.isFloat(spMaterial.item_count)){

            boolean flag = true;
            for(SpMaterial m : billData){
                if(m.mtId.equals(spMaterial.mtId)){
                    m.item_count = spMaterial.item_count;
                    m.item_remark = spMaterial.item_remark;
                    m.itemImages = spMaterial.itemImages;
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
