package com.better.wakelock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class Logger {

    private static final String TAG = Logger.class.getSimpleName();

    public enum LogLevel {
        NONE, DEBUG, INFO, WARN, ERR
    }

    /**
     * Log writing strategy
     * 
     * @author Yuriy
     * 
     */
    public interface LogWriter {

        public void write(LogLevel level, String tag, String message);

        public void write(LogLevel level, String tag, String message, Throwable e);
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

    private final List<LogWriter> writers;

    private Logger() {
        mLogLevels = new HashMap<String, Logger.LogLevel>();
        writers = new ArrayList<LogWriter>();
    }

    public void addLogWriter(LogWriter logWriter) {
        writers.add(logWriter);
    }

    public void removeLogWriter(LogWriter logWriter) {
        writers.remove(logWriter);
    }

    public void setLogLevel(Class<?> logClass, LogLevel logLevel) {
        String simpleName = logClass.getSimpleName();
        mLogLevels.put(simpleName, logLevel);
        String string = "Adding " + simpleName + " with LogLevel " + logLevel.toString();
        Log.d(TAG, string);
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
            String string = "no LogLevel was found for " + logClass;
            Log.w(TAG, string);
            String string2 = "Adding " + logClass + " with LogLevel " + logLevel.toString();
            Log.d(TAG, string2);
        }

        String formatTag = formatTag();

        for (LogWriter writer : writers) {
            writer.write(logLevel, formatTag, message);
        }

    }

    /**
     * Log errors
     */
    public void w(String message) {
        String tag = formatTag();
        for (LogWriter writer : writers) {
            writer.write(LogLevel.WARN, tag, message);
        }
    }

    /**
     * Log errors
     */
    public void e(String message) {
        String tag = formatTag();
        for (LogWriter writer : writers) {
            writer.write(LogLevel.ERR, tag, message);
        }
    }

    public void e(String message, Throwable e) {
        String tag = formatTag();
        for (LogWriter writer : writers) {
            writer.write(LogLevel.ERR, tag, message, e);
        }
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
