package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.VideoRecyclerViewAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestSubPerson;
import com.act.quzhibo.entity.NearPhotoEntity;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.LoadNetView;
import com.devlin_n.videoplayer.player.IjkVideoView;
import com.devlin_n.videoplayer.player.VideoViewManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;


public class NearMediaVideoListActivity extends AppCompatActivity{

    private LoadNetView loadNetView;
    private XRecyclerView recyclerView;
    private int handlerMediaInfoSize;
    private ArrayList<NearPhotoEntity> medias = new ArrayList<>();
    private VideoRecyclerViewAdapter mInfoListAdapter;
    InterestSubPerson subPerson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
//        subPerson= (InterestSubPerson) getIntent().getSerializableExtra(Constants.NEAR_VIDEO_USER);
//        loadNetView = (LoadNetView) findViewById(R.id.loadview);
//        loadNetView.setReloadButtonListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadNetView.setlayoutVisily(Constants.LOAD);
////                initMediaVideoListData(Constants.REFRESH);
//            }
//        });
//
//        loadNetView.setLoadButtonListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadNetView.setlayoutVisily(Constants.LOAD);
////                initMediaVideoListData(Constants.REFRESH);
//            }
//        });
//        initView();
    }


    private void initView() {

        recyclerView = (XRecyclerView) findViewById(R.id.recycler_view);
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
//                        initMediaVideoListData(Constants.REFRESH);
                        recyclerView.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (handlerMediaInfoSize > 0) {

//                            initMediaVideoListData(Constants.LOADMORE);
                            recyclerView.loadMoreComplete();

                        } else {
                            recyclerView.setNoMore(true);
                        }
                    }
                }, 1000);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(NearMediaVideoListActivity.this, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
//        initMediaVideoListData(Constants.REFRESH);
    }

    @Override
    public void onPause() {
        super.onPause();
        IjkVideoView currentVideoPlayer = VideoViewManager.instance().getCurrentVideoPlayer();
        if (currentVideoPlayer != null) {
            currentVideoPlayer.release();
        }
    }




    private void initMediaVideoListData() {
        final ArrayList<NearPhotoEntity> nearPhotoEntities = CommonUtil.jsonToArrayList(subPerson.photoLibraries, NearPhotoEntity.class);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<NearPhotoEntity> mediaInfos = (ArrayList<NearPhotoEntity>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (mediaInfos != null) {
                    handlerMediaInfoSize = mediaInfos.size();
                    medias.addAll(mediaInfos);
                } else {
                    handlerMediaInfoSize = 0;
                    if (msg.what == Constants.LOADMORE) {
                        recyclerView.setNoMore(true);
                    }
                }

                if (mInfoListAdapter == null) {
//                    mInfoListAdapter = new VideoRecyclerViewAdapter(medias, NearMediaVideoListActivity.this);
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
