package com.showworld.live.main.module;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 16/6/14.
 */
public class getLiveListRet extends BasePojo {


    @SerializedName("data")
    public List<Data> data = new ArrayList<Data>();

    public static class Data {

        @SerializedName("userphone")
        public String userphone;
        @SerializedName("username")
        public String username;
        @SerializedName("headimagepath")
        public String headimagepath;
        @SerializedName("programid")
        public int programid;
        @SerializedName("subject")
        public String subject;
        @SerializedName("viewernum")
        public int viewernum;
        @SerializedName("praisenum")
        public int praisenum;
        @SerializedName("begin_time")
        public String begin_time;
        @SerializedName("totalnum")
        public String totalnum;
        @SerializedName("groupid")
        public String groupid;
        @SerializedName("coverimagepath")
        public String coverimagepath;
        @SerializedName("url")
        public String url;
        @SerializedName("addr")
        public String addr;

    }

}
