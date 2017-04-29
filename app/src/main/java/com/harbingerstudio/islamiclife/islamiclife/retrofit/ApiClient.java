package com.harbingerstudio.islamiclife.islamiclife.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by User on 4/27/2017.
 */

public class ApiClient {
    public static final String BASE_URL = "https://maps.googleapis.com/maps/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
