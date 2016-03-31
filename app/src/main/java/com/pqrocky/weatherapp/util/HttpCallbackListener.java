package com.pqrocky.weatherapp.util;

/**
 * Created by 从鹏 on 2016-03-31.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
