package com.mrw.wzmokhttp;

import android.support.v4.util.ArrayMap;

import com.mrw.wzmokhttp.network.BaseRequest;

/**
 * Created by Administrator on 2016/6/8.
 */
public class UserInfoRequest extends BaseRequest {

    private String id;

    public UserInfoRequest(String id) {
        super();
        this.id = id;
    }

    @Override
    protected ArrayMap<String, Object> getParams() {
        ArrayMap<String,Object> arrayMap = new ArrayMap();
        arrayMap.put("id",id);
        return arrayMap;
    }

    @Override
    protected String getUrl() {
        return "http://nrhy.amow.cn"+"/app/user/detail";
    }


}
