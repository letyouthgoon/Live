package com.showworld.live;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by alex on 16/6/8.
 */
public class RequestManager {
    private static RequestManager mInstance;
    private RequestQueue mQueue;

    private RequestManager(Context context) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(context);
        }
    }


    public synchronized static RequestManager getInstance() {
        if (mInstance == null) {
            mInstance = new RequestManager(SWLApplication.getContext());
        }

        return mInstance;
    }

    public void addRequest(Object tag, Request<?> request) {
        if (tag != null) {
            request.setTag(tag);
        }
        mQueue.add(request);
    }

    public void cancelAll(Object tag) {
        mQueue.cancelAll(tag);
    }


}
