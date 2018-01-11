package com.vomont.vlink.ploice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlink.base.BaseActivity;
import com.vomont.vlink.comm.ACaCheContrast;
import com.vomont.vlink.home.fragment.adapter.PloiceAdapter;
import com.vomont.vlink.service.WifiAndPoliceService;
import com.vomont.vlink.util.ACache;
import com.vomont.vlinkersdk.WMDeviceInfo;
import com.vomont.vlinkersdk.WMUserEventMsg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/12/19 0019.
 */

public class PoliceListActivity extends BaseActivity {

    @BindView(R.id.top_back)
    ImageView top_back;

    @BindView(R.id.top_name)
    TextView top_name;

    @BindView(R.id.search_list_police)
    ListView search_list_police;

    PloiceAdapter adapter;

    private WMUserEventMsg[] userEventMsgs;

    private List<WMUserEventMsg> mlist = new ArrayList<>();

    private List<WMDeviceInfo> devInfos;

    private ACache aCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policelist);
        ButterKnife.bind(this);
        top_name.setText(R.string.police);
        adapter = new PloiceAdapter(this);
        aCache = ACache.get(this);
        search_list_police.setAdapter(adapter);
        search_list_police.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PoliceListActivity.this, PloiceDetailActivity.class);
                intent.putExtra("police", mlist.get(position));
                startActivity(intent);
            }
        });
        top_back.setVisibility(View.VISIBLE);
        top_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mlist = (List<WMUserEventMsg>) getIntent().getSerializableExtra("userEventMsgs");
        if (mlist != null && mlist.size() != 0) {
            for (WMUserEventMsg msg : mlist) {
                intToString(msg);
            }
        }
        adapter.setData(mlist);
        adapter.notifyDataSetChanged();
    }

    private void intToString(WMUserEventMsg msg) {
        long timeNow=msg.getEventTime()*1000l;
        devInfos = (List<WMDeviceInfo>) aCache.getAsObject(ACaCheContrast.devItem);
        String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(timeNow));
        msg.setTime(time);
        if (devInfos != null) {
            for (WMDeviceInfo info : devInfos) {
                if (info.getDevId() == msg.getDevId()) {
                    msg.setName(info.getDevName());
                    break;
                }
            }
            msg.setTypeName(WifiAndPoliceService.getTypeName(msg.getEventType()));
        }
    }

    @OnClick(R.id.top_back)
    public void goBack() {
        finish();
    }

}
