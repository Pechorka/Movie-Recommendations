package ru.surf.course.movierecommendations.tmdbTasks;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ru.surf.course.movierecommendations.models.Media;

/**
 * Created by Sergey on 12.02.2017.
 */

public abstract class GetMediaTask extends AsyncTask<String, Void, List<? extends Media>> {
    protected final String API_KEY_PARAM = "api_key";
    protected final String LANGUAGE_PARAM = "language";
    protected final String PAGE_PARAM = "page";
    protected final String NAME_PARAM = "query";
    protected final String RELEASE_DATE_GTE = "release_date.gte";
    protected final String RELEASE_DATE_LTE = "release_date.lte";
    protected final String WITH_GENRES = "with_genres";
    protected final String WITH_KEYWORDS = "with_keywords";
    protected final String SORT_BY = "sort_by";
    protected boolean isLoadingList;
    protected boolean newResult;
    protected Tasks task;
    protected List<TaskCompletedListener> listeners = new ArrayList<>();

    public abstract void getMediaById(int movieId, String language);

    public abstract void getMediaByFilter(String filter, String language, String page);

    public abstract void getMediaByName(String name, String language, String page);

    public abstract void getMediaByGenre(String genreIds, String language, String page);

    public abstract void getSimilarMedia(int movieId, String language, String page);

    public abstract void getMediaByKeywords(String keywordIds, String language, String page);

    public abstract void getMediaByCustomFilter(String language, String page, String genres,
                                                String releaseDateGTE, String releaseDateLTE,
                                                String sortBy);

    public void addListener(TaskCompletedListener toAdd) {
        listeners.add(toAdd);
    }

    public interface TaskCompletedListener<T extends Media> {
        void mediaLoaded(List<T> result, boolean newResult);
    }

}
