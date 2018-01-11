package com.vomont.vlink.ploice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlink.base.BaseActivity;
import com.vomont.vlinkersdk.WMUserEventMsg;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/9 0009.
 */
public class PloiceDetailActivity extends BaseActivity {
    @BindView(R.id.top_back)
    ImageView top_back;
    @BindView(R.id.top_name)
    TextView top_name;
    @BindView(R.id.police_detail_name)
    TextView police_detail_name;
    @BindView(R.id.police_detail_type)
    TextView police_detail_type;

    @BindView(R.id.police_detail_time)
    TextView police_detail_time;

    private WMUserEventMsg wmUserEventMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ploice_detai);
        ButterKnife.bind(this);
        top_back.setVisibility(View.VISIBLE);
        top_name.setText("报警详情");
        wmUserEventMsg= (WMUserEventMsg) getIntent().getSerializableExtra("police");
        police_detail_name.setText(wmUserEventMsg.getName());
        police_detail_type.setText(wmUserEventMsg.getTypeName());
        police_detail_time.setText(wmUserEventMsg.getTime());
    }


    @OnClick(R.id.top_back)
    public void  goBackOnClick()
    {
        finish();
    }

}
