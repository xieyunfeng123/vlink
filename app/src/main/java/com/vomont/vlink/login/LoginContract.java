package com.vomont.vlink.login;


import com.vomont.vlink.base.BasePresenter;
import com.vomont.vlink.base.BaseView;
import com.vomont.vlink.bean.UserInfo;

public interface LoginContract
{
    interface View extends BaseView<Presenter>
    {
        /**
         * 
         * 登录成功
         * <功能详细描述>
         * @see [类、类#方法、类#成员]
         */
        void loginSucess(UserInfo userInfo);
        
        /**
         * 
         * 登录失败
         * <功能详细描述>
         * @see [类、类#方法、类#成员]
         */
        void loginFail(int i);
        
        /**
         * 
         * 登录出错
         * <功能详细描述>
         * @see [类、类#方法、类#成员]
         */
        void loginError();


        void userImageError();

        void userImageFail();

        void userImageSucess(String imgUrl);
    }
    
    interface Presenter extends BasePresenter
    {
        void login(String name, String password, String ip, int port);

        void userImage(int userid);
    }
    
}
