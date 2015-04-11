package com.ezhuang.common.network;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ezhuang.MyApp;
import com.ezhuang.user.UserDetailActivity_;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import com.ezhuang.BaseActivity;
import com.ezhuang.R;
import com.ezhuang.common.CustomDialog;
import com.ezhuang.common.Global;
import com.ezhuang.common.ImageLoadTool;
import com.ezhuang.FootUpdate;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cc191954 on 14-8-11.
 * 封装了图片下载
 * 封装了网络请求
 */
public class BaseFragment extends Fragment implements NetworkCallback, FootUpdate.LoadMore {

    protected NetworkImpl networkImpl;
    private ImageLoadTool imageLoadTool = new ImageLoadTool();

    protected LayoutInflater mInflater;

    private ProgressDialog mProgressDialog;

    public String REFRESH_SESSION = Global.HOST + "/app/stf/loginAgain.do";

    protected void showProgressBar(boolean show) {
        showProgressBar(show, "");
    }

    protected void setProgressBarProgress(int progress) {
        if (mProgressDialog == null) {
            return;
        }

        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgress(progress);
    }

    @Override
    public void refreshSession() {

        RequestParams params = new RequestParams();
        params.put("phone", MyApp.currentUser.getPhone());
        params.put("password", MyApp.currentUser.getPassword());
        postNetwork(REFRESH_SESSION,params,REFRESH_SESSION);
    }

    protected void showProgressBar(boolean show, String message) {
        if (mProgressDialog == null) {
            return;
        }

        if (show) {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        } else {
            mProgressDialog.hide();
        }
    }

    public ActionBarActivity getActionBarActivity() {
        return (ActionBarActivity) getActivity();
    }

    protected void showProgressBar(boolean show, int messageId) {
        String message = getString(messageId);
        showProgressBar(show, message);
    }

    protected boolean progressBarIsShowing() {
        return mProgressDialog.isShowing();
    }

    protected ImageLoadTool getImageLoad() {
        return imageLoadTool;
    }

    @Override
    public void loadMore() {

    }

    protected FootUpdate mFootUpdate = new FootUpdate();

    protected View.OnClickListener mOnClickUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String globalKey = (String) v.getTag();

            Intent intent = new Intent(getActivity(), UserDetailActivity_.class);
            intent.putExtra("globalKey", globalKey);
            startActivity(intent);
        }
    };

    protected void initSetting() {
        networkImpl.initSetting();
    }

    public boolean isLoadingFirstPage(String tag) {
        return networkImpl.isLoadingFirstPage(tag);
    }

    public boolean isLoadingLastPage(String tag) {
        return networkImpl.isLoadingLastPage(tag);
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        mInflater = LayoutInflater.from(getActivity());
        networkImpl = new NetworkImpl(getActivity(), this);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
    }

    @Override
    public void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initSetting();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {
    }

    @Override
    public void showError(String msg) {
        showButtomToast(msg);
    }

    protected void postNetwork(String url, RequestParams params, final String tag) {
        networkImpl.loadData(url, params, tag, -1, null, NetworkImpl.Request.Post);
    }

    protected void postNetwork(String url, RequestParams params, final String tag, int dataPos, Object data) {
        networkImpl.loadData(url, params, tag, dataPos, data, NetworkImpl.Request.Post);
    }

    @Override
    public void getNetwork(String url, final String tag) {
        networkImpl.loadData(url, null, tag, -1, null, NetworkImpl.Request.Get);
    }

    protected void getNetwork(String url, final String tag, int dataPos, Object data) {
        networkImpl.loadData(url, null, tag, dataPos, data, NetworkImpl.Request.Get);
    }

    public void putNetwork(String url, final String tag) {
        networkImpl.loadData(url, null, tag, -1, null, NetworkImpl.Request.Put);
    }

    protected void showDialog(String title, String msg, DialogInterface.OnClickListener clickOk) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定", clickOk)
                .setNegativeButton("取消", null)
                .show();
        CustomDialog.dialogTitleLineColor(getActivity(), dialog);
    }


    public void putNetwork(String url, final String tag, int dataPos, Object data) {
        networkImpl.loadData(url, null, tag, dataPos, data, NetworkImpl.Request.Put);
    }

    public void putNetwork(String url, RequestParams params, final String tag, Object data) {
        networkImpl.loadData(url, params, tag, -1, data, NetworkImpl.Request.Put);
    }

    public void deleteNetwork(String url, final String tag) {
        networkImpl.loadData(url, null, tag, -1, null, NetworkImpl.Request.Delete);
    }

    public void deleteNetwork(String url, final String tag, int dataPos, Object data) {
        networkImpl.loadData(url, null, tag, dataPos, data, NetworkImpl.Request.Delete);
    }

    public void deleteNetwork(String url, final String tag, Object data) {
        networkImpl.loadData(url, null, tag, -1, data, NetworkImpl.Request.Delete);
    }

    protected void getNextPageNetwork(String url, final String tag) {
        networkImpl.getNextPageNetwork(url, tag);
    }

    protected void showErrorMsg(int code, JSONObject json) {
        if (code == NetworkImpl.NETWORK_ERROR) {
            showButtomToast(R.string.connect_service_fail);
        } else {
            String msg = Global.getErrorMsg(json);
            if (!msg.isEmpty()) {
                showButtomToast(msg);
            }
        }
    }

    protected void showButtomToast(int messageId) {
        if (!isResumed()) {
            return;
        }

        String message = getString(messageId);
        showButtomToast(message);
    }

    protected void showButtomToast(String msg) {
        if (!isResumed()) {
            return;
        }

        Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    protected void showMiddleToast(String msg) {
        if (!isResumed()) {
            return;
        }

        Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    protected void iconfromNetwork(ImageView view, String url) {
        imageLoadTool.loadImage(view, Global.makeSmallUrl(view, url));
    }

//    protected void iconfromNetwork(ImageView view, String url, int minWitdh) {
//        imageLoadTool.loadImage(view, Global.makeSmallUrl(view, url, minWitdh));
//    }

    protected void iconfromNetwork(ImageView view, String url, DisplayImageOptions options) {
        imageLoadTool.loadImage(view, Global.makeSmallUrl(view, url), options);
    }

    protected void showDialogLoading() {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showDialogLoading();
        }
    }

    protected void hideProgressDialog() {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).hideProgressDialog();
        }
    }
}
