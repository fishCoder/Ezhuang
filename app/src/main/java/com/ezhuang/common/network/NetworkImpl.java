package com.ezhuang.common.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ezhuang.LoginActivity_;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.ezhuang.common.Global;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class NetworkImpl {
    public static final int NETWORK_ERROR = -1;
    public static final int SERVICE_SYS_ERROR = 40001;
    public static final int SESSION_TIME_OUT = 20001;
    public static final int REQ_SUCCESSS = 10001;

    private final NetworkCallback callback;

    public HashMap<String, PageInfo> mPages = new HashMap<String, PageInfo>();
    private HashMap<String, Boolean> mUpdateing = new HashMap<String, Boolean>();

    Context appContext;

    public enum Request {
        Get, Post, Put, Delete
    }

    public NetworkImpl(Context ctx, NetworkCallback networkCallback) {
        this.appContext = ctx;
        this.callback = networkCallback;
    }

    // 是否需要刷新所有数据
    public boolean isLoadingFirstPage(String tag) {
        PageInfo info = mPages.get(tag);
        return info == null || info.isNewRequest;
    }

    class FunctionParams{
        String url;
        RequestParams params;
        String tag;
        int dataPos;
        Object data;
        Request type;
    }

    Queue<FunctionParams> queueRequest = new LinkedList<>();

    public void loadData(final String url, final RequestParams params, final String tag, final int dataPos, final Object data, final Request type) {
        Log.d("", "url " + type + " " + url);

        if (mUpdateing.containsKey(tag) && mUpdateing.get(tag).booleanValue()) {
            Log.d("", "url#" + (params == null ? "get " : "post ") + url);
            return;
        }

        mUpdateing.put(tag, true);

        AsyncHttpClient client = MyAsyncHttpClient.createClient(appContext);

        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    int code = response.getInt("code");


                    if (callback.getSessionUrl().equals(tag)){
                        if (code == 10003 && !(appContext instanceof LoginActivity_)) {
                            appContext.startActivity(new Intent(appContext, LoginActivity_.class));

                        }

                        if (code == REQ_SUCCESSS) {

                            while (true){
                                FunctionParams params = queueRequest.poll();
                                if(params == null){
                                    break;
                                }else{
                                    loadData(params.url,params.params,params.tag,params.dataPos,params.data,params.type);
                                }
                            }

                        }

                        return;
                    }
                    if (code == SERVICE_SYS_ERROR){
                        callback.showError("服务器内部错误");
                    }

                    if (code == SESSION_TIME_OUT){
                        Log.e("Response","session 失效");
                        callback.refreshSession();
                        FunctionParams tmpParams = new FunctionParams();
                        tmpParams.url = url;
                        tmpParams.params = params;
                        tmpParams.tag = tag;
                        tmpParams.dataPos = dataPos;
                        tmpParams.data = data;
                        tmpParams.type = type;
                        queueRequest.add(tmpParams);

                    }else{
                        callback.parseJson(code, response, tag, dataPos, data);
                    }

                    try {
                        updatePage(response, tag);
                    } catch (Exception e) {
                    }


                    try {
                        updateRequest(response, tag);
                    } catch (Exception e) {

                    }

                } catch (Exception e) {
                    Global.errorLog(e);
                }
                mUpdateing.put(tag, false);
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    callback.showError("网络出错了，请检查网络连接");
                    callback.parseJson(NETWORK_ERROR, errorResponse, tag, dataPos, data);

                    if (isPageRequest(tag)) {
//                        callback.setPageBottom(NetworkCallback.PageStyle.LoadingFail);
                    }

                } catch (Exception e) {
                    Global.errorLog(e);
                }
                mUpdateing.put(tag, false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                try {
                    callback.showError("网络出错了，请检查网络连接");
                    callback.parseJson(NETWORK_ERROR, new JSONObject(""), tag, dataPos, data);
                    if (isPageRequest(tag)) {
//                        callback.setPageBottom(NetworkCallback.PageStyle.LoadingFail);
                    }

                } catch (Exception e) {
                    Global.errorLog(e);
                }
                mUpdateing.put(tag, false);
            }


            @Override
            public void onFinish() {
            }
        };

        switch (type) {
            case Get:
                client.get(url, jsonHttpResponseHandler);
                break;

            case Post:
                client.post(url, params, jsonHttpResponseHandler);
                break;

            case Put:
                client.put(url, params, jsonHttpResponseHandler);
                break;

            case Delete:
                client.delete(url, jsonHttpResponseHandler);
                break;
        }
    }

    public void initSetting() {
        mPages = new HashMap<String, PageInfo>();
    }

    private boolean isPageRequest(String tag) {
        return mPages.containsKey(tag);
    }

    private void updatePage(JSONObject json, final String tag) throws JSONException {
        if (!isPageRequest(tag)) {
            return;
        }

        PageInfo pageInfo = mPages.get(tag);
        if (json.has("data")) {
            json = json.getJSONObject("data");
            if (json.has("totalPage")) {
                pageInfo.pageAll = json.getInt("totalPage");
                pageInfo.pageIndex = json.getInt("page");
            } else if (json.has("page")) {
                pageInfo.pageIndex = json.getInt("page");
                pageInfo.pageAll = json.getInt("pageSize");
            } else {
                pageInfo.pageIndex = 0;
                pageInfo.pageAll = 0;
            }
        } else {
            pageInfo.pageIndex = 0;
            pageInfo.pageAll = 0;
        }

//        if (pageInfo.isLoadingLastPage()) {
//            callback.setPageBottom(NetworkCallback.PageStyle.NoData);
//        } else {
//            callback.setPageBottom(NetworkCallback.PageStyle.Loading);
//        }
    }


    private void updateRequest(JSONObject json, final String tag) throws JSONException {
        if (!isPageRequest(tag)) {
            return;
        }

        PageInfo pageInfo = mPages.get(tag);
        pageInfo.isNewRequest = false;
    }

    public boolean isLoadingLastPage(String tag) {
        PageInfo pageInfo = mPages.get(tag);
        return pageInfo != null && pageInfo.isLoadingLastPage();
    }

    public void getNextPageNetwork(String url, final String tag) {
        PageInfo pageInfo = mPages.get(tag);
        if (pageInfo == null) {
            pageInfo = new PageInfo();
            mPages.put(tag, pageInfo);
        }

        if (pageInfo.isLoadingLastPage()) {
//            callback.setPageBottom(NetworkCallback.PageStyle.NoData);
            return;
        }

        String pageUrl = url + "&page=" + (pageInfo.pageIndex + 1);
        callback.getNetwork(pageUrl, tag);
    }

}