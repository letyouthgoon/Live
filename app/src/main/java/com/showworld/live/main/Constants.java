package com.showworld.live.main;

public class Constants {

    public static final int DEMO_ERROR_BASE = -99999999;
    /**
     * 空指针
     */
    public static final int DEMO_ERROR_NULL_POINTER = DEMO_ERROR_BASE + 1;


    public static final String EXTRA_ROOM_NUM = "roomnum";

    public static final String EXTRA_SELF_IDENTIFIER = "selfIdentifier";

    public static final String EXTRA_GROUP_ID = "groupid";

    public static final String EXTRA_PRAISE_NUM = "praisenum";

    public static final int APPID = 1400001692;

    public static final String ACCOUNTTYPE = "884";

    public static final int AUDIO_VOICE_CHAT_MODE = 0;

    public static final String LOCAL_DATA = "local_data";

    public static final String LOCAL_PHONE = "phonenumber";

    //extra
    private static final String PACKAGE = "com.showworld.live";

    public static final String ACTION_START_CONTEXT_COMPLETE = PACKAGE
            + ".ACTION_START_CONTEXT_COMPLETE";
    public static final String EXTRA_AV_ERROR_RESULT = "av_error_result";
    public static final String ACTION_CLOSE_CONTEXT_COMPLETE = PACKAGE
            + ".ACTION_CLOSE_CONTEXT_COMPLETE";
    public static final String ACTION_ROOM_CREATE_COMPLETE = PACKAGE
            + ".ACTION_ROOM_CREATE_COMPLETE";
    public static final String ACTION_CLOSE_ROOM_COMPLETE = PACKAGE
            + ".ACTION_CLOSE_ROOM_COMPLETE";
    public static final String ACTION_MEMBER_CHANGE = PACKAGE
            + ".ACTION_MEMBER_CHANGE";
    public static final String ACTION_OUTPUT_MODE_CHANGE = PACKAGE
            + ".ACTION_OUTPUT_MODE_CHANGE";
    public static final String EXTRA_USER_PHONE = "userphone";
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_USER_NAME = "username";
}
