/*
 * Copyright @2016  www.chengmi.com. All rights reserved.
 */
package com.showworld.live.base;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.showworld.live.RequestManager;
import com.showworld.live.main.Constants;
import com.showworld.live.main.HttpUtil;
import com.showworld.live.main.module.BasePojo;
import com.showworld.live.main.module.GetMemberInfoRet;
import com.showworld.live.main.module.Params;
import com.showworld.live.main.module.getLiveListRet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


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


    public void cancelAll(Object tag) {
        RequestManager.getInstance().cancelAll(tag);
    }


    @Override
    public void login(String userphone, final ActionCallbackListener<GetMemberInfoRet> actionCallbackListener) {
        Params.GetMemberInfoParam param = new Params.GetMemberInfoParam();
        param.userphone = userphone;

        query(new GsonRequest<GetMemberInfoRet>(HttpUtil.UserInfoUrl, getParamsJson(new Gson().toJson(param)),
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
        Map<String, String> params = new HashMap<String, String>();
        params.put("userphone", userphone);

        query(new GsonRequest<GetMemberInfoRet>(HttpUtil.UserInfoUrl, getParamsJson1(params),
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

    @Override
    public void getLiveVideoList(final ActionCallbackListener<getLiveListRet> actionCallbackListener) {
        query(new GsonRequest<getLiveListRet>(HttpUtil.getLiveListUrl, "", getLiveListRet.class, new Response.Listener<getLiveListRet>() {
            @Override
            public void onResponse(getLiveListRet bean) {
                actionCallbackListener.onSuccess(bean);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                actionCallbackListener.onFailure("", "");
            }
        }
        ));
    }


    private String getParamsJson(String s) {
        Params.BaseParam bp = new Params.BaseParam();
        bp.data = s;
        String ret = new Gson().toJson(bp);
        return ret;
    }

    private JSONObject getParamsJson1(Map<String, String> map) {

        JSONObject jsonObject = new JSONObject(map);
        Map<String, String> real_params = new HashMap<String, String>();
//        real_params.put("data", jsonObject.toString());
        real_params.put("data", "{'userphone':'18511707565'}");
        return new JSONObject(real_params);
    }

    @Override
    public void enterRoom(int mRoomNum, String userPhone, final ActionCallbackListener<BasePojo> actionCallbackListener) {

        Map<String, String> map = new HashMap<String, String>();
        map.put(Constants.EXTRA_ROOM_NUM, mRoomNum + "");
        map.put(Constants.EXTRA_USER_PHONE, userPhone);

        query(new TestRequest<BasePojo>(HttpUtil.enterRoomUrl, map,
                BasePojo.class, new Response.Listener<BasePojo>() {
            @Override
            public void onResponse(BasePojo bean) {
                actionCallbackListener.onSuccess(bean);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                actionCallbackListener.onFailure("", "");
            }
        }
        ));

        Params.enterRoomParam param = new Params.enterRoomParam();
        JSONObject object = new JSONObject();
        try {
            object.put(Constants.EXTRA_ROOM_NUM, mRoomNum);
            object.put(Constants.EXTRA_USER_PHONE, userPhone);
            param.viewerdata = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        
//        query(new GsonRequest<BasePojo>(HttpUtil.enterRoomUrl, new Gson().toJson(param),
//                BasePojo.class, new Response.Listener<BasePojo>() {
//            @Override
//            public void onResponse(BasePojo bean) {
//                actionCallbackListener.onSuccess(bean);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                actionCallbackListener.onFailure("", "");
//            }
//        }
//        ));
    }

    @Override
    public void leaveLive(int roomNum, String phoneNum, final ActionCallbackListener<BasePojo> actionCallbackListener) {
        Params.closeLive param = new Params.closeLive();
        JSONObject object = new JSONObject();
        try {
            object.put(Constants.EXTRA_ROOM_NUM, roomNum);
            object.put(Constants.EXTRA_USER_PHONE, phoneNum);
            param.closedata = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        query(new GsonRequest<BasePojo>(HttpUtil.liveLeaveUrl, new Gson().toJson(param), BasePojo.class, new Response.Listener<BasePojo>() {
            @Override
            public void onResponse(BasePojo bean) {
                actionCallbackListener.onSuccess(bean);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                actionCallbackListener.onFailure("", "");
            }
        }));

    }

    @Override
    public void closeLive(int roomNum, final ActionCallbackListener<BasePojo> actionCallbackListener) {
        Params.closeLive param = new Params.closeLive();
        JSONObject object = new JSONObject();
        try {
            object.put(Constants.EXTRA_ROOM_NUM, roomNum);
            param.closedata = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        query(new GsonRequest<BasePojo>(HttpUtil.liveCloseUrl, new Gson().toJson(param), BasePojo.class, new Response.Listener<BasePojo>() {
            @Override
            public void onResponse(BasePojo bean) {
                actionCallbackListener.onSuccess(bean);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                actionCallbackListener.onFailure("", "");
            }
        }));

    }

}
