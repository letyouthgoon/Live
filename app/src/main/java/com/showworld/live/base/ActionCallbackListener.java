/*
 * Copyright @2016  www.chengmi.com. All rights reserved.
 */
package com.showworld.live.base;

/**
 * Action的处理结果回调监听器
 *
 * @author Alex
 * @version 1.0
 * @date 15/6/25
 */
public interface ActionCallbackListener<T> {
    /**
     * 成功时调用
     *
     * @param data 返回的数据
     */
    void onSuccess(T data);

    /**
     * 失败时调用
     *
     * @param errorEvent 错误码
     * @param message    错误信息
     */
    void onFailure(String errorEvent, String message);
}
