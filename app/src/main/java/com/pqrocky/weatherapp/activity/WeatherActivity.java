package com.pqrocky.weatherapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pqrocky.weatherapp.R;
import com.pqrocky.weatherapp.util.HttpCallbackListener;
import com.pqrocky.weatherapp.util.HttpUtil;
import com.pqrocky.weatherapp.util.Utility;

import butterknife.Bind;
import butterknife.ButterKnife;


public class WeatherActivity extends Activity implements View.OnClickListener {

    @Bind(R.id.city_name)
    TextView cityName;
    @Bind(R.id.publish_text)
    TextView publishText;
    @Bind(R.id.current_date)
    TextView currentDate;
    @Bind(R.id.weather_desp)
    TextView weatherDesp;
    @Bind(R.id.temp1)
    TextView temp1;
    @Bind(R.id.temp2)
    TextView temp2;
    @Bind(R.id.weather_info_layout)
    LinearLayout weatherInfoLayout;
    @Bind(R.id.switch_city)
    Button switchCity;
    @Bind(R.id.refresh_weather)
    Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        ButterKnife.bind(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            publishText.setText("同步中...");
            weatherDesp.setVisibility(View.INVISIBLE);
            cityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);

    }

    private void queryWeatherCode(String countyCode) {

        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        Log.d("queryWeatherCode_Address", address);
        queryFromServer(address, "countyCode");
    }

    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });

                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("weatherActivity", e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityName.setText(prefs.getString("city_name", ""));
        temp1.setText(prefs.getString("temp1", ""));
        temp2.setText(prefs.getString("temp2", ""));
        weatherDesp.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDate.setText(prefs.getString("current_date", ""));
        weatherDesp.setVisibility(View.VISIBLE);
        cityName.setVisibility(View.VISIBLE);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                Log.d("refresh_weather", "start");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                Log.d("refresh_weather", "start2");
                String weatherCode = prefs.getString("city_code", "");
                Log.d("refresh_weather", weatherCode);
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);

                }


                break;
            default:
                break;
        }
    }

    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        Log.d("queryWeatherInfo", address);
        queryFromServer(address, "weatherCode");
    }
}
