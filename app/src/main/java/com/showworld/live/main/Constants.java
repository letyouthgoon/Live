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

    //action
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
    public static final String ACTION_SURFACE_CREATED = PACKAGE
            + ".ACTION_SURFACE_CREATED";
    public static final String ACTION_MEMBER_CHANGE = PACKAGE
            + ".ACTION_MEMBER_CHANGE";
    public static final String ACTION_SHOW_VIDEO_MEMBER_INFO = PACKAGE
            + ".ACTION_SHOW_VIDEO_MEMBER_INFO";
    public static final String ACTION_VIDEO_SHOW = PACKAGE
            + ".ACTION_VIDEO_SHOW";
    public static final String ACTION_MEMBER_VIDEO_SHOW = PACKAGE
            + ".ACTION_MEMBER_VIDEO_SHOW";
    public static final String ACTION_REQUEST_MEMBER_VIEW = PACKAGE + ".ACTION_REQUEST_MEMBER_VIEW";

    public static final String ACTION_VIDEO_CLOSE = PACKAGE
            + ".ACTION_VIDEO_CLOSE";
    public static final String ACTION_ENABLE_CAMERA_COMPLETE = PACKAGE
            + ".ACTION_ENABLE_CAMERA_COMPLETE";
    public static final String ACTION_SWITCH_CAMERA_COMPLETE = PACKAGE
            + ".ACTION_SWITCH_CAMERA_COMPLETE";
    public static final String ACTION_OUTPUT_MODE_CHANGE = PACKAGE
            + ".ACTION_OUTPUT_MODE_CHANGE";
    public static final String ACTION_ENABLE_EXTERNAL_CAPTURE_COMPLETE = PACKAGE
            + ".ACTION_ENABLE_EXTERNAL_CAPTURE_COMPLETE";

    public static final String ACTION_CREATE_GROUP_ID_COMPLETE = PACKAGE
            + ".ACTION_CREATE_GROUP_ID_COMPLETE";

    public static final String ACTION_CREATE_ROOM_NUM_COMPLETE = PACKAGE
            + ".ACTION_CREATE_ROOM_NUM_COMPLETE";

    public static final String ACTION_INSERT_ROOM_TO_SERVER_COMPLETE = PACKAGE + ".ACTION_INSERT_ROOM_TO_SERVER_COMPLETE";
    public static final String ACTION_INVITE_MEMBER_VIDEOCHAT = PACKAGE + ".ACTION_INVITE_MEMBER_VIDEOCHAT";
    public static final String ACTION_CLOSE_MEMBER_VIDEOCHAT = PACKAGE + ".ACTION_CLOSE_MEMBER_VIDEOCHAT";


    //extra
    public static final String EXTRA_USER_PHONE = "userphone";
    public static final String EXTRA_PASSWORD = "password";
    public static final String EXTRA_USER_NAME = "username";
    public static final String EXTRA_IDENTIFIER = "identifier";
    public static final String EXTRA_IS_ENABLE = "isEnable";
    public static final String EXTRA_IS_FRONT = "isFront";
}
