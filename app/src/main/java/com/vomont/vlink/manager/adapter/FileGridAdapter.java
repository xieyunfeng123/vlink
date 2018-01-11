package com.vomont.vlink.manager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.vomont.vlink.R;
import com.vomont.vlink.manager.bean.ItemFile;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/11/16 0016.
 */

public class FileGridAdapter extends BaseAdapter {

    private Context context;

    private List<ItemFile> mlist;

    public FileGridAdapter(Context context) {
        this.context = context;
    }


    public void setData(List<ItemFile> mlist) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_file_manage_grid, null);
            holder=new Holder();
            holder.itm_manage_grid_bg=convertView.findViewById(R.id.itm_manage_grid_bg);
            holder.itm_manage_grid_img=convertView.findViewById(R.id.itm_manage_grid_img);
            holder.itm_manage_grid_video=convertView.findViewById(R.id.itm_manage_grid_video);
            holder.itm_manage_grid_checked=convertView.findViewById(R.id.itm_manage_grid_checked);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if(mlist.get(position).getName().contains("mp4"))
        {
            holder.itm_manage_grid_video.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.itm_manage_grid_video.setVisibility(View.GONE);
        }
        if(mlist.get(position).isChoose())
        {
            holder.itm_manage_grid_bg.setVisibility(View.VISIBLE);
            holder.itm_manage_grid_checked.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.itm_manage_grid_bg.setVisibility(View.GONE);
            holder.itm_manage_grid_checked.setVisibility(View.GONE);
        }
        Glide.with(context).load(new File(mlist.get(position).getName())).into(holder.itm_manage_grid_img);
        return convertView;
    }


    class Holder {
        LinearLayout itm_manage_grid_bg;

        ImageView itm_manage_grid_img;

        ImageView itm_manage_grid_video;

        ImageView itm_manage_grid_checked;
    }
}
