package com.ezhuang.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/21 0021.
 */
// 因为PhotoData包含Uri，不能直接序列化，所以有了这个类
public class PhotoDataSerializable implements Serializable {
    public String uriString = "";
    public String serviceUri = "";

    public PhotoDataSerializable(PhotoData data) {
        uriString = data.uri.toString();
        serviceUri = data.serviceUri;
    }
}
