package com.ezhuang.common;

import com.alibaba.fastjson.JSON;


public class JsonUtil {

	public static String Object2Json(Object obj){
		return JSON.toJSONString(obj);
	}
	
	public static <T> T Json2Object(String json,Class<T> clazz){
		return JSON.parseObject(json, clazz);
	}
	
}
