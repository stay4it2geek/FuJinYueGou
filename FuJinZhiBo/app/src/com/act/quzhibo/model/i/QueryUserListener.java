package com.act.quzhibo.model.i;

import com.act.quzhibo.entity.RootUser;

import cn.bmob.newim.listener.BmobListener1;
import cn.bmob.v3.exception.BmobException;

/**
 * @author :smile
 * @project:QueryUserListener
 * @date :2016-02-01-16:23
 */
public abstract class QueryUserListener extends BmobListener1<RootUser> {

    public abstract void done(RootUser s, BmobException e);

    @Override
    protected void postDone(RootUser o, BmobException e) {
        done(o, e);
    }
}
