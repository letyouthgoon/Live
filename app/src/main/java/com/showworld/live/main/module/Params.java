/*
 * Copyright @2016  www.chengmi.com. All rights reserved.
 */

package com.showworld.live.main.module;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Params extends BasePojo {

    /**
     * 构建加密的参数
     *
     * @author alex
     */
    public static class BaseParam {
        @SerializedName("data")
        public String data;
    }

    public static class GetMemberInfoParam {

        @SerializedName("userphone")
        public String userphone;
    }

}
