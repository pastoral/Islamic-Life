package com.harbingerstudio.islamiclife.islamiclife.retrofit;

import com.harbingerstudio.islamiclife.islamiclife.pojo.arabicdate.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by User on 5/4/2017.
 */

public interface EndPointArabicDateInterface {
    @GET("/gToH")
    Call<Example> getArabicDate(@Query("date") String date);
}

