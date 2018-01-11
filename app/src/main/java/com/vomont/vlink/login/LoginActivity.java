package com.vomont.vlink.login;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.vomont.vlink.R;
import com.vomont.vlink.base.BaseApplication;
import com.vomont.vlink.bean.UserInfo;
import com.vomont.vlink.comm.ACaCheContrast;
import com.vomont.vlink.home.HomeActivity;
import com.vomont.vlink.util.ACache;
import com.vomont.vlink.view.ProgressDialog;
import com.vomont.vlinkersdk.WMVlinkerSDK;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("deprecation")
public class  LoginActivity extends AppCompatActivity implements LoginContract.View {
    // 输入用户名 密码
    @BindView(R.id.login_num)
    EditText login_num;

    @BindView(R.id.login_psd)
    EditText login_psd;

    // 登录
    @BindView(R.id.login_app)
    Button login_app;

    // 查看密码
    @BindView(R.id.login_psd_look)
    ImageView login_psd_look;

    @BindView(R.id.ipsetting)
    ImageView ipsetting;

    @BindView(R.id.newuser_register)
    TextView newuser_register;

    @BindView(R.id.register_login)
    TextView register_login;

    private LoginContract.Presenter presenter;
    private ACache aCache;
    UserInfo userLogin;

    // 是否查看密码
    private boolean isLook;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        aCache = ACache.get(this);
        ButterKnife.bind(this);
        presenter = new LoginPresenter(this);
        isNeedLogin();
    }

    /**
     * 登录的点击事件
     */
    @OnClick(R.id.login_app)
    public void loginOnClick() {
        login();
    }

    /**
     * 是否可以查看密码
     */
    @OnClick(R.id.login_psd_look)
    public void lookPsdOnClick() {
        // 是否可以查看密码
        if (isLook) {
            login_psd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            login_psd_look.setImageResource(R.mipmap.eye_password_select);
            isLook = false;
        } else {
            login_psd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            login_psd_look.setImageResource(R.mipmap.eye_password);
            isLook = true;
        }
    }

    /**
     * 设置ip
     */
    @OnClick(R.id.ipsetting)
    public void IPSettingOnclick() {
        Intent intent = new Intent(this, SetIpActivity.class);
        startActivity(intent);
    }


    /**
     * 判断是否需要自动登录
     */
    private void isNeedLogin() {
        userLogin = (UserInfo) aCache.getAsObject(ACaCheContrast.user);
        if (userLogin != null) {
            if (userLogin.getNum() != null && userLogin.getPassword() != null) {
//                // 首页不用显示用户的头像
//                if(userLogin.getImgUrl()!=null) {
//                     Glide.with(this).load(userLogin.getImgUrl()).error(R.mipmap.personal_defaultplayericon).into(user_img);
//                }
                login_num.setText(userLogin.getNum());
                login_psd.setText(userLogin.getPassword());
            }
        }
    }

    /**
     * 登录
     */
    private void login() {
        // 密码为空
        dialog = ProgressDialog.createLoadingDialog(this, "登录中...");
        dialog.show();
        if (TextUtils.isEmpty(login_num.getText().toString()) || TextUtils.isEmpty(login_psd.getText().toString())) {
            Toast.makeText(this, R.string.error_num_psd, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else {
            String str = BaseApplication.BASE_URL.replace("http://", "");
            String[] items = str.split(":");
            WMVlinkerSDK.getInstance().init(255);
            presenter.login(login_num.getText().toString(), login_psd.getText().toString(), items[0], Integer.parseInt(items[1]));
        }
    }


    @Override
    public void loginSucess(UserInfo userInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (userLogin == null) {
                    userLogin = new UserInfo();
                }
                userLogin.setNum(login_num.getText().toString());
                userLogin.setPassword(login_psd.getText().toString());
//        userLogin=userInfo;
                //登录成功保存用户信息
                aCache.put(ACaCheContrast.user, userLogin);
                //保存登录成功的ip 用于下次登陆
                aCache.put(ACaCheContrast.ip, BaseApplication.BASE_URL.replace("http://", ""));
                if (aCache.getAsObject("ips") != null) {
                    // 有历史的ip地址
                    List<String> ips = (List<String>) aCache.getAsObject("ips");
                    boolean hasIp = false;
                    for (String allip : ips) {
                        // 判断当前的ip地址是否用过 如果用过 就不需要加到历史ip地址的列表中去
                        if (allip.equals(BaseApplication.BASE_URL.replace("http://", ""))) {
                            hasIp = true;
                            break;
                        }
                    }
                    if (!hasIp) {
                        ips.add(BaseApplication.BASE_URL.replace("http://", ""));
                        aCache.put("ips", (Serializable) ips);
                    }
                } else {
                    // 没有历史ip地址列表 就创建一个
                    List<String> ips = new ArrayList<String>();
                    ips.add(BaseApplication.BASE_URL.replace("http://", ""));
                    aCache.put("ips", (Serializable) ips);
                }
                // 将登录成功的ip地址保存本地 下次启动后作为默认的ip地址
                aCache.put("ip", BaseApplication.BASE_URL.replace("http://", ""));
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void loginFail(int i) {
        //登录失败
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, R.string.error_num, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
//
    }

    @Override
    public void loginError() {
        //登录出错
        Toast.makeText(this, R.string.error_login, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void userImageError() {
        //网络请求出错
        Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void userImageFail() {
        //获取头像失败
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void userImageSucess(String imgUrl) {
        //获取头像成功
        userLogin.setImgUrl(imgUrl);
        aCache.put(ACaCheContrast.user, userLogin);
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
