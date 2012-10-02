package com.better.wakelock;

import android.util.Log;

import com.better.wakelock.Logger.LogLevel;

public class LogcatLogWriter implements Logger.LogWriter {
    @Override
    public void write(LogLevel level, String tag, String message) {
        switch (level) {
        case INFO:
            Log.i(tag, message);
            break;

        case DEBUG:
            Log.d(tag, message);
            break;

        case WARN:
            Log.w(tag, message);
            break;

        case ERR:
            Log.e(tag, message);
            break;

        default:
            break;
        }
    }

    @Override
    public void write(LogLevel level, String tag, String message, Throwable e) {
        switch (level) {
        case INFO:
            Log.i(tag, message, e);
            break;

        case DEBUG:
            Log.d(tag, message, e);
            break;

        case WARN:
            Log.w(tag, message, e);
            break;

        case ERR:
            Log.e(tag, message, e);
            break;

        default:
            break;
        }
    }
}
