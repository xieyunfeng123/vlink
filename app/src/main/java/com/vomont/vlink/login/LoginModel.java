package com.vomont.vlink.login;

import com.squareup.okhttp.Request;
import com.vomont.vlink.base.BaseApplication;
import com.vomont.vlink.base.BaseModel;
import com.vomont.vlink.bean.UserImage;
import com.vomont.vlink.bean.UserInfo;
import com.vomont.vlink.util.MD5Util;
import com.vomont.vlink.util.OKHttpClientManager;

import java.util.HashMap;

public class LoginModel implements BaseModel
{
    /**
     * 登录接口
     * @param name 用户名
     * @param password  密码
     * @param httpListener 回调
     */
    public void login(String name, String password,  final BaseModel.HttpListener<UserInfo> httpListener)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("msgid", "259");
        map.put("tel", name);
        map.put("pswd", MD5Util.getMd5(password));
        OKHttpClientManager.postAsyn(BaseApplication.BASE_URL, new OKHttpClientManager.ResultCallback<UserInfo>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                httpListener.onError();
            }
            
            @Override
            public void onResponse(UserInfo response)
            {
                httpListener.onSucess(response);
            }
        }, map);
    }

    /**
     * 获取用户头像
     * @param userid  用户id
     * @param httpListener 回调
     */
    public void getUserImage(int userid,  final BaseModel.HttpListener<UserImage> httpListener)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("msgid", "263");
        map.put("userid",userid+"");
        OKHttpClientManager.postAsyn(BaseApplication.BASE_URL, new OKHttpClientManager.ResultCallback<UserImage>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                httpListener.onError();
            }
            
            @Override
            public void onResponse(UserImage response)
            {
                if (response.getResult() == 0)
                {
                    httpListener.onFail();
                }
                else
                {
                    httpListener.onSucess(response);
                }
            }
        }, map);
    }
    
}
