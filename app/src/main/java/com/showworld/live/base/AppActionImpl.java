/*
 * Copyright @2016  www.chengmi.com. All rights reserved.
 */
package com.showworld.live.base;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.showworld.live.RequestManager;
import com.showworld.live.main.HttpUtil;
import com.showworld.live.main.module.GetMemberInfoRet;
import com.showworld.live.main.module.Params;


/**
 * AppAction接口的实现类
 *
 * @author Alex
 * @version 1.0
 * @date 15/6/25
 */
public class AppActionImpl implements AppAction {

    Object mTag = null;

    public AppActionImpl(Object tag) {
        mTag = tag;
    }

    public void query(Request<?> request) {
        RequestManager.getInstance().addRequest(mTag, request);
    }

    @Override
    public void cancelAll(Object tag) {
        RequestManager.getInstance().cancelAll(tag);
    }


    @Override
    public void login(String userphone, final ActionCallbackListener<GetMemberInfoRet> actionCallbackListener) {
        Params.GetMemberInfoParam param = new Params.GetMemberInfoParam();
        param.userphone = userphone;

        query(new GsonRequest<GetMemberInfoRet>(HttpUtil.UserInfoUrl, new Gson().toJson(param),
                GetMemberInfoRet.class,
                new Response.Listener<GetMemberInfoRet>() {

                    @Override
                    public void onResponse(GetMemberInfoRet bean) {
                        actionCallbackListener.onSuccess(bean);
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                actionCallbackListener.onFailure("", "");
            }
        }));
    }
}
