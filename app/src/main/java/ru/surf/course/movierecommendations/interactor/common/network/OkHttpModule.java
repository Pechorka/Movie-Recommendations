package ru.surf.course.movierecommendations.interactor.common.network;

import com.agna.ferro.mvp.component.scope.PerApplication;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import ru.surf.course.movierecommendations.interactor.common.network.request.RequestCacheInterceptor;
import ru.surf.course.movierecommendations.interactor.common.network.response.ResponseCacheInterceptor;


@Module
public class OkHttpModule {

    private static final int NETWORK_TIMEOUT = 10; //seconds

    @Provides
    @PerApplication
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor httpLoggingInterceptor,
                                     ResponseCacheInterceptor responseCacheInterceptor,
                                     RequestCacheInterceptor requestCacheInterceptor,
                                     Cache cache) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(NETWORK_TIMEOUT, TimeUnit.SECONDS);

        okHttpClientBuilder.addInterceptor(httpLoggingInterceptor);
        okHttpClientBuilder.addInterceptor(requestCacheInterceptor);
        okHttpClientBuilder.addNetworkInterceptor(responseCacheInterceptor);
        okHttpClientBuilder.cache(cache);
        return okHttpClientBuilder.build();
    }

}
