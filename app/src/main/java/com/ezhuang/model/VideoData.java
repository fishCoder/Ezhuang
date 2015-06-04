package com.ezhuang.model;

import android.net.Uri;

import java.io.File;

/**
 * Created by Administrator on 2015/4/21 0021.
 */
public class VideoData {
    public Uri uri = Uri.parse("");
    public String url;
    public String thumdUrl;

    public String serviceUri = "";

    public VideoData(){
    }

    public VideoData(String url){
        uri = Uri.parse(url);
    }

    public VideoData(File file) {
        uri = Uri.fromFile(file);
    }

    public VideoData(PhotoDataSerializable data) {
        uri = Uri.parse(data.uriString);
        serviceUri = data.serviceUri;
    }
}