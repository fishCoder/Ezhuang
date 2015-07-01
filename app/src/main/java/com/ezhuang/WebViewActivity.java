package com.ezhuang;

import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;

import com.ezhuang.model.Scheme;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Administrator on 2015/5/12 0012.
 */
@EActivity(R.layout.activity_webview)
public class WebViewActivity extends BaseActivity {

    @ViewById
    WebView webView;

    @Extra
    String url;

    @Extra
    Scheme scheme;

    @AfterViews
    void init(){
        Log.i("url",url);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if(scheme!=null){
            getMenuInflater().inflate(R.menu.menu_share, menu);
        }
        return true;
    }

    @OptionsItem(android.R.id.home)
    void home() {
        onBackPressed();
    }
    @OptionsItem(R.id.action_share)
    void action_share(){
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        oks.setTitle(getString(R.string.share));
        oks.setText(scheme.schemeName);
        oks.setUrl(url);
        oks.setImageUrl(scheme.cover);
        oks.show(this);
    }
}
