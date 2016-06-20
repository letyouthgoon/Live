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
public class GsonRequest<T> extends JsonRequest<T> {
    /**
     * Can be final & static ?
     */
    private final Gson mGson = new Gson();
    private final Class<T> mClazz;
    private final Listener<T> mListener;
    private final Map<String, String> mHeaders;

    /**
     * @param url
     * @param params        实体转化json
     * @param clazz
     * @param listener
     * @param errorListener
     */
    public GsonRequest(String url, JSONObject params, Class<T> clazz,
                       Listener<T> listener, ErrorListener errorListener) {
        this(url, params, clazz, null, listener, errorListener);
    }

    public GsonRequest(String url, JSONObject params, Class<T> clazz,
                       Map<String, String> headers, Listener<T> listener,
                       ErrorListener errorListener) {
        super(Method.POST, url, params.toString(), listener, errorListener);
        this.mClazz = clazz;
        this.mHeaders = headers;
        this.mListener = listener;

        setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, 1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * @param url
     * @param params        hashmap
     * @param clazz
     * @param listener
     * @param errorListener
     */
    public GsonRequest(String url, String params, Class<T> clazz,
                       Listener<T> listener, ErrorListener errorListener) {
        this(url, params, clazz, null, listener, errorListener);
    }

    public GsonRequest(String url, String params, Class<T> clazz,
                       Map<String, String> headers, Listener<T> listener,
                       ErrorListener errorListener) {
        super(Method.POST, url, params.toString(), listener, errorListener);
        this.mClazz = clazz;
        this.mHeaders = headers;
        this.mListener = listener;

        setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS, 1,
                // DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data,"UTF-8");
//                    HttpHeaderParser.parseCharset(response.headers));
            Log.i("--->", "data:\n" + json);

            return Response.success(mGson.fromJson(json, mClazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }
}
