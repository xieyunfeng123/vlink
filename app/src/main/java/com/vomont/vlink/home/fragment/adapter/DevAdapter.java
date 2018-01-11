package com.vomont.vlink.home.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlink.view.CustomExpandableListView;
import com.vomont.vlinkersdk.WMChannelInfo;
import com.vomont.vlinkersdk.WMDeviceGroup;
import com.vomont.vlinkersdk.WMDeviceInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/11/3 0003.
 */

public class DevAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<WMDeviceGroup> mlist;

    OnDevClickListener onDevItemClickListener;

    private LayoutInflater inflater;

    public DevAdapter(Context context) {
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    public void setData(List<WMDeviceGroup> mlist) {
        this.mlist = mlist;
    }


    @Override
    public int getGroupCount() {
        return null != mlist ? mlist.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //菜鸡一样的水平 如果用child的size 数据有多少 会重复多少次数据 坑的一批吊骚
        return mlist.get(groupPosition).getWmDeviceInfoList() != null ? 1 : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mlist.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mlist.get(groupPosition).getWmDeviceInfoList().get(childPosition);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ex_group, null);
            groupHolder = new GroupHolder();
            groupHolder.name = convertView.findViewById(R.id.item_dev_group_name);
            groupHolder.num = convertView.findViewById(R.id.item_dev_group_num);
            groupHolder.max = convertView.findViewById(R.id.item_dev_group_max);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        groupHolder.name.setText(mlist.get(groupPosition).getGroupName());
        if (mlist.get(groupPosition).getWmDeviceInfoList() != null) {
            int max = 0;
            int num = 0;
            for (WMDeviceInfo info : mlist.get(groupPosition).getWmDeviceInfoList()) {
                for (WMChannelInfo channelInfo : info.getChannelArr()) {
                    if (info.getStatus() == 1) {
                        num++;
                    }
                    max++;
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
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChidHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ex_child, null);
            holder = new ChidHolder();
            holder.list = convertView.findViewById(R.id.item_dev_ex);
            convertView.setTag(holder);
        } else {
            holder = (ChidHolder) convertView.getTag();
        }
        ItemDevAdapter adapter = new ItemDevAdapter(context);
        adapter.setData(mlist.get(groupPosition).getWmDeviceInfoList());
        holder.list.setAdapter(adapter);

        final ItemDevAdapter adapterFinal = adapter;

        holder.list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int itemGroupPosition, int itemChildPosition, long id) {
                adapterFinal.setSelectPosition(itemGroupPosition, itemChildPosition);
                if (onDevItemClickListener != null) {
                    onDevItemClickListener.OnClick(groupPosition, itemGroupPosition, itemChildPosition);
                }
                return false;
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    class GroupHolder {
        TextView name;

        TextView num;

        TextView max;
    }


    class ChidHolder {
        CustomExpandableListView list;

    }

    public void setOnDevItemClickListener(OnDevClickListener onDevItemClickListener) {
        this.onDevItemClickListener = onDevItemClickListener;
    }

    //11-03 15:45:48.410: E/insert(28867): yundd123456118.244.236.679000
    //11-03 15:45:52.882: E/insert(28867): 663===12
    public interface OnDevClickListener {
        void OnClick(int groupPosition, int childPosition, int lastPosition);
    }
}
