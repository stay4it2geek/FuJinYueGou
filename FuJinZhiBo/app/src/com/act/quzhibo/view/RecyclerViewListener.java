package com.act.quzhibo.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

/**
 * Created by weiminglin on 17/5/31.
 */

public class RecyclerViewListener extends RecyclerView.OnScrollListener {

    ChangeScrollStateCallback mChangeScrollStateCallback;

    public void setChangeScrollStateCallback(ChangeScrollStateCallback mChangeScrollStateCallback) {
        this.mChangeScrollStateCallback = mChangeScrollStateCallback;

    }

    public interface ChangeScrollStateCallback {

         void change(int c);

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        mChangeScrollStateCallback.change(newState);

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

    }
}

