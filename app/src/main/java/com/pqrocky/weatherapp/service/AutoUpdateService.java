package com.pqrocky.weatherapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by 从鹏 on 2016-04-05.
 * 后台自动更新天气
 */
public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
