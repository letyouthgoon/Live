package com.showworld.live.main;

/**
 * Created by alex on 16/6/1.
 */
public class HttpUtil {
    private static String TAG = "HttpUtil";
    public static final String SERVER_URL = "http://203.195.167.34/";
    public static final String enterRoomUrl = SERVER_URL + "enter_room.php";
    public static final String rootUrl = SERVER_URL + "image_get.php";
    public static final String UserInfoUrl = SERVER_URL + "user_getinfo.php";
    public static final String getLiveListUrl = SERVER_URL + "live_listget.php";

    public static final int SUCCESS = 200;
    public static final int FAIL = 500;
}
