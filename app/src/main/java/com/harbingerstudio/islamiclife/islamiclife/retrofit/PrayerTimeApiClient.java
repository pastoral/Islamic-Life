package com.harbingerstudio.islamiclife.islamiclife.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by User on 5/1/2017.
 */

public class PrayerTimeApiClient {
    public static final String PrayerTime_BASE_URL = "http://api.aladhan.com";
    private static Retrofit retrofit = null;
    public static Retrofit getPrayerTimeApiClient(){
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(PrayerTime_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
