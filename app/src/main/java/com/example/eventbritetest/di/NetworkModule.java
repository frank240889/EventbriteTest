package com.example.eventbritetest.di;

import com.example.eventbritetest.BuildConfig;
import com.example.eventbritetest.network.EventbriteApiService;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {


    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor interceptor) {
        return new OkHttpClient.Builder().
                readTimeout(10, TimeUnit.SECONDS).
                writeTimeout(10, TimeUnit.SECONDS).
                connectTimeout(10, TimeUnit.SECONDS).
                addInterceptor(interceptor).
                build();
    }

    @Singleton
    @Provides
    Retrofit provideRetrofit(OkHttpClient okHttpClient){
        return new Retrofit.Builder().
                baseUrl(BuildConfig.BASE_URL).
                client(okHttpClient).
                addConverterFactory(GsonConverterFactory.create()).
                build();
    }

    @Singleton
    @Provides
    EventbriteApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(EventbriteApiService.class);
    }
}
