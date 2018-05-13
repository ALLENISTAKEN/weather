package com.example.allen.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Basic
 *
 * @author: Allen
 * @time: 2018/3/2 17:29
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    @SerializedName("sport")
    public Sport sport;

    public class Comfort {

        @SerializedName("txt")
        public String info;
    }
    public class CarWash {

        @SerializedName("txt")
        public String info;
    }
    public class Sport {

        @SerializedName("txt")
        public String info;
    }
}
