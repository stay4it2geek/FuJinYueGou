package com.act.quzhibo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.CourseCategoryInfo;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CourseCategoryAdapter extends BaseAdapter {
    ArrayList<CourseCategoryInfo> courseCatogerys;
    Context context;

    public CourseCategoryAdapter(ArrayList<CourseCategoryInfo> courseCatogerys, Context context) {
        this.courseCatogerys = courseCatogerys;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_member, null, false);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            viewHolder.categoryImg = (ImageView) convertView.findViewById(R.id.categoryImg);

            viewHolder.categoryName = (TextView) convertView.findViewById(R.id.categoryName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.categoryImg.setVisibility(View.VISIBLE);
        viewHolder.avatar.setVisibility(View.GONE);
        viewHolder.categoryName.setVisibility(View.VISIBLE);
        viewHolder.categoryName.setText(courseCatogerys.get(position).coursesCategoryName);

        if (!TextUtils.isEmpty(courseCatogerys.get(position).courseCategoryImgPath)) {
            Glide.with(context).load(courseCatogerys.get(position).courseCategoryImgPath).into(viewHolder.categoryImg);
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView avatar;
        ImageView categoryImg;
        TextView categoryName;
    }


    @Override
    public int getCount() {
        return courseCatogerys.size();
    }

    @Override
    public Object getItem(int position) {
        return courseCatogerys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
