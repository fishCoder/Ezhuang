package com.ezhuang.quality;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.ImagePagerActivity_;
import com.ezhuang.MyApp;
import com.ezhuang.R;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.PhotoOperate;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.common.photopick.PhotoPickActivity;
import com.ezhuang.model.BillingDetail;
import com.ezhuang.model.PhotoData;
import com.ezhuang.model.SpMaterial;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by Administrator on 2015/4/22 0022.
 */
@EActivity(R.layout.activity_add_project_progress)
public class AddProjectProgressActivity extends BaseActivity {

    @ViewById
    GridView gridView;

    @ViewById
    EditText message;

    @ViewById
    TextView node_name;

    @ViewById
    TextView user_name;

    String pg_node="";

    String pg_deal="";

    List<PhotoData> mData = new LinkedList<>();

    String ADD_PROGRESS = Global.HOST + "/app/progress/addProgress.do";

    String QINIU_TOKEN = Global.HOST + "/app/qiniu/appToken.do";

    @Extra
    String projectId;

    PhotoOperate photoOperate = new PhotoOperate(this);

    int imageWidthPx;
    ImageSize mSize;

    private Uri fileUri;

    int PHOTO_MAX_COUNT = 9;

    String pgPtUrl = "";

    @AfterViews
    void init(){

        imageWidthPx = Global.dpToPx(100);
        mSize = new ImageSize(imageWidthPx, imageWidthPx);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mData.size()) {
                    int count = PHOTO_MAX_COUNT - mData.size();
                    if (count <= 0) {
                        return;
                    }

                    Intent intent = new Intent(AddProjectProgressActivity.this, PhotoPickActivity.class);
                    intent.putExtra(PhotoPickActivity.EXTRA_MAX, count);
                    startActivityForResult(intent, RESULT_REQUEST_PICK_PHOTO);

                } else {
                    Intent intent = new Intent(AddProjectProgressActivity.this, ImagePagerActivity_.class);
                    ArrayList<String> arrayUri = new ArrayList<String>();
                    for (PhotoData item : mData) {
                        arrayUri.add(item.uri.toString());
                    }
                    intent.putExtra("mArrayUri", arrayUri);
                    intent.putExtra("mPagerPosition", position);
                    intent.putExtra("needEdit", true);
                    startActivityForResult(intent, RESULT_REQUEST_IMAGE);
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_project_progress, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public static final int RESULT_REQUEST_IMAGE = 100;
    public static final int RESULT_REQUEST_FOLLOW = 1002;
    public static final int RESULT_REQUEST_PICK_PHOTO = 1003;
    public static final int RESULT_REQUEST_PHOTO = 1005;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_REQUEST_PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    ArrayList<PhotoPickActivity.ImageInfo> pickPhots = (ArrayList<PhotoPickActivity.ImageInfo>) data.getSerializableExtra("data");
                    for (PhotoPickActivity.ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File outputFile = photoOperate.scal(uri);
                        mData.add(new PhotoData(outputFile));
                    }
                } catch (Exception e) {
                    showMiddleToast("缩放图片失败");
                    Global.errorLog(e);
                }
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == RESULT_REQUEST_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    File outputFile = photoOperate.scal(fileUri);
                    mData.add(mData.size(), new PhotoData(outputFile));
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    showMiddleToast("缩放图片失败");
                    Global.errorLog(e);
                }
            }
        } else if (requestCode == RESULT_REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> delUris = data.getStringArrayListExtra("mDelUrls");
                for (String item : delUris) {
                    for (int i = 0; i < mData.size(); ++i) {
                        if (mData.get(i).uri.toString().equals(item)) {
                            mData.remove(i);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == SELECT_NODE_TYEP) {
            if(resultCode == RESULT_OK){
                pg_node = data.getStringExtra("pg_node");
                node_name.setText(data.getStringExtra("node_name"));
            }
        } else if (requestCode == SELECT_DEAL_TYEP) {
            if(resultCode == RESULT_OK){
                pg_deal = data.getStringExtra("pg_deal");
                user_name.setText(data.getStringExtra("deal_name"));
            }
        } else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    BaseAdapter adapter = new BaseAdapter() {

        public int getCount() {
            return mData.size() + 1;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        ArrayList<ViewHolder> holderList = new ArrayList<ViewHolder>();

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                holder.image = (ImageView) mInflater.inflate(R.layout.image_make_maopao, parent, false);
                holderList.add(holder);
                holder.image.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            if (position == getCount() - 1) {
                if (getCount() == (PHOTO_MAX_COUNT + 1)) {
                    holder.image.setVisibility(View.INVISIBLE);

                } else {
                    holder.image.setVisibility(View.VISIBLE);
                    holder.image.setImageResource(R.mipmap.make_maopao_add);
                    holder.uri = "";
                }

            } else {
                holder.image.setVisibility(View.VISIBLE);
                PhotoData photoData = mData.get(position);
                Uri data = photoData.uri;
                holder.uri = data.toString();

                ImageLoader.getInstance().loadImage(data.toString(), mSize, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        for (ViewHolder item : holderList) {
                            if (item.uri.equals(imageUri)) {
                                item.image.setImageBitmap(loadedImage);
                            }
                        }
                    }
                });
            }

            return holder.image;
        }

        class ViewHolder {
            ImageView image;
            String uri = "";
        }

    };

    //上传失败图片数量
    int failCount = 0;
    //待上传图片总数
    int imgCount = 0;
    //已上传图片数量
    int hasUpImgCount = 0;
    //本地路径 服务器路径
    Map<String,String> hasUpPic = new HashMap<>();

    @Override
    public void parseJson(int code, JSONObject respanse, final String tag, int pos, Object data) throws JSONException {
        if (QINIU_TOKEN.equals(tag)) {
            if (code == NetworkImpl.REQ_SUCCESSS) {

                imgCount = 0;
                hasUpImgCount = 0;
                failCount = 0;
                String token = respanse.getString("data");
                Log.i("七牛上传凭证", token);
                UploadManager uploadManager = new UploadManager();

                for (PhotoData photoData : mData) {
                    Log.i("上传图片本地路径", photoData.uri.toString());
                    final String url = photoData.uri.toString();

                    String fileType = url.substring(url.lastIndexOf("."), url.length());
                    final String key = new StringBuffer(MyApp.currentUser.getCompanyId())
                            .append("/bill/")
                            .append(UUID.randomUUID().toString())
                            .append(fileType).toString();

                    imgCount++;
                    if(hasUpPic.get(url)==null||hasUpPic.get(url).isEmpty()){
                        hasUpImgCount++;
                    }

                    uploadManager.put(new File(Global.getPath(this, photoData.uri)), key, token, new UpCompletionHandler() {
                        @Override
                        public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                            if (responseInfo.statusCode == 200) {
                                hasUpPic.put(url, s);

                                hasUpImgCount++;
                                showProgressBar(true, String.format("上传图片[%d/%d]", hasUpImgCount, imgCount));

                                if (hasUpImgCount == imgCount) {
                                    add_progress();
                                }

                                if (failCount != 0 && imgCount == (hasUpImgCount + failCount)){
                                    showProgressBar(false);
                                    showButtomToast(String.format("上传%d张 失败%d张",hasUpImgCount,failCount));
                                }
                            } else {
                                failCount ++;
                            }
                        }

                    }, null);
                    imgCount++;

                }
                showProgressBar(true, String.format("上传图片[%d/%d]", hasUpImgCount, imgCount));
            } else {
                showButtomToast("请求TOKEN失败");
                showProgressBar(false);
            }
        }
        if(ADD_PROGRESS.equals(tag)){

            Log.i("提交开单",""+code);
            showProgressBar(false);
            if(code == NetworkImpl.REQ_SUCCESSS){
                showButtomToast("提交成功");
                finish();
            }else{
                showButtomToast("错误码:"+code);
            }
        }

    }


    int SELECT_NODE_TYEP = 2000;
    int SELECT_DEAL_TYEP = 2001;
    @Click
    void item_node() {
        Intent intent = new Intent(this, SelectNodeAndUserActivity_.class);
        intent.putExtra("select_type", "node");
        startActivityForResult(intent,SELECT_NODE_TYEP);
    }
    @Click
    void item_deal() {
        Intent intent = new Intent(this, SelectNodeAndUserActivity_.class);
        intent.putExtra("select_type", "deal");
        startActivityForResult(intent,SELECT_DEAL_TYEP);
    }
    @OptionsItem
    void action_add(){
        String msg = message.getText().toString();
        if(pg_deal.isEmpty()){
            showMiddleToast("未选择审核人员");
            return;
        }
        if(pg_node.isEmpty()){
            showMiddleToast("未添加装修节点");
            return;
        }
        if(msg.isEmpty()){
            showMiddleToast("请填写描述");
            return;
        }

        if(mData.size() == 0){
            add_progress();
        }else{
            getNetwork(QINIU_TOKEN,QINIU_TOKEN);
            showProgressBar(true,"请求TOKEN");
        }

    }

    @OptionsItem(android.R.id.home)
    void home() {

        if(message.getText().toString().isEmpty()&&pgPtUrl.isEmpty()){
            onBackPressed();
        }else{
            com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(this,"提示", "放弃这次编辑吗？");
            dialog.show();
            dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            dialog.setOnCancelButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }


    }


    void add_progress(){
        showProgressBar(true, "提交进度");
        RequestParams params = new RequestParams();
        params.put("pgPjId",projectId);
        params.put("pgRemark",message.getText().toString());
        params.put("pgPtUrl",pgPtUrl);
        params.put("pgNode",pg_node);
        params.put("pgDeal",pg_deal);

        postNetwork(ADD_PROGRESS, params, ADD_PROGRESS);
    }
}
