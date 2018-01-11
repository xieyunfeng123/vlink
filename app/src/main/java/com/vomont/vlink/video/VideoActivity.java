package com.vomont.vlink.video;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
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
import com.vomont.vlink.base.BaseActivity;
import com.vomont.vlink.comm.ACaCheContrast;
import com.vomont.vlink.home.fragment.DevInfo;
import com.vomont.vlink.home.fragment.adapter.DevAdapter;
import com.vomont.vlink.util.ACache;
import com.vomont.vlink.util.VlinkRealPlayUtil;
import com.vomont.vlink.video.fragment.BackFragment;
import com.vomont.vlink.video.fragment.RealFragment;
import com.vomont.vlinkersdk.WMDeviceGroup;
import com.vomont.vlinkersdk.WMDeviceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/10 0010.
 */

public class VideoActivity extends BaseActivity   implements View.OnClickListener{

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

    private HashMap<WMDeviceGroup,WMDeviceInfo> map;


    private int groupP = -1;

    private int childP = -1;

    private int lastP = -1;

    @BindView(R.id.video_go_back)
    public  ImageView video_go_back;

    private ACache aCache;

    private boolean isChange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        ButterKnife.bind(this);
        initPopupWindow();
        initFragment();
        initData();
        select_dev_img.setVisibility(View.GONE);
        video_go_back.setVisibility(View.VISIBLE);
        DevInfo devInfo= (DevInfo) getIntent().getSerializableExtra("devinfo");
        WMDeviceGroup wmDeviceGroup= (WMDeviceGroup) getIntent().getSerializableExtra("groupinfo");
        String name = "";
        if(wmDeviceGroup!=null)
        {
            name=wmDeviceGroup.getGroupName()+"/"+devInfo.getDeviceInfo().getDevName();
        }
        else
        {
            name = devInfo.getDeviceInfo().getDevName();
        }
        name = name + "/" + "通道"+devInfo.getChannelInfo().getChannelId();
        seleted_dev_name.setText(name);
        RealFragment.getInstance().play(devInfo.getDeviceInfo(),devInfo.getChannelInfo());
        BackFragment.getInstence().setDev(devInfo.getDeviceInfo(),devInfo.getChannelInfo());
        if (isIndex) {
            //如果是首页 就播放实时预览的视频
            RealFragment.getInstance().startPlay();
        } else {
            BackFragment.getInstence().initData();
        }

    }


    /**
     * 初始化数据
     */
    private void initData() {
        deviceInfos = new ArrayList<>();
        util = new VlinkRealPlayUtil(this);
        groups = new ArrayList<>();
        map=new HashMap<>();
        aCache=ACache.get(this);
        //获取设备列表
//        util.getDevList(deviceInfos, groups);
        groups= (List<WMDeviceGroup>) aCache.getAsObject(ACaCheContrast.dev);

//        for(WMDeviceGroup wmDeviceGroup:groups)
//        {
//            List<WMDeviceInfo> deviceInfoList=new ArrayList<>();
//            for(WMDeviceInfo info:deviceInfos)
//            {
//                if(wmDeviceGroup.getGroupId()==info.getDevGroupId())
//                {
//                    deviceInfoList.add(info);
//                }
//            }
//            wmDeviceGroup.setWmDeviceInfoList(deviceInfoList);
//        }

        adapter = new DevAdapter(this);
        adapter.setData(groups);
        adapter.setOnDevItemClickListener(new DevAdapter.OnDevClickListener() {
            @Override
            public void OnClick(int groupPosition, int childPosition, int lastPosition) {
                groupP = groupPosition;
                childP = childPosition;
                lastP = lastPosition;
                String name = "";
                name = groups.get(groupPosition).getWmDeviceInfoList().get(childPosition).getDevName();
                name = name + "/" + "通道"+groups.get(groupPosition).getWmDeviceInfoList().get(childPosition).getChannelArr()[lastPosition].getChannelId();
                seleted_dev_name.setText(name);
            }
        });
        dev_list.setAdapter(adapter);
    }

    /**
     * 初始化framgent
     */
    private void initFragment() {
        // 初始化 默认第一个页面
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.item_fragment_video, RealFragment.getInstance());
        fragmentTransaction.commit();
        video_back_button_bg.setVisibility(View.GONE);
    }

    @OnClick({R.id.radio_realplay, R.id.radio_playback, R.id.select_dev_bg,R.id.video_go_back})
    public void radioOnClick(View view) {
        switch (view.getId()) {
            case R.id.radio_playback:
                //切换到回放界面
                if (isIndex) {
                    //只有在实时预览界面点击才能有效
                    getFragmentManager().beginTransaction()
                            .replace(R.id.item_fragment_video, BackFragment.getInstence())
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
//                //显示选择设备的界面
//                if (Build.VERSION.SDK_INT < 24) {
//                    mPopupWindow.showAsDropDown(select_dev_bg);
//                } else {
//                    //7.0以上 showAsDropDown没卵子用 得这么写
//                    int[] location = new int[2];
//                    select_dev_bg.getLocationOnScreen(location);
//                    int x = location[0];
//                    int y = location[1];
//                    mPopupWindow.showAtLocation(select_dev_bg, Gravity.NO_GRAVITY, 0, y + select_dev_bg.getHeight());
//                }

//                //显示选择设备的界面
//                mPopupWindow.showAsDropDown(select_dev_bg);
//                setViewRotation(true);
                break;
            case R.id.video_go_back:
                finish();
                break;
            default:
                break;
        }
    }


    /**
     * 初始化下拉菜单
     */
    private void initPopupWindow() {

        View popupView = getLayoutInflater().inflate(R.layout.layout_popupwindow, null);
        video_back_client = popupView.findViewById(R.id.video_back_client);
        video_back_cloud = popupView.findViewById(R.id.video_back_cloud);
        dev_list = popupView.findViewById(R.id.dev_list);
        video_back_button_bg = popupView.findViewById(R.id.video_back_button_bg);
        video_start_play = popupView.findViewById(R.id.video_start_play);
        video_back_client.setOnClickListener(this);
        video_back_cloud.setOnClickListener(this);
        video_start_play.setOnClickListener(this);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
//        mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
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
                if(groups!=null&&groups.get(groupP).getWmDeviceInfoList()!=null) {
                    RealFragment.getInstance().play(groups.get(groupP).getWmDeviceInfoList().get(childP), groups.get(groupP).getWmDeviceInfoList().get(childP).getChannelArr()[lastP]);
                    BackFragment.getInstence().setDev(groups.get(groupP).getWmDeviceInfoList().get(childP), groups.get(groupP).getWmDeviceInfoList().get(childP).getChannelArr()[lastP]);
                }
                    if (isIndex) {
                    //如果是首页 就播放实时预览的视频
                    RealFragment.getInstance().startPlay();
                } else {
                   BackFragment.getInstence().initData();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            select_dev_bg.setVisibility(View.GONE);
            video_top.setVisibility(View.GONE);
            isChange = true;
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            select_dev_bg.setVisibility(View.VISIBLE);
            video_top.setVisibility(View.VISIBLE);
            isChange = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (isChange) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
              finish();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
