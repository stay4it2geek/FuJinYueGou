package com.act.quzhibo.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.adapter.DownloadListAdapter;
import com.act.quzhibo.download.domain.MediaInfo;
import com.act.quzhibo.view.LoadNetView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by weiminglin on 17/8/31.
 */

public class PhotoAlbumListFragment extends BackHandledFragment {
    @Override
    public boolean onBackPressed() {
        return false;
    }

    private RecyclerView mRvmediaPreview;
    private DownloadListAdapter mInfoListAdapter;
    private LoadNetView loadNetView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = getView();

        if (rootView == null) {

            rootView = inflater.inflate(R.layout.fragment_preview, null);

            mRvmediaPreview = (RecyclerView) rootView.findViewById(R.id.rvPreview);
            mRvmediaPreview.addItemDecoration(new mediaPreviewItemDecoration(getActivity()));
            mRvmediaPreview.setHasFixedSize(true);
            mRvmediaPreview.setLayoutManager(new GridLayoutManager(getActivity(), 2));

     
            loadNetView = (LoadNetView) rootView.findViewById(R.id.loadview);
            loadNetView.setReloadButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadNetView.setlayoutVisily(Constants.LOAD);
                    initmediaPreviewData(Constants.REFRESH);
                }
            });
            initmediaPreviewData(Constants.REFRESH);
        }
        return rootView;
    }




    private int limit = 10; // 每页的数据是10条
    private String lastTime = "";
    private int mediasSize;

    private ArrayList<MediaInfo> medias= new ArrayList<>();

    /**
     * 分页获取数据
     *
     * @param actionType
     */
    private void initmediaPreviewData(final int actionType) {
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
        query3.addWhereEqualTo("mediaType", Constants.VIDEO_ALBUM);
//        BmobQuery<MediaInfo> query4 = new BmobQuery<>();
//        query4.addWhereEqualTo("author", getArguments().getSerializable("author"));
        queries.add(query3);
//        queries.add(query4);
        query.and(queries);
        query.order("-updatedAt");
        query.findObjects(new FindListener<MediaInfo>() {
            @Override
            public void done(List<MediaInfo> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        ArrayList<MediaInfo> mediaModels = new ArrayList<>();
                        if (actionType == Constants.REFRESH) {
                            medias.clear();
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                            medias.addAll(mediaModels);
                        } else if (actionType == Constants.LOADMORE) {
                            medias.addAll(mediaModels);
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                        }

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
                        mInfoListAdapter = new DownloadListAdapter(getActivity());
                        mInfoListAdapter.setData(mediaInfos);
                        mRvmediaPreview.setAdapter(mInfoListAdapter);
                        mInfoListAdapter.setOnItemClickListener(new DownloadListAdapter.OnMediaInfoRecyclerViewItemClickListener() {

                            @Override
                            public void onItemClick(MediaInfo MediaInfo) {

                            }
                        });
                    }else{
                        mInfoListAdapter.notifyDataSetChanged();
                    }

                    loadNetView.setVisibility(View.GONE);
                } else {
                    loadNetView.setVisibility(View.VISIBLE);
                    loadNetView.setlayoutVisily(Constants.RELOAD);

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
