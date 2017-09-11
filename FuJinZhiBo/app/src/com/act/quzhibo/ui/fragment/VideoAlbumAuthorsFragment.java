package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.MediaAuthorListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.MediaAuthor;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.view.LoadNetView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.act.quzhibo.common.Constants.VIDEO_ALBUM;


public class VideoAlbumAuthorsFragment extends BackHandledFragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.video_authors_fragment, null);

        recyclerView = (XRecyclerView) view.findViewById(R.id.media_author_rv);
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
                        queryData(Constants.REFRESH);
                        recyclerView.refreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        queryData(Constants.LOADMORE);
                        recyclerView.loadMoreComplete();
                    }
                }, 1000);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        queryData(Constants.REFRESH);
        loadNetView = (LoadNetView) view.findViewById(R.id.loadview);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryData(Constants.REFRESH);
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;     //截断事件的传递
            }
        });
        return view;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    private XRecyclerView recyclerView;
    private MediaAuthorListAdapter mediaAuthorListAdapter;
    private LoadNetView loadNetView;
    private int limit = 10; // 每页的数据是10条
    private String lastTime = "";
    private ArrayList<MediaAuthor> medias = new ArrayList<>();
    private int mediasSize;


    /**
     * 分页获取数据
     *
     * @param actionType
     */
    private void queryData(final int actionType) {
        BmobQuery<MediaAuthor> query = new BmobQuery<>();
        BmobQuery<MediaAuthor> query2 = new BmobQuery<>();
        List<BmobQuery<MediaAuthor>> queries = new ArrayList<>();
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
        BmobQuery<MediaAuthor> query3 = new BmobQuery<>();
        query3.addWhereEqualTo("type", VIDEO_ALBUM);
        queries.add(query3);
        query.and(queries);
        query.order("-updatedAt");
        query.findObjects(new FindListener<MediaAuthor>() {
            @Override
            public void done(List<MediaAuthor> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if (actionType == Constants.REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            medias.clear();
                        }
                        medias.addAll(list);
                        lastTime = list.get(list.size() - 1).getCreatedAt();
                        Message message = new Message();
                        message.obj = medias;
                        message.what = actionType;
                        handler.sendMessage(message);
                    } else {
                        handler.sendEmptyMessage(Constants.NO_MORE);
                    }
                }else{
                    handler.sendEmptyMessage(Constants.NetWorkError);
                }
            }
        });
    }

    public static final class ComparatorValues implements Comparator<MediaAuthor> {

        @Override
        public int compare(MediaAuthor mediaAuthor1, MediaAuthor mediaAuthor2) {
            int m1 = Integer.parseInt(TextUtils.isEmpty(mediaAuthor1.age) ? "0" : mediaAuthor1.age);
            int m2 = Integer.parseInt(TextUtils.isEmpty(mediaAuthor2.age) ? "0" : mediaAuthor2.age);
            int result = 0;
            if (m1 > m2) {
                result = 1;
            }
            if (m1 < m2) {
                result = -1;
            }
            return result;
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<MediaAuthor> mediaAuthor = (ArrayList<MediaAuthor>) msg.obj;
            if (msg.what != Constants.NetWorkError) {
                if (msg.what != Constants.NO_MORE) {
                    if (mediaAuthor != null) {
                        mediasSize = mediaAuthor.size();
                    }
                    Collections.sort(medias, new ComparatorValues());
                    if (mediasSize > 0) {
                        if (mediaAuthorListAdapter == null) {
                            mediaAuthorListAdapter = new MediaAuthorListAdapter(getActivity(), mediaAuthor);
                            recyclerView.setAdapter(mediaAuthorListAdapter);
                            mediaAuthorListAdapter.setOnItemClickListener(new MediaAuthorListAdapter.OnMediaRecyclerViewItemClickListener() {
                                @Override
                                public void onItemClick(MediaAuthor mediaAuthor) {
                                    VideoAlbumListFragment videoAlbumListFragment = new VideoAlbumListFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("author", mediaAuthor);
                                    videoAlbumListFragment.setArguments(bundle);
                                    CommonUtil.switchFragment(videoAlbumListFragment, R.id._videolayoutContainer, getActivity());
                                }
                            });
                        } else {
                            mediaAuthorListAdapter.notifyDataSetChanged();
                        }
                    }

                    loadNetView.setVisibility(View.GONE);
                } else {
                    recyclerView.setNoMore(true);
                }
            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }
        }
    };

}
