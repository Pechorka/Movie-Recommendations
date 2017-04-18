package ru.surf.course.movierecommendations.app.log;

import android.util.Log;

import timber.log.Timber;

/**
 * Created by andrew on 2/20/17.
 */

/**
 * логгирует в logcat
 */
public class LoggerTree extends Timber.DebugTree {

    public static final String REMOTE_LOGGER_LOG_FORMAT = "%s: %s";
    public static final String REMOTE_LOGGER_SEND_LOG_ERROR = "error sending to RemoteLogger";
    private final int mLogPriority;


    /**
     * приоритет по умолчанию - DEBUG
     */
    public LoggerTree() {
        this(Log.DEBUG);
    }

    public LoggerTree(int mLogPriority) {
        this.mLogPriority = mLogPriority;
    }


}
