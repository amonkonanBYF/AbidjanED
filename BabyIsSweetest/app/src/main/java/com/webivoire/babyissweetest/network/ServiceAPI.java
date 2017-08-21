package com.webivoire.babyissweetest.network;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by fabia on 16/03/2017.
 */

public interface ServiceAPI {

    @GET("android/data.json")
    Call<JsonElement> getItemFromDB();
}
