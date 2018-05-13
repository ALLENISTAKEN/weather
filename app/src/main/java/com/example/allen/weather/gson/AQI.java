package com.example.allen.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Basic
 *
 * @author: Allen
 * @time: 2018/3/2 17:29
 */

public class AQI {
    public AQICity city;
    public class AQICity {

        public String aqi;
        public String pm25;
    }
}
