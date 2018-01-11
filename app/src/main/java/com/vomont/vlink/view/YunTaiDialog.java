package com.vomont.vlink.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.vomont.vlink.R;
import com.vomont.vlinkersdk.Constants;

/**
 * Created by Administrator on 2017/11/13 0013.
 */

public class YunTaiDialog implements View.OnTouchListener {


    protected static final float FLIP_DISTANCE = 50;
    private WindowManager mWindowManager;
    private Context context;
    private Display display;
    private Dialog dialog;


    private ImageView contrl_yuntai;

    private  ImageView finish_yuntai;
    private GestureDetector mGestureDetector;

    OnControlListener onControlListener;
    public YunTaiDialog( Context context) {
        this.context=context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = mWindowManager.getDefaultDisplay();
    }

    public YunTaiDialog builder()
    {
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_yuntai, null);
        contrl_yuntai=view.findViewById(R.id.contrl_yuntai);
        finish_yuntai=view.findViewById(R.id.finish_yuntai);
        view.setMinimumWidth(display.getWidth());
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        contrl_yuntai.setOnTouchListener(this);
        contrl_yuntai.setFocusable(true);
        contrl_yuntai.setClickable(true);
        contrl_yuntai.setLongClickable(true);
        mGestureDetector = new GestureDetector(new GestureListener()); // 使用派生自
        finish_yuntai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return this;
    }

    public void show() {
        dialog.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


    class GestureListener implements GestureDetector.OnGestureListener
    {

        @Override
        public boolean onDown(MotionEvent e)
        {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e)
        {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if (e1.getX() - e2.getX() > FLIP_DISTANCE)
            {
                if(onControlListener!=null)
                {
                    onControlListener.onMove(Constants.WMPtzCommand_Left);
                }
            }
            if (e2.getX() - e1.getX() > FLIP_DISTANCE)
            {
                if(onControlListener!=null)
                {
                    onControlListener.onMove(Constants.WMPtzCommand_Right);
                }
//                playutil.ptzControlStart(Constants.WMPtzCommand_Right);
            }
            if (e1.getY() - e2.getY() > FLIP_DISTANCE)
            {
                if(onControlListener!=null)
                {
                    onControlListener.onMove(Constants.WMPtzCommand_Up);
                }

            }
            if (e2.getY() - e1.getY() > FLIP_DISTANCE)
            {
                if(onControlListener!=null)
                {
                    onControlListener.onMove(Constants.WMPtzCommand_Down);
                }
            }
            return false;
        }
    }


    public  void setOnControlListener(OnControlListener onControlListener)
    {
        this.onControlListener=onControlListener;
    }


    public interface  OnControlListener
    {
        void  onMove(int i);
    }
}
