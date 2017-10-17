package com.act.quzhibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.base.BaseRecyclerAdapter;
import com.act.quzhibo.adapter.base.BaseRecyclerHolder;
import com.act.quzhibo.adapter.base.IMutlipleItem;
import com.act.quzhibo.bean.Friend;
import com.act.quzhibo.db.NewFriendManager;
import com.act.quzhibo.entity.RootUser;

import java.util.Collection;


/**联系人
 * 一种简洁的Adapter实现方式，可用于多种Item布局的recycleView实现，不用再写ViewHolder啦
 */
public class ContactAdapter extends BaseRecyclerAdapter<Friend> {

    public static final int TYPE_NEW_FRIEND = 0;
    public static final int TYPE_ITEM = 1;

    public ContactAdapter(Activity activity, IMutlipleItem<Friend> items, Collection<Friend> datas) {
        super(activity,items,datas);
    }

    @Override
    public void bindView(BaseRecyclerHolder holder, Friend friend, int position) {
        if(holder.layoutId== R.layout.item_contact){
            RootUser user =friend.getFriendUser();
            //好友头像
            holder.setImageView(user == null ? null : user.getAvatar(), R.mipmap.head, R.id.iv_recent_avatar);
            //好友名称
            holder.setText(R.id.tv_recent_name,user==null?"未知":user.getUsername());
        }else if(holder.layoutId==R.layout.header_new_friend){
            if(NewFriendManager.getInstance(activity).hasNewFriendInvitation()){
                holder.setVisible(R.id.iv_msg_tips, View.VISIBLE);
            }else{
                holder.setVisible(R.id.iv_msg_tips, View.GONE);
            }
        }
    }

}
