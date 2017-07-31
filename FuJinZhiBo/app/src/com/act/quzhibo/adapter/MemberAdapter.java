package com.act.quzhibo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.Member;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class MemberAdapter extends BaseListAdapter{
    ArrayList<Member> members;

    public MemberAdapter(Context ctx,  ArrayList<Member> members) {
        super(ctx);
        this.members=members;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_member, null, false);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (!TextUtils.isEmpty(members.get(position).headUrl)) {
            Glide.with(ctx).load(members.get(position).headUrl).into(viewHolder.avatar);
        }
        return  convertView ;
    }
    public class ViewHolder{
        ImageView avatar ;
    }
}
