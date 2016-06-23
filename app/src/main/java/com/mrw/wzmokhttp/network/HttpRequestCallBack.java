package com.mrw.wzmokhttp.network;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/6/6.
 */
public interface HttpRequestCallBack {

    int NETWORK_ERROR = 0;//网络异常
    int SERVER_ERROR = 1;//服务器异常
    int PARSE_ERROR = 2;//解析错误

    /**
     * 请求成功
     * @param jsonObject 返回信息
     */
    void onSucceed(JSONObject jsonObject);

    /**
     * 请求出错
     * @param errorType 异常类型
     * @param errorCode 异常码
     * @param errorMsg 异常信息
     */
    void onError(int errorType,int errorCode,String errorMsg);

}
