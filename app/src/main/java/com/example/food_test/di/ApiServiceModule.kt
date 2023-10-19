package com.example.food_test.di

import com.example.food_test.api_service.ApiService
import com.example.food_test.api_service.PostApiService
import com.example.food_test.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(Singleton::class)
object ApiServiceModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient().newBuilder().addInterceptor{ chain ->
        val request = chain.request()
        val newRequest = request.newBuilder().build()
        chain.proceed(newRequest)
    }
        .connectTimeout(1,TimeUnit.MINUTES)
        .readTimeout(1,TimeUnit.MINUTES)
        .writeTimeout(1,TimeUnit.MINUTES)
        .build()

    @Provides
    @Singleton
    fun provideFoodApiService(okHttpClient: OkHttpClient): ApiService =
        Retrofit.Builder()
            .baseUrl(Constants.FOOD_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    @Provides
    @Singleton
    fun providePostApiService(okHttpClient: OkHttpClient): PostApiService =
        Retrofit.Builder()
            .baseUrl(Constants.POST_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostApiService::class.java)
}