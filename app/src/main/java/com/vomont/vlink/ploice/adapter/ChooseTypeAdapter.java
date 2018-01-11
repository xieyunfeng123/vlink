package com.vomont.vlink.ploice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlink.bean.PoliceTpye;
import com.vomont.vlink.service.WifiAndPoliceService;

import java.util.List;

/**
 * Created by Administrator on 2017/12/19 0019.
 */

public class ChooseTypeAdapter extends BaseAdapter {

    private List<PoliceTpye> mlist;
    private Context context;
    private LayoutInflater inflater;

    OnPoliceListener onPoiceListener;

    public ChooseTypeAdapter(Context context) {
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
            convertView = inflater.inflate(R.layout.item_police_type, null);
            holder = new Holder();
            holder.img = convertView.findViewById(R.id.item_police_type_img);
            holder.name = convertView.findViewById(R.id.item_police_type_name);
            holder.ll = convertView.findViewById(R.id.item_police_type_ll);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if(mlist.get(position).isChoose())
        {
            holder.img.setVisibility(View.VISIBLE);
            holder.name.setTextColor(context.getResources().getColor(R.color.main_color));
        }
        else
        {
            holder.img.setVisibility(View.INVISIBLE);
            holder.name.setTextColor(context.getResources().getColor(R.color.text_color));
        }
        holder.name.setText(WifiAndPoliceService.getTypeName(mlist.get(position).getId()-1));
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPoiceListener != null) {
                    onPoiceListener.onClick(position,mlist.get(position).isChoose());
                }
            }
        });
        return convertView;
    }


    class Holder {
        ImageView img;
        TextView name;
        LinearLayout ll;
    }


    public void setOnPoiceListener(OnPoliceListener onPoiceListener) {
        this.onPoiceListener = onPoiceListener;
    }

    public interface OnPoliceListener {
        void onClick(int position, boolean isChoose);
    }


}
