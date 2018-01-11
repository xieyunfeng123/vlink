package com.vomont.vlink.more.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlink.bean.WifiBean;

import java.util.List;

/**
 * Created by Administrator on 2017/12/11 0011.
 */

public class WifiAdapter extends BaseAdapter {

    Context context;

    List<WifiBean> mlist;

    public WifiAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<WifiBean> mlist) {
        this.mlist = mlist;
    }

    @Override
    public int getCount() {
        return null != mlist ? mlist.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_wifi, null);
            holder = new Holder();
            holder.wifi_dev_name = convertView.findViewById(R.id.wifi_dev_name);
            holder.wifi_get_time = convertView.findViewById(R.id.wifi_get_time);
            holder.wifi_byte_data = convertView.findViewById(R.id.wifi_byte_data);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.wifi_dev_name.setText(mlist.get(position).getName());
        holder.wifi_get_time.setText(mlist.get(position).getTime());
        holder.wifi_byte_data.setText(mlist.get(position).getDes().substring(0,17));
        return convertView;
    }

    class Holder {
        TextView wifi_dev_name;
        TextView wifi_get_time;
        TextView wifi_byte_data;
    }
}
