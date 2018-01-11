package com.vomont.vlink.video.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.vomont.vlink.R;
import com.vomont.vlink.bean.UserInfo;
import com.vomont.vlink.comm.ACaCheContrast;
import com.vomont.vlink.home.fragment.ItemBackFragment;
import com.vomont.vlink.home.fragment.VideoFragment;
import com.vomont.vlink.util.ACache;
import com.vomont.vlink.util.CommonUtil;
import com.vomont.vlink.util.PlayBackUtil;
import com.vomont.vlink.view.KCalendar;
import com.vomont.vlink.view.PlayBackSeekBar;
import com.vomont.vlink.view.ProgressDialog;
import com.vomont.vlinkersdk.SearchFrontEndCallBack;
import com.vomont.vlinkersdk.StreamPlayer;
import com.vomont.vlinkersdk.WMChannelInfo;
import com.vomont.vlinkersdk.WMDeviceInfo;
import com.vomont.vlinkersdk.WMFileSearchCondition;
import com.vomont.vlinkersdk.WMFrontEndFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class BackFragment extends Fragment {

    @BindView(R.id.date_back_rl)
    LinearLayout date_back_rl;

    @BindView(R.id.ll_popup)
    LinearLayout ll_popup;


    @BindView(R.id.play_back_control_rl)
    RelativeLayout play_back_control_rl;

    // 播放窗口
    @BindView(R.id.playback_layout)
    RelativeLayout playback_layout;
    @BindView(R.id.play_back_all)
    RelativeLayout play_back_all;

    // 日期时间
    @BindView(R.id.popupwindow_calendar_month)
    TextView popupwindow_calendar_month;

    @BindView(R.id.popupwindow_calendar_last_month)
    RelativeLayout popupwindow_calendar_last_month;

    @BindView(R.id.popupwindow_calendar_next_month)
    RelativeLayout popupwindow_calendar_next_month;
    // 1天 1小时 5分钟时间切换的button
    @BindView(R.id.one_day)
    Button one_day;
    @BindView(R.id.one_hour)
    Button one_hour;
    @BindView(R.id.five_min)
    Button five_min;
    // 日期控件
    @BindView(R.id.popupwindow_calendar)
    KCalendar calendar;

    @BindView(R.id.play_back_scroll)
    ScrollView play_back_scroll;

    @BindView(R.id.play_back_stop)
    ImageView play_back_stop;
    // 日期
    private String date = "";

    private int on_day;

    @BindView(R.id.playbackbar)
    PlayBackSeekBar playbackbar;

    @BindView(R.id.playback_progressbar)
    ProgressBar playback_progressbar;


    @BindView(R.id.play_back_change)
    ImageView play_back_change;


    @BindView(R.id.play_back_zhua)
    ImageView play_back_zhua;


    public static BackFragment instence;

    private WMDeviceInfo info;

    private WMChannelInfo channelInfo;

    // 回放接口
    private PlayBackUtil playBackUtil;

    // 回放的文件
    private List<WMFrontEndFile> mlFileInfos;

    // 回放的类型
    private int type;

    // 回放的结束时间
    long endTime = 0;

    // 回放的开始时间
    long startTime = 0;

    // 时间轴开始时间
    long seekStartTime = 0;

    // 时间轴结束时间
    long seekEndTime = 0;

    private Handler handler;
    private int seekBar_type = 0;

    // 播放的pos点
    private int nPos;

    // 播放的片段
    private int index;

    // 流媒体播放器
    private StreamPlayer streamPlayer;

    // 定时器
    private Timer timer;

    private MyTimeTask myTimeTask;

    private Dialog dialog;

    // 判断是否滑动到可播放的区域
    private boolean isScrollToPlay = false;

    // 当前是否在播放
    private boolean isPlayIng = false;

    private boolean isChange = false;


    //屏幕的宽度 和播放容器的高度
    private int width, height;

    private ACache aCache;

    UserInfo userLogin;

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/vlinker";

    @BindView(R.id.playback_add_dev)
    ImageView playback_add_dev;

    private boolean isCallBackList;


    private int sendTime;

    OnAddListener onAddListener;

    public static BackFragment getInstence() {
        if (instence == null) {
            instence = new BackFragment();
        }
        return instence;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_playback, container, false);
        ButterKnife.bind(this, view);
        playback_add_dev.setVisibility(View.GONE);
        initView();
        initData();
        return view;
    }

    public void setDev(WMDeviceInfo info, WMChannelInfo channelInfo) {
        this.info = info;
        this.channelInfo = channelInfo;
    }

    public void clearData() {
        info = null;
        channelInfo = null;
    }

    private void initView() {
        playBackUtil = new PlayBackUtil(getActivity());
        aCache = ACache.get(getActivity());
        userLogin = (UserInfo) aCache.getAsObject(ACaCheContrast.user);
        playBackUtil.setPath(path + "/" + userLogin.getNum() + "/img/");
        playBackUtil.setPlayBackListener(new PlayBackUtil.PlayBackListener() {
            @Override
            public void onSucess() {
                handler.sendEmptyMessage(30);
            }

            @Override
            public void onFail() {
                handler.sendEmptyMessage(40);
            }

            @Override
            public void onConnectTimeOut() {
                handler.sendEmptyMessage(50);
            }
        });
        playbackbar.setScrollview(play_back_scroll);
        playbackbar.setOnScrollListener(new PlayBackSeekBar.OnPlaySeekBarListener() {
            @Override
            public void onScrolling() {
                stopTimer();
            }

            @Override
            public void onScrolled(long nowTime) {
                if (mlFileInfos != null && mlFileInfos.size() != 0) {
                    for (int i = 0; i < mlFileInfos.size(); i++) {
                        if (nowTime >= mlFileInfos.get(i).getBeginTime() && nowTime <= mlFileInfos.get(i).getEndTime()) {
                            isScrollToPlay = true;
                            isPlayIng = false;
                            if (i == index) {
                                setPos((int) nowTime);
                            } else {
                                index = i;
                                sendTime = (int) (nowTime - mlFileInfos.get(index).getBeginTime());
                                startPlayVideo();
                            }
                            if (seekBar_type != 0 && nowTime*1000 == seekStartTime) {
                                updataSeek(seekBar_type);
                            } else if (seekBar_type != 0 && nowTime*1000== seekEndTime) {
                                updataSeek(seekBar_type);
                            }
                            break;
                        }
                    }
                }
                if (isPlayIng && !isScrollToPlay) {
                    startTimer(0);
                    isScrollToPlay = false;
                }
            }
        });

        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = sFormat.format(new Date());
        mlFileInfos = new ArrayList<WMFrontEndFile>();
        if (null != date) {
            int years = Integer.parseInt(date.substring(0, date.indexOf("-")));
            int month = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
            int day = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1, date.length()));
            popupwindow_calendar_month.setText(years + "/" + time0(month));
            calendar.showCalendar(years, month);
            calendar.setCalendarDayBgColor(date, R.mipmap.new_dot_three);
            try {
                startTime = sFormat.parse(date + " 00:00:00").getTime();
                endTime = startTime + 86400000;
                seekStartTime = startTime;
                seekEndTime = endTime;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // 监听所选中的日期
        calendar.setOnCalendarClickListener(new KCalendar.OnCalendarClickListener() {
            public void onCalendarClick(int row, int col, String dateFormat) {
                int year = Integer.parseInt(dateFormat.substring(0, dateFormat.indexOf("-")));
                int month = Integer.parseInt(dateFormat.substring(dateFormat.indexOf("-") + 1, dateFormat.lastIndexOf("-")));
                if (calendar.getCalendarMonth() - month == 1// 跨年跳转
                        || calendar.getCalendarMonth() - month == -11) {
                    calendar.lastMonth();
                } else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
                        || month - calendar.getCalendarMonth() == -11) {
                    calendar.nextMonth();
                } else {
                    calendar.removeAllBgColor();
                    calendar.setCalendarDayBgColor(dateFormat, R.mipmap.new_dot_three);
                    date = dateFormat;// 最后返回给全局 date
                    on_day = Integer.parseInt(dateFormat.substring(dateFormat.lastIndexOf("-") + 1, dateFormat.length()));
                    popupwindow_calendar_month.setText(year + "/" + time0(month));
                    try {
                        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
                        startTime = sFormat.parse(date + " 00:00:00").getTime();
                        endTime = startTime + 86400000;
                        // 切換日期后 初始化所有数据
                        seekStartTime = startTime;
                        seekEndTime = endTime;
                        index = 0;
                        one_day.setBackgroundResource(R.drawable.time_button_bg);
                        one_day.setTextColor(getResources().getColor(R.color.white));

                        one_hour.setBackgroundResource(R.drawable.time_button_bg_pressed);
                        one_hour.setTextColor(getResources().getColor(R.color.text_color));

                        five_min.setBackgroundResource(R.drawable.time_button_bg_pressed);
                        five_min.setTextColor(getResources().getColor(R.color.text_color));
                        seekBar_type = 0;
                        stopTimer();
                        initData();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // 上月监听按钮
        popupwindow_calendar_last_month.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                calendar.lastMonth();
            }
        });

        // 下月监听按钮
        popupwindow_calendar_next_month.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                calendar.nextMonth();
            }
        });

        calendar.setOnCalendarDateChangedListener(new KCalendar.OnCalendarDateChangedListener() {
            @Override
            public void onCalendarDateChanged(int year, int month) {
                popupwindow_calendar_month.setText(year + "/" + time0(month));
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 100:
                        if (mlFileInfos != null && mlFileInfos.size() != 0) {
                            playbackbar.setTime(seekStartTime, seekEndTime, seekBar_type);
                            playbackbar.setTimeFile(mlFileInfos);
                            if (mlFileInfos.get(index).getBeginTime() < seekStartTime) {
                                sendTime = playBackUtil.frontEndPlayGetPos();
                            }
                            startPlayVideo();
                        } else {
                            // 如果没有数据
                            playbackbar.setTime(startTime, endTime, seekBar_type);
                            if (getActivity() != null) {
                                playback_add_dev.setVisibility(View.GONE);
                            }
                            dialog.dismiss();
                        }

                        break;
                    case 200:
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!isCallBackList) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            }
                        }).start();
                        break;
                    case 20:
                        new Thread(new Runnable() {
                            public void run() {
                                final int now = playBackUtil.frontEndPlayGetPos();
                                if (now != 0) {
                                    sendTime = now;
                                }
                                if(getActivity()==null)
                                {
                                    return;
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (now*1000 == mlFileInfos.get(index).getEndTime()) {
                                            // 如果這一小段播放結束 则判断是否还有下一个片段 如果有就继续播放
                                            if ((index + 1) <= (mlFileInfos.size() - 1)) {
                                                index = index + 1;
                                                sendTime = 0;
                                                startPlayVideo();
                                                // 如果播放的片段的起始时间不在该片段内则更新时间轴的时间
                                                if (mlFileInfos.get(index).getBeginTime() >= seekEndTime) {
                                                    updataSeek(seekBar_type);
                                                }
                                            } else {
                                                // 没有新的片段刷新界面的效果
                                                stopPlayVideo();
                                            }
                                            return;
                                        } else {
                                            // 如果播放的时间超过了时间轴的最右边 则刷新时间轴
                                            if (getPosTime()*1000 >= seekEndTime && getPosTime()*1000 <= endTime) {
                                                updataSeek(seekBar_type);
                                            } else if (getPosTime()*1000 > endTime) {
                                                stopPlayVideo();
                                            } else { // 没超过 就移动滑块
                                                playbackbar.setScroll(getPosTime());
                                            }
                                        }
                                    }
                                });
                            }
                        }).start();
                        break;
                    case 300:
//                        ll_popup.setVisibility(View.VISIBLE);
                        startPlayVideo();
                        break;
                    case 30:
                        isScrollToPlay = false;
                        isPlayIng = true;
                        play_back_stop.setImageResource(R.mipmap.real_stop);
                        startTimer(0);
                        break;
                    case 40:
                        isPlayIng = false;
                        play_back_stop.setImageResource(R.mipmap.real_start);
                        if (getActivity() != null) {
                            playback_add_dev.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "打开失败！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 50:
                        isPlayIng = false;
                        play_back_stop.setImageResource(R.mipmap.real_start);
                        if (getActivity() != null) {
                            playback_add_dev.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "连接超时！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

    }

    /**
     * 初始化数据
     */
    public void initData() {
        getViewWidth();
        if (info != null) {
            mlFileInfos.clear();
            stopPlayVideo();
            dialog = ProgressDialog.createLoadingDialog(getActivity(), "");
            dialog.show();
            new Thread(new Runnable() {
                public void run() {
                    mlFileInfos = new ArrayList<WMFrontEndFile>();
                    WMFileSearchCondition condition = new WMFileSearchCondition();
                    condition.setChannelId(channelInfo.getChannelId());
                    condition.setDevId(info.getDevId());
                    int start = (int) (startTime / 1000);
                    int end = (int) (endTime / 1000);
                    condition.setBeginTime(start);
                    condition.setEndTime(end);
                    condition.setFileType(VideoFragment.STATE_PLAY_BACK_TYPE);
                    int result = playBackUtil.searFile(condition, new SearchFrontEndCallBack() {
                        @Override
                        public void fFrontEndSearchResultCallBack(int result, int iFileCnt, WMFrontEndFile[] objArr) {
                            isCallBackList = true;
                            if (objArr != null && objArr.length != 0) {
                                for (WMFrontEndFile wmFrontEndFile : objArr) {
                                    mlFileInfos.add(wmFrontEndFile);
                                }
                            }
                            Message message = new Message();
                            message.what = 100;
                            handler.sendMessage(message);
                        }
                    });
                    if (result == 0) {
                        Message message = new Message();
                        message.what = 200;
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = 100;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }
    }

    public void setPos(int time) {
        if (mlFileInfos != null && mlFileInfos.size() != 0) {
            int result = playBackUtil.setFrontTime(time - mlFileInfos.get(index).getBeginTime(), streamPlayer);
            if (result == 0) {
                isPlayIng = true;
                startTimer(1000);
            } else {
                stopPlayVideo();
            }
        }
    }

    /**
     * 播放回放片段
     */
    public void startPlayVideo() {
        stopPlayVideo();
        if (info != null && mlFileInfos.size() != 0&&(index<=(mlFileInfos.size()-1))) {
            playback_add_dev.setVisibility(View.GONE);
            playback_progressbar.setVisibility(View.VISIBLE);
            playback_progressbar.postDelayed(new Runnable() {
                public void run() {
                    play_back_stop.setImageResource(R.mipmap.real_stop);
                    streamPlayer = playBackUtil.getStreamPlayer(info.getDevType(), playback_layout, info.getDevId(), channelInfo.getChannelId());
                    playBackUtil.startFrontEndFilePlay(sendTime, mlFileInfos.get(index), streamPlayer);
                    dialog.dismiss();
                }
            }, 300);
        }
        else
        {
            dialog.dismiss();
        }
    }

    /**
     * 停止播放
     */
    private void stopPlayVideo() {
        isPlayIng = false;
        isScrollToPlay = false;
        if (playBackUtil != null) {
            playBackUtil.stopPlay();
            playback_progressbar.setVisibility(View.GONE);
            play_back_stop.setImageResource(R.mipmap.real_start);
            playback_add_dev.setVisibility(View.GONE);
            stopTimer();
        }
    }

    /**
     * 停止计时
     * 播放器关闭时停止计时 不需要在获取pos点
     *
     * @see [类、类#方法、类#成员]
     */
    private void stopTimer() {
        if (timer != null) {
            myTimeTask.cancel();
            myTimeTask = null;
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 開始計時
     * 当回放视频开始播放时，获取当前播放pos 用于时间轴上滑块的位置的实时更新
     *
     * @see [类、类#方法、类#成员]
     */
    private void startTimer(int time) {
        stopTimer();
        timer = new Timer();
        myTimeTask = new MyTimeTask();
        timer.schedule(myTimeTask, time, 1000);
    }

    class MyTimeTask extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(20);
        }
    }

    @OnClick({R.id.one_day, R.id.one_hour, R.id.five_min, R.id.play_back_change, R.id.playback_layout, R.id.play_back_zhua, R.id.play_back_stop, R.id.playback_add_dev})
    public void dateOnClick(View view) {
        switch (view.getId()) {
            case R.id.five_min:
                /**
                 * 5分钟
                 */
                five_min.setBackgroundResource(R.drawable.time_button_bg);
                five_min.setTextColor(getResources().getColor(R.color.white));
                one_hour.setBackgroundResource(R.drawable.time_button_bg_pressed);
                one_hour.setTextColor(getResources().getColor(R.color.text_color));
                one_day.setBackgroundResource(R.drawable.time_button_bg_pressed);
                one_day.setTextColor(getResources().getColor(R.color.text_color));
                seekBar_type = 2;
                updataSeek(seekBar_type);
                break;
            case R.id.one_hour:
                /**
                 * 一小时
                 */
                one_hour.setBackgroundResource(R.drawable.time_button_bg);
                one_hour.setTextColor(getResources().getColor(R.color.white));

                one_day.setBackgroundResource(R.drawable.time_button_bg_pressed);
                one_day.setTextColor(getResources().getColor(R.color.text_color));

                five_min.setBackgroundResource(R.drawable.time_button_bg_pressed);
                five_min.setTextColor(getResources().getColor(R.color.text_color));
                seekBar_type = 1;
                updataSeek(seekBar_type);
                break;
            case R.id.one_day:
                /**
                 * 一天
                 */
                one_day.setBackgroundResource(R.drawable.time_button_bg);
                one_day.setTextColor(getResources().getColor(R.color.white));

                one_hour.setBackgroundResource(R.drawable.time_button_bg_pressed);
                one_hour.setTextColor(getResources().getColor(R.color.text_color));

                five_min.setBackgroundResource(R.drawable.time_button_bg_pressed);
                five_min.setTextColor(getResources().getColor(R.color.text_color));

                // 更新状态 已经改变时间轴的起始和结束时间
                seekBar_type = 0;
                updataSeek(seekBar_type);
                break;
            case R.id.play_back_change:
                //控制是否全屏
                if (isChange) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
            case R.id.playback_layout:
                //是否显示播放器菜单
                if (play_back_control_rl.getVisibility() == View.VISIBLE) {
                    TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 100);
                    animation.setDuration(200);
                    play_back_control_rl.setAnimation(animation);
                    play_back_control_rl.setVisibility(View.GONE);
                } else {
                    TranslateAnimation animation = new TranslateAnimation(0, 0, 100, 0);
                    animation.setDuration(200);
                    play_back_control_rl.setAnimation(animation);
                    play_back_control_rl.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.play_back_zhua:
                if (info != null && channelInfo != null) {
                    playBackUtil.saveSnapshot(info.getDevGroupId(), info.getDevId(), channelInfo.getChannelId());
                }
                break;
            case R.id.play_back_stop:
                //暂停
                if (isPlayIng) {
                    stopPlayVideo();
                } else {
                    startPlayVideo();
                }
                break;
            case R.id.playback_add_dev:
//                if (onAddListener != null) {
//                    onAddListener.OnClick();
//                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isChange = !isChange;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land
            play_back_scroll.setVisibility(View.GONE);
            CommonUtil.setFullScreen(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, width);
            play_back_all.setLayoutParams(params);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port
            CommonUtil.cancelFullScreen(getActivity());
            play_back_scroll.setVisibility(View.VISIBLE);
            play_back_scroll.smoothScrollTo(0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            play_back_all.setLayoutParams(params);
        }
    }

    @SuppressWarnings("deprecation")
    private void getViewWidth() {
        WindowManager wm = getActivity().getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        int view_width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int view_height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        play_back_all.measure(view_width, view_height);
        height = play_back_all.getMeasuredHeight();
    }


    /**
     * 根据当前时间更新时间轴时间
     * <功能详细描述>
     *
     * @param seektype 时间轴的类型
     * @see [类、类#方法、类#成员]
     */
    private void updataSeek(int seektype) {
        long  nowTime=getPosTime();
        if (seektype == 0) {
            seekStartTime = startTime;
            seekEndTime = endTime;
        } else {
            int longTime = 0;
            if (seektype == 1) {
                longTime = (int) (60 * 60 * 0.5)*1000;

            } else {
                longTime = (int) (60 * 2.5)*1000;
            }
            if ((nowTime*1000  - startTime) >= longTime && (endTime - nowTime*1000) >= longTime) {
                seekStartTime = nowTime *1000 - longTime;
                seekEndTime =  nowTime *1000 + longTime;
            } else if ((nowTime*1000 - startTime) < longTime) {
                seekStartTime = startTime;
                seekEndTime =  startTime + longTime * 2;
            } else {
                // 时间大于当天的23:57:30
                seekStartTime =  endTime - longTime * 2;
                seekEndTime = endTime;
            }
            seekStartTime=noSecond(seekStartTime);
            seekEndTime=noSecond(seekEndTime);
        }
        playbackbar.setTime(seekStartTime, seekEndTime, seekBar_type);
        playbackbar.setScroll(nowTime);
    }


    private long noSecond(long time)  {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String str= dateFormat.format(new Date(time));
        str=str+":00";
        SimpleDateFormat fFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long ret= 0;
        try {
            ret = fFormat.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  ret;
    }




    /**
     * 获取当前播放的时间节点 单位是毫秒
     * 通过pos点获取当前播放的时间节点
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public long getPosTime() {
        if (mlFileInfos != null && mlFileInfos.size() != 0) {
            long time = playBackUtil.frontEndPlayGetPos();
            return time + mlFileInfos.get(index).getBeginTime();
        }
        return 0;
    }

    private String time0(int time) {
        if (time < 10) {
            return "0" + time;
        }
        return time + "";
    }

    @Override
    public void onStop() {
        super.onStop();
        stopPlayVideo();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void setPlayBackAddListener(OnAddListener onAddListener) {
        this.onAddListener = onAddListener;
    }

    public interface OnAddListener {
        void OnClick();
    }

}