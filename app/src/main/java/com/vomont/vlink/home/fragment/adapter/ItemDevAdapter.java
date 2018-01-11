package com.vomont.vlink.home.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlinkersdk.WMChannelInfo;
import com.vomont.vlinkersdk.WMDeviceInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/11/3 0003.
 */

public class ItemDevAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<WMDeviceInfo> mlist;
    private int position=-1;

    private int cPosition=-1;

    public  ItemDevAdapter(Context context)
    {
            this.context=context;
    }

    public void setData(List<WMDeviceInfo> mlist)
    {
        this.mlist=mlist;
    }

    public  void  setSelectPosition(int position,int cPosition)
    {
        this.position=position;
        this.cPosition=cPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return null!=mlist?mlist.size():0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return null!=mlist.get(groupPosition).getChannelArr()?mlist.get(groupPosition).getChannelArr().length:0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mlist.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mlist.get(groupPosition).getChannelArr()[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
       GroupHolder groupHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ex_child_dev, null);
            groupHolder = new GroupHolder();
            groupHolder.name = convertView.findViewById(R.id.item_dev_chid_name);
            groupHolder.num = convertView.findViewById(R.id.item_dev_chid_num);
            groupHolder.max = convertView.findViewById(R.id.item_dev_chid_max);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.name.setText(mlist.get(groupPosition).getDevName());
        if (mlist.get(groupPosition).getChannelArr() != null) {
            int max = mlist.get(groupPosition).getChannelArr().length;
            int num = 0;
            for (WMChannelInfo info : mlist.get(groupPosition).getChannelArr()) {
                if (mlist.get(groupPosition).getStatus() == 1) {
                    num++;
                }
            }
            groupHolder.num.setText(num + "");
            groupHolder.max.setText(max + "");
        } else {
            groupHolder.num.setText("0");
            groupHolder.max.setText("0");
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ex_child_dev_channl, null);
            holder = new ChildHolder();
            holder.name = convertView.findViewById(R.id.item_dev_chid_channl_name);
            holder.item_dev_chid_channl_state=convertView.findViewById(R.id.item_dev_chid_channl_state);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        holder.name.setText("通道"+mlist.get(groupPosition).getChannelArr()[childPosition].getChannelId());
        if((position!=-1)&&(cPosition!=-1))
        {
            if((groupPosition==position)&&(childPosition==cPosition))
            {
                convertView.setBackgroundResource(R.color.back_color);
            }
            else
            {
                convertView.setBackgroundResource(R.color.white);
            }
        }
        else
        {
            convertView.setBackgroundResource(R.color.white);
        }
        if(mlist.get(groupPosition).getStatus()==1)
        {
            holder.item_dev_chid_channl_state.setTextColor(context.getResources().getColor(R.color.blue));
            holder.item_dev_chid_channl_state.setText("在线");
        }
        else
        {
            holder.item_dev_chid_channl_state.setTextColor(context.getResources().getColor(R.color.text_color));
            holder.item_dev_chid_channl_state.setText("离线");
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder
    {
        TextView name;

        TextView num;

        TextView max;
    }

    class ChildHolder
    {
        TextView name;
        TextView item_dev_chid_channl_state;
    }
}
