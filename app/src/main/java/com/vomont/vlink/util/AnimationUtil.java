package com.vomont.vlink.util;

import android.view.View;
import android.view.animation.TranslateAnimation;

/**
 * Created by Administrator on 2017/11/16 0016.
 */

public class AnimationUtil {

    /**
     * 从下到上显示的动画
     * @param view
     */
    public static void buttomTotop(View view) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 100, 0);
        animation.setDuration(500);
        view.setAnimation(animation);
    }

    /**
     * 从上到下显示的动画
     * @param view
     */
    public static void topToButtom(View view) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, 100);
        animation.setDuration(500);
        view.setAnimation(animation);
    }
}
