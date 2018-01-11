package com.vomont.vlink.img;

import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.vomont.vlink.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectPicPopupWindow extends Activity implements OnClickListener,IWXAPIEventHandler
{
    
    private Button btn_cancel;
    
    private LinearLayout qq_share, wx_share;
    
    // qq分享
    private static String APP_ID = "1106474573";
    
    // qq
    private Tencent mTencent;
    
    // 微信ID
    private static final String WXAPP_ID = "wx335f801b824ce88a";
    
    private IWXAPI api;
    
    private String path;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        // qq创建实例
        if (mTencent == null)
        {
            mTencent = Tencent.createInstance(APP_ID, getApplicationContext());
        }
        btn_cancel = (Button)this.findViewById(R.id.btn_cancel);
        qq_share = (LinearLayout)findViewById(R.id.qq_share);
        wx_share = (LinearLayout)findViewById(R.id.wx_share);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        // 添加按钮监听
        btn_cancel.setOnClickListener(this);
        qq_share.setOnClickListener(this);
        wx_share.setOnClickListener(this);
    }
    
    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        finish();
        return true;
    }
    
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.qq_share:
                if (path != null)
                {
                    share(path);
                }
                break;
            case R.id.wx_share:
                if (path != null)
                {
                    shareWX(path);
                }
                break;
            default:
                break;
        }
        finish();
    }
    
    /**
     * qq分享的方法
     * <功能详细描述>
     * @param path
     * @see [类、类#方法、类#成员]
     */
    private void share(String path)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "vlinker");
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, path);
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME,"vlinker");
        mTencent.shareToQQ(this, bundle, qqShareListener);
    }
    
    /**
     * 微信分享的方法
     * <功能详细描述>
     * @param path
     * @see [类、类#方法、类#成员]
     */
    private void shareWX(String path)
    {
        // 微信创建开发实例
        api = WXAPIFactory.createWXAPI(this, WXAPP_ID, true);
        api.handleIntent(getIntent(),this);
        // 将应用注册到微信
        // api.registerApp(WXAPP_ID);
        if (!api.isWXAppInstalled())
        {
            Toast.makeText(this, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }

        
        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(path);
        
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        
        Bitmap bmp = BitmapFactory.decodeFile(path);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true);
        bmp.recycle();
        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
        
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);

    }
    
    private String buildTransaction(final String type)
    {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    
    IUiListener qqShareListener = new IUiListener()
    {
        @Override
        public void onComplete(Object arg0)
        {
            
        }
        
        @Override
        public void onError(UiError arg0)
        {
            Toast.makeText(SelectPicPopupWindow.this,  arg0.errorMessage, Toast.LENGTH_SHORT).show();
        }
        
        @Override
        public void onCancel()
        {
        }
    };
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
    };

    @Override
    public void onReq(BaseReq baseReq) {
        Log.i("insert","错误号：" + baseReq.transaction + "；信息：" + baseReq.getType());
          }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.i("insert","错误号：" + baseResp.errCode + "；信息：" + baseResp.errStr);
    }

}