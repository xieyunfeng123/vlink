package com.vomont.vlink.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.vomont.vlink.R;
import com.vomont.vlink.base.BaseActivity;
import com.vomont.vlink.bean.User;
import com.vomont.vlink.bean.UserInfo;
import com.vomont.vlink.comm.ACaCheContrast;
import com.vomont.vlink.util.ACache;
import com.vomont.vlinkersdk.ResultCallBack;
import com.vomont.vlinkersdk.WMVlinkerSDK;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/11/17 0017.
 */

public class NewPasswordActivity extends BaseActivity {

    @BindView(R.id.old_psd_password)
    EditText old_psd_password;

    @BindView(R.id.new_psd_password)
    EditText new_psd_password;

    @BindView(R.id.new_psd_again_password)
    EditText new_psd_again_password;

    @BindView(R.id.new_psd_look_psd)
    ToggleButton new_psd_look_psd;

    @BindView(R.id.new_psd_finish)
    Button new_psd_finish;

    @BindView(R.id.top_back)
    ImageView top_back;
    @BindView(R.id.top_name)
    TextView top_name;

    ACache aCache;

    UserInfo user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_psd);
        ButterKnife.bind(this);
        top_name.setText("重置密码");
        top_back.setVisibility(View.VISIBLE);
        top_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new_psd_look_psd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new_psd_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    new_psd_again_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    new_psd_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    new_psd_again_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        new_psd_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePsd();
            }
        });
        aCache=ACache.get(this);
        user= (UserInfo) aCache.getAsObject(ACaCheContrast.user);
    }


    public void changePsd() {
        final String old = old_psd_password.getText().toString();
        final String newP = new_psd_password.getText().toString();
        final String againNP = new_psd_again_password.getText().toString();
        if (old == null || old.equals("")) {
            Toast.makeText(NewPasswordActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newP == null || newP.equals("")) {
            Toast.makeText(NewPasswordActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (againNP == null || againNP.equals("")) {
            Toast.makeText(NewPasswordActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newP.length() < 6) {
            Toast.makeText(NewPasswordActivity.this, "密码不能少于6位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newP.equals(againNP)) {
            Toast.makeText(NewPasswordActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        if(old!=null&&!old.equals(user.getPassword()))
        {
            Toast.makeText(NewPasswordActivity.this, "原密码错误", Toast.LENGTH_SHORT).show();
            return;
        }
//       int i=  WMClientSdk.getInstance().updatePassword(old,newP);
        new Thread(new Runnable() {
            @Override
            public void run() {
                WMVlinkerSDK.getInstance().updataPassword(old, newP, new ResultCallBack() {
                    @Override
                    public void onResultCallBack(int resultType, final int resultCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultCode == 0) {
                                    Toast.makeText(NewPasswordActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(NewPasswordActivity.this, "密码修改失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });

            }
        }).start();

//
//        if(i==0)
//        {
//            Toast.makeText(this,"密码修改成功",Toast.LENGTH_SHORT);
//            finish();
//        }
//        else
//        {
//            Toast.makeText(this,"密码修改失败",Toast.LENGTH_SHORT);
//        }
    }
}
