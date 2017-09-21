package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.PhotoAlbumListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.domain.MediaInfo;
import com.act.quzhibo.entity.MediaAuthor;
import com.act.quzhibo.view.LoadNetView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class PhotoAlbumListFragment extends BackHandledFragment {

    private ArrayList<MediaInfo> medias = new ArrayList<>();
    private PhotoAlbumListAdapter mInfoListAdapter;
    private XRecyclerView recycleview;
    private LoadNetView loadNetView;
    private String lastTime = "";
    private int mediasSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = getView();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_preview, null);
            recycleview = (XRecyclerView) rootView.findViewById(R.id.rvPreview);
            recycleview.setLoadingListener(new XRecyclerView.LoadingListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recycleview.setNoMore(false);
                            recycleview.setLoadingMoreEnabled(true);
                            getPhotoListData(Constants.REFRESH);
                            recycleview.refreshComplete();
                        }
                    }, 1000);
                }

                @Override
                public void onLoadMore() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mediasSize > 0) {
                                getPhotoListData(Constants.LOADMORE);
                                recycleview.loadMoreComplete();
                            } else {
                                recycleview.setNoMore(true);
                            }
                        }
                    }, 1000);
                }
            });
            recycleview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            loadNetView = (LoadNetView) rootView.findViewById(R.id.loadview);
            loadNetView.setReloadButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadNetView.setlayoutVisily(Constants.LOAD);
                    getPhotoListData(Constants.REFRESH);
                }
            });
            getPhotoListData(Constants.REFRESH);
        }


        loadNetView.setLoadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                getPhotoListData(Constants.REFRESH);
            }
        });
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return rootView;
    }

    private void getPhotoListData(final int actionType) {
        BmobQuery<MediaInfo> query = new BmobQuery<>();
        BmobQuery<MediaInfo> query2 = new BmobQuery<>();
        List<BmobQuery<MediaInfo>> queries = new ArrayList<>();
        if (actionType == Constants.LOADMORE) {
            Date date;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
                query2.addWhereLessThanOrEqualTo("updatedAt", new BmobDate(date));
                queries.add(query2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        BmobQuery<MediaInfo> query3 = new BmobQuery<>();
        query3.addWhereEqualTo("authorId", ((MediaAuthor) getArguments().getSerializable("author")).getObjectId());
        Log.e("authoid", ((MediaAuthor) getArguments().getSerializable("author")).getObjectId());
        queries.add(query3);
        BmobQuery<MediaInfo> query4 = new BmobQuery<>();
        query4.addWhereEqualTo("type", Constants.PHOTO_ALBUM);
        queries.add(query4);
        query.and(queries);
        query.setLimit(10);
        query.order("-updatedAt");
        query.findObjects(new FindListener<MediaInfo>() {
            @Override
            public void done(List<MediaInfo> list, BmobException e) {
                if (e == null) {
                    if (actionType == Constants.REFRESH) {
                        medias.clear();
                    }
                    if (list.size() > 0) {
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                    }
                    Message message = new Message();
                    message.obj = list;
                    message.what = actionType;
                    handler.sendMessage(message);
                } else {
                    handler.sendEmptyMessage(Constants.NetWorkError);
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<MediaInfo> mediaInfos = (ArrayList<MediaInfo>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (mediaInfos != null) {
                    mediasSize = mediaInfos.size();
                    medias.addAll(mediaInfos);
                } else {
                    mediasSize = 0;
                    if(msg.what==Constants.LOADMORE){
                        recycleview.setNoMore(true);
                    }
                }
                if (mInfoListAdapter == null) {
                    mInfoListAdapter = new PhotoAlbumListAdapter(getActivity(), mediaInfos);
                    recycleview.setAdapter(mInfoListAdapter);
                } else {
                    mInfoListAdapter.notifyDataSetChanged();
                }
                loadNetView.setVisibility(View.GONE);
                if (medias.size() == 0) {
                    loadNetView.setVisibility(View.VISIBLE);
                    loadNetView.setlayoutVisily(Constants.NO_DATA);
                    return;
                }
            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }
        }
    };

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
