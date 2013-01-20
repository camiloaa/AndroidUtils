package com.github.androidutils.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.github.androidutils.logger.Logger.LogLevel;
import com.github.androidutils.logger.Logger.LogWriter;

public class FileLogWriter implements LogWriter {
    private static final String DEFAULT_LOGS_DIRECTORY = "logs";
    private final DateFormat df;
    private final DateFormat dtf;
    private final String logDirectory;

    public FileLogWriter() {
        this(DEFAULT_LOGS_DIRECTORY);
    }

    public FileLogWriter(String logDirectory) {
        df = DateFormat.getDateInstance();
        dtf = DateFormat.getDateTimeInstance();
        this.logDirectory = "/sdcard/" + logDirectory + "/";
    }

    @Override
    public void write(LogLevel level, String tag, String message) {
        write(level, tag, message, null);
    }

    @Override
    public void write(LogLevel level, String tag, String message, Throwable throwable) {
        Calendar today = Calendar.getInstance();
        String date = df.format(today.getTime());
        File direct = new File(logDirectory);
        if (!direct.exists()) {
            direct.mkdir();
        }
        final File logFile = new File(logDirectory + date + "_log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
                Log.d(getClass().getName(), "Created a new file");
            } catch (final IOException e) {
                Log.d(getClass().getName(), "Creating new file failed - " + e.getMessage());
                return;
            }
        }

        try {
            // BufferedWriter for performance, true to set append to file flag
            final FileWriter fileWriter = new FileWriter(logFile, true);
            final BufferedWriter buf = new BufferedWriter(fileWriter);
            final Date timeStamp = new Date(System.currentTimeMillis());
            buf.append(dtf.format(timeStamp));
            buf.append(" ");
            buf.append(level.name());
            buf.append(" ");
            buf.append(tag);
            buf.append(" ");
            buf.append(message);
            if (throwable != null) {
                final PrintStream stream = new PrintStream(logFile);
                throwable.printStackTrace(stream);
            }
            buf.newLine();
            buf.flush();
            buf.close();
            fileWriter.close();
        } catch (final IOException e) {
            Log.d(getClass().getName(), "Writing failed - " + e.getMessage());
        }
    }
}
