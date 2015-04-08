package com.ezhuang;

import android.support.v7.app.ActionBarActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import  com.ezhuang.common.CustomDialog;
import com.ezhuang.common.Global;
import com.ezhuang.common.ImageLoadTool;
import com.ezhuang.common.UnreadNotify;
import com.ezhuang.common.network.NetworkCallback;
import com.ezhuang.common.network.NetworkImpl;
import com.ezhuang.common.DialogUtil;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;




/**
 * Created by Administrator on 2015/4/2 0002.
 */
public class BaseActivity extends ActionBarActivity implements NetworkCallback {
    protected LayoutInflater mInflater;
    private ImageLoadTool imageLoadTool = new ImageLoadTool();

    private ProgressDialog mProgressDialog;

    private NetworkImpl networkImpl;

    protected ImagePagerActivity.FootUpdate mFootUpdate = new ImagePagerActivity.FootUpdate();

    protected void showProgressBar(boolean show) {
        showProgressBar(show, "");
    }
    protected void showProgressBar(boolean show, String message) {
        if (show) {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        } else {
            mProgressDialog.hide();
        }
    }

    protected void showProgressBar(boolean show, int messageId) {
        String message = getString(messageId);
        showProgressBar(show, message);
    }

    protected View.OnClickListener mOnClickUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String globalKey = (String) v.getTag();

//            UserDetailActivity_.intent(BaseActivity.this)
//                    .globalKey(globalKey)
//                    .start();
        }
    };

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

    protected ImageLoadTool getImageLoad() {
        return imageLoadTool;
    }

    protected boolean isLoadingFirstPage(String tag) {
        return networkImpl.isLoadingFirstPage(tag);
    }

    protected boolean isLoadingLastPage(String tag) {
        return networkImpl.isLoadingLastPage(tag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkImpl = new NetworkImpl(this, this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);

        mInflater = getLayoutInflater();
        initSetting();

        UnreadNotify.update(this);
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        super.onDestroy();
    }

    protected void initSetting() {
        networkImpl.initSetting();
    }

    @Override
    public void parseJson(int code, JSONObject respanse, String tag, int pos, Object data) throws JSONException {

    }

    protected void getNextPageNetwork(String url, final String tag) {
        networkImpl.getNextPageNetwork(url, tag);
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

    protected void putNetwork(String url, RequestParams params, final String tag) {
        networkImpl.loadData(url, params, tag, -1, null, NetworkImpl.Request.Put);
    }

    protected void putNetwork(String url, final String tag, int dataPos, Object data) {
        networkImpl.loadData(url, null, tag, dataPos, data, NetworkImpl.Request.Put);
    }

    protected void deleteNetwork(String url, final String tag) {
        networkImpl.loadData(url, null, tag, -1, null, NetworkImpl.Request.Delete);
    }

    protected void deleteNetwork(String url, final String tag, Object id) {
        networkImpl.loadData(url, null, tag, -1, id, NetworkImpl.Request.Delete);
    }

    protected void deleteNetwork(String url, final String tag, int dataPos, Object id) {
        networkImpl.loadData(url, null, tag, dataPos, id, NetworkImpl.Request.Delete);
    }

    protected void showDialog(String title, String msg, DialogInterface.OnClickListener clickOk) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定", clickOk)
                .setNegativeButton("取消", null)
                .show();
        dialogTitleLineColor(dialog);
    }

    protected void showButtomToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showButtomToast(int messageId) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
    }

    protected void showMiddleToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    protected void iconfromNetwork(ImageView view, String url) {
        imageLoadTool.loadImage(view, Global.makeSmallUrl(view, url));
    }

    protected void iconfromNetwork(ImageView view, String url, SimpleImageLoadingListener animate) {
        imageLoadTool.loadImage(view, Global.makeSmallUrl(view, url), animate);
    }

    protected void imagefromNetwork(ImageView view, String url) {
        imageLoadTool.loadImageFromUrl(view, url);
    }

    protected void imagefromNetwork(ImageView view, String url, DisplayImageOptions options) {
        imageLoadTool.loadImageFromUrl(view, url, options);
    }

    public final void dialogTitleLineColor(Dialog dialog) {
        CustomDialog.dialogTitleLineColor(this, dialog);
    }

    /**
     * 载入动画
     */
    private DialogUtil.LoadingPopupWindow mDialogProgressPopWindow = null;

    public void initDialogLoading() {
        if (mDialogProgressPopWindow == null) {
            PopupWindow.OnDismissListener onDismissListener = new PopupWindow.OnDismissListener() {
                public void onDismiss() {
                    hideProgressDialog();
                }
            };

            mDialogProgressPopWindow = DialogUtil.initProgressDialog(this, onDismissListener);
        }
    }

    public void showDialogLoading(String title) {
        initDialogLoading();
        DialogUtil.showProgressDialog(this, mDialogProgressPopWindow, title);
    }

    public void showDialogLoading() {
        showDialogLoading("");
    }

    public void hideProgressDialog() {
        if (mDialogProgressPopWindow != null) {
            DialogUtil.hideDialog(mDialogProgressPopWindow);
        }
    }
}
