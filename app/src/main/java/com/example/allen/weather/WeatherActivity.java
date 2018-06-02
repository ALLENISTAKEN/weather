package com.example.allen.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.allen.weather.gson.Forecast;
import com.example.allen.weather.gson.Weather;
import com.example.allen.weather.service.AutoUpdateService;
import com.example.allen.weather.util.HttpUtil;
import com.example.allen.weather.util.Utility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView apiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView cardWashText;
    private TextView sportText;

    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    private Button navButton;
    private Weather weather;
    private static final String TAG = "WeatherActivity";
    private LineChartView chartView;
    private LineChartOnValueSelectListener lineChartOnValueSelectListener = new LineChartOnValueSelectListener() {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(WeatherActivity.this, "温度：" + value.getY() + "℃", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);

        bingPicImg = findViewById(R.id.bing_pic_img);
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        apiText = findViewById(R.id.api_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        cardWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        chartView = findViewById(R.id.chartView);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        final String weatherId;

        if (weatherString != null) {
//            Weather weather = Utility.handleWeatherResponse(weatherString);
            Weather weather = Utility.handleWeather5Response(weatherString);
            this.weather = weather;
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.VISIBLE);
            requestWeather(weatherId);
        }


        //加载必应背景图片
//        String bingPic = prefs.getString("bing_pic", null);
//        if (bingPic != null) {
//            Glide.with(this).load(bingPic).into(bingPicImg);
//        } else {
//            loadBingPic();
//        }

        //加载背景图片
        loadWeatherPic();
        //数据初始化图表
        addData2ChartView();

        //下拉刷新天气信息
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                requestWeather(weatherId);
                requestWeather(weather.basic.weatherId);
                addData2ChartView();
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();
                Log.d(TAG, "onResponse: bingPic" + bingPic);

                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic)
                                .into(bingPicImg);
                    }
                });
            }
        });
    }

    //加载本地的天气壁纸
    private void loadWeatherPic() {

        if (weather == null) {
            loadBingPic();
            return;
        }
        String weatherInfo = weather.now.more.info;

//        int sunny = R.drawable.sunny;
//        int cloudy = R.drawable.cloudy;
//        int rain = R.drawable.rain;
//        int snow = R.drawable.snow;
//        int overcast = R.drawable.overcast;
//        int foggy = R.drawable.foggy;
//        int haze = R.drawable.foggy;
//
//        final Map<String, Integer> map = new HashMap<> ();
//        map.put("晴",sunny);
//        map.put("大风",sunny);
//        map.put("多云",cloudy);
//        map.put("少云",cloudy);
//        map.put("晴间多云",cloudy);
//        map.put("阴",overcast);
//        map.put("小雨",rain);
//        map.put("阵雨",rain);
//        map.put("雷阵雨",rain);
//        map.put("中雨",rain);
//        map.put("大雨",rain);
//        map.put("暴雨",rain);
//        map.put("小雪",snow);
//        map.put("中雪",snow);
//        map.put("大雪",snow);
//        map.put("雾",foggy);
//        map.put("霾",haze);
//
//        Object pic = map.get(weatherInfo);
//        if (pic != null) {
//            Glide.with(WeatherActivity.this).load(pic)
//                    .into(bingPicImg);
//        } else {
//            loadBingPic();
//        }

        changeBackground(weatherInfo);
    }

    //该方法调用了loadBingPic()方法来加载背景图片
    public void requestWeather( final String weatherId) {
//        String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
//                weatherId+ "&key=bc0418b57b2d4918819d3974ac1285d9";
//        String url = "https://free-api.heweather.com/v5/weather?city=" + weatherId +
//                "&key=bc0418b57b2d4918819d3974ac1285d9";
        String url1 = "https://free-api.heweather.com/v5/weather?city=" + weatherId +
                "&key=1cce17d4649344949ef7a619efec1cd5";
        HttpUtil.sendOkHttpRequest(url1, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseText = response.body().string();
//                final Weather weather = Utility.handleWeatherResponse(responseText);
                final Weather weather = Utility.handleWeather5Response(responseText);
                WeatherActivity.this.weather = weather;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

//        loadBingPic();
        loadWeatherPic();
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1] + " 更新";
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).
                    inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max + "℃");
            minText.setText(forecast.temperature.min + "℃");
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            apiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        cardWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void changeBackground(String weatherInfo) {
        ImageView backgroundView = (ImageView) findViewById(R.id.bing_pic_img);
        ImageView moveView1 = (ImageView) findViewById(R.id.move_iv1);
        ImageView moveView2 = (ImageView) findViewById(R.id.move_iv2);
        SnowView snowView = findViewById(R.id.snow_view);
        RainView rainView = findViewById(R.id.rain_view);
        final Map<String, Integer> map = new HashMap<> ();
        map.put("晴",0);
        map.put("大风",0);
        map.put("多云",1);
        map.put("少云",1);
        map.put("晴间多云",1);
        map.put("阴",2);
        map.put("雾",2);
        map.put("霾",2);
        map.put("小雨",3);
        map.put("阵雨",3);
        map.put("雷阵雨",3);
        map.put("中雨",3);
        map.put("大雨",3);
        map.put("暴雨",3);
        map.put("小雪",4);
        map.put("中雪",4);
        map.put("大雪",4);

        RotateAnimation rAnimation = new RotateAnimation(0, 30, 0, 0);
        rAnimation.setDuration(3000);
        rAnimation.setRepeatMode(Animation.REVERSE);
        rAnimation.setRepeatCount(Integer.MAX_VALUE);

        TranslateAnimation tAnimation1 = new TranslateAnimation(-100f, 300f, -400f, -400f);
        tAnimation1.setDuration(8000);
        tAnimation1.setRepeatMode(Animation.REVERSE);
        tAnimation1.setRepeatCount(Integer.MAX_VALUE);

        TranslateAnimation tAnimation2 = new TranslateAnimation(200f, -300f, -200f, -300f);
        tAnimation2.setDuration(10000);
        tAnimation2.setRepeatMode(Animation.REVERSE);
        tAnimation2.setRepeatCount(Integer.MAX_VALUE);

        switch (map.get(weatherInfo)) {
            case 0:
                Glide.with(this).load(R.drawable.bg0_fine_day).into(backgroundView);
                moveView1.setImageResource(R.drawable.light);
                moveView1.setAnimation(rAnimation);
                moveView1.startAnimation(rAnimation);
                moveView2.setVisibility(View.INVISIBLE);
                break;
            case 1 :
                Glide.with(this).load(R.drawable.cloudy).into(backgroundView);
                moveView1.setImageResource(R.drawable.fine_day_cloud1);
                moveView2.setImageResource(R.drawable.fine_day_cloud2);
                moveView1.startAnimation(tAnimation1);
                moveView2.startAnimation(tAnimation2);
                break;
            case 2 :
                Glide.with(this).load(R.drawable.bg_fog_and_haze).into(backgroundView);
                moveView1.setImageResource(R.drawable.fine_day_cloud1);
                moveView2.setImageResource(R.drawable.fine_day_cloud2);
                moveView1.startAnimation(tAnimation1);
                moveView2.startAnimation(tAnimation2);
                break;
            case 3 :
                Glide.with(this).load(R.drawable.bg_heavy_rain_night).into(backgroundView);
                rainView.setVisibility(View.VISIBLE);
                snowView.setVisibility(View.INVISIBLE);
                break;
            case 4 :
                Glide.with(this).load(R.drawable.bg_heavy_rain_night).into(backgroundView);
                snowView.setVisibility(View.VISIBLE);
                rainView.setVisibility(View.INVISIBLE);
                break;
            default:
                loadBingPic();
        }

    }

    /**
     * 初始化数据到chart view中
     */
    private void addData2ChartView() {
        Weather weather = this.weather;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        List<PointValue> maxValues = new ArrayList<>();
        List<PointValue> minValues = new ArrayList<>();
        try {
            for (Forecast forecast : weather.forecastList) {
                calendar.setTime(sdf.parse(forecast.date));
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int max = Integer.parseInt(forecast.temperature.max);
                int min = Integer.parseInt(forecast.temperature.min);
                maxValues.add(new PointValue(day, max));
                minValues.add(new PointValue(day, min));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Line maxLine = new Line(maxValues).setColor(Color.RED).setCubic(false);
        Line minLine = new Line(minValues).setColor(Color.BLUE).setCubic(false);
        List<Line> lines = new ArrayList<>();
        lines.add(maxLine);
        lines.add(minLine);
        LineChartData data = new LineChartData();
        data.setLines(lines);
        Axis axisX = new Axis();
        //setHasLines(true),设定是否有网格线
        Axis axisY = new Axis().setHasLines(false);
        //为两个坐标系设定名称
        axisX.setName("日期");
        axisY.setName("温度");
        //设置图标所在位置
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        //将数据添加到View中
        chartView.setLineChartData(data);
        chartView.setOnValueTouchListener(lineChartOnValueSelectListener);
        chartView.setInteractive(true);
    }

}
