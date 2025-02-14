package com.example.weather;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("forecast")
    Call<Response> getWeather(@Query("latitude") String latitude, @Query("longitude") String longitude, @Query("current") String current, @Query("hourly") String hourly, @Query("daily") String daily, @Query("timezone") String timezone);

    @GET("reverse")
    Call<List<Address>> getAddress(@Query("lat") String latitude, @Query("lon") String longitude, @Query("limit") int limit, @Query("appid") String appid);
}
