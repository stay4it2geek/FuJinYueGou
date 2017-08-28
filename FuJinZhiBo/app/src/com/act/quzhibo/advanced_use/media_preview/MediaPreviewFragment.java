package com.act.quzhibo.advanced_use.media_preview;

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
import com.act.quzhibo.advanced_use.model.MediaInfo;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.ui.fragment.BackHandledFragment;
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

public class MediaPreviewFragment extends BackHandledFragment {


    private RecyclerView mRvmediaPreview;
    private MediaPreviewAdapter mMediaPreviewAdapter;
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

            if (mMediaPreviewAdapter != null) {
                mMediaPreviewAdapter.release();
            }
            mMediaPreviewAdapter = new MediaPreviewAdapter(getActivity(),null);
            mRvmediaPreview.setAdapter(mMediaPreviewAdapter);
            loadNetView= (LoadNetView) rootView.findViewById(R.id.loadview);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPreviewAdapter != null) {
            mMediaPreviewAdapter.release();
        }
    }

    
    private int limit = 10; // 每页的数据是10条
    private String lastTime = "";
    private ArrayList<MediaInfo> medias = new ArrayList<>();
    private int mediasSize;


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
        query3.addWhereEqualTo("mediaType", "0");
        BmobQuery<MediaInfo> query4 = new BmobQuery<>();
        query4.addWhereEqualTo("author", getArguments().getSerializable("author"));
        queries.add(query3);
        queries.add(query4);
        query.and(queries);
        query.order("-updatedAt");
        query.findObjects(new FindListener<MediaInfo>() {
            @Override
            public void done(List<MediaInfo> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if (actionType == Constants.REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            medias.clear();
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                            medias.addAll(list);
                        } else if (actionType == Constants.LOADMORE) {
                            medias.addAll(list);
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                        }
                        Message message = new Message();
                        message.obj = medias;
                        message.what = actionType;
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(Constants.NO_MORE);
                    }
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
                if (msg.what != Constants.NO_MORE) {
                    if (mediaInfos != null) {
                        mediasSize = mediaInfos.size();
                    }
//                    Collections.sort(medias, new ComparatorValues());
                    if (mediasSize > 0) {
                        if (mMediaPreviewAdapter == null) {
                            mMediaPreviewAdapter.update(getActivity(),mediaInfos);
                            mMediaPreviewAdapter.setOnItemClickListener(new MediaPreviewAdapter.OnMediaInfoRecyclerViewItemClickListener() {

                                @Override
                                public void onItemClick(MediaInfo mediaInfo) {

                                }
                            });
                        } else {
                            mMediaPreviewAdapter.notifyDataSetChanged();
                        }
                    }

                    loadNetView.setVisibility(View.GONE);
                } else {
                    loadNetView.setVisibility(View.VISIBLE);
                    loadNetView.setlayoutVisily(Constants.RELOAD);
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
