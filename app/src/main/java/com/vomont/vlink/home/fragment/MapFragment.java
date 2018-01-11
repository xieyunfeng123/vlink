package com.vomont.vlink.home.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.vomont.vlink.R;
import com.vomont.vlink.video.VideoActivity;
import com.vomont.vlinkersdk.WMChannelInfo;
import com.vomont.vlinkersdk.WMDeviceInfo;
import com.vomont.vlinkersdk.WMMapNodeInfo;
import com.vomont.vlinkersdk.WMVlinkerSDK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/30 0030.
 */

public class MapFragment extends Fragment {

    private TextureMapView mMapView;

    private RelativeLayout rl_map;
    private BaiduMap mBaiduMap;
    private SensorManager mSensorManager;
    boolean isFirstLoc = true; // 是否首次定位
    @BindView(R.id.return_location)
    public ImageView return_location;
    @BindView(R.id.map_dev_info_bg)
    public LinearLayout map_dev_info_bg;

    @BindView(R.id.map_dev_name)
    public TextView map_dev_name;

    @BindView(R.id.etcmap_add)
    public ImageView etcmap_add;
    @BindView(R.id.etcmap_subtraction)
    public ImageView etcmap_subtraction;
    @BindView(R.id.search_dev_name)
    public EditText search_dev_name;

    public static final long Api_Code_Gps_Prec = 10000000000000000L;
    private List<WMMapNodeInfo> mlist;
    private List<WMDeviceInfo> deviceInfos;
    Marker hasCodeMark = null;
    BitmapDescriptor hasBdA = null;
    private Map<Marker, DevInfo> markerDevInfoMap;
    DevInfo devInfo;
    private BDLocation bdLocation;
    float  zoom=21f;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        rl_map = (RelativeLayout) view.findViewById(R.id.mTexturemap);
        rl_map.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }, 300);
        return view;
    }


    public void initData() {
        rl_map.removeAllViews();
        BaiduMapOptions options = new BaiduMapOptions();
        options.zoomControlsEnabled(false);
        mMapView = new TextureMapView(getActivity(), options);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mMapView.setLayoutParams(params);
        rl_map.addView(mMapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mlist = new ArrayList<>();
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //是否显示播放器菜单
                if (map_dev_info_bg.getVisibility() == View.VISIBLE) {
                    TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 100);
                    animation.setDuration(300);
                    map_dev_info_bg.setAnimation(animation);
                    map_dev_info_bg.setVisibility(View.GONE);
                }
                if (hasCodeMark != null && hasBdA != null) {
                    hasCodeMark.setIcon(hasBdA);
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        WMVlinkerSDK.getInstance().getMapList(mlist);
        //定义Maker坐标点


//构建Marker图标
        deviceInfos = new ArrayList<>();
        WMVlinkerSDK.getInstance().getDevInfoList(deviceInfos);
        markerDevInfoMap = new HashMap<>();
        for (int i = 0; i < mlist.size(); i++) {
            if (deviceInfos != null) {
                for (WMDeviceInfo info : deviceInfos) {
                    if (info.getChannelArr() != null) {
                        for (WMChannelInfo channelInfo : info.getChannelArr()) {
                            if (info.getDevId() == mlist.get(i).getDevId() && channelInfo.getChannelId() == mlist.get(i).getChannelId()) {
                                BitmapDescriptor bdA = null;
                                if (info.getStatus() == 0) {
                                    bdA = BitmapDescriptorFactory.fromResource(R.mipmap.etcmap_devoffline);
                                } else {
                                    //etcmap_devonline
                                    bdA = BitmapDescriptorFactory.fromResource(R.mipmap.etcmap_devonline);
                                }
                                LatLng llA = new LatLng(mlist.get(i).getLatitude(), mlist.get(i).getLongitude());
                                MarkerOptions ooA = new MarkerOptions().position(llA).icon(bdA).zIndex(9).draggable(true);
                                Marker marker = (Marker) mBaiduMap.addOverlay(ooA);
                                DevInfo devInfo = new DevInfo();
                                devInfo.setChannelInfo(channelInfo);
                                devInfo.setDeviceInfo(info);
                                devInfo.setMapNodeInfo(mlist.get(i));
                                markerDevInfoMap.put(marker, devInfo);
                            }
                        }
                    }
                }
            }
        }


        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker mar) {
                devInfo = markerDevInfoMap.get(mar);
                map_dev_name.setText(devInfo.getDeviceInfo().getDevName() + "/通道" + devInfo.getChannelInfo().getChannelId());
                if (hasCodeMark != null && hasBdA != null) {
                    hasCodeMark.setIcon(hasBdA);
                }
                hasCodeMark = mar;
                hasBdA = mar.getIcon();
                BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.mipmap.etcmap_devselect);
                mar.setIcon(bdA);
                map_dev_info_bg.setVisibility(View.GONE);
                TranslateAnimation animation = new TranslateAnimation(0, 0, 100, 0);
                animation.setDuration(300);
                map_dev_info_bg.setAnimation(animation);
                map_dev_info_bg.setVisibility(View.VISIBLE);
                return false;
            }
        });
        toLocation();
        searchDev();
    }


    private  void searchDev()
    {
        search_dev_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("insert","==========");
                WMDeviceInfo devInfo=null;
                if(s!=null&&s.length()!=0&&deviceInfos!=null&&deviceInfos.size()!=0)
                {
                    for(WMDeviceInfo dev:deviceInfos)
                    {
                        if(s.toString().equals(dev.getDevName()))
                        {
                            devInfo=dev;
                            break;
                        }
                    }
                }
                if(devInfo!=null&&devInfo.getChannelArr()!=null&&mlist!=null&&mlist.size()!=0)
                {
                    for(WMChannelInfo channelInfo:devInfo.getChannelArr())
                    {
                        for(WMMapNodeInfo info:mlist)
                        {
                            if(info.getDevId()==devInfo.getDevId()&&info.getChannelId()==channelInfo.getChannelId())
                            {
                                LatLng ll = new LatLng(info.getLatitude(), info.getLongitude());
                                MapStatus.Builder builder = new MapStatus.Builder();
                                builder.target(ll).zoom(18.0f);
                                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                                break;
                            }
                        }
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null)
            mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null)
            mMapView.onDestroy();
    }

    @OnClick(R.id.return_location)
    public void returnLocation() {
        isFirstLoc = true;
        toLocation();
    }

    @OnClick(R.id.map_dev_info_bg)
    public void startPlay() {
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        intent.putExtra("devinfo", devInfo);
        startActivity(intent);
    }

    @OnClick(R.id.etcmap_add)
    public void add() {
        zoom++;
        if(zoom>21f)
        {
            zoom=21f;
        }
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(zoom).build()));
    }

    @OnClick(R.id.etcmap_subtraction)
    public void subtraction() {

        zoom--;
        if(zoom<5f)
        {
            zoom=5f;
        }
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(zoom).build()));
    }

    public void setLogcation(BDLocation bdLocation) {
        this.bdLocation = bdLocation;
        toLocation();
    }

    private void toLocation() {
        if (bdLocation == null || mBaiduMap == null) {
            return;
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100)
                .latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude())
                .build();
        mBaiduMap.setMyLocationData(locData);
        if (bdLocation != null && isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }


}
