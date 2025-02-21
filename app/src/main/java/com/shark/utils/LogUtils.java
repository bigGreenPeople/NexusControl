package com.shark.utils;

import android.util.Log;

public class LogUtils {

    // 每行最大长度
    private static final int MAX_LOG_LENGTH = 4000;  // 你可以根据需求调整这个值

    // 打印日志的方法
    public static void logLongString( String longMessage) {
        String tag = "SharkMod";
        if (longMessage != null && longMessage.length() > MAX_LOG_LENGTH) {
            // 如果日志超过最大长度，分段打印
            int start = 0;
            int end = MAX_LOG_LENGTH;
            while (start < longMessage.length()) {
                // 打印当前部分日志
                Log.d(tag, longMessage.substring(start, Math.min(end, longMessage.length())));
                start = end;
                end = start + MAX_LOG_LENGTH;
            }
        } else {
            // 如果日志长度小于最大长度，直接打印
            Log.d(tag, longMessage);
        }
    }
}
