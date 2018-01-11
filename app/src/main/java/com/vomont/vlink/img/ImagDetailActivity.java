package com.vomont.vlink.img;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigText;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.TextParams;
import com.orhanobut.logger.Logger;
import com.vomont.vlink.R;
import com.vomont.vlink.base.BaseActivity;
import com.vomont.vlink.img.fragment.ImageDetailFragment;
import com.vomont.vlink.manager.FileManageActivity;
import com.vomont.vlink.util.CommonUtil;
import com.vomont.vlink.util.DensityUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Administrator on 2017/11/15 0015.
 */

public class ImagDetailActivity extends BaseActivity {

    @BindView(R.id.center_pic_pager)
    ViewPager center_pic_pager;

    @BindView(R.id.share_pic)
    RelativeLayout share_pic;

    @BindView(R.id.delete_pic)
    RelativeLayout delete_pic;

    @BindView(R.id.top_back)
    ImageView top_back;

    @BindView(R.id.top_name)
    TextView top_name;

    @BindView(R.id.img_detail_top_layout)
    View img_detail_top_layout;

    private List<String> mlist;

    private int position;
    FragAdapter adapter;

    List<ImageDetailFragment> fragments;

    //屏幕的宽度 和播放容器的高度
    private int width, height;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgdetail);
        ButterKnife.bind(this);
        initView();
        getViewwidth();
    }

    private void initView() {
        top_name.setText("详情");
        top_back.setVisibility(View.VISIBLE);
        mlist = (List<String>) getIntent().getSerializableExtra("pic");
        position = getIntent().getIntExtra("position", 0);
        initFragment();
        center_pic_pager.setCurrentItem(position);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land
            img_detail_top_layout.setVisibility(View.GONE);
            CommonUtil.setFullScreen(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, width);
            center_pic_pager.setLayoutParams(params);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port
            CommonUtil.cancelFullScreen(this);
            img_detail_top_layout.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 300));
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            center_pic_pager.setLayoutParams(params);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("deprecation")
    private void getViewwidth() {
        WindowManager wm = getWindowManager();
        width = wm.getDefaultDisplay().getWidth();

        int view_width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        int view_height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        center_pic_pager.measure(view_width, view_height);

        height = center_pic_pager.getMeasuredHeight();

    }

    @OnClick(R.id.share_pic)
    public void sharePic() {

        String path = mlist.get(position);
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        imageIntent.setType("image/*");
        imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        //createChooser中第二参数 是分享时弹出界面的标题
        startActivity(Intent.createChooser(imageIntent, "分享"));




//        Intent intent = new Intent(this, SelectPicPopupWindow.class);
//        Logger.e(mlist.get(position));
//        intent.putExtra("path", mlist.get(position));
//        startActivity(intent);
//        overridePendingTransition(R.anim.view_from_bu_to_top, R.anim.view_from_top_to_bu);
    }


    @OnClick(R.id.top_back)
    public void goBack() {
        finish();
    }


    @OnClick(R.id.delete_pic)
    public void delete() {
        if (mlist.size() == 0) {
            return;
        }
        new CircleDialog.Builder(this)
                .setWidth(0.6f)
                .configDialog(new ConfigDialog() {
                    @Override
                    public void onConfig(DialogParams params) {
                    }
                })
                .setText("确认删除文件")
                .configText(new ConfigText() {
                    @Override
                    public void onConfig(TextParams params) {
                        params.textSize= DensityUtil.dip2px(ImagDetailActivity.this,18);
                        params.height=DensityUtil.dip2px(ImagDetailActivity.this,60);
                        params.gravity= Gravity.CENTER;
                    }
                })
                .setNegative("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File file = new File(mlist.get(position));
                        if (file != null && file.exists()) {
                            file.delete();
                        }
                        String name = mlist.get(position);
                        mlist.remove(name);
                        fragments.remove(position);
                        adapter.notifyDataSetChanged();
                        if (!((position) >= mlist.size())) {
                            center_pic_pager.setCurrentItem(position);
                        } else {
                            if (mlist.size() != 0) {
                                center_pic_pager.setCurrentItem(mlist.size() - 1);
                            }
                        }
                    }
                })
                .configNegative(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        params.textSize= DensityUtil.dip2px(ImagDetailActivity.this,16);
                        params.height=DensityUtil.dip2px(ImagDetailActivity.this,60);
                    }
                })
                .setPositive("取消",null)
                .configPositive(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        params.textSize= DensityUtil.dip2px(ImagDetailActivity.this,16);
                        params.height=DensityUtil.dip2px(ImagDetailActivity.this,60);
                    }
                })
                .show();
    }

    private void initFragment() {
        //构造适配器
        fragments = new ArrayList<ImageDetailFragment>();
        for (int i = 0; i < mlist.size(); i++) {
            ImageDetailFragment fragment = new ImageDetailFragment();
            fragment.setData(mlist.get(i));
            fragments.add(fragment);
        }
        adapter = new FragAdapter(getSupportFragmentManager());
        adapter.setData(fragments);
        center_pic_pager.setAdapter(adapter);
        center_pic_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ImagDetailActivity.this.position = position;
                if (fragments.size() != 0) {
                    fragments.get(position).setOnScreenListener(new ImageDetailFragment.OnScreenListener() {
                        @Override
                        public void NoScreen() {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }

                        @Override
                        public void startScreen() {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                    });
//                    fragments.get(position).show(mlist.get(position));
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}
