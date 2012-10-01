package com.better.wakelock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;

import android.util.Log;

import com.better.wakelock.Logger.LogLevel;
import com.better.wakelock.Logger.LogWriter;

public class FileLogWriter implements LogWriter {
    DateFormat df;

    public FileLogWriter() {
        df = DateFormat.getDateTimeInstance();
    }

    @Override
    public void write(LogLevel level, String tag, String message) {
        write(level, tag, message, null);
    }

    @Override
    public void write(LogLevel level, String tag, String message, Throwable throwable) {
        if (level.equals(LogLevel.NONE)) return;

        File logFile = new File("/sdcard/log.txt");
        if (!logFile.exists()) {
            try {
                Log.d(getClass().getName(), "creating new file");
                logFile.createNewFile();
            } catch (IOException e) {
                Log.d(getClass().getName(), "oops", e);
            }
        }

        try {
            // BufferedWriter for performance, true to set append to file flag
            FileWriter fileWriter = new FileWriter(logFile, true);
            BufferedWriter buf = new BufferedWriter(fileWriter);
            Date timeStamp = new Date(System.currentTimeMillis());
            buf.append(df.format(timeStamp));
            buf.append(" ");
            buf.append(level.name());
            buf.append(" ");
            buf.append(tag);
            buf.append(" ");
            buf.append(message);
            if (throwable != null) {
                PrintStream stream = new PrintStream(logFile);
                throwable.printStackTrace(stream);
            }
            buf.newLine();
            buf.flush();
            buf.close();
            fileWriter.close();
        } catch (IOException e) {
            Log.d(getClass().getName(), "oops", e);
        }
    }

}
