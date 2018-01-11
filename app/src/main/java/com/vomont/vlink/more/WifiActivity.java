package com.vomont.vlink.more;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlink.base.BaseActivity;
import com.vomont.vlink.bean.WifiBean;
import com.vomont.vlink.more.adpter.WifiAdapter;
import com.vomont.vlink.service.WifiAndPoliceService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/12/10 0010.
 */

public class WifiActivity extends BaseActivity {
    @BindView(R.id.wifi_list)
    ListView wifi_list;
    @BindView(R.id.empty_view_wifi)
    RelativeLayout empty_view_wifi;
    @BindView(R.id.top_back)
    ImageView top_back;
    @BindView(R.id.top_name)
    TextView top_name;
    @BindView(R.id.top_right_name)
    TextView top_right_name;
    WifiAdapter adapter;

    private  WifiAndPoliceService wifiAndPoliceService;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        ButterKnife.bind(this);
        top_back.setVisibility(View.VISIBLE);
        top_name.setText(R.string.wifi);
        top_right_name.setVisibility(View.VISIBLE);
        top_right_name.setText("清空");
        wifi_list.setEmptyView(empty_view_wifi);
        adapter = new WifiAdapter(this);
        wifi_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
            if(wifiAndPoliceService!=null)
            {
                wifiAndPoliceService.setWifiDataResult(new WifiAndPoliceService.WifiCallBack() {
                    @Override
                    public void call(final List<WifiBean> mlist) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                adapter.setData(mlist);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        }
    };

    @OnClick(R.id.top_back)
    public void onFinish() {
        finish();
    }

    @OnClick(R.id.top_right_name)
    public void clear() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
