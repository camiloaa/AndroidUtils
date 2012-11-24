package com.better.wakelock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class Logger {

    private static final String TAG = Logger.class.getSimpleName();

    public enum LogLevel {
        ERR, WARN, DEBUG, INFO
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

    /**
     * For a given logClass only messages with logLevel above will be logged.
     * 
     * @param logClass
     * @param logLevel
     */
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
     * Logs the message if configured log level for the class is above requested
     * log level. If configured {@link LogLevel} is {@link LogLevel#WARN}, only
     * logs with {@link LogLevel#ERR} and {@link LogLevel#WARN} will be shown.
     * 
     * @param logLevel
     * @param message
     */
    public void log(LogLevel logLevel, String message) {
        logIfApplicable(logLevel, message, null);
    }

    private void logIfApplicable(LogLevel logLevel, String message, Throwable throwable) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String fileName = caller.getFileName();
        String logClass = fileName.substring(0, fileName.length() - 5);

        LogLevel configuredLogLevel = mLogLevels.get(logClass);

        if (configuredLogLevel == null) {
            configuredLogLevel = LogLevel.DEBUG;
            mLogLevels.put(logClass, configuredLogLevel);
            String string = "no LogLevel was found for " + logClass;
            Log.w(TAG, string);
            String string2 = "Adding " + logClass + " with LogLevel "
                    + configuredLogLevel.toString();
            Log.d(TAG, string2);
        }
        boolean shouldBeLogged = logLevel.ordinal() <= configuredLogLevel.ordinal();
        if (shouldBeLogged) {
            String formatTag = formatTag();
            for (LogWriter writer : writers) {
                writer.write(logLevel, formatTag, message, throwable);
            }
        }
    }

    static public void d(String message) {
        sInstance.logIfApplicable(LogLevel.DEBUG, message, null);
    }

    static public void w(String message) {
        sInstance.logIfApplicable(LogLevel.WARN, message, null);
    }

    static public void e(String message) {
        sInstance.logIfApplicable(LogLevel.ERR, message, null);
    }

    static public void e(String message, Throwable throwable) {
        sInstance.logIfApplicable(LogLevel.ERR, message, throwable);
    }

    private String formatTag() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[5];
        String fileName = caller.getFileName();
        String logClass = fileName.substring(0, fileName.length() - 5);
        String methodName = caller.getMethodName();
        String tag = "[" + logClass + "." + methodName + "]";
        return tag;
    }
}
