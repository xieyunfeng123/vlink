package com.vomont.vlink.img.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.vomont.vlink.R;
import com.vomont.vlink.view.CommonVideoView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/11/15 0015.
 */

public class ImageDetailFragment extends Fragment {


    @BindView(R.id.fragment_img_detail_show_img)
    PhotoView fragment_img_detail_show_img;

    @BindView(R.id.fragment_video_detail_show)
    CommonVideoView fragment_video_detail_show;

    @BindView(R.id.textname)
    TextView textname;

    OnScreenListener onScreenListener;

    private String name;

//Error:Error: Avoid non-default constructors in fragments: use a default constructor plus Fragment#setArguments(Bundle) instead [ValidFragment]
//    public ImageDetailFragment(String name) {
//        this.name = name;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_img_detail, container, false);
        ButterKnife.bind(this, view);
        fragment_img_detail_show_img.enable();
        fragment_video_detail_show.setOnScreenListener(new CommonVideoView.OnScreenListener() {
            @Override
            public void NoScreen() {
                if (onScreenListener != null) {
                    onScreenListener.NoScreen();
                }
            }

            @Override
            public void startScreen() {
                if (onScreenListener != null) {
                    onScreenListener.startScreen();
                }
            }
        });
        show();
        return view;
    }


    public  void  setData(String  name)
    {
        this.name=name;
        show();
    }

    public void show() {
//        if(fragment_img_detail_show_img!=null) {
            if (name.substring(name.length() - 3, name.length()).equals("mp4")) {
                fragment_img_detail_show_img.setVisibility(View.GONE);
                fragment_video_detail_show.setVisibility(View.VISIBLE);
                fragment_video_detail_show.startPath(name);
            } else {
                if (fragment_img_detail_show_img != null) {
                    fragment_img_detail_show_img.setVisibility(View.VISIBLE);
                    fragment_video_detail_show.setVisibility(View.GONE);
                    Glide.with(getActivity()).load(new File(name)).skipMemoryCache(true).into(fragment_img_detail_show_img);
                }
            }
//        }
    }

//    public  void upData(String path)
//    {
//        if (path.substring(path.length() - 3, path.length()).equals("mp4")) {
//            fragment_img_detail_show_img.setVisibility(View.GONE);
//            fragment_video_detail_show.setVisibility(View.VISIBLE);
//            fragment_video_detail_show.startPath(path);
//        } else {
//            if (fragment_img_detail_show_img != null) {
//                fragment_img_detail_show_img.setVisibility(View.VISIBLE);
//                fragment_video_detail_show.setVisibility(View.GONE);
//                Glide.with(getActivity()).load(new File(path)).skipMemoryCache(true).into(fragment_img_detail_show_img);
//            }
//        }
//}

    public void setOnScreenListener(OnScreenListener onScreenListener) {
        this.onScreenListener = onScreenListener;
    }


    public interface OnScreenListener {
        void NoScreen();

        void startScreen();
    }
}
