package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.VideoRecyclerViewAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.bean.MediaInfo;
import com.act.quzhibo.bean.MediaAuthor;
import com.act.quzhibo.i.OnQueryDataListner;
import com.act.quzhibo.util.ViewDataUtil;
import com.act.quzhibo.widget.LoadNetView;
import com.devlin_n.videoplayer.player.IjkVideoView;
import com.devlin_n.videoplayer.player.VideoViewManager;
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

public class VideoAlbumListFragment extends BackHandledFragment {
     View view;
     LoadNetView loadNetView;
     XRecyclerView recyclerView;
     String lastTime = "";
     int handlerMediaInfoSize;
     ArrayList<MediaInfo> medias = new ArrayList<>();
     VideoRecyclerViewAdapter mInfoListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_common, null, false);
        loadNetView = (LoadNetView) view.findViewById(R.id.loadview);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                initMediaVideoListData(Constants.REFRESH);
            }
        });

        loadNetView.setLoadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                initMediaVideoListData(Constants.REFRESH);
            }
        });
        initView();
        return view;
    }

     void initView() {
        recyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview);
//        recyclerView.setPullRefreshEnabled(true);
//        recyclerView.setLoadingMoreEnabled(true);
//        recyclerView.setLoadingMoreProgressStyle(R.style.Small);
//        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        recyclerView.setNoMore(false);
//                        recyclerView.setLoadingMoreEnabled(true);
//                        recyclerView.refreshComplete();
//                    }
//                }, 1000);
//            }
//
//            @Override
//            public void onLoadMore() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (handlerMediaInfoSize > 0) {
//                            recyclerView.loadMoreComplete();
//
//                        } else {
//                            recyclerView.setNoMore(true);
//                        }
//                    }
//                }, 1000);
//            }
//        });
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
//        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(gridLayoutManager);

         ViewDataUtil.setLayManager(handlerMediaInfoSize, new OnQueryDataListner() {
             @Override
             public void onRefresh() {
                        initMediaVideoListData(Constants.REFRESH);
             }

             @Override
             public void onLoadMore() {
                            initMediaVideoListData(Constants.LOADMORE);
             }
         },getActivity(), recyclerView, 1, true, true);
        initMediaVideoListData(Constants.REFRESH);
    }

    @Override
    public void onPause() {
        super.onPause();
        IjkVideoView currentVideoPlayer = VideoViewManager.instance().getCurrentVideoPlayer();
        if (currentVideoPlayer != null) {
            currentVideoPlayer.release();
        }
    }



    @Override
    public boolean onBackPressed() {
        return false;
    }

     void initMediaVideoListData(final int actionType) {
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
        queries.add(query3);
        BmobQuery<MediaInfo> query4 = new BmobQuery<>();
        query4.addWhereEqualTo("type", Constants.VIDEO_ALBUM);
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
                        if(mInfoListAdapter!=null){
                            mInfoListAdapter.notifyDataSetChanged();
                        }
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
                    handlerMediaInfoSize = mediaInfos.size();
                    medias.addAll(mediaInfos);
                } else {
                    handlerMediaInfoSize = 0;

                }

                if (mInfoListAdapter == null) {
                    mInfoListAdapter = new VideoRecyclerViewAdapter(medias, getActivity());
                    recyclerView.setAdapter(mInfoListAdapter);
                    recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                        @Override
                        public void onChildViewAttachedToWindow(View view) {
                        }

                        @Override
                        public void onChildViewDetachedFromWindow(View view) {
                            IjkVideoView ijkVideoView = (IjkVideoView) view.findViewById(R.id.video_player);
                            if (ijkVideoView != null && !ijkVideoView.isFullScreen()) {
                                ijkVideoView.release();
                            }
                        }
                    });
                } else {
                    mInfoListAdapter.notifyDataSetChanged();
                }

                if (msg.what == Constants.LOADMORE) {
                    recyclerView.setNoMore(true);
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

}