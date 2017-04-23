package ru.surf.course.movierecommendations.app.log;

import android.util.Log;

import timber.log.Timber;

/**
 * логгирует в logcat
 */
class LoggerTree extends Timber.DebugTree {

    public static final String REMOTE_LOGGER_LOG_FORMAT = "%s: %s";
    public static final String REMOTE_LOGGER_SEND_LOG_ERROR = "error sending to RemoteLogger";


    /**
     * приоритет по умолчанию - DEBUG
     */
    public LoggerTree() {
        this(Log.DEBUG);
    }

    private LoggerTree(int mLogPriority) {
        int mLogPriority1 = mLogPriority;
    }


}
