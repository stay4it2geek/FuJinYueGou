package com.act.quzhibo.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MediaListAdapter;
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
    @Override
    public boolean onBackPressed() {
        return false;
    }

    private XRecyclerView recycleview;
    private MediaListAdapter mInfoListAdapter;
    private LoadNetView loadNetView;

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
                            initPhotoListData(Constants.REFRESH);
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
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        initPhotoListData(Constants.LOADMORE);
                                        recycleview.loadMoreComplete();
                                    }
                                }, 1000);
                            } else {
                                recycleview.setNoMore(true);
                            }
                        }
                    }, 1000);
                }
            });
            recycleview.addItemDecoration(new mediaPreviewItemDecoration(getActivity()));
            recycleview.setHasFixedSize(true);
            recycleview.setLayoutManager(new GridLayoutManager(getActivity(), 2));

            loadNetView = (LoadNetView) rootView.findViewById(R.id.loadview);
            loadNetView.setReloadButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadNetView.setlayoutVisily(Constants.LOAD);
                    initPhotoListData(Constants.REFRESH);
                }
            });
            initPhotoListData(Constants.REFRESH);
        }
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;     //截断事件的传递
            }
        });
        return rootView;
    }


    private int limit = 10; // 每页的数据是10条
    private String lastTime = "";
    private int mediasSize;

    private ArrayList<MediaInfo> medias = new ArrayList<>();

    /**
     * 分页获取数据
     *
     * @param actionType
     */
    private void initPhotoListData(final int actionType) {
        BmobQuery<MediaInfo> query = new BmobQuery<>();
        BmobQuery<MediaInfo> query2 = new BmobQuery<>();
        List<BmobQuery<MediaInfo>> queries = new ArrayList<>();

        query2.setLimit(limit);
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
        Log.e("authoid", ((MediaAuthor) getArguments().getSerializable("author")).getObjectId());
        queries.add(query3);
        BmobQuery<MediaInfo> query4 = new BmobQuery<>();
        query4.addWhereEqualTo("type", Constants.PHOTO_ALBUM);
        queries.add(query4);
        query.and(queries);
        query.order("-updatedAt");
        query.findObjects(new FindListener<MediaInfo>() {
            @Override
            public void done(List<MediaInfo> list, BmobException e) {
                if (e == null) {
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
                if (mediasSize > 0) {
                    if (mInfoListAdapter == null) {
                        mInfoListAdapter = new MediaListAdapter(getActivity(), mediaInfos);
                        recycleview.setAdapter(mInfoListAdapter);

                    } else {
                        mInfoListAdapter.notifyDataSetChanged();
                    }

                    loadNetView.setVisibility(View.GONE);
                } else {
                    recycleview.setNoMore(true);

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
