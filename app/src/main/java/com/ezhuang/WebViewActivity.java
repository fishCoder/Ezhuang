package com.ezhuang;

import android.webkit.WebView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Administrator on 2015/5/12 0012.
 */
@EActivity(R.layout.activity_webview)
public class WebViewActivity extends BaseActivity {

    @ViewById
    WebView webView;

    @Extra
    String url;

    @AfterViews
    void init(){
        webView.loadUrl(url);
    }
}
