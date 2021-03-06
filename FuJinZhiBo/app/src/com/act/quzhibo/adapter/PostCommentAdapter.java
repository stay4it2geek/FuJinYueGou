package com.act.quzhibo.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.ProvinceAndCityEntify;
import com.act.quzhibo.R;
import com.act.quzhibo.entity.InterestPostPageCommentDetail;
import com.act.quzhibo.util.CommonUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by weiminglin on 17/6/4.
 */

public class PostCommentAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<InterestPostPageCommentDetail> commentDetails;

    public PostCommentAdapter(Context context, ArrayList<InterestPostPageCommentDetail> commentDetails) {
        this.context = context;
        this.commentDetails = commentDetails;

    }

    public int getCount() {
        return commentDetails.size();
    }

    public Object getItem(int item) {
        return item;
    }

    public long getItemId(int id) {
        return id;
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_comment_layout, parent, false);
            viewHolder.nickName = (TextView) view.findViewById(R.id.nickName);
            viewHolder.createTime = (TextView) view.findViewById(R.id.createTime);
            viewHolder.arealocation = (TextView) view.findViewById(R.id.arealocation);
            viewHolder.userImage = (ImageView) view.findViewById(R.id.userImage);
            viewHolder.sexAndAge = (TextView) view.findViewById(R.id.sexAndAge);
            viewHolder.content = (io.github.rockerhieu.emojicon.EmojiconTextView) view.findViewById(R.id.re_content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Glide.with(context).load(commentDetails.get(position).user.photoUrl).placeholder(R.drawable.ic_launcher).into(viewHolder.userImage);//加载网络图片
        viewHolder.arealocation.setText("fffflocaion");
        long l=System.currentTimeMillis()-commentDetails.get(position).ctime;
        long day=l/(24*60*60*1000);
        long hour=(l/(60*60*1000)-day*24);
        long min=((l/(60*1000))-day*24*60-hour*60);
        if(!commentDetails.get(position).user.sex.equals("2") ){
            viewHolder.sexAndAge.setBackgroundColor(context.getResources().getColor(R.color.blue));
        }
        viewHolder.sexAndAge.setText(commentDetails.get(position).user.sex.equals("2") ? "女": "男");
        viewHolder.createTime.setText(hour + "小时"+min+"分钟前");
        viewHolder.nickName.setText(commentDetails.get(position).user.nick);
        viewHolder.content.setText(commentDetails.get(position).message+"");
       new AsyncTask<Void, Void, String>() {
           @Override
           protected String doInBackground(Void... params) {
               ArrayList<ProvinceAndCityEntify> datas = CommonUtil.parseLocation(context).data;
               if (null != datas) {
                   for(ProvinceAndCityEntify entify:datas){
                       if (TextUtils.equals(commentDetails.get(position).user.proCode,entify.proId + "")) {
                           for (ProvinceAndCityEntify.CitySub citySub :entify.citySub){
                               if (TextUtils.equals(commentDetails.get(position).user.cityCode, citySub.cityId + "")) {
                                  return !TextUtils.equals("",entify.name+citySub.name+"")?entify.name + citySub.name + "":"----";
                               }
                           }
                       }
                   }
               }
               return "----";
           }

           @Override
           protected void onPostExecute(String text) {
               super.onPostExecute(text);
               viewHolder.arealocation.setText(text);
           }
       }.execute();
        return view;
    }

    public class ViewHolder {
        private TextView arealocation;
        private TextView createTime;
        private ImageView userImage;
        private TextView sexAndAge;
        private TextView nickName;
        private io.github.rockerhieu.emojicon.EmojiconTextView content;
    }

}
