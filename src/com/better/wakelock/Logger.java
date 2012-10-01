package com.better.wakelock;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class Logger {

    private static final String TAG = Logger.class.getSimpleName();

    public enum LogLevel {
        NONE, DEBUG, INFO
    }

    private final Map<String, LogLevel> mLogLevels;

    private static Logger sInstance;

    public static Logger getDefaultLogger() {
        if (sInstance == null) {
            sInstance = new Logger();
        }
        return sInstance;
    }

    public static Logger init() {
        if (sInstance != null) {
            sInstance = new Logger();
        }
        return sInstance;
    }

    private Logger() {
        mLogLevels = new HashMap<String, Logger.LogLevel>();
    }

    public void setLogLevel(Class<?> logClass, LogLevel logLevel) {
        String simpleName = logClass.getSimpleName();
        mLogLevels.put(simpleName, logLevel);
        Log.d(TAG, "Adding " + simpleName + " with LogLevel " + logLevel.toString());
    }

    public LogLevel getLevel(Class<?> logClass) {
        return mLogLevels.get(logClass.getSimpleName());
    }

    /**
     * Log with default level
     * 
     * @param e
     */
    public void d(String message) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String fileName = caller.getFileName();
        String logClass = fileName.substring(0, fileName.length() - 5);

        LogLevel logLevel = mLogLevels.get(logClass);
        if (logLevel == null) {
            logLevel = LogLevel.DEBUG;
            mLogLevels.put(logClass, logLevel);
            Log.w(TAG, "no LogLevel was found for " + logClass);
            Log.d(TAG, "Adding " + logClass + " with LogLevel " + logLevel.toString());
        }

        switch (logLevel) {
        case NONE:
            // do nothing
            break;

        case DEBUG:
            Log.d(formatTag(), message);
            break;

        case INFO:
            Log.i(formatTag(), message);
            break;

        default:
            throw new RuntimeException("all loglevels should be in the switch!");
        }
    }

    /**
     * Log errors
     */
    public void w(String message) {
        String tag = formatTag();
        Log.w(tag, message);
    }

    /**
     * Log errors
     */
    public void e(String message) {
        String tag = formatTag();
        Log.e(tag, message);
    }

    public void e(String message, Throwable e) {
        String string = formatTag();
        Log.e(string, message, e);
    }

    private String formatTag() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String fileName = caller.getFileName();
        String logClass = fileName.substring(0, fileName.length() - 5);
        String methodName = caller.getMethodName();
        String tag = "[" + logClass + "." + methodName + "]";
        return tag;
    }
}
