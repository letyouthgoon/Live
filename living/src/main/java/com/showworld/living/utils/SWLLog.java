package com.showworld.living.utils;

import android.util.Log;

import java.util.Calendar;

/**
 * 日志输出
 */
public class SwlLog {
    public enum SwlLogLevel {
        OFF,
        ERROR,
        WARN,
        DEBUG,
        INFO
    }

    static private SwlLogLevel level = SwlLogLevel.INFO;

    /**
     * enum 2 String[]
     * @return
     */
    static public String[] getStringValues() {
        SwlLogLevel[] levels = SwlLogLevel.values();
        String[] stringValuse = new String[levels.length];
        for (int i = 0; i < levels.length; i++) {
            stringValuse[i] = levels[i].toString();
        }
        return stringValuse;
    }

    static public void setLogLevel(SwlLogLevel newLevel) {
        level = newLevel;
        w("Log", "change log level: " + newLevel);
    }

    public static void v(String strTag, String strInfo) {
        Log.v(strTag, strInfo);
        if (level.ordinal() >= SwlLogLevel.INFO.ordinal()) {
            SwlLogImpl.writeLog("I", strTag, strInfo, null);
        }
    }

    public static void i(String strTag, String strInfo) {
        v(strTag, strInfo);
    }

    public static void d(String strTag, String strInfo) {
        Log.d(strTag, strInfo);
        if (level.ordinal() >= SwlLogLevel.DEBUG.ordinal()) {
            SwlLogImpl.writeLog("D", strTag, strInfo, null);
        }
    }


    public static void w(String strTag, String strInfo) {
        Log.w(strTag, strInfo);
        if (level.ordinal() >= SwlLogLevel.WARN.ordinal()) {
            SwlLogImpl.writeLog("W", strTag, strInfo, null);
        }
    }

    public static void e(String strTag, String strInfo) {
        Log.e(strTag, strInfo);
        if (level.ordinal() >= SwlLogLevel.ERROR.ordinal()) {
            SwlLogImpl.writeLog("E", strTag, strInfo, null);
        }
    }

    public static void writeException(String strTag, String strInfo, Exception tr) {
        SwlLogImpl.writeLog("C", strTag, strInfo, tr);
    }

    public static String getTime() {

        long currentTimeMillis = System.currentTimeMillis();

        Log.v("Test", String.valueOf(currentTimeMillis));


        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(currentTimeMillis);

        String time = calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + ":" + calendar.get(Calendar.MILLISECOND);
        return time;
    }
}
