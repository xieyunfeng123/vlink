package com.vomont.vlink.login;


import com.vomont.vlink.base.BaseModel;
import com.vomont.vlink.bean.UserImage;
import com.vomont.vlinkersdk.WMVlinkerSDK;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;

    private LoginModel model;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        model = new LoginModel();
    }

    @Override
    public void login(final String name, final String password, final String ip, final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = WMVlinkerSDK.getInstance().login(name, password, ip, port);
                if (result == 0) {
                    view.loginSucess(null);
                } else {
                    view.loginFail(result);
                }
            }
        }).start();

    }

    @Override
    public void userImage(int userid) {
        model.getUserImage(userid, new BaseModel.HttpListener<UserImage>() {
            @Override
            public void onSucess(UserImage object) {
                view.userImageSucess(object.getIconurl());
            }

            @Override
            public void onFail() {
                view.userImageFail();
            }

            @Override
            public void onError() {
                view.userImageError();
            }
        });
    }

}
