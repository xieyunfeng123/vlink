package com.vomont.vlink.home.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigText;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.TextParams;
import com.vomont.vlink.R;
import com.vomont.vlink.login.LoginActivity;
import com.vomont.vlink.manager.FileManageActivity;
import com.vomont.vlink.more.DevListActivity;
import com.vomont.vlink.more.NewPasswordActivity;
import com.vomont.vlink.more.WifiActivity;
import com.vomont.vlink.setting.AboutActivity;
import com.vomont.vlink.util.DensityUtil;
import com.vomont.vlinkersdk.WMVlinkerSDK;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/30 0030.
 */

public class MoreFragment extends Fragment {

    @BindView(R.id.to_dev_list)
    LinearLayout to_dev_list;
    @BindView(R.id.more_to_file_manage)
    LinearLayout more_to_file_manage;
    @BindView(R.id.to_change_psd)
    LinearLayout to_change_psd;
    @BindView(R.id.sys_exit)
    Button sys_exit;
    @BindView(R.id.more_to_about)
    LinearLayout more_to_about;
    @BindView(R.id.more_to_wifi)
    LinearLayout more_to_wifi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.to_dev_list, R.id.more_to_file_manage, R.id.to_change_psd, R.id.sys_exit, R.id.more_to_about,R.id.more_to_wifi})
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.to_dev_list:
                intent = new Intent(getActivity(), DevListActivity.class);
                startActivity(intent);
                break;
            case R.id.more_to_file_manage:
                intent = new Intent(getActivity(), FileManageActivity.class);
                startActivity(intent);
                break;
            case R.id.to_change_psd:
                intent = new Intent(getActivity(), NewPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.sys_exit:
                new CircleDialog.Builder((FragmentActivity)getActivity())
                        .setWidth(0.6f)
                        .configDialog(new ConfigDialog() {
                            @Override
                            public void onConfig(DialogParams params) {
                            }
                        })
                        .setText("确认注销")
                        .configText(new ConfigText() {
                            @Override
                            public void onConfig(TextParams params) {
                                params.textSize= DensityUtil.dip2px(getActivity(),19);
                                params.height=DensityUtil.dip2px(getActivity(),60);
                                params.gravity= Gravity.CENTER;
                            }
                        })
                        .setNegative("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                                WMVlinkerSDK.getInstance().logout();
                                WMVlinkerSDK.getInstance().uninit();
                            }
                        })
                        .configNegative(new ConfigButton() {
                            @Override
                            public void onConfig(ButtonParams params) {
                                params.textSize= DensityUtil.dip2px(getActivity(),16);
                                params.height=DensityUtil.dip2px(getActivity(),60);
                            }
                        })
                        .setPositive("取消",null)
                        .configPositive(new ConfigButton() {
                            @Override
                            public void onConfig(ButtonParams params) {
                                params.textSize= DensityUtil.dip2px(getActivity(),16);
                                params.height=DensityUtil.dip2px(getActivity(),60);
                            }
                        })
                        .show();
                break;
            case R.id.more_to_about:
                intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.more_to_wifi:
                intent = new Intent(getActivity(), WifiActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
