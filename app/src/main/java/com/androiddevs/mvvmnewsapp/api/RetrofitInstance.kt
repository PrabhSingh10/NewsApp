package com.androiddevs.mvvmnewsapp.api

import com.androiddevs.mvvmnewsapp.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {

            val logging = HttpLoggingInterceptor()
                //Logging Interceptors logs request and responses of retrofit which is useful for
                //debugging
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder() //Basically prints the responses in logcat
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                    //To convert JSON to Kotlin Objects
                .client(client)
                .build()
        }

        val api: NewsAPI by lazy {   //The NewsAPI interface is attached to retrofit here.
            //It is done separately because if need to have other API interfaces attached
            //with retrofit, we don't have to build another instance and can just
            //attach the existing retrofit object with another interface.
            retrofit.create(NewsAPI::class.java)
        }
    }

}