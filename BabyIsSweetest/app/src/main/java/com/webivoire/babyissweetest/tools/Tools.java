package com.webivoire.babyissweetest.tools;

import com.webivoire.babyissweetest.network.ServiceAPI;
import com.webivoire.babyissweetest.statics.Statics;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fabia on 16/03/2017.
 */

public class Tools {

    public static ServiceAPI getServiceAPI(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                                    .addInterceptor(interceptor)
                                    .build();

        Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(Statics.SERVICE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .client(client).build();

        return retrofit.create(ServiceAPI.class);
    }
}
