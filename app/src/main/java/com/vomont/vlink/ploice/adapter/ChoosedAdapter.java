package com.vomont.vlink.ploice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlink.bean.PoliceTpye;
import com.vomont.vlink.service.WifiAndPoliceService;

import java.util.List;

/**
 * Created by Administrator on 2017/12/19 0019.
 */

public class ChoosedAdapter extends BaseAdapter {
    private List<PoliceTpye> mlist;
    private Context context;
    private LayoutInflater inflater;

    ChooseTypeAdapter.OnPoliceListener onPoiceListener;

    public ChoosedAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<PoliceTpye> mlist) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_choosed_police_type, null);
            holder = new Holder();
            holder.name = convertView.findViewById(R.id.item_has_choose_police_type);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.name.setText(WifiAndPoliceService.getTypeName(mlist.get(position).getId()-1));
        return convertView;
    }


    class Holder {
        TextView name;
    }
}
