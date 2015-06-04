package com.ezhuang.quality;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
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
import com.ezhuang.common.PhotoOperate;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.common.photopick.PhotoPickActivity;
import com.ezhuang.common.photopick.VideoPickActivity;
import com.ezhuang.model.PhotoData;
import com.ezhuang.model.ProjectProgress;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
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
    GridView videoGridView;

    @ViewById
    EditText message;

    @ViewById
    TextView node_name;

    @ViewById
    TextView user_name;

    String pg_node="";

    String pg_deal="";

    List<PhotoData> mData = new LinkedList<>();

    List<VideoPickActivity.VideoInfo> videoData = new LinkedList<>();

    String ADD_PROGRESS = Global.HOST + "/app/progress/addProgress.do";

    String QINIU_TOKEN = Global.HOST + "/app/qiniu/appToken.do";


    @Extra
    String projectId;

    @Extra
    ProjectProgress pg;

    @StringArrayRes
    String[] progress_node;

    PhotoOperate photoOperate = new PhotoOperate(this);

    int imageWidthPx;
    ImageSize mSize;

    private Uri fileUri;

    int PHOTO_MAX_COUNT = 9;
    int VIDEO_MAX_COUNT = 3;

    String pgPtUrl = "";

    String pgVideoUrl = "";


    @AfterViews
    void init(){

        imageWidthPx = Global.dpToPx(100);
        mSize = new ImageSize(imageWidthPx, imageWidthPx);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        videoGridView.setAdapter(videoAdapter);
        videoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == videoData.size()){
                    video();
                }
            }
        });


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

        if(pg!=null){
            message.setText(pg.pgRemark);
            node_name.setText(pg.nodeName);

            for (int i=0;i<progress_node.length;i++){
                if(progress_node[i].equals(pg.nodeName)){
                    pg_node = String.valueOf(i+1);
                    break;
                }
            }


            String dealName = "";
            if(pg.pgDeal.equals("10")){
                dealName = "业主";
                pg_deal = "10";
            }else
            if(pg.pgDeal.equals("01")){
                dealName = "质检员";
                pg_deal = "01";
            }else
            if(pg.pgDeal.equals("11")){
                dealName = "业主 质检员";
                pg_deal = "11";
            }
            user_name.setText(dealName);
        }
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
    public static final int RESULT_REQUEST_PICK_VIDEO = 1006;

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
        } else if (requestCode == RESULT_REQUEST_PICK_VIDEO){
            try {

            if(resultCode == RESULT_OK){
                videoData = (ArrayList<VideoPickActivity.VideoInfo>) data.getSerializableExtra("data");
                videoAdapter.notifyDataSetChanged();
            }

            } catch (Exception e) {
                showMiddleToast("获取视频失败");
                Global.errorLog(e);
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
                    holder.image.setImageResource(R.mipmap.ic_add_image);
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


    BaseAdapter videoAdapter = new BaseAdapter() {

        public int getCount() {
            return videoData.size() + 1;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }


        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            holder = new ViewHolder();
            holder.image = (ImageView) mInflater.inflate(R.layout.image_make_maopao, parent, false);
            holder.image.setTag(holder);

            if (position == getCount()-1) {

                holder.image.setImageResource(R.mipmap.ic_add_video);
                holder.uri = "";

            } else {

                VideoPickActivity.VideoInfo data = videoData.get(position);

                holder.image.setImageResource(R.mipmap.ic_default_image);
                new AsyncTask<Object,Void,Bitmap>(){
                    ImageView icon;

                    @Override
                    protected Bitmap doInBackground(Object... params) {
                        Long id = (Long) params[0];

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inDither = false;
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        options.inSampleSize = 1;

                        Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, options);
                        ImageView icon = (ImageView) params[1];

                        this.icon = icon;
                        return bitmap;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        icon.setImageBitmap(bitmap);
                    }
                }.execute(data.photoId,holder.image,position);
            }

            return holder.image;
        }

        class ViewHolder {
            ImageView image;
            String uri = "";
        }

    };

    int totalVideo = 0;
    int fialVideoCount = 0;
    int hasVideoCount = 0;
    //上传失败图片数量
    int failCount = 0;
    //待上传图片总数
    int imgCount = 0;
    //已上传图片数量
    int hasUpImgCount = 0;
    //本地路径 服务器路径
    Map<String,String> hasUpPic = new HashMap<>();

    Map<String,String> hasUpVideo = new HashMap<>();

    String token = "";

    @Override
    public void parseJson(int code, JSONObject respanse, final String tag, int pos, Object data) throws JSONException {
        showProgressBar(false);
        if (QINIU_TOKEN.equals(tag)) {
            if (code == NetworkImpl.REQ_SUCCESSS) {

                imgCount = 0;
                hasUpImgCount = 0;
                failCount = 0;
                token = respanse.getString("data");
                Log.i("七牛上传凭证", token);
                UploadManager uploadManager = new UploadManager();

                if(mData.size()==0){
                    failCount = 0;
                    hasVideoCount = 0;
                    totalVideo = videoData.size();
                    upVideo();
                    return;
                }

                for (PhotoData photoData : mData) {
                    Log.i("上传图片本地路径", photoData.uri.toString());
                    final String url = photoData.uri.toString();

                    String fileType = url.substring(url.lastIndexOf("."), url.length());
                    final String key = new StringBuffer(MyApp.currentUser.getCompanyId())
                            .append("/progress/")
                            .append(UUID.randomUUID().toString())
                            .append(fileType).toString();

                    imgCount++;
                    if(hasUpPic.get(url)!=null&&!hasUpPic.get(url).isEmpty()){
                        hasUpImgCount++;
                        continue;
                    }

                    uploadManager.put(new File(Global.getPath(this, photoData.uri)), key, token, new UpCompletionHandler() {
                        @Override
                        public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                        if (responseInfo.statusCode == 200) {
                            hasUpPic.put(url, s);

                            hasUpImgCount++;
                            showProgressBar(true, String.format("上传图片[%d/%d]", hasUpImgCount, imgCount));

                            if(pgPtUrl.isEmpty()){
                                pgPtUrl = s;
                            }else{
                                pgPtUrl += "&"+s;
                            }

                            if (hasUpImgCount == imgCount) {
                                if(videoData.size() == 0){
                                    add_progress();
                                }else{
                                    failCount = 0;
                                    hasVideoCount = 0;
                                    totalVideo = videoData.size();
                                    Log.v("上传图片完成后调用","upVideo");
                                    upVideo();
                                }

                            }

                            if (failCount != 0 && imgCount == (hasUpImgCount + failCount)){
                                showProgressBar(false);
                                showButtomToast(String.format("上传%d张 失败%d张",hasUpImgCount,failCount));
                            }
                        } else {
                            failCount ++;

                            if (failCount != 0 && imgCount == (hasUpImgCount + failCount)){
                                showProgressBar(false);
                                showButtomToast(String.format("上传%d张 失败%d张",hasUpImgCount,failCount));
                            }
                        }
                        }

                    }, null);

                }
                if(hasUpImgCount==imgCount){
                    if(videoData.size()!=0){
                        Log.v("重新上传调用","upVideo");
                        upVideo();
                        return;
                    }
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
                if(pg!=null){
                    Intent intent = new Intent();
                    intent.putExtra("pgId",pg.pgId);
                    setResult(RESULT_OK,intent);
                }else{
                    setResult(RESULT_CANCELED);
                }
                finish();
            }else{
                showButtomToast("错误码:"+code);
            }
        }

    }



    void upVideo(){

        UploadManager uploadManager = new UploadManager();

        for (int i=0;i<videoData.size();i++){
            VideoPickActivity.VideoInfo info = videoData.get(i);

            if(hasUpVideo.get(info.path)==null){

                final String url = info.path;
                Log.d("url",url);
                String fileType = url.substring(url.lastIndexOf("."), url.length());
                final String key = new StringBuffer()
                        .append(MyApp.currentUser.getCompanyId())
                        .append("/progress/")
                        .append(UUID.randomUUID().toString())
                        .append(fileType).toString();
                File file = new File(url);
                Log.d("file",String.valueOf(file.exists()));
                Log.i("七牛上传凭证", token);
                uploadManager.put(file,key,token,new UpCompletionHandler(){
                    @Override
                    public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                        if(responseInfo.statusCode == 200){
                            hasVideoCount ++;
                            hasUpVideo.put(url, s);

                            Log.v("video url",s);

                            if (pgVideoUrl.isEmpty()){
                                pgVideoUrl = s;
                            }else{
                                pgVideoUrl = pgVideoUrl + "&" + s;
                            }


                            if(hasVideoCount==totalVideo){
                                add_progress();
                                return;
                            }else
                            if((hasVideoCount+failCount)==totalVideo){
                                showProgressBar(false);
                                showButtomToast(String.format("上传%d个视频 失败%d个",hasVideoCount,fialVideoCount));
                            }else{
                                Log.v("上传完一个视频后调用","upVideo");
                                upVideo();
                            }


                        }else{
                            fialVideoCount ++;

                            if((hasVideoCount+fialVideoCount)==totalVideo){
                                showProgressBar(false);
                                showButtomToast(String.format("上传%d个视频 失败%d个",hasVideoCount,fialVideoCount));
                            }else{
                                upVideo();
                            }
                        }


                    }
                },new UploadOptions(null,null, false,
                        new UpProgressHandler(){
                            public void progress(String key, double percent){
                                Log.i("qiniu", key + ": " + percent);
                                final double progress = percent*100;
                                runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        showProgressBar(true, String.format("上传视频[%d/%d]  进度  %.2f%%", (totalVideo-videoData.size()), totalVideo ,progress));
                                    }
                                });

                            }
                        }, null));

                break;
            }
        }


    }

    int SELECT_NODE_TYEP = 2000;
    int SELECT_DEAL_TYEP = 2001;
    @Click
    void item_node() {
        if(pg!=null)return;
        Intent intent = new Intent(this, SelectNodeAndUserActivity_.class);
        intent.putExtra("select_type", "node");
        startActivityForResult(intent,SELECT_NODE_TYEP);
    }
    @Click
    void item_deal() {
        if(pg!=null)return;
        Intent intent = new Intent(this, SelectNodeAndUserActivity_.class);
        intent.putExtra("select_type", "deal");
        startActivityForResult(intent,SELECT_DEAL_TYEP);
    }

    void video(){
        Intent intent = new Intent(AddProjectProgressActivity.this, VideoPickActivity.class);
        intent.putExtra(VideoPickActivity.EXTRA_MAX, VIDEO_MAX_COUNT);
        startActivityForResult(intent, RESULT_REQUEST_PICK_VIDEO);
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

        if(mData.size() == 0&&videoData.size() == 0){
            showMiddleToast("没有图片或视频");
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
            dialog.getButtonAccept().setText("接受");
            dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }


    }


    void add_progress(){
        showProgressBar(true, "提交进度");
        RequestParams params = new RequestParams();

        if(pg!=null){
            params.put("pgId",pg.pgId);
        }
        params.put("pgPjId",projectId);
        params.put("pgRemark",message.getText().toString());
        params.put("pgPtUrl",pgPtUrl);
        params.put("pgVideoUrl",pgVideoUrl);
        params.put("pgNode",pg_node);
        params.put("pgDeal",pg_deal);

        postNetwork(ADD_PROGRESS, params, ADD_PROGRESS);
    }
}
