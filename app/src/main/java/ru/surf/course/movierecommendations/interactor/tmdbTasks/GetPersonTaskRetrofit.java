package ru.surf.course.movierecommendations.interactor.tmdbTasks;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.surf.course.movierecommendations.domain.people.Person;
import rx.Observable;

public interface GetPersonTaskRetrofit {

    @GET("person/{id}")
    Observable<Person> getPersonById(@Path("id") int id,
                                     @Query("api_key") String apiKey, @Query("language") String language);
}
