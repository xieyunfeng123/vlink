package com.vomont.vlink.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.vomont.vlink.bean.WifiBean;
import com.vomont.vlinkersdk.PoliceCallBack;
import com.vomont.vlinkersdk.TransparentCallBack;
import com.vomont.vlinkersdk.WMDeviceInfo;
import com.vomont.vlinkersdk.WMUserEventMsg;
import com.vomont.vlinkersdk.WMVlinkerSDK;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/12/14 0014.
 */

public class WifiAndPoliceService extends Service {

    private Context context;

    WifiCallBack wifiCallBack;
    PoliceDataCallBack policeResult;

    private List<WifiBean> wifiBeanList;


    private List<WMUserEventMsg> policeBeanList;

    private List<WMDeviceInfo> deviceInfos = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new WifiAndPoliceBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        WMVlinkerSDK.getInstance().getDevInfoList(deviceInfos);
    }

    public void getPolice() {
        policeBeanList = new ArrayList<>();
        int result = WMVlinkerSDK.getInstance().setEventMsgCallBack(new PoliceCallBack() {
            @Override
            public void fAlarmMessageCallBack(int iAlarmType, int iDeviceId, int iChanId) {
                if (iAlarmType != -1) {
                    WMUserEventMsg bean = new WMUserEventMsg();
                    bean.setDevId(iDeviceId);
                    bean.setChannelId(iChanId);
                    bean.setEventType(iAlarmType);
                    for (WMDeviceInfo info : deviceInfos) {
                        if (info.getDevId() == iDeviceId) {
                            bean.setName(info.getDevName());
                            break;
                        }
                    }
                    bean.setTypeName(getTypeName(iAlarmType));
                    bean.setTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                    policeBeanList.add(0, bean);
                    if (policeBeanList.size() > 50) {
                        policeBeanList.remove(policeBeanList.size() - 1);
                    }
                    if (policeResult != null) {
                        policeResult.call(policeBeanList);
                    }
                }
            }
        });
    }


    public void getWifi() {
        wifiBeanList = new ArrayList<>();
        WMVlinkerSDK.getInstance().setTransparentDataCallBack(new TransparentCallBack() {
            @Override
            public void fTransparentDataCallBack(int iDeviceId, byte[] pDataBuffer, int iDataSize) {
                WifiBean bean = new WifiBean();
                bean.setDeviceId(iDeviceId);
                bean.setDes(new String(pDataBuffer));
                bean.setTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                for (WMDeviceInfo info : deviceInfos) {
                    if (info.getDevId() == iDeviceId) {
                        bean.setName(info.getDevName());
                        break;
                    }
                }
                wifiBeanList.add(0, bean);
                if (wifiBeanList.size() > 50) {
                    wifiBeanList.remove(wifiBeanList.size() - 1);
                }
                if (wifiCallBack != null) {
                    wifiCallBack.call(wifiBeanList);
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public static String getTypeName(int i) {
        if (i == -1) {
            return "全部";
        } else if (i == 0) {
            return "视频(信号)丢失";
        } else if (i == 1) {
            return "外部(信号量)报警";
        } else if (i == 2) {
            return "视频遮盖";
        } else if (i == 3) {
            return "移动侦测";
        } else if (i == 4) {
            return "布撤防报警";
        } else if (i == 5) {
            return "人脸侦测";
        } else if (i == 6) {
            return "目标进入区域";
        } else if (i == 7) {
            return "目标离开区域";
        } else if (i == 8) {
            return "周界入侵（区域入侵）";
        } else if (i == 9) {
            return "物品遗留";
        } else if (i == 10) {
            return "物品拿取";
        } else {
            return "";
        }
    }
    public class WifiAndPoliceBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public WifiAndPoliceService getService() {
            return WifiAndPoliceService.this;
        }
    }

    public List<WifiBean> getWifiBeanList() {
        return wifiBeanList;
    }

    public List<WMUserEventMsg> getPoliceBeanList() {
        return policeBeanList;
    }

    public void setWifiDataResult(WifiCallBack wifiCallBack) {
        this.wifiCallBack = wifiCallBack;
    }

    public interface WifiCallBack {
        void call(List<WifiBean> mlist);
    }

    public void setPoliceResult(PoliceDataCallBack policeResult) {
        this.policeResult = policeResult;
    }


    public interface PoliceDataCallBack {
        void call(List<WMUserEventMsg> mlist);
    }
}
