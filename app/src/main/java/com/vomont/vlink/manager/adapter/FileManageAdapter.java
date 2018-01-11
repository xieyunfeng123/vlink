package com.vomont.vlink.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlink.manager.bean.ManageFile;
import com.vomont.vlink.view.NoScrollGridView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/11/16 0016.
 */

public class FileManageAdapter extends RecyclerView.Adapter<FileManageAdapter.MyViewHolder> {
    Context context;
    List<ManageFile> mlist;

    OnPicListener onPicListener;

    public FileManageAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<ManageFile> mlist) {
        this.mlist = mlist;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_file_manage, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        SimpleDateFormat format =new SimpleDateFormat("yyyyMMdd");
        try {
            Date date= format.parse(mlist.get(position).getDate());

            SimpleDateFormat formatTo =new SimpleDateFormat("yyyy-MM-dd");
            holder.item_manage_date.setText(formatTo.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        FileGridAdapter adapter = new FileGridAdapter(context);
        adapter.setData(mlist.get(position).getItemFiles());
        holder.item_manage_scrollgridview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        holder.item_manage_scrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int childposition, long id) {
                if(onPicListener!=null)
                {
                    onPicListener.OnClick(position,childposition);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView item_manage_date;

        NoScrollGridView item_manage_scrollgridview;

        public MyViewHolder(View view) {
            super(view);
            item_manage_date = (TextView) view.findViewById(R.id.item_manage_date);
            item_manage_scrollgridview = (NoScrollGridView) view.findViewById(R.id.item_manage_scrollgridview);
        }
    }

    public void setOnPicListener(OnPicListener onPicListener) {
            this.onPicListener=onPicListener;
    }


    public interface OnPicListener {
        void OnClick(int groupPosition, int childPosition);
    }

}
