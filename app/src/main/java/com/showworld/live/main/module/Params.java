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
        @SerializedName("app_id")
        public String appId;
        @SerializedName("verify")
        public String verify;
        @SerializedName("params")
        public String params;
    }

    public static class Base4VersionParam implements Serializable {
        private static final long serialVersionUID = 1L;
        @SerializedName("version")
        public String version = "v30";
        // TODO: 16/1/20
    }

    public static class GetMemberInfoParam {
        @SerializedName("userphone")
        public String userphone;
    }

}
