/*
 * Copyright @2016  www.chengmi.com. All rights reserved.
 */

package com.showworld.live.main.module;

import com.google.gson.annotations.SerializedName;

public class GetMemberInfoRet extends BasePojo {

    @SerializedName("data")
    public Data data;

    public static class Data {

        @SerializedName("username")
        public String username;

        @SerializedName("userphone")
        public String userphone;

        @SerializedName("sex")
        public String sex;

        @SerializedName("constellation")
        public String constellation;

        @SerializedName("headimagepath")
        public String headimagepath;

        @SerializedName("address")
        public String address;

        @SerializedName("signature")
        public String signature;

        @SerializedName("praisenum")
        public String praisenum;
    }
}