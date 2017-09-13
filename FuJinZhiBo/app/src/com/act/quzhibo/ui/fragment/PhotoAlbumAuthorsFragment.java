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

import static com.act.quzhibo.common.Constants.PHOTO_ALBUM;


public class PhotoAlbumAuthorsFragment extends BackHandledFragment {
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.media_authors_fragment, null);
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
                        if (mediasSize > 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    queryData(Constants.LOADMORE);
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

    private void queryData(final int actionType) {
        BmobQuery<MediaAuthor> query = new BmobQuery<>();
        BmobQuery<MediaAuthor> query2 = new BmobQuery<>();
        List<BmobQuery<MediaAuthor>> queries = new ArrayList<>();
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
        query3.addWhereEqualTo("type", PHOTO_ALBUM);
        queries.add(query3);
        query.and(queries);
        query.setLimit(10);
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
                        lastTime = list.get(list.size() - 1).getUpdatedAt();
                        Message message = new Message();
                        message.obj = list;
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
                if (mediaAuthor != null) {
                    mediasSize = mediaAuthor.size();
                    medias.addAll(mediaAuthor);
                } else {
                    mediasSize = 0;
                }
                Collections.sort(medias, new ComparatorValues());
                if (mediasSize > 0) {
                    if (mediaAuthorListAdapter == null) {
                        mediaAuthorListAdapter = new MediaAuthorListAdapter(getActivity(), medias);
                        recyclerView.setAdapter(mediaAuthorListAdapter);
                        mediaAuthorListAdapter.setOnItemClickListener(new MediaAuthorListAdapter.OnMediaRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(MediaAuthor mediaAuthor) {
                                PhotoAlbumListFragment photoAlbumListFragment = new PhotoAlbumListFragment();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("author", mediaAuthor);
                                photoAlbumListFragment.setArguments(bundle);
                                CommonUtil.switchFragment(photoAlbumListFragment, R.id.layoutContainer, getActivity());
                            }
                        });
                    } else {
                        mediaAuthorListAdapter.notifyDataSetChanged();
                    }
                }
                loadNetView.setVisibility(View.GONE);
            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }
        }
    };
}
