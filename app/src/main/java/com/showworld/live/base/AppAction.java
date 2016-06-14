/*
 * Copyright @2016  www.chengmi.com. All rights reserved.
 */

package com.showworld.live.base;

import com.showworld.live.main.module.BasePojo;
import com.showworld.live.main.module.GetMemberInfoRet;
import com.showworld.live.main.module.LiveInfo;

/**
 * 接收app层的各种Action
 *
 * @author Alex
 * @version 1.0
 * @date 15/6/25
 */
public interface AppAction {

    void cancelAll(Object tag);

    /**
     * 登陆
     */
    void login(String trim, ActionCallbackListener<GetMemberInfoRet> actionCallbackListener);

    /**
     * 获取直播列表
     *
     * @param actionCallbackListener
     */
    void getLiveVideoList(final ActionCallbackListener<LiveInfo> actionCallbackListener);
}
