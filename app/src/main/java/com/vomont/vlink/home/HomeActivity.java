package com.vomont.vlink.home;

import android.Manifest;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.vomont.vlink.R;
import com.vomont.vlink.base.BaseActivity;
import com.vomont.vlink.home.fragment.MapFragment;
import com.vomont.vlink.home.fragment.MoreFragment;
import com.vomont.vlink.home.fragment.PloiceFragment;
import com.vomont.vlink.home.fragment.VideoFragment;
import com.vomont.vlink.service.WifiAndPoliceService;
import com.vomont.vlinkersdk.WMUserEventMsg;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeActivity extends BaseActivity {

    @BindView(R.id.activity_home_frame)
    FrameLayout activity_home_frame;
    //视频
    @BindView(R.id.radio_video)
    RadioButton radio_video;
    //地图
    @BindView(R.id.radio_map)
    RadioButton radio_map;
    //报警
    @BindView(R.id.radio_callplolice)
    RadioButton radio_callplolice;
    //更多
    @BindView(R.id.radio_more)
    RadioButton radio_more;

    @BindView(R.id.home_end)
    LinearLayout home_end;
    private FragmentManager fragmentManager;
    //实时预览界面
    private VideoFragment videoFragment;

    //地图界面
    private MapFragment mapFragment;
    //报警界面
    private PloiceFragment ploiceFragment;
    //更多界面
    private MoreFragment moreFragment;


    private FragmentTransaction fragmentTransaction;


    private boolean isChange;
    private long exitTime = 0;

    private MyLocationListenner myListener = new MyLocationListenner();

    // 定位相关
    private LocationClient mLocClient;

    private BDLocation bdLocation;
    private static boolean isPermissionRequested = false;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private WifiAndPoliceService wifiAndPoliceService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_home);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        ButterKnife.bind(this);
        initFragment();
        requestPermission();
        requestSDPermission();
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        Intent bind = new Intent(this, WifiAndPoliceService.class);
        bindService(bind, connection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            wifiAndPoliceService = ((WifiAndPoliceService.WifiAndPoliceBinder) service).getService();
            wifiAndPoliceService.getPolice();
            wifiAndPoliceService.getWifi();
            wifiAndPoliceService.setPoliceResult(new WifiAndPoliceService.PoliceDataCallBack() {
                @Override
                public void call(final List<WMUserEventMsg> mlist) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ploiceFragment != null) {
                                ploiceFragment.setData(mlist);
                            }
                        }
                    });
                }
            });
        }
    };


    /**
     * 初始化framgent
     */
    private void initFragment() {
        // 初始化 默认第一个页面
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        videoFragment = new VideoFragment();
        fragmentTransaction.add(R.id.activity_home_frame, videoFragment);
        fragmentTransaction.commit();
    }

    /**
     * RadioButton的点击事件
     * 切换fragment
     *
     * @param view
     */
    @OnClick({R.id.radio_video, R.id.radio_map, R.id.radio_callplolice, R.id.radio_more})
    public void radioOnClick(View view) {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.radio_video:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                if (videoFragment == null) {
                    videoFragment = new VideoFragment();
                    fragmentTransaction.add(R.id.activity_home_frame, videoFragment);
                } else {
                    fragmentTransaction.show(videoFragment);
                }
                if (mapFragment != null) {
                    fragmentTransaction.hide(mapFragment);
                }
                if (ploiceFragment != null) {
                    fragmentTransaction.hide(ploiceFragment);
                }
                if (moreFragment != null) {
                    fragmentTransaction.hide(moreFragment);
                }
                fragmentTransaction.commit();
                break;
            case R.id.radio_map:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (mapFragment == null) {
                    mapFragment = new MapFragment();
                    fragmentTransaction.add(R.id.activity_home_frame, mapFragment);
                    mapFragment.setLogcation(bdLocation);
                } else {
                    fragmentTransaction.show(mapFragment);
                    mapFragment.setLogcation(bdLocation);
                }
                if (videoFragment != null) {
                    fragmentTransaction.hide(videoFragment);
                }
                if (ploiceFragment != null) {
                    fragmentTransaction.hide(ploiceFragment);
                }
                if (moreFragment != null) {
                    fragmentTransaction.hide(moreFragment);
                }
                fragmentTransaction.commit();
                break;
            case R.id.radio_callplolice:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (ploiceFragment == null) {
                    ploiceFragment = new PloiceFragment();
                    fragmentTransaction.add(R.id.activity_home_frame, ploiceFragment);
                } else {
                    fragmentTransaction.show(ploiceFragment);
                }
                if (videoFragment != null) {
                    fragmentTransaction.hide(videoFragment);
                }
                if (mapFragment != null) {
                    fragmentTransaction.hide(mapFragment);
                }
                if (moreFragment != null) {
                    fragmentTransaction.hide(moreFragment);
                }
                fragmentTransaction.commit();
                break;
            case R.id.radio_more:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                if (moreFragment == null) {
                    moreFragment = new MoreFragment();
                    fragmentTransaction.add(R.id.activity_home_frame, moreFragment);
                } else {
                    fragmentTransaction.show(moreFragment);
                }
                if (videoFragment != null) {
                    fragmentTransaction.hide(videoFragment);
                }
                if (mapFragment != null) {
                    fragmentTransaction.hide(mapFragment);
                }
                if (ploiceFragment != null) {
                    fragmentTransaction.hide(ploiceFragment);
                }
                fragmentTransaction.commit();
                break;
            default:
                break;
        }
    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissions = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (permissions.size() == 0) {
                return;
            } else {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
            }
        }
    }

    private void requestSDPermission() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        String message===? "屏幕设置为：横屏" : "屏幕设置为：竖屏";
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            home_end.setVisibility(View.GONE);
            isChange = true;
        } else {
            home_end.setVisibility(View.VISIBLE);
            isChange = false;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (isChange) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                exit();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            HomeActivity.this.bdLocation = bdLocation;
            if (mapFragment != null) {
                mapFragment.setLogcation(bdLocation);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
