package com.vomont.vlink.img;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vomont.vlink.img.fragment.ImageDetailFragment;

import java.util.List;

/**
 * Created by Administrator on 2017/11/21 0021.
 */

public class FragAdapter extends FragmentStatePagerAdapter {

    private List<ImageDetailFragment> mFragments;


    public FragAdapter(FragmentManager fm) {
        super(fm);
    }

    public  void setData( List<ImageDetailFragment> fragments)
    {
        mFragments = fragments;
    }


    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int arg0) {
        return mFragments.get(arg0);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }


}