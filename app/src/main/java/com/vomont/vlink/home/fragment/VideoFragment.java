package com.vomont.vlink.home.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlink.comm.ACaCheContrast;
import com.vomont.vlink.home.fragment.adapter.DevAdapter;
import com.vomont.vlink.util.ACache;
import com.vomont.vlink.util.VlinkRealPlayUtil;
import com.vomont.vlinkersdk.DevStatusCallBack;
import com.vomont.vlinkersdk.WMDeviceGroup;
import com.vomont.vlinkersdk.WMDeviceInfo;
import com.vomont.vlinkersdk.WMVlinkerSDK;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/10/30 0030.
 */

public class VideoFragment extends Fragment implements View.OnClickListener {

    //显示碎片
    @BindView(R.id.item_fragment_video)
    FrameLayout item_fragment_video;
    //实时预览
    @BindView(R.id.radio_realplay)
    RadioButton radio_realplay;
    //回放
    @BindView(R.id.radio_playback)
    RadioButton radio_playback;
    //设备回放
    RadioButton video_back_client;
    //云端回放
    RadioButton video_back_cloud;
    //设备列表
    ExpandableListView dev_list;
    //是否显示回放的按钮
    LinearLayout video_back_button_bg;
    //开始播放
    Button video_start_play;
    //选择设备
    @BindView(R.id.select_dev_bg)
    LinearLayout select_dev_bg;
    //选择设备列表的小箭头
    @BindView(R.id.select_dev_img)
    ImageView select_dev_img;
    //显示当前设备
    @BindView(R.id.seleted_dev_name)
    TextView seleted_dev_name;
    //头部菜单
    @BindView(R.id.video_top)
    RelativeLayout video_top;

    private FragmentManager fragmentManager;

    private FragmentTransaction fragmentTransaction;
    //是否是第一页
    private boolean isIndex = true;

    private PopupWindow mPopupWindow;

    private List<WMDeviceInfo> deviceInfos;

    private List<WMDeviceGroup> groups;

    private VlinkRealPlayUtil util;

    private DevAdapter adapter;

    private HashMap<WMDeviceGroup, WMDeviceInfo> map;

    private Unbinder unbinder;

    private int groupP = -1;

    private int childP = -1;

    private int lastP = -1;
    public static    int STATE_PLAY_BACK_TYPE=1;
    private ACache aCache;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        unbinder = ButterKnife.bind(this, view);
        initPopupWindow();
        initFragment();
        deviceInfos=null;
        groups=null;
        ItemRealFragment.getInstance().clearData();
        ItemBackFragment.getInstence().clearData();
        initData();
        return view;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        deviceInfos = new ArrayList<>();
        util = new VlinkRealPlayUtil(getActivity());
        groups = new ArrayList<>();
        map = new HashMap<>();
        aCache=ACache.get(getActivity());
        //获取设备列表
        util.getDevList(deviceInfos, groups);
        for (WMDeviceGroup wmDeviceGroup : groups) {
            List<WMDeviceInfo> deviceInfoList = new ArrayList<>();
            for (WMDeviceInfo info : deviceInfos) {
                if (wmDeviceGroup.getGroupId() == info.getDevGroupId()) {
                    deviceInfoList.add(info);
                }
            }
            wmDeviceGroup.setWmDeviceInfoList(deviceInfoList);
        }
        aCache.put(ACaCheContrast.dev,(Serializable) groups);
        aCache.put(ACaCheContrast.devItem,(Serializable)deviceInfos);
        util.setDevStatusCallBack(new DevStatusCallBack() {
            @Override
            public void fDevStatusCallBack(int iDeviceId, int iStatus) {
                for (WMDeviceGroup wmDeviceGroup : groups) {
                    for (WMDeviceInfo info : wmDeviceGroup.getWmDeviceInfoList()) {
                        if (iDeviceId == info.getDevId()) {
                            info.setStatus(iStatus);
                        }
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                aCache.put(ACaCheContrast.dev,(Serializable) groups);
            }
        });
        adapter = new DevAdapter(getActivity());
        adapter.setData(groups);
        adapter.setOnDevItemClickListener(new DevAdapter.OnDevClickListener() {
            @Override
            public void OnClick(int groupPosition, int childPosition, int lastPosition) {
                groupP = groupPosition;
                childP = childPosition;
                lastP = lastPosition;
                String name = "";
                name=groups.get(groupPosition).getGroupName();
                name =name+"/"+ groups.get(groupPosition).getWmDeviceInfoList().get(childPosition).getDevName();
                name = name + "/" + "通道" + groups.get(groupPosition).getWmDeviceInfoList().get(childPosition).getChannelArr()[lastPosition].getChannelId();
                seleted_dev_name.setText(name);
            }
        });
        dev_list.setAdapter(adapter);

        ItemRealFragment.getInstance().setOnAddDevClick(new ItemRealFragment.OnClickListener() {
            @Override
            public void onClick() {
                if (Build.VERSION.SDK_INT != 24) {
                    //只有24这个版本有问题，好像是源码的问题
                    mPopupWindow.showAsDropDown(select_dev_bg);
                } else {
                    //7.0 showAsDropDown没卵子用 得这么写
                    int[] location = new int[2];
                    select_dev_bg.getLocationOnScreen(location);
                    int x = location[0];
                    int y = location[1];
                    mPopupWindow.showAtLocation(select_dev_bg, Gravity.NO_GRAVITY, 0, y + select_dev_bg.getHeight());
                }
                setViewRotation(true);
            }
        });

        ItemBackFragment.getInstence().setPlayBackAddListener(new ItemBackFragment.OnAddListener() {
            @Override
            public void OnClick() {
                if (Build.VERSION.SDK_INT != 24) {
                    mPopupWindow.showAsDropDown(select_dev_bg);
                } else {
                    //7.0以上 showAsDropDown没卵子用 得这么写
                    int[] location = new int[2];
                    select_dev_bg.getLocationOnScreen(location);
                    int x = location[0];
                    int y = location[1];
                    mPopupWindow.showAtLocation(select_dev_bg, Gravity.NO_GRAVITY, 0, y + select_dev_bg.getHeight());
                }
                setViewRotation(true);
            }
        });
    }

    /**
     * 初始化framgent
     */
    private void initFragment() {
        // 初始化 默认第一个页面
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.item_fragment_video, ItemRealFragment.getInstance());
        fragmentTransaction.commit();
        video_back_button_bg.setVisibility(View.GONE);
    }

    @OnClick({R.id.radio_realplay, R.id.radio_playback, R.id.select_dev_bg})
    public void radioOnClick(View view) {
        switch (view.getId()) {
            case R.id.radio_playback:
                //切换到回放界面
                if (isIndex) {
                    //只有在实时预览界面点击才能有效
                    getFragmentManager().beginTransaction()
//                            .setCustomAnimations(R.animator.enter, R.animator.exit, R.animator.enter_re, R.animator.exit_re)
                            .replace(R.id.item_fragment_video, ItemBackFragment.getInstence())
                            .addToBackStack(null)
                            .commit();
                    isIndex = false;
                    video_back_button_bg.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.radio_realplay:
                //切换到实时预览界面
                if (!isIndex) {
                    //只有才回放界面点击才能有用
                    getFragmentManager().popBackStack();
                    isIndex = true;
                    video_back_button_bg.setVisibility(View.GONE);
                }
                break;
            case R.id.select_dev_bg:
                //显示选择设备的界面
                if (Build.VERSION.SDK_INT != 24) {
                    mPopupWindow.showAsDropDown(select_dev_bg);
                } else {
                    //7.0以上 showAsDropDown没卵子用 得这么写
                    int[] location = new int[2];
                    select_dev_bg.getLocationOnScreen(location);
                    int x = location[0];
                    int y = location[1];
                    mPopupWindow.showAtLocation(select_dev_bg, Gravity.NO_GRAVITY, 0, y + select_dev_bg.getHeight());
                }
                setViewRotation(true);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化下拉菜单
     */
    private void initPopupWindow() {
        View popupView = getActivity().getLayoutInflater().inflate(R.layout.layout_popupwindow, null);
        video_back_client = popupView.findViewById(R.id.video_back_client);
        video_back_cloud = popupView.findViewById(R.id.video_back_cloud);
        dev_list = popupView.findViewById(R.id.dev_list);
        video_back_button_bg = popupView.findViewById(R.id.video_back_button_bg);
        video_start_play = popupView.findViewById(R.id.video_start_play);
        video_back_client.setOnClickListener(this);
        video_back_cloud.setOnClickListener(this);
        video_start_play.setOnClickListener(this);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.getContentView().setFocusable(true);
        mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setViewRotation(false);
            }
        });
        video_back_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                STATE_PLAY_BACK_TYPE=0;
            }
        });
        video_back_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                STATE_PLAY_BACK_TYPE=1;
            }
        });
    }

    /**
     * 箭头的显示样式
     *
     * @param isRotation
     */
    private void setViewRotation(boolean isRotation) {
        if (isRotation) {
            select_dev_img.setRotation(180);
        } else {
            select_dev_img.setRotation(360);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_back_client:
                //设备回放
                break;
            case R.id.video_back_cloud:
                //云端回放
                break;
            case R.id.video_start_play:
                //开始播放视频
                mPopupWindow.dismiss();
                if (groupP != -1 && childP != -1) {
                    ItemRealFragment.getInstance().play(groups.get(groupP).getWmDeviceInfoList().get(childP), groups.get(groupP).getWmDeviceInfoList().get(childP).getChannelArr()[lastP]);
                    ItemBackFragment.getInstence().setDev(groups.get(groupP).getWmDeviceInfoList().get(childP), groups.get(groupP).getWmDeviceInfoList().get(childP).getChannelArr()[lastP]);
                    if (isIndex) {
                        //如果是首页 就播放实时预览的视频
                        ItemRealFragment.getInstance().startPlay();
                    } else {
                        ItemBackFragment.getInstence().initData();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            ItemRealFragment.getInstance().onStop();
            ItemBackFragment.getInstence().onStop();
        } else {
            ItemRealFragment.getInstance().onResume();
            ItemBackFragment.getInstence().onResume();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            select_dev_bg.setVisibility(View.GONE);
            video_top.setVisibility(View.GONE);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            select_dev_bg.setVisibility(View.VISIBLE);
            video_top.setVisibility(View.VISIBLE);
        }
    }
}
