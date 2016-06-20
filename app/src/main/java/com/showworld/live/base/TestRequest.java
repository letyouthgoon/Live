/*
 * Copyright @2016  www.chengmi.com. All rights reserved.
 */

package com.showworld.live.base;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.showworld.live.main.Constants;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * vollery gson post
 *
 * @author alex
 */
public class TestRequest<T> extends StringRequest {
    /**
     * Can be final & static ?
     */
    private final Gson mGson = new Gson();
    private final Class<T> mClazz;
    private final Listener<T> mListener;
    private final Map<String, String> mHeaders;
    private final Map<String, String> mParams;

    /**
     * @param url
     * @param params        实体转化json
     * @param clazz
     * @param listener
     * @param errorListener
     */
    public TestRequest(String url, Map<String, String> params, Class<T> clazz,
                       Listener listener, ErrorListener errorListener) {
        this(url, params, clazz, null, listener, errorListener);
    }

    public TestRequest(String url, Map<String, String> params, Class<T> clazz,
                       Map<String, String> headers, Listener listener,
                       ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        this.mClazz = clazz;
        this.mHeaders = headers;
        this.mListener = listener;
        this.mParams = params;

        setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(mGson.fromJson(response, mClazz));
    }
}
