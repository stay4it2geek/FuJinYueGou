package com.act.quzhibo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.Member;
import com.bumptech.glide.Glide;


public class MemberAdapter extends BaseListAdapter<Member> {
    public MemberAdapter(Context ctx) {
        super(ctx);
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
        Member member = datas.get(position);
        if (!TextUtils.isEmpty(member.img)) {
            Glide.with(ctx).load(member.img).placeholder(R.drawable.default_head).into(viewHolder.avatar);
        }else {
            viewHolder.avatar.setImageResource(R.drawable.default_head);
        }
        return  convertView ;
    }
    public class ViewHolder{
        ImageView avatar ;
    }
}
