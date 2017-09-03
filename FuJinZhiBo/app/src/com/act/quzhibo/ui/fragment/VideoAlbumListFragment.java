package com.act.quzhibo.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.VideoRecyclerViewAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.domain.MediaInfo;
import com.act.quzhibo.entity.MediaAuthor;
import com.act.quzhibo.view.LoadNetView;
import com.act.quzhibo.view.RecycleViewDivider;
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
    private LoadNetView loadNetView;
    private XRecyclerView recyclerView;

    private String lastTime = "";
    private int mediasSize;
    private ArrayList<MediaInfo> medias = new ArrayList<>();
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

        initView();
        return view;
    }

    private void initView() {
        recyclerView = (XRecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setLoadingMoreProgressStyle(R.style.Small);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setNoMore(false);
                        recyclerView.setLoadingMoreEnabled(true);
                        initMediaVideoListData(Constants.REFRESH);
                        recyclerView.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mediasSize > 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    initMediaVideoListData(Constants.LOADMORE);
                                    recyclerView.loadMoreComplete();
                                }
                            }, 1000);
                        } else {
                            recyclerView.setNoMore(true);
                        }
                    }
                }, 1000);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(gridLayoutManager);
        initMediaVideoListData(Constants.REFRESH);
    }

//    public List<VideoBean> getVideoList() {
//        List<VideoBean> videoList = new ArrayList<>();
//        videoList.add(new VideoBean("办公室小野开番外了，居然在办公室开澡堂！老板还点赞？",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-30-43.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4"));
//
//        videoList.add(new VideoBean("小野在办公室用丝袜做茶叶蛋 边上班边看《外科风云》",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-09-58.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-20-26.mp4"));
//
//        videoList.add(new VideoBean("花盆叫花鸡，怀念玩泥巴，过家家，捡根竹竿当打狗棒的小时候",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_12-52-08.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_13-02-41.mp4"));
//
//        videoList.add(new VideoBean("针织方便面，这可能是史上最不方便的方便面",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-18-22.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-20-56.mp4"));
//
//        videoList.add(new VideoBean("宵夜的下午茶，办公室不只有KPI，也有诗和远方",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-00-28.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-06-25.mp4"));
//
//        videoList.add(new VideoBean("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
//
//        videoList.add(new VideoBean("办公室小野开番外了，居然在办公室开澡堂！老板还点赞？",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-30-43.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4"));
//
//        videoList.add(new VideoBean("小野在办公室用丝袜做茶叶蛋 边上班边看《外科风云》",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-09-58.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-10_10-20-26.mp4"));
//
//        videoList.add(new VideoBean("花盆叫花鸡，怀念玩泥巴，过家家，捡根竹竿当打狗棒的小时候",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_12-52-08.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-03_13-02-41.mp4"));
//
//        videoList.add(new VideoBean("针织方便面，这可能是史上最不方便的方便面",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-18-22.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-28_18-20-56.mp4"));
//
//        videoList.add(new VideoBean("宵夜的下午茶，办公室不只有KPI，也有诗和远方",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-00-28.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-26_10-06-25.mp4"));
//
//        videoList.add(new VideoBean("可乐爆米花，嘭嘭嘭......收花的人说要把我娶回家",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-37-16.jpg",
//                "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/04/2017-04-21_16-41-07.mp4"));
//        loadNetView.setVisibility(View.GONE);
////    } else {
////        loadNetView.setVisibility(View.VISIBLE);
////        loadNetView.setlayoutVisily(Constants.RELOAD)
//        return videoList;
//    }

    @Override
    public void onPause() {
        super.onPause();
        IjkVideoView currentVideoPlayer = VideoViewManager.instance().getCurrentVideoPlayer();
        if (currentVideoPlayer != null) {
            currentVideoPlayer.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    /**
     * 分页获取数据
     *
     * @param actionType
     */
    private void initMediaVideoListData(final int actionType) {
        BmobQuery<MediaInfo> query = new BmobQuery<>();
        BmobQuery<MediaInfo> query2 = new BmobQuery<>();
        List<BmobQuery<MediaInfo>> queries = new ArrayList<>();

        query2.setLimit(10);
        if (actionType == Constants.LOADMORE) {
            // 只查询小于最后一个item发表时间的数据
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
        query.order("-updatedAt");
        query.findObjects(new FindListener<MediaInfo>() {
            @Override
            public void done(List<MediaInfo> list, BmobException e) {
                if (e == null) {
                    Log.e("list.size()",list.size()+"ppp");
                    if (list.size() > 0) {
                        if (actionType == Constants.REFRESH) {
                            medias.clear();
                        }
                        lastTime = list.get(list.size() - 1).getCreatedAt();
                        medias.addAll(list);
                        Message message = new Message();
                        message.obj = medias;
                        message.what = actionType;
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(Constants.NO_MORE);
                    }
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
                }
//                    Collections.sort(medias, new ComparatorValues());
                if (mediasSize > 0) {
                    if (mInfoListAdapter == null) {
                        mInfoListAdapter = new VideoRecyclerViewAdapter(mediaInfos, getActivity());
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

                    loadNetView.setVisibility(View.GONE);

                } else {
                    recyclerView.setNoMore(true);
                }
            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.NetWorkError);
            }
        }
    };


    public static class mediaPreviewItemDecoration extends RecyclerView.ItemDecoration {

        private int margin;

        public mediaPreviewItemDecoration(Context context) {
            margin = context.getResources().getDimensionPixelSize(R.dimen
                    .media_preview_item_decoration_margin);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(margin, margin, margin, margin);
        }

    }


}