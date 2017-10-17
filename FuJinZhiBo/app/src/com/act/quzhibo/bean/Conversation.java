package com.act.quzhibo.bean;

import android.support.v4.app.FragmentActivity;

import com.act.quzhibo.adapter.base.BaseRecyclerAdapter;

import java.io.Serializable;

import cn.bmob.newim.bean.BmobIMConversationType;

/**
 * 对BmobIMConversation的抽象封装
 */
public abstract class Conversation implements Serializable,Comparable {

    /**
     * 会话id
     */
    protected String cId;
    /**
     * 会话类型
     */
    protected BmobIMConversationType cType;
    /**
     * 会话名称
     */
    protected String cName;

    /**
     * 获取头像-用于会话界面显示
     */
    abstract public Object getAvatar();

    /**
     * 获取最后一条消息的时间
     */
    abstract public long getLastMessageTime();

    /**
     * 获取最后一条消息的时间
     * @return
     */
    abstract public String getLastMessageContent();

    /**
     * 获取未读会话个数
     * @return
     */
    abstract public int getUnReadCount();

    /**
     * 将所有消息标记为已读
     */
    abstract public void readAllMessages();

    /**
     * 点击事件
     * @param activity
     */
    abstract public void onClick(FragmentActivity activity);

    /**
     * 长按事件
     * @param context
     */
    abstract public void onLongClick(FragmentActivity context,BaseRecyclerAdapter adapter,int position);

    public String getcName() {
        return cName;
    }

    public String getcId(){
        return cId;
    }

    public BmobIMConversationType getcType(){
        return cType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        if (!cId.equals(that.cId)) return false;
        return cType == that.cType;
    }

    @Override
    public int hashCode() {
        int result = cId.hashCode();
        result = 31 * result + cType.hashCode();
        return result;
    }


    @Override
    public int compareTo(Object another) {
        if (another instanceof Conversation){
            Conversation anotherConversation = (Conversation) another;
            long timeGap = anotherConversation.getLastMessageTime() - getLastMessageTime();
            if (timeGap > 0) return  1;
            else if (timeGap < 0) return -1;
            return 0;
        }else{
            throw new ClassCastException();
        }
    }

}
