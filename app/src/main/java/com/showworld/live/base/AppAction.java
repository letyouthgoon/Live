/*
 * Copyright @2016  www.chengmi.com. All rights reserved.
 */

package com.showworld.live.base;

import com.showworld.live.main.module.GetMemberInfoRet;

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
     * 获取发现地区列表
     *
     */
    void login(String trim, ActionCallbackListener<GetMemberInfoRet> actionCallbackListener);
}
