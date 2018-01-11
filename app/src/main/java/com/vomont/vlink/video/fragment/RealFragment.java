package com.vomont.vlink.video.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vomont.vlink.R;
import com.vomont.vlink.bean.UserInfo;
import com.vomont.vlink.comm.ACaCheContrast;
import com.vomont.vlink.home.fragment.adapter.ItemImageAdapter;
import com.vomont.vlink.img.ImagDetailActivity;
import com.vomont.vlink.util.ACache;
import com.vomont.vlink.util.AnimationUtil;
import com.vomont.vlink.util.CommonUtil;
import com.vomont.vlink.util.DensityUtil;
import com.vomont.vlink.util.VlinkRealPlayUtil;
import com.vomont.vlinkersdk.Constants;
import com.vomont.vlinkersdk.WMChannelInfo;
import com.vomont.vlinkersdk.WMDeviceInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class RealFragment extends Fragment implements View.OnTouchListener {
    //全屏
    @BindView(R.id.video_change)
    ImageView video_change;

    //播放容器
    @BindView(R.id.surfaceview_layout)
    RelativeLayout surfaceview_layout;
    //播放菜单条
    @BindView(R.id.play_statue_line)
    RelativeLayout play_statue_line;
    //是否暂停
    @BindView(R.id.video_control_play)
    ImageView video_control_play;

    @BindView(R.id.realplay_scroll_bg)
    ScrollView realplay_scroll_bg;
    //播放容器的父布局
    @BindView(R.id.video_all)
    RelativeLayout video_all;

    //加载的效果
    @BindView(R.id.real_progressbar)
    ProgressBar real_progressbar;
    //标高清切换的view
    @BindView(R.id.stream_type)
    TextView stream_type;

    LinearLayout control_stream;
    //高清
    TextView stream_type_sub;
    //标清
    TextView stream_type_main;
    //抓拍
    @BindView(R.id.video_take_photo)
    ImageView video_take_photo;
    //录像
    @BindView(R.id.video_snapshot)
    ImageView video_snapshot;
    //抓拍
    @BindView(R.id.control_video_camera)
    LinearLayout control_video_camera;
    //录像
    @BindView(R.id.control_video_video)
    LinearLayout control_video_video;
    //云台
    @BindView(R.id.control_video_yunnan)
    LinearLayout control_video_yunnan;
    //没有图片是显示的view
    @BindView(R.id.empty_img)
    TextView empty_img;

    @BindView(R.id.show_img_grid)
    GridView show_img_grid;

//    //添加图片的view
//    @BindView(R.id.phone_img)
//    LinearLayout phone_img;

    @BindView(R.id.contrl_video_img)
    ImageView contrl_video_img;

    @BindView(R.id.real_add_dev)
    ImageView real_add_dev;

    @BindView(R.id.video_voice)
    ImageView video_voice;

    private ImageView contrl_yuntai;

    private ImageView finish_yuntai;
    //是否全屏
    private boolean isChange;

    private Unbinder unbinder;

    private VlinkRealPlayUtil util;

    //判断当前界面是否可以点击操作
    private boolean canOnClick;

    private Handler handler;

    //是否在播放
    private boolean isPlay;

    public static RealFragment instance;
    //播放设备的信息
    private WMDeviceInfo info;
    //播放通道的信息
    WMChannelInfo wmChannelInfo;

    //屏幕的宽度 和播放容器的高度
    private int width, height;


    public PopupWindow mPopupWindow;
    public PopupWindow mYunWindow;

    View popupView;
    //1是标清 0是高清
    private static int STREAM_TYPE = 1;

    private ACache aCache;

    UserInfo userLogin;

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/vlinker";

    List<String> mlist_name;

    protected static final float FLIP_DISTANCE = 50;

    private GestureDetector mGestureDetector;

    private boolean isOpenVoice;
    private boolean isVideotape;

    private ItemImageAdapter imageAdapter;

    public static RealFragment getInstance() {
        if (instance == null) {
            instance = new RealFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_real, container, false);
        unbinder = ButterKnife.bind(this, view);
        real_add_dev.setVisibility(View.GONE);
        imageAdapter = new ItemImageAdapter(getActivity());
        util = new VlinkRealPlayUtil(getActivity());
        aCache = ACache.get(getActivity());
        userLogin = (UserInfo) aCache.getAsObject(ACaCheContrast.user);
        util.setPath(path + "/" + userLogin.getNum() + "/img");
        show_img_grid.setAdapter(imageAdapter);
        addPopu();
        initHandler();
        initDev();
        getViewwidth();
        addPhoneToView();
//        addYuntai();
        return view;
    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 10:
                        canOnClick = true;
                        break;
                    default:
                        break;
                }
            }
        };
    }


    private void initDev() {

        show_img_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ImagDetailActivity.class);
                intent.putExtra("pic", (Serializable) mlist_name);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        util.addPlayListener(new VlinkRealPlayUtil.PlayListener() {
            @Override
            public void onPlaySuccess(int playid) {
                if (getActivity() != null && real_progressbar != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            real_progressbar.setVisibility(View.GONE);
                            video_control_play.setImageResource(R.mipmap.real_stop);
                            isPlay = true;
                        }
                    });
                }
            }

            @Override
            public void onPlayFailed() {
                if (getActivity() != null && real_progressbar != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            real_progressbar.setVisibility(View.GONE);
                            video_control_play.setImageResource(R.mipmap.real_start);
                            isPlay = false;
                        }
                    });
                }
            }

            @Override
            public void onPlayConnectTimeOut() {
                if (getActivity() != null && real_progressbar != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            real_progressbar.setVisibility(View.GONE);
                            isPlay = false;
                            video_control_play.setImageResource(R.mipmap.real_start);
                        }
                    });
                }
            }
        });
    }


    public void addPopu() {
        popupView = getActivity().getLayoutInflater().inflate(R.layout.layout_pp_video, null);
        control_stream = popupView.findViewById(R.id.control_stream);
        stream_type_sub = popupView.findViewById(R.id.stream_type_sub);
        stream_type_main = popupView.findViewById(R.id.stream_type_main);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px(getActivity(), 70), true);
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
        stream_type_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info != null) {
                    real_progressbar.setVisibility(View.VISIBLE);
                    util.stopIng();

                    real_progressbar.postDelayed(new Runnable() {
                        public void run() {
                            if (info != null) {
                                util.startPlay(info.getDevType(), surfaceview_layout, info.getDevId(), wmChannelInfo.getChannelId(), 1);
                            }
                        }
                    }, 300);
                }
                STREAM_TYPE = 1;
                stream_type.setText("标清");
                mPopupWindow.dismiss();
            }
        });
        stream_type_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info != null) {
                    real_progressbar.setVisibility(View.VISIBLE);
                    util.stopIng();
                    real_progressbar.postDelayed(new Runnable() {
                        public void run() {
                            util.startPlay(info.getDevType(), surfaceview_layout, info.getDevId(), wmChannelInfo.getChannelId(), 0);
                        }
                    }, 300);
                }
                STREAM_TYPE = 0;
                stream_type.setText("高清");
                mPopupWindow.dismiss();
            }
        });
    }


    public void addYuntai() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_yuntai, null);
        contrl_yuntai = view.findViewById(R.id.contrl_yuntai);
        finish_yuntai = view.findViewById(R.id.finish_yuntai);
        mYunWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getActivity(), 100) + video_all.getBottom(), true);
        mYunWindow.setTouchable(true);
        mYunWindow.setOutsideTouchable(false);
        mYunWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mYunWindow.getContentView().setFocusableInTouchMode(true);
        mYunWindow.getContentView().setFocusable(true);
        mYunWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mYunWindow != null && mYunWindow.isShowing()) {
                        mYunWindow.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
        contrl_yuntai.setOnTouchListener(this);
        contrl_yuntai.setFocusable(true);
        contrl_yuntai.setClickable(true);
        contrl_yuntai.setLongClickable(true);
        mGestureDetector = new GestureDetector(new RealFragment.GestureListener());
        finish_yuntai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYunWindow.dismiss();
            }
        });


    }

    /**
     * 播放视频
     *
     * @param info
     */
    public void play(final WMDeviceInfo info, final WMChannelInfo wmChannelInfo) {
        this.info = info;
        this.wmChannelInfo = wmChannelInfo;
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        initPlay();
        if (real_progressbar != null && info != null) {
            if (info.getStatus() == Constants.DeviceStatus_Online) {
                real_progressbar.setVisibility(View.VISIBLE);
                video_control_play.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        util.startPlay(info.getDevType(), surfaceview_layout, info.getDevId(), wmChannelInfo.getChannelId(), STREAM_TYPE);
                    }
                }, 300);
            } else {
                Toast.makeText(getActivity(), "设备不在线", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initPlay() {
        if (video_control_play != null) {
            util.stopIng();
            video_control_play.setImageResource(R.mipmap.real_start);
        }
        isPlay = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isPlay && info != null) {
            startPlay();
        }
        addPhoneToView();
    }

    @Override
    public void onStop() {
        super.onStop();
        initPlay();
    }

    @OnClick({R.id.surfaceview_layout, R.id.video_control_play, R.id.stream_type, R.id.video_take_photo, R.id.control_video_camera, R.id.control_video_yunnan, R.id.control_video_video, R.id.video_voice})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.surfaceview_layout:
                //是否显示播放器菜单
                if (play_statue_line.getVisibility() == View.VISIBLE) {
                    AnimationUtil.topToButtom(play_statue_line);
                    play_statue_line.setVisibility(View.GONE);
                } else {
                    AnimationUtil.buttomTotop(play_statue_line);
                    play_statue_line.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.video_control_play:
                //控制播放或者暂停
                if (!isPlay && info != null) {
                    initPlay();
                    startPlay();
                } else {
                    initPlay();
                }
                break;
            case R.id.stream_type:
                //标高清切换
//                mPopupWindow.showAsDropDown(stream_type, 0, -(DensityUtil.dip2px(getActivity(), 70) + stream_type.getHeight()));
                break;
            case R.id.video_take_photo:
                //抓拍
                if (info != null && wmChannelInfo != null) {
                    util.saveSnapshot(info.getDevGroupId(), info.getDevId(), wmChannelInfo.getChannelId());
                    addPhoneToView();
                }
                break;
            case R.id.control_video_camera:
                //抓拍
                if (info != null && wmChannelInfo != null) {
                    util.saveSnapshot(info.getDevGroupId(), info.getDevId(), wmChannelInfo.getChannelId());
                    addPhoneToView();
                }
                break;
            case R.id.control_video_yunnan:
                //云台
                if (info != null && wmChannelInfo != null) {
                    addYuntai();
                    mYunWindow.showAtLocation(control_video_yunnan, Gravity.BOTTOM, 0, 0);
                }
                break;
            case R.id.control_video_video:
//                recordVideo();
                break;
            case R.id.video_voice:
                util.openVoice(!isOpenVoice);
                isOpenVoice = !isOpenVoice;
                if (isOpenVoice) {
                    video_voice.setImageResource(R.mipmap.real_voice);
                } else {
                    video_voice.setImageResource(R.mipmap.forbid_voice);
                }
                break;
            default:
                break;
        }
    }

    private void recordVideo() {
        if (info != null && wmChannelInfo != null) {
            if (!isVideotape) {
                String name = util.startRecord(info.getDevGroupId(), info.getDevId(), wmChannelInfo.getChannelId());
                if (name != null) {
                    contrl_video_img.setImageResource(R.mipmap.video_tape_stop);
                    isVideotape = true;
                }
            } else {
                contrl_video_img.setImageResource(R.mipmap.button_video);
                isVideotape = false;
            }
        }
    }


    @OnClick(R.id.video_change)
    public void changeConfig() {
        //控制是否全屏
        if (isChange) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isChange = !isChange;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land
            realplay_scroll_bg.setVisibility(View.GONE);
            CommonUtil.setFullScreen(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, width);
            video_all.setLayoutParams(params);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port
            CommonUtil.cancelFullScreen(getActivity());
            realplay_scroll_bg.setVisibility(View.VISIBLE);
            realplay_scroll_bg.smoothScrollTo(0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            video_all.setLayoutParams(params);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @SuppressWarnings("deprecation")
    private void getViewwidth() {
        WindowManager wm = getActivity().getWindowManager();
        width = wm.getDefaultDisplay().getWidth();

        int view_width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        int view_height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        video_all.measure(view_width, view_height);

        height = video_all.getMeasuredHeight();

    }

    /**
     * 显示图片列表
     */
    public void addPhoneToView() {
        if (util.getImageName() != null && util.getImageName().size() != 0) {
            mlist_name = util.getImageName();
//            Collections.reverse(mlist_name);
            empty_img.setVisibility(View.GONE);
            show_img_grid.setVisibility(View.VISIBLE);
        } else {
            empty_img.setVisibility(View.VISIBLE);
            show_img_grid.setVisibility(View.GONE);
            mlist_name = new ArrayList<String>();
            return;
        }
        if (mlist_name.size() != 0) {
            imageAdapter.setData(mlist_name);
            imageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


    class GestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > FLIP_DISTANCE) {
                util.ptzControlStart(Constants.WMPtzCommand_Left);
            }
            if (e2.getX() - e1.getX() > FLIP_DISTANCE) {
                util.ptzControlStart(Constants.WMPtzCommand_Right);
            }
            if (e1.getY() - e2.getY() > FLIP_DISTANCE) {
                util.ptzControlStart(Constants.WMPtzCommand_Up);

            }
            if (e2.getY() - e1.getY() > FLIP_DISTANCE) {
                util.ptzControlStart(Constants.WMPtzCommand_Down);

            }
            return false;
        }
    }
}
