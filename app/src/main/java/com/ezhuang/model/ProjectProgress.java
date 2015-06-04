package com.ezhuang.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/23 0023.
 */
public class ProjectProgress implements Serializable {

    public static int SCORE = 5;

    public String  pgId;
    public String  quoName;
    public int     quoCheckResult;
    public String  owerName;
    public int     owerCheckResult;
    public String  time;
    public int     pgState;
    public String[]  imgUrls;
    public String  ownerId;
    public String  pgRemark;
    public String  nodeName;
    public String  pgDeal;
    public String  quoRemark;
    public String  owerRemark;
    public int owerScore;
    public String[] mediaUrls;

    public String pgVideoUrl; // 视频url(多张"&"分开)
    public String pgVideoPicUrl; // 视频截图url(多张"&"分开)


    public boolean isNeedQualityCheck(){
        return '1'==pgDeal.charAt(1);
    }

    public boolean isNeedOwnerCheck(){
        return '1'==pgDeal.charAt(0);
    }
}
