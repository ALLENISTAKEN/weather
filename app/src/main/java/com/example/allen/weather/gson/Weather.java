package com.example.allen.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Weather
 *
 * @author: Allen
 * @time: 2018/3/2 17:59
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
