package com.mrw.wzmokhttp;

import android.support.v4.util.ArrayMap;
import android.widget.Toast;

import com.mrw.wzmokhttp.network.BaseRequest;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/6/7.
 */
public class LoginRequest extends BaseRequest {


    @Override
    protected ArrayMap<String, Object> getParams() {
        ArrayMap<String,Object> params = new ArrayMap<>();
        params.put("phone","15625056202");
        params.put("password","e99a18c428cb38d5f260853678922e03");
        return params;
    }

    @Override
    protected String getUrl() {
        return "http://nrhy.amow.cn"+"/app/user/signIn";
    }

}
