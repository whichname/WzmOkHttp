package com.mrw.wzmokhttp.network;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/6/6.
 */
public class HttpUtils {

    private final String TAG = "OkHttp";
    private static HttpUtils mHttpUtils;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;
    private ArrayMap<String,List<Cookie>> cookieStore;

    private HttpUtils() {
        cookieStore = new ArrayMap<>();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(),cookies);
                if (cookies == null) return;
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < cookies.size(); i++) {
                    stringBuilder.append(cookies.get(i).toString());
                    stringBuilder.append(";");
                }
                android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
                cookieManager.setCookie(url.toString(),stringBuilder.toString());
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                if (cookies == null)
                    cookies = new ArrayList<>();
                return cookies;
            }
        });
        mOkHttpClient = builder.build();
        mHandler = new Handler(Looper.getMainLooper());
    }

    private synchronized static HttpUtils getInstance() {
        if (mHttpUtils == null) {
            mHttpUtils = new HttpUtils();
        }
        return mHttpUtils;
    }

    /*****************************************************************GET*************************************************************************/

    /**同步get*/
    private Response __getSyn(Object tag,String url) throws IOException {
        Request request = new Request.Builder().url(url).tag(tag).build();
        return mOkHttpClient.newCall(request).execute();
    }

    /**同步get*/
    private String __getSynAsString(Object tag,String url) throws IOException {
        return __getSyn(tag,url).body().string();
    }

    /**同步带参数get*/
    private Response __getSyn(Object tag,String url, ArrayMap params) throws IOException {
        return __getSyn(tag,__concatGetUrl(url, params));
    }

    /**同步带参数get*/
    private String __getSynAsString(Object tag,String url, ArrayMap params) throws IOException {
        return __getSynAsString(tag,__concatGetUrl(url, params));
    }

    /**异步get请求*/
    private void __getAsyn(Object tag,String url, HttpRequestCallBack httpRequestCallBack) {
        Request request = new Request.Builder().url(url).tag(tag).build();
        __deliveryResponse(httpRequestCallBack, request);
    }

    /**异步带参数get请求*/
    private void __getAsyn(Object tag,String url, ArrayMap params, HttpRequestCallBack httpRequestCallBack) {
        Request request = new Request.Builder().url(__concatGetUrl(url, params)).tag(tag).build();
        __deliveryResponse(httpRequestCallBack, request);
    }

    /**拼接get请求url*/
    private String __concatGetUrl(String url, ArrayMap params) {
        if (params == null) {
            return url;
        }
        StringBuilder stringBuilder = new StringBuilder(url);
        stringBuilder.append("?");
        for (int i = 0; i < params.size(); i++) {
            try {
                stringBuilder.append(params.keyAt(i)).append("=").append(URLEncoder.encode(params.valueAt(i).toString(), "utf-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.substring(0, stringBuilder.length());
    }



    /*****************************************************************POST*************************************************************************/

    /**同步的post请求(JSON形式)*/
    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    private Response __postSynJSON(Object tag,String url, ArrayMap params) throws IOException {
        if (params == null) {
            params = new ArrayMap<>();
        }
        RequestBody requestBody = RequestBody.create(JSON,new JSONObject(params).toString());
        Request request = new Request.Builder().url(url).tag(tag).post(requestBody).build();
        return mOkHttpClient.newCall(request).execute();
    }

    /**同步的post请求(JSON形式)*/
    private String __postSynJSONAsString(Object tag,String url, ArrayMap params) throws IOException {
        return __postSynJSON(tag,url, params).body().string();
    }

    /**异步的post请求(JSON形式)*/
    private void __postAsynJSON(Object tag,String url, ArrayMap params, HttpRequestCallBack httpRequestCallBack) {
        if (params == null) {
            params = new ArrayMap<>();
        }
        RequestBody requestBody = RequestBody.create(JSON,new JSONObject(params).toString());
        Request request = new Request.Builder().url(url).tag(tag).post(requestBody).build();
        __deliveryResponse(httpRequestCallBack, request);
    }

    /**同步的post请求(form-data形式)*/
    private Response __postSynForm(Object tag,String url, ArrayMap params) throws IOException {
        if (params == null) {
            params = new ArrayMap<>();
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (int i = 0; i < params.size(); i++) {
            builder.add(params.keyAt(i).toString(),params.valueAt(i).toString());
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder().url(url).tag(tag).post(requestBody).build();
        return mOkHttpClient.newCall(request).execute();
    }

    /**同步的post请求(form-data形式)*/
    private String __postSynFormAsString(Object tag,String url, ArrayMap params) throws IOException {
        return __postSynForm(tag,url, params).body().string();
    }

    /**异步的post请求(form-data形式)*/
    private void __postAsynForm(Object tag,String url, ArrayMap params, HttpRequestCallBack httpRequestCallBack) {
        if (params == null) {
            params = new ArrayMap<>();
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (int i = 0; i < params.size(); i++) {
            builder.add(params.keyAt(i).toString(),params.valueAt(i).toString());
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder().url(url).post(requestBody).tag(tag).build();
        __deliveryResponse(httpRequestCallBack, request);
    }


    /*****************************************************************UPLOAD*************************************************************************/
    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    public static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    public static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");

    /**同步的上传文件*/
    private Response __uploadSyn(String url, File file) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("upload", file.getName(), __getFileRequestBody(file)).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return mOkHttpClient.newCall(request).execute();
    }

    /**同步的上传文件*/
    private String __uploadSynAsString(String url, File file) throws IOException {
        return __uploadSyn(url, file).body().string();
    }

    /**异步的上传文件*/
    private void __uploadAsyn(String url, File file, HttpRequestCallBack httpRequestCallBack) {
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("upload", file.getName(), __getFileRequestBody(file)).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        __deliveryResponse(httpRequestCallBack, request);
    }

    /**获得文件Content-Type*/
    private RequestBody __getFileRequestBody(File file) {
        if (file.getName().endsWith(".png")) {
            return RequestBody.create(MEDIA_TYPE_PNG, file);
        }
        if (file.getName().endsWith(".jpg")) {
            return RequestBody.create(MEDIA_TYPE_JPG, file);
        }
        if (file.getName().endsWith(".jpeg")) {
            return RequestBody.create(MEDIA_TYPE_JPEG, file);
        }
        return RequestBody.create(MEDIA_TYPE_PNG, file);
    }


    /**处理异步结果回调到主线程*/
    private void __deliveryResponse(final HttpRequestCallBack httpRequestCallBack, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                __sendFailureCallBack(e, httpRequestCallBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (!jsonObject.getBoolean("error"))
                        __sendSuccessCallBack(httpRequestCallBack, jsonObject);
                    else
                        __sendFailureCallBack(jsonObject.getString("msg"), jsonObject.getInt("errorCode"), httpRequestCallBack);
                } catch (JSONException e) {
                    e.printStackTrace();
                    __sendFailureCallBack(e, httpRequestCallBack);
                }
            }
        });
    }

    /**回调请求失败的函数*/
    private void __sendFailureCallBack(final Exception e, final HttpRequestCallBack httpRequestCallBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                httpRequestCallBack.onError(HttpRequestCallBack.PARSE_ERROR, 0, e.getLocalizedMessage());
            }
        });
    }

    /**回调请求失败的函数*/
    private void __sendFailureCallBack(final String msg, final int errorCode, final HttpRequestCallBack httpRequestCallBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                httpRequestCallBack.onError(HttpRequestCallBack.SERVER_ERROR, errorCode, msg);
            }
        });
    }

    /**回调请求成功的函数*/
    private void __sendSuccessCallBack(final HttpRequestCallBack httpRequestCallBack, final JSONObject jsonObject) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                httpRequestCallBack.onSucceed(jsonObject);
            }
        });
    }

    /**取消请求*/
    private void __cancelCallsWithTag(Object tag) {
        if (tag == null) return;

        synchronized (mOkHttpClient.dispatcher().getClass()) {
            for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) call.cancel();
            }
            for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) call.cancel();
            }
        }

    }


    /*****************************************************************暴露给外部的方法*************************************************************************/
    public static Response getSyn(Object tag,String url) throws IOException {
        return getInstance().__getSyn(tag,url);
    }

    public static String getSynAsString(Object tag,String url) throws IOException {
        return getInstance().__getSynAsString(tag,url);
    }

    public static Response getSyn(Object tag,String url,ArrayMap params) throws IOException {
        return getInstance().__getSyn(tag,url, params);
    }

    public static String getSynAsString(Object tag,String url,ArrayMap params) throws IOException {
        return getInstance().__getSynAsString(tag,url, params);
    }

    public static void getAsyn(Object tag,String url,HttpRequestCallBack httpRequestCallBack) {
        getInstance().__getAsyn(tag,url,httpRequestCallBack);
    }

    public static void getAsyn(Object tag,String url,ArrayMap params,HttpRequestCallBack httpRequestCallBack) {
        getInstance().__getAsyn(tag,url,params,httpRequestCallBack);
    }

    public static Response postSynJSON(Object tag,String url,ArrayMap params) throws IOException {
        return getInstance().__postSynJSON(tag,url,params);
    }

    public static String postSynJSONAsString(Object tag,String url,ArrayMap params) throws IOException {
        return getInstance().__postSynJSONAsString(tag,url, params);
    }

    public static void postAsynJSON(Object tag,String url,ArrayMap params,HttpRequestCallBack httpRequestCallBack) {
        getInstance().__postAsynJSON(tag,url,params,httpRequestCallBack);
    }

    public static Response postSynForm(Object tag,String url,ArrayMap params) throws IOException {
        return getInstance().__postSynForm(tag,url,params);
    }

    public static String postSynFormAsString(Object tag,String url,ArrayMap params) throws IOException {
        return getInstance().__postSynFormAsString(tag,url, params);
    }

    public static void postAsynForm(Object tag,String url,ArrayMap params,HttpRequestCallBack httpRequestCallBack) {
        getInstance().__postAsynForm(tag,url,params,httpRequestCallBack);
    }

    public static Response uploadSyn(String url,File file) throws IOException {
        return getInstance().__uploadSyn(url,file);
    }

    public static String uploadSynAsString(String url,File file) throws IOException {
        return getInstance().__uploadSynAsString(url, file);
    }

    public static void uploadAsyn(String url,File file,HttpRequestCallBack httpRequestCallBack) {
        getInstance().__uploadAsyn(url,file,httpRequestCallBack);
    }

    public static void cancelRequest(Object tag) {
        getInstance().__cancelCallsWithTag(tag);
    }

}
