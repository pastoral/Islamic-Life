package com.harbingerstudio.islamiclife.islamiclife.retrofit;

import com.harbingerstudio.islamiclife.islamiclife.pojo.prayertime.Example;
import com.harbingerstudio.islamiclife.islamiclife.pojo.prayertime.Data;
import com.harbingerstudio.islamiclife.islamiclife.pojo.prayertime.Timings;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by User on 5/1/2017.
 */

public interface EndpointPrayerTimeInterface {
@GET("/timings")
    Call<Example>getPrayerTime(@Query("latitude") String latitude, @Query("longitude") String longitude, @Query("timezonestring") String timezonestring, @Query("method") String method);
}
