package com.vomont.vlink.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.vomont.vlink.R;
import com.vomont.vlink.base.BaseActivity;
import com.vomont.vlink.base.BaseApplication;
import com.vomont.vlink.comm.ACaCheContrast;
import com.vomont.vlink.login.adapter.ItemIpAdapter;
import com.vomont.vlink.util.ACache;
import com.vomont.vlink.util.ListViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetIpActivity extends BaseActivity
{
    @BindView(R.id.ip_go_back)
    ImageView ip_go_back;
    
    @BindView(R.id.ip_sure)
    TextView ip_sure;
    
    @BindView(R.id.input_ip)
    EditText input_ip;
    
    @BindView(R.id.clear_ip)
    ImageView clear_ip;
    
    @BindView(R.id.history_ip)
    ListView history_ip;
    
    private String top = "http://";
    //101.201.75.83:9001
    
    private String url;
    
    private ACache aCache;
    
    private List<String> mlist;
    
    private ItemIpAdapter adapter;
    
    private boolean isFinish = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_setting);
        ButterKnife.bind(this);
        initData();
    }
    
    @SuppressWarnings("unchecked")
    protected void initData()
    {
        aCache = ACache.get(this);
        mlist = (List<String>)aCache.getAsObject(ACaCheContrast.ips);
        url = BaseApplication.BASE_URL.replace(top, "");
        Logger.d(url);
        if (!TextUtils.isEmpty(url))
        {
            input_ip.setText(url);
        }
        adapter = new ItemIpAdapter(this);
        adapter.setData(mlist);
        history_ip.setAdapter(adapter);
         adapter.notifyDataSetChanged();
         ListViewUtils.setListViewHeightBasedOnChildren(history_ip);
         history_ip.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                input_ip.setText(mlist.get(position));
                BaseApplication.BASE_URL = top + mlist.get(position);
                String[] strs = BaseApplication.BASE_URL.split(":");
                if (strs.length != 3)
                {
                    BaseApplication.BASE_URL = BaseApplication.BASE_URL + ":8051";
                }
            }
        });
    }
    
    @OnClick(R.id.ip_go_back)
    public void finishOnclick()
    {
        finish();
    }
    
    @OnClick(R.id.ip_sure)
    public void sureIPOnclick()
    {
        isFinish = true;
        if (!input_ip.getText().toString().isEmpty())
        {
            BaseApplication.BASE_URL = top + input_ip.getText().toString();
            String[] str = BaseApplication.BASE_URL.split(":");
            if (str.length != 3)
            {
                if (BaseApplication.BASE_URL.endsWith(":"))
                {
                    BaseApplication.BASE_URL = BaseApplication.BASE_URL + "8051";
                }
                else
                {
                    BaseApplication.BASE_URL = BaseApplication.BASE_URL + ":8051";
                }
            }
            else
            {
                for (int i = 0; i < str.length; i++)
                {
                    if (i == 1 && str[1].isEmpty())
                    {
                        Toast.makeText(SetIpActivity.this, "请输入IP地址", Toast.LENGTH_SHORT).show();
                        isFinish = false;
                        break;
                    }
                    if (i == 2 && str[2].isEmpty() || str[2].substring(0, 1).equals("0"))
                    {
                        Toast.makeText(SetIpActivity.this, "请输入正确的端口号", Toast.LENGTH_SHORT).show();
                        isFinish = false;
                        break;
                    }
                }
            }
            if (isFinish)
                finish();
        }
        else
        {
            Toast.makeText(SetIpActivity.this, "请输入IP地址", Toast.LENGTH_SHORT).show();
        }
    }
    
    @OnClick(R.id.clear_ip)
    public void clearOnClick()
    {
        input_ip.setText("");
    }
    
}
