package com.vomont.vlink.home.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlinkersdk.WMUserEventMsg;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class PloiceAdapter extends BaseAdapter {
    Context context;

    List<WMUserEventMsg> mlist;

    public  PloiceAdapter(Context context)
    {
        this.context=context;
    }

    public void  setData( List<WMUserEventMsg> mlist)
    {
        this.mlist=mlist;
    }

    @Override
    public int getCount() {
        return null!=mlist?mlist.size():0;
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
        Holder holder=null;
        if(convertView==null)
        {
            convertView= LayoutInflater.from(context).inflate(R.layout.item_ploice,null);
            holder=new Holder();
            holder.police_dev_name=convertView.findViewById(R.id.police_dev_name);
            holder.police_type=convertView.findViewById(R.id.police_type);
            holder.police_get_data=convertView.findViewById(R.id.police_get_data);
            convertView.setTag(holder);
        }
        else
        {
            holder= (Holder) convertView.getTag();
        }
        holder.police_dev_name.setText(mlist.get(position).getName());
        holder.police_get_data.setText(mlist.get(position).getTime());
        holder.police_type.setText(mlist.get(position).getTypeName());

        return convertView;
    }


    class Holder
    {
        TextView police_dev_name;
        TextView police_type;
        TextView police_get_data;
    }
}
