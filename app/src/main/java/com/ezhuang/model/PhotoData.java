package com.ezhuang.model;

import android.net.Uri;

import java.io.File;

/**
 * Created by Administrator on 2015/4/21 0021.
 */
public class PhotoData {
    public Uri uri = Uri.parse("");
    public String serviceUri = "";

    public PhotoData(String url){
        uri = Uri.parse(url);
    }

    public PhotoData(File file) {
        uri = Uri.fromFile(file);
    }

    public PhotoData(PhotoDataSerializable data) {
        uri = Uri.parse(data.uriString);
        serviceUri = data.serviceUri;
    }
}