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
        String methodName = caller.getMethodName();

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
            Log.d("[" + logClass + "." + methodName + "]", message);
            break;

        case INFO:
            Log.i("[" + logClass + "." + methodName + "]", message);
            break;

        default:
            throw new RuntimeException("all loglevels should be in the switch!");
        }
    }

    /**
     * Log errors
     */
    public void w(String message) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String fileName = caller.getFileName();
        String logClass = fileName.substring(0, fileName.length() - 5);
        String methodName = caller.getMethodName();
        Log.w("[" + logClass + "." + methodName + "]", message);
    }

    /**
     * Log errors
     */
    public void e(String message) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String fileName = caller.getFileName();
        String logClass = fileName.substring(0, fileName.length() - 5);
        String methodName = caller.getMethodName();
        Log.e("[" + logClass + "." + methodName + "]", message);
    }

    public void e(String message, Throwable e) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String fileName = caller.getFileName();
        String logClass = fileName.substring(0, fileName.length() - 5);
        String methodName = caller.getMethodName();
        Log.e("[" + logClass + "." + methodName + "]", message, e);
    }
}
