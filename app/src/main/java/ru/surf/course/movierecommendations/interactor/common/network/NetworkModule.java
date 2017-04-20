package ru.surf.course.movierecommendations.interactor.common.network;

import com.agna.ferro.mvp.component.scope.PerApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.surf.course.movierecommendations.BuildConfig;
import ru.surf.course.movierecommendations.interactor.common.network.request.RequestCacheInterceptor;
import ru.surf.course.movierecommendations.interactor.common.network.response.ResponseCacheInterceptor;
import ru.surf.course.movierecommendations.interactor.network.connection.NetworkConnectionChecker;
import rx.schedulers.Schedulers;
import timber.log.Timber;


@Module
public class NetworkModule {

  public static final String HTTP_LOG_TAG = "OkHttp";

  @Provides
  @PerApplication
  Retrofit provideRetrofit(OkHttpClient okHttpClient,
      Gson gson, RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {
    return new Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(ServerUrls.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .build();
  }

  @Provides
  @PerApplication
  Gson provideGson() {
    return new GsonBuilder()
        .create();
  }

  @Provides
  @PerApplication
  RxJavaCallAdapterFactory provideRxJavaCallAdapterFactory() {
    return RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
  }

  @Provides
  @PerApplication
  HttpLoggingInterceptor provideHttpLoggingInterceptor() {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
        message -> Timber.tag(HTTP_LOG_TAG).d(message));
    if (BuildConfig.DEBUG) {
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    } else {
      loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
    }
    return loggingInterceptor;
  }

  @Provides
  @PerApplication
  ResponseCacheInterceptor provideResponseCacheInterceptor() {
    return new ResponseCacheInterceptor();
  }

  @Provides
  @PerApplication
  RequestCacheInterceptor provideRequestCacheInterceptor(
      NetworkConnectionChecker networkConnectionChecker) {
    return new RequestCacheInterceptor(networkConnectionChecker);
  }

}
