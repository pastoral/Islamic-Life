package com.harbingerstudio.islamiclife.islamiclife.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by User on 5/4/2017.
 */

public class ApiArabicDate {
    public static final String ArabicDate_BASE_URL = "http://api.aladhan.com";
    private static Retrofit retrofit = null;
    public static Retrofit getArabicDateApiClient(){
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ArabicDate_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
