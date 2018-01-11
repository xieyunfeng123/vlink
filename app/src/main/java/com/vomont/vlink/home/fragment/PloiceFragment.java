package com.vomont.vlink.home.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vomont.vlink.R;
import com.vomont.vlink.home.fragment.adapter.PloiceAdapter;
import com.vomont.vlink.ploice.PloiceDetailActivity;
import com.vomont.vlink.ploice.PloiceTypeActivity;
import com.vomont.vlinkersdk.WMUserEventMsg;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/10/30 0030.
 */

public class PloiceFragment extends Fragment {

    @BindView(R.id.ploice_list)
    public ListView ploice_list;

    @BindView(R.id.empty_view_police)
    public RelativeLayout empty_view_police;

    @BindView(R.id.top_search)
    public ImageView top_search;

    @BindView(R.id.top_name)
    TextView top_name;

    private PloiceAdapter adapter;


    private List<WMUserEventMsg> mlist;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_ploice,container,false);
        ButterKnife.bind(this,view);
        top_search.setVisibility(View.VISIBLE);
        top_name.setText("报警");
        adapter=new PloiceAdapter(getActivity());
        ploice_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        ploice_list.setEmptyView(empty_view_police);
        ploice_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), PloiceDetailActivity.class);
                intent.putExtra("police",mlist.get(position));
                startActivity(intent);
            }
        });

        top_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), PloiceTypeActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
    public  void  setData(List<WMUserEventMsg> mlist)
    {
        if(ploice_list!=null&&adapter!=null) {
            this.mlist=mlist;
                adapter.setData(mlist);
                adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
