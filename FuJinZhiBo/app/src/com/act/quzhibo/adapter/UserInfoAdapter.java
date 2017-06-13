package com.act.quzhibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.act.quzhibo.R;

/**
 * Created by weiminglin on 17/6/13.
 */

public class UserInfoAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private Context context;

    public UserInfoAdapter(Context context,String[] titles){
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}