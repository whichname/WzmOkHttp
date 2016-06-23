package com.mrw.wzmokhttp.network;

import android.support.v4.util.ArrayMap;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/6/7.
 */
public abstract class BaseRequest  {

    protected String TAG;

    public BaseRequest() {
        TAG = getClass().getSimpleName();
    }

    protected abstract ArrayMap<String,Object> getParams();

    protected abstract String getUrl();

    /**发送post请求*/
    public void startPost(HttpRequestCallBack httpRequestCallBack) {
        HttpUtils.postAsynForm(TAG,getUrl(),getParams(),httpRequestCallBack);
    }

    /**发送get请求*/
    public void startGet(HttpRequestCallBack httpRequestCallBack) {
        HttpUtils.getAsyn(TAG,getUrl(),getParams(),httpRequestCallBack);
    }


}
