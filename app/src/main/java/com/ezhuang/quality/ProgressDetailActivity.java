package com.ezhuang.quality;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezhuang.BaseActivity;
import com.ezhuang.ImagePagerActivity_;
import com.ezhuang.R;
import com.ezhuang.adapter.GridImageAdapter;
import com.ezhuang.common.Global;
import com.ezhuang.common.JsonUtil;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.common.photopick.VideoPickActivity;
import com.ezhuang.model.PhotoData;
import com.ezhuang.model.Project;
import com.ezhuang.model.ProjectProgress;
import com.ezhuang.model.VideoData;
import com.ezhuang.project.FillBillItemFragment;
import com.ezhuang.project.ProjectDetailActivity;
import com.ezhuang.project.ProjectDetailActivity_;
import com.ezhuang.project.detail.SetProjectInfo_;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Administrator on 2015/4/24 0024.
 */
@EActivity(R.layout.activity_progress_detail)
public class ProgressDetailActivity extends BaseActivity {

    @ViewById
    TextView pg_time;
    @ViewById
    View     layout_quality;
    @ViewById
    View     layout_owner;
    @ViewById
    TextView quality_state;
    @ViewById
    TextView owner_state;
    @ViewById
    TextView pg_remark;
    @ViewById
    TextView pg_name;
    @ViewById
    TextView pgQtEmeExplain;
    @ViewById
    TextView pgOwnerEmeExplain;
    @ViewById
    GridView gridView;
    @ViewById
    GridView videoGridView;
    @ViewById
    View layout_share;

    @StringArrayRes
    String[] pg_state;
    int[] pg_state_color = {R.color.undo,R.color.undo,R.color.pass,R.color.reject};

    @Extra("projectProgress")
    ProjectProgress pg;

    @Extra("roleId")
    String roleId;
    @Extra("pjId")
    String pjId;
    @Extra("pgId")
    String pgId;
    @Extra
    Project project;

    boolean isOperatOk = false;
    int state;

    View action_pass;
    View action_reject;

    String PG_NODE_EXAMIE = Global.HOST + "/app/progress/addNodeExamine.do";

    String QUERY_PG_DETAIL = Global.HOST + "/app/progress/queryProgressDetail.do?pgId=%s";

    ActionBar actionBar;

    List<VideoData> videoDatas = new LinkedList<>();

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showStubImage(R.mipmap.ic_default_image)          // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.mipmap.ic_default_image)  // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.mipmap.ic_default_image)       // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
            .cacheOnDisc(true)
            .build();

    int imageWidthPx;
    ImageSize mSize;

    @AfterViews
    void init() {
        imageWidthPx = Global.dpToPx(120);
        mSize = new ImageSize(imageWidthPx, imageWidthPx);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        if(pg!=null){
            fill_progress();
        }else{
            getNetwork(String.format(QUERY_PG_DETAIL,pgId),QUERY_PG_DETAIL);
        }

        if(project!=null){
            findViewById(R.id.item_project).setVisibility(View.VISIBLE);
            TextView pj_name = (TextView) findViewById(R.id.pj_name);
            pj_name.setText(project.getPjName());
            findViewById(R.id.layout_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProjectDetailActivity_.intent(ProgressDetailActivity.this).project(project).start();
                }
            });
        }else{
            findViewById(R.id.item_project).setVisibility(View.GONE);
        }

        if(pg!=null){
            if(Global.PROJECT_MANAGER.equals(roleId)&&pg.owerScore!=0){
                layout_share.setVisibility(View.VISIBLE);
                layout_share.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShareSDK.initSDK(ProgressDetailActivity.this);
                        OnekeyShare oks = new OnekeyShare();
                        oks.setTitle(getString(R.string.share));
                        oks.setText("装修进度-"+pg.nodeName);
                        oks.setUrl("http://www.91jzw.com/index/home/share_pg.do?pgId="+pg.pgId);
                        for(String url:pg.imgUrls)
                            oks.setImageUrl(url);
                        oks.show(ProgressDetailActivity.this);
                    }
                });
            }
        }

    }

    void fill_progress(){
        if (Global.QUALITY.equals(roleId)&&pg.quoCheckResult==1) {
            actionBar.setCustomView(R.layout.chcek_bill_actionbar);
            TextView textView = (TextView) findViewById(R.id.title);
            textView.setText("进度审核");
            action_pass = findViewById(R.id.action_pass);
            action_pass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action_pass();
                }
            });
            action_reject = findViewById(R.id.action_reject);
            action_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action_reject();
                }
            });
        }

        pg_name.setText(pg.nodeName);
        pg_time.setText(pg.time);
        pg_remark.setText(pg.pgRemark);
        if (pg.isNeedOwnerCheck()&&Global.PROJECT_MANAGER.equals(roleId)) {
            layout_owner.setVisibility(View.VISIBLE);
            if(pg.owerScore==0){
                owner_state.setText(pg_state[pg.owerCheckResult]);
                owner_state.setTextColor(getResources().getColor(pg_state_color[pg.owerCheckResult]));
            }else{
                int i=0;
                StringBuffer stars = new StringBuffer();
                for(;i<pg.owerScore;i++){
                    stars.append("★");
                }
                for (;i<ProjectProgress.SCORE;i++){
                    stars.append("☆");
                }
                owner_state.setText(stars.toString());
                owner_state.setTextColor(getResources().getColor(R.color.yellow));
            }

            pgOwnerEmeExplain.setText(pg.owerRemark==null?"":pg.owerRemark);
        } else {
            layout_owner.setVisibility(View.GONE);
        }

        if (pg.isNeedQualityCheck()) {
            layout_quality.setVisibility(View.VISIBLE);
            quality_state.setText(pg_state[pg.quoCheckResult]);
            quality_state.setTextColor(pg_state_color[pg.quoCheckResult]);
            pgQtEmeExplain.setText(pg.quoRemark==null?"":pg.quoRemark);
        } else {
            layout_quality.setVisibility(View.GONE);
        }

        if (pg.imgUrls == null || pg.imgUrls.length == 0) {
            gridView.setVisibility(View.GONE);
        } else {
            final List<PhotoData> list = new LinkedList<>();

            for (String url : pg.imgUrls) {
                PhotoData photoData = new PhotoData(url);
                Log.i("图片路径", url);
                list.add(photoData);
            }

            gridView.setVisibility(View.VISIBLE);
            GridImageAdapter myAdapter = new GridImageAdapter();
            myAdapter.setData(list,mInflater);
            myAdapter.notifyDataSetChanged();
            gridView.setAdapter(myAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ProgressDetailActivity.this, ImagePagerActivity_.class);
                    ArrayList<String> arrayUri = new ArrayList<String>();
                    for (PhotoData item : list) {
                        arrayUri.add(item.uri.toString());
                    }
                    intent.putExtra("mArrayUri", arrayUri);
                    intent.putExtra("mPagerPosition", position);
                    intent.putExtra("needEdit", false);
                    startActivityForResult(intent, FillBillItemFragment.RESULT_REQUEST_IMAGE);
                }
            });

        }

        if(pg.mediaUrls==null||pg.mediaUrls.length==0){
            videoGridView.setVisibility(View.GONE);
        }else{
            for (String url:pg.mediaUrls){
                VideoData videoData = new VideoData();
                videoData.url = url;
                videoData.thumdUrl = url+"?vframe/jpg/offset/0/w/360/h/360";
                videoDatas.add(videoData);
            }

            videoGridView.setVisibility(View.VISIBLE);
            videoGridView.setAdapter(videoAdapter);
            videoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String url = videoDatas.get(position).url;
                    Log.d("video web url",url);
                    intent.setDataAndType(Uri.parse(url),
                            "video/*");
                    startActivity(intent);
                }
            });
        }
    }

    @OptionsItem(android.R.id.home)
    void home() {
        if(isOperatOk){
            Intent intent = getIntent();
            intent.putExtra("state",state);
            intent.putExtra("roleId",roleId);
            intent.putExtra("pgId",pgId);
            intent.putExtra("msg_state",4);
            setResult(RESULT_OK, intent);
            finish();
        }else{
            onBackPressed();
        }
    }


    public final static int PG_PASS = 2002;
    public final static int PG_REJECT = 2003;

    void action_pass(){
        SetProjectInfo_.intent(this).row(PG_PASS).title("审核意见").startForResult(PG_PASS);
    }


    void action_reject(){
        SetProjectInfo_.intent(this).row(PG_REJECT).title("驳回意见").startForResult(PG_REJECT);
    }


    BaseAdapter videoAdapter = new BaseAdapter() {

        public int getCount() {
            return videoDatas.size();
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
                holder.image = (ImageView) mInflater.inflate(R.layout.image_display, parent, false);
                holderList.add(holder);
                holder.image.setTag(holder);
                holder.image.setImageResource(R.mipmap.ic_video_play);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            String thumdUrl = videoDatas.get(position).thumdUrl;
            holder.uri = thumdUrl;
            ImageLoader.getInstance().loadImage(thumdUrl, mSize, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    for (ViewHolder item : holderList) {
                        if (item.uri.equals(imageUri)) {
                            if (android.os.Build.VERSION.SDK_INT >= 16){
                                setBackgroundV16Plus(item.image, loadedImage);
                            }
                            else{
                                setBackgroundV16Minus(item.image, loadedImage);
                            }
                        }
                    }
                }
            });

            return holder.image;
        }

        class ViewHolder {
            ImageView image;
            String uri = "";
        }

    };

    @TargetApi(16)
    private void setBackgroundV16Plus(View view, Bitmap bitmap) {
        view.setBackground(new BitmapDrawable(getResources(), bitmap));

    }

    @SuppressWarnings("deprecation")
    private void setBackgroundV16Minus(View view, Bitmap bitmap) {
        view.setBackgroundDrawable(new BitmapDrawable(bitmap));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            int row = data.getIntExtra("row",2003);
            String value = data.getStringExtra("itemValue");
            RequestParams params = new RequestParams();
            state = (row-2000);

            params.put("pgId",pg.pgId);
            params.put("pjId",pjId);
            params.put("quoCheckResult",state);
            params.put("quoRemark", value);

            pg.quoCheckResult = state;
            pgQtEmeExplain.setText(value);
            postNetwork(PG_NODE_EXAMIE, params, PG_NODE_EXAMIE);
            showProgressBar(true,"提交操作");
        }
    }



    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
        if(PG_NODE_EXAMIE.equals(tag)){
            showProgressBar(false);
            if(code == NetworkImpl.REQ_SUCCESSS){
                isOperatOk = true;
                action_pass.setVisibility(View.GONE);
                action_reject.setVisibility(View.GONE);
            }else{
                showButtomToast("错误码 "+code);
            }
        }else
        if(QUERY_PG_DETAIL.equals(tag)){
            if(code == NetworkImpl.REQ_SUCCESSS){
                Log.d("data",respanse.getString("data"));
                pg = JsonUtil.Json2Object(respanse.getJSONObject("data").getString("progress"),ProjectProgress.class);
                project = JsonUtil.Json2Object(respanse.getJSONObject("data").getString("project"),Project.class);
                pjId = project.getPjId();
                fill_progress();
            }else{

            }
        }
    }
}
