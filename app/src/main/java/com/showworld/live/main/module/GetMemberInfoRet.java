/*
 * Copyright @2016  www.chengmi.com. All rights reserved.
 */

package com.showworld.live.main.module;

import com.google.gson.annotations.SerializedName;

public class GetMemberInfoRet extends BasePojo {

    @SerializedName("code")
    public int code;

    public static class data {

        @SerializedName("username")
        public String username;

        @SerializedName("userphone")
        public String userphone;
    }
}