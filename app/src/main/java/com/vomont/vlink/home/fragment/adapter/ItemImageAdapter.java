package com.vomont.vlink.home.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vomont.vlink.R;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/11/15 0015.
 */

public class ItemImageAdapter extends BaseAdapter {

    Context context;

    private List<String> mlist;
    public  ItemImageAdapter(Context context)
    {
        this.context=context;
    }

    public void setData(List<String> mlist)
    {
        this.mlist=mlist;
    }


    @Override
    public int getCount() {
        int max=null!=mlist?mlist.size():0;
        if(max>4)
        {
            max=4;
        }
        return  max;
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
            convertView= LayoutInflater.from(context).inflate(R.layout.item_grid_video_img,null);
            holder=new Holder();
            holder.img=convertView.findViewById(R.id.item_grid_img);
            holder.isVideo=convertView.findViewById(R.id.item_grid_img_video);
            convertView.setTag(holder);
        }
        else
        {
            holder= (Holder) convertView.getTag();
        }
        if((mlist.get(position).substring(mlist.get(position).length()-3,mlist.get(position).length())).equals("mp4"))
        {
            holder.isVideo.setVisibility(View.VISIBLE);
//            Glide
//                    .with( context )
//                    .load( Uri.fromFile( new File( mlist.get(position) ) ) )
//                    .into( holder.img );
            Glide.with(context).load(new File(mlist.get(position).replace("mp4","jpg"))).skipMemoryCache(true).into(holder.img);
        }
        else
        {
            holder.isVideo.setVisibility(View.GONE);
            Glide.with(context).load(new File(mlist.get(position))).skipMemoryCache(true).into(holder.img);
        }
        return convertView;
    }



    class Holder
    {
        ImageView img;

        ImageView isVideo;
    }
}
