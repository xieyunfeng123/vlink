package com.vomont.vlink.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.baidu.mapapi.SDKInitializer;
import com.vomont.vlink.bean.IPInfo;
import com.vomont.vlink.comm.ACaCheContrast;
import com.vomont.vlink.util.ACache;

import java.io.File;


/**
 * APPLICATION
 */
public class BaseApplication extends Application {

    private static BaseApplication baseApplication;
    public static String BASE_URL = "http://101.201.75.83:9001";
    //http://118.244.236.67:9000
//    public static final String HOST = "http://www.vomont.com/yundd/addr.php";

    private ACache aCache;

    private Handler handler;
    @Override
    public void onCreate() {
        super.onCreate();
        aCache = ACache.get(this);
        baseApplication = this;
        SDKInitializer.initialize(getApplicationContext());
        getURL();
    }

    public static Context getAppContext() {
        return baseApplication;
    }
    public static Resources getAppResources() {
        return baseApplication.getResources();
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    private void getURL()
    {
        // 判断缓存里是否已经有ip地址 如果有就使用缓存的ip地址 如果没有就获取ip地址
        if (aCache.getAsString(ACaCheContrast.ip) != null)
        {
            BASE_URL = "http://" + aCache.getAsString(ACaCheContrast.ip);
            String[] str = BASE_URL.split(":");
            if (str.length != 3)
            {
                BASE_URL = BASE_URL + ":9001";
            }
        }
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if (msg.what == 100)
                {
                    IPInfo ipInfo = (IPInfo)msg.obj;
                    String new_url = "http://" + ipInfo.getVfilesvrip() + ":" + ipInfo.getVfilesvrport();
                    if (!new_url.equals(BASE_URL))
                    {
                        aCache.put("ip", ipInfo.getVfilesvrip() + ":" + ipInfo.getVfilesvrport());
                        BASE_URL = new_url;
                    }
                }
            }
        };
    }


}
