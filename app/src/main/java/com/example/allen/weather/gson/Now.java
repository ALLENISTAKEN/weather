package com.example.allen.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Basic
 *
 * @author: Allen
 * @time: 2018/3/2 17:29
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;
    public class More {

        @SerializedName("txt")
        public String info;
    }
}
