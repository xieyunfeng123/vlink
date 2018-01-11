package com.vomont.vlink.ploice;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.vomont.vlink.R;
import com.vomont.vlink.base.BaseActivity;
import com.vomont.vlink.bean.PoliceTpye;
import com.vomont.vlink.ploice.adapter.ChooseTypeAdapter;
import com.vomont.vlink.ploice.adapter.ChoosedAdapter;
import com.vomont.vlink.view.DatePopuWindow;
import com.vomont.vlink.view.ProgressDialog;
import com.vomont.vlinkersdk.HistoryEventCallBack;
import com.vomont.vlinkersdk.WMHistoryEventSearchCondition;
import com.vomont.vlinkersdk.WMUserEventMsg;
import com.vomont.vlinkersdk.WMVlinkerSDK;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/11 0011.
 */

public class PloiceTypeActivity extends BaseActivity {


    @BindView(R.id.top_back)
    public ImageView top_back;
    @BindView(R.id.top_name)
    public TextView top_name;
    @BindView(R.id.police_starttime_bg)
    public LinearLayout police_starttime_bg;
    @BindView(R.id.police_endtime_bg)
    public LinearLayout police_endtime_bg;

    @BindView(R.id.ploice_type_bg)
    public LinearLayout ploice_type_bg;
    @BindView(R.id.police_starttime)
    public TextView police_starttime;

    @BindView(R.id.police_endtime)
    public TextView police_endtime;


    @BindView(R.id.police_search_type)
    public Button police_search_type;

    @BindView(R.id.has_choose_type)
    public ListView has_choose_type;

    @BindView(R.id.is_move_listview)
    public TextView is_move_listview;

    private DatePopuWindow datePopuWindow;

    private PopupWindow mPopupWindow;

    private Button yes_choose_type_use;

    private ListView police_type_listview;

    private List<PoliceTpye> mlist;

    private ChooseTypeAdapter chooseTypeAdapter;

    private ChoosedAdapter choosedAdapter;

    private List<PoliceTpye> chooseMlist;

    private int startTime, endTime;

    private Dialog dialog;

    private boolean isFirst=true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ploicetype);
        ButterKnife.bind(this);
        top_back.setVisibility(View.VISIBLE);
        top_name.setText("搜索");
        is_move_listview.setVisibility(View.GONE);
        initType();
        initPopupWindow();


    }

    private void initType() {
        mlist = new ArrayList<>();
        chooseMlist = new ArrayList<>();
        choosedAdapter = new ChoosedAdapter(this);
        has_choose_type.setAdapter(choosedAdapter);
        for (int i = 0; i < 12; i++) {
            PoliceTpye tpye = new PoliceTpye();
            tpye.setId(i);
            tpye.setChoose(false);
            mlist.add(tpye);
        }
    }

    int i = 0;

    //m_WMClientEventCfg.m_nEventTypes += (1<<WMAlarmType_VIDEO_LOST);
    @OnClick({R.id.top_back, R.id.police_starttime_bg, R.id.police_endtime_bg, R.id.police_search_type})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_back:
                finish();
                break;
            case R.id.police_starttime_bg:
                datePopuWindow = new DatePopuWindow(this);
                datePopuWindow.showAtLocation(this.findViewById(R.id.ploice_type_bg), Gravity.BOTTOM, 0, 0);
                datePopuWindow.setOnClickBack(new DatePopuWindow.SetClickBack() {
                    @Override
                    public void getDateString(String date) {
                        String time = "";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                        try {
                            time = new SimpleDateFormat("yyyy年MM月dd日").format(sdf.parse(date));
                            Date daTime = sdf.parse(date);
//                            startTime = (int) daTime.getTime();
                            startTime=  new Long(daTime.getTime()/1000).intValue();
                            endTime = startTime + 60 * 60 * 24;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        police_starttime.setText(time);
                    }
                });
                break;
            case R.id.police_endtime_bg:
                if (Build.VERSION.SDK_INT != 24) {
                    mPopupWindow.setWidth(police_endtime_bg.getWidth());
                    mPopupWindow.showAsDropDown(police_endtime_bg);
                } else {
                    //7.0 showAsDropDown没卵子用 得这么写
                    int[] location = new int[2];
                    police_endtime_bg.getLocationOnScreen(location);
                    int x = location[0];
                    int y = location[1];
                    mPopupWindow.setWidth(police_endtime_bg.getWidth());
                    mPopupWindow.showAtLocation(police_endtime_bg, Gravity.NO_GRAVITY, (int) police_endtime_bg.getX(), y + police_endtime_bg.getHeight());
                }
                break;
            case R.id.police_search_type:
                if (booleanSearch()) {
                    searchHis();
                }
                break;
            default:
                break;
        }
    }

    private boolean booleanSearch() {
        if (startTime == 0) {
            Toast.makeText(this, "请选择报警时间", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (chooseMlist.size() == 0) {
            Toast.makeText(this, "请选择报警类型", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void searchHis() {
        dialog= ProgressDialog.createLoadingDialog(this,"");
        dialog.show();
        WMHistoryEventSearchCondition condition = new WMHistoryEventSearchCondition();
        condition.setDevId(0);
        condition.setChannelId(0);
        condition.setBeginTime(startTime);
        condition.setEndTime(endTime);
        int eventType = 0;
        for (PoliceTpye tpye : chooseMlist) {
            eventType += (1 << (tpye.getId() - 1));
        }
        condition.setEventType(eventType);
         int result=    WMVlinkerSDK.getInstance().historyEventMsgSearch(condition, new HistoryEventCallBack() {
            @Override
            public void fSearchHistoryAlarmCallBack(final int result, int total, int isFinish, final WMUserEventMsg[] userEventMsgs) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isFirst) {
                            if (result == 0&&userEventMsgs!=null&&userEventMsgs.length!=0) {
                                List<WMUserEventMsg> list=new ArrayList<WMUserEventMsg>();
                                for(WMUserEventMsg msg:userEventMsgs)
                                {
                                    list.add(msg);
                                }
                                Intent intent = new Intent(PloiceTypeActivity.this, PoliceListActivity.class);
                                intent.putExtra("userEventMsgs", (Serializable) list);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(PloiceTypeActivity.this, PoliceListActivity.class);
                                startActivity(intent);
                            }
                            isFirst=false;
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        if(result!=0)
        {
            Intent intent = new Intent(PloiceTypeActivity.this, PoliceListActivity.class);
            startActivity(intent);
            dialog.dismiss();
            isFirst=false;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isFirst=true;
    }

    /**
     * 初始化下拉菜单
     */
    private void initPopupWindow() {
        View popupView = getLayoutInflater().inflate(R.layout.layout_police_type, null);
        yes_choose_type_use = popupView.findViewById(R.id.yes_choose_type_use);
        police_type_listview = popupView.findViewById(R.id.police_type_listview);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
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
        chooseTypeAdapter = new ChooseTypeAdapter(this);
        chooseTypeAdapter.setData(mlist);
        police_type_listview.setAdapter(chooseTypeAdapter);
        chooseTypeAdapter.notifyDataSetChanged();
        chooseTypeAdapter.setOnPoiceListener(new ChooseTypeAdapter.OnPoliceListener() {
            @Override
            public void onClick(int position, boolean isChoose) {
                if (position == 0 && !isChoose) {
                    for (PoliceTpye tpye : mlist) {
                        tpye.setChoose(false);
                    }
                } else if (mlist.get(0).isChoose()) {
                    mlist.get(0).setChoose(false);
                }
                mlist.get(position).setChoose(!isChoose);
                chooseTypeAdapter.notifyDataSetChanged();
            }
        });
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        yes_choose_type_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseType();
                choosedAdapter.setData(chooseMlist);
                choosedAdapter.notifyDataSetChanged();
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });
    }

    private void chooseType() {
        chooseMlist.clear();
        if (mlist.get(0).isChoose()) {
            for (PoliceTpye policeTpye : mlist) {
                if (policeTpye.getId() != 0) {
                    chooseMlist.add(policeTpye);
                }
            }
        } else {
            for (PoliceTpye policeTpye : mlist) {
                if (policeTpye.isChoose()) {
                    chooseMlist.add(policeTpye);
                }
            }
        }
        if (chooseMlist.size() != 0) {
            is_move_listview.setVisibility(View.INVISIBLE);
        } else {
            is_move_listview.setVisibility(View.GONE);
        }

//
//        for(int i=0;i<11;i++)
//        {
//            if((m&(1<<i))==(1<<i))
//            {
//
//            }
//        }
    }
}
