package ru.surf.course.movierecommendations.tmdbTasks;

import android.os.AsyncTask;

import java.util.List;

import ru.surf.course.movierecommendations.models.People;

/**
 * Created by andrew on 1/29/17.
 */

public class GetPeopleTask extends AsyncTask<String, Void, List<People>> {

    private final String API_KEY_PARAM = "api_key";

    private Tasks task;
    private boolean isLoadingList;

    @Override
    protected List<People> doInBackground(String... strings) {

        switch (task) {
            case GET_MOVIE_CREDITS:

                break;
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<People> peoples) {
        super.onPostExecute(peoples);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public void getMovieCredits(int movieId) {
        isLoadingList = true;
        task = Tasks.GET_MOVIE_CREDITS;
        execute(String.valueOf(movieId));
    }


}
