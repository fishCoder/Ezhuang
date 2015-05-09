package com.ezhuang.model;

/**
 * Created by Administrator on 2015/5/4 0004.
 */
public class Message {
    public String newsId;
    public String newsTitle;
    public String newsContent;
    public String sendTo;
    public String createTime;
    public String sender;
    public int    newsType;
    public int    state; // 1开始
    public String source;
    public String newsPjId;

    public String getTitle(){
        return  getValue(newsTitle);
    }

    public String getContent(){
        return getValue(newsContent);
    }

    public String getTime(){
        return getValue(createTime);
    }

    String getValue(String content){
        if(content==null || content.isEmpty()){
            return "";
        }else{
            return content;
        }
    }
}
