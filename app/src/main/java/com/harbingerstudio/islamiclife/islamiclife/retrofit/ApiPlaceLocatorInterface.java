package com.harbingerstudio.islamiclife.islamiclife.retrofit;

import com.harbingerstudio.islamiclife.islamiclife.pojo.mosque.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by User on 4/27/2017.
 */

public interface ApiPlaceLocatorInterface {
 @GET("api/place/nearbysearch/json")
    Call<Example>getNearbyPlaces(@Query("location") String location, @Query("radius") String radius,
                                 @Query("type") String type, @Query("key") String key );
}
