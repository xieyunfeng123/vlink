package com.vomont.vlink.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlink.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/11/28 0028.
 */

public class AboutActivity extends BaseActivity {

    @BindView(R.id.top_back)
    ImageView top_back;

    @BindView(R.id.top_name)
    TextView top_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        top_back.setVisibility(View.VISIBLE);
        top_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });
        top_name.setText("关于");
    }
}
