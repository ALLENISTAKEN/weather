package com.example.allen.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Basic
 *
 * @author: Allen
 * @time: 2018/3/2 17:29
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;
    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;
    }
}
