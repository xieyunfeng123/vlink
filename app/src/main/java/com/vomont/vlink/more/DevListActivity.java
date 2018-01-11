package com.vomont.vlink.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlink.base.BaseActivity;
import com.vomont.vlink.comm.ACaCheContrast;
import com.vomont.vlink.home.fragment.DevInfo;
import com.vomont.vlink.home.fragment.adapter.DevAdapter;
import com.vomont.vlink.util.ACache;
import com.vomont.vlink.util.VlinkRealPlayUtil;
import com.vomont.vlink.video.VideoActivity;
import com.vomont.vlink.view.ClearEditText;
import com.vomont.vlinkersdk.WMDeviceGroup;
import com.vomont.vlinkersdk.WMDeviceInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/11/13 0013.
 */

public class DevListActivity extends BaseActivity {

    @BindView(R.id.filter_edit)
    ClearEditText filter_edit;

    @BindView(R.id.more_dev_list)
    ExpandableListView more_dev_list;

    @BindView(R.id.top_back)
    ImageView top_back;

    @BindView(R.id.top_name)
    TextView top_name;

    private VlinkRealPlayUtil util;

    private DevAdapter adapter;

    private List<WMDeviceInfo> deviceInfos;

    private List<WMDeviceGroup> groups;

    private List<WMDeviceGroup> sendList;
    private ACache aCache;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_list);
        ButterKnife.bind(this);
        top_back.setVisibility(View.VISIBLE);
        top_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        top_name.setText("设备列表");
        initData();
    }

    private void initData() {
        deviceInfos = new ArrayList<>();
        util = new VlinkRealPlayUtil(this);
        groups = new ArrayList<>();
        sendList=new ArrayList<>();
        aCache=ACache.get(this);
        //获取设备列表
//        util.getDevList(deviceInfos, groups);
        groups= (List<WMDeviceGroup>) aCache.getAsObject(ACaCheContrast.dev);
        //获取设备列表
//        util.getDevList(deviceInfos, groups);
//
//        for (WMDeviceGroup wmDeviceGroup : groups) {
//            List<WMDeviceInfo> deviceInfoList = new ArrayList<>();
//            for (WMDeviceInfo info : deviceInfos) {
//                if (wmDeviceGroup.getGroupId() == info.getDevGroupId()) {
//                    deviceInfoList.add(info);
//                }
//            }
//            wmDeviceGroup.setWmDeviceInfoList(deviceInfoList);
//        }
        sendList.addAll(groups);
        adapter = new DevAdapter(this);
        adapter.setData(sendList);
        more_dev_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        filter_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getList(s.toString());
            }
        });

        adapter.setOnDevItemClickListener(new DevAdapter.OnDevClickListener() {
            @Override
            public void OnClick(int groupPosition, int childPosition, int lastPosition) {
                DevInfo devInfo=new DevInfo();
                devInfo.setDeviceInfo(sendList.get(groupPosition).getWmDeviceInfoList().get(childPosition));
                devInfo.setChannelInfo(sendList.get(groupPosition).getWmDeviceInfoList().get(childPosition).getChannelArr()[lastPosition]);
                Intent intent = new Intent(DevListActivity.this, VideoActivity.class);
                intent.putExtra("devinfo", devInfo);
                intent.putExtra("groupinfo", sendList.get(groupPosition));
                startActivity(intent);
                finish();
            }
        });

    }


    private void getList(String s) {
        if(s!=null&&s.length()!=0) {
            List<WMDeviceGroup> newWmdevGroup = new ArrayList<>();
//            newWmdevGroup.addAll(groups);
            for (WMDeviceGroup wmDeviceGroup : groups) {
                boolean hasStr = false;
                List<WMDeviceInfo> devs=new ArrayList<>();
                if( wmDeviceGroup.getWmDeviceInfoList()!=null) {
                    for (WMDeviceInfo info : wmDeviceGroup.getWmDeviceInfoList()) {
                        if (info.getDevName().contains(s)) {
                            hasStr = true;
                            devs.add(info);
                        }
                    }
                }
                if (hasStr) {
                    WMDeviceGroup wn= new WMDeviceGroup();
                    wn.setFatherGroupId(wmDeviceGroup.getFatherGroupId());
                    wn.setWmDeviceInfoList(devs);
                    wn.setGroupId(wmDeviceGroup.getGroupId());
                    wn.setGroupName(wmDeviceGroup.getGroupName());
                    newWmdevGroup.add(wn);
                }
            }
            sendList.clear();
            sendList.addAll(newWmdevGroup);
            adapter.setData(sendList);
            adapter.notifyDataSetChanged();
            for (int i = 0; i < newWmdevGroup.size(); i++) {
                more_dev_list.expandGroup(i);
            }
        }
        else
        {
            sendList.clear();
            sendList.addAll(groups);
            adapter.setData(sendList);
            adapter.notifyDataSetChanged();
            for (int i = 0; i < groups.size(); i++) {
                more_dev_list.collapseGroup(i);
            }
        }

    }


}
