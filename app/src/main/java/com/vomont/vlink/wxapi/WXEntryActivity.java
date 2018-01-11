package com.vomont.vlink.wxapi;


import android.app.Activity;
import android.os.Bundle;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/** 微信客户端回调activity示例 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler
{
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        api = WXAPIFactory.createWXAPI(this, "wx335f801b824ce88a", true);
        // 将应用注册到微信
        api.registerApp("wx335f801b824ce88a");
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onReq(BaseReq arg0)
    {
        
    }
    
    @Override
    public void onResp(BaseResp resp)
    {
        switch (resp.errCode)
        {
            case BaseResp.ErrCode.ERR_OK:
                // 分享成功
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                // 分享取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                // 分享拒绝
                break;
        }
    }
}
