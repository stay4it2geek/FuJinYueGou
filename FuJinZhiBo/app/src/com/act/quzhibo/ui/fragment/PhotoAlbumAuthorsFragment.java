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
import com.act.quzhibo.bean.MediaAuthor;
import com.act.quzhibo.i.OnQueryDataListner;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewDataUtil;
import com.act.quzhibo.widget.LoadNetView;
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
     XRecyclerView recyclerView;
     MediaAuthorListAdapter mediaAuthorListAdapter;
     LoadNetView loadNetView;
     String lastTime = "";
     ArrayList<MediaAuthor> mediaAuthors = new ArrayList<>();
     int handlerMediaAuthorSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_layout, null);
        recyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview);
        ViewDataUtil.setLayManager(handlerMediaAuthorSize, new OnQueryDataListner() {
            @Override
            public void onRefresh() {
                queryData(Constants.REFRESH);
            }

            @Override
            public void onLoadMore() {
                            queryData(Constants.LOADMORE);
            }
        },getActivity(), recyclerView, 1, true, true);

        queryData(Constants.REFRESH);
        loadNetView = (LoadNetView) view.findViewById(R.id.loadview);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryData(Constants.REFRESH);
            }
        });

        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                queryData(Constants.REFRESH);
            }
        });


        loadNetView.setLoadButtonListener(new View.OnClickListener() {
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


     void queryData(final int actionType) {
        BmobQuery<MediaAuthor> query = new BmobQuery<>();
        BmobQuery<MediaAuthor> query2 = new BmobQuery<>();
        List<BmobQuery<MediaAuthor>> queries = new ArrayList<>();
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
                            mediaAuthors.clear();
                            if(mediaAuthorListAdapter!=null){
                                mediaAuthorListAdapter.notifyDataSetChanged();
                            }
                        }
                        if (list.size() > 0) {
                            lastTime = list.get(list.size() - 1).getUpdatedAt();
                        }
                        Message message = new Message();
                        message.obj = list;
                        message.what = actionType;
                        handler.sendMessage(message);
                    }
                } else {
                    handler.sendEmptyMessage(Constants.NetWorkError);
                }
            }
        });
    }

    public static class ComparatorValues implements Comparator<MediaAuthor> {

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
                    handlerMediaAuthorSize = mediaAuthor.size();
                    mediaAuthors.addAll(mediaAuthor);
                } else {
                    handlerMediaAuthorSize = 0;
                }
                Collections.sort(mediaAuthors, new ComparatorValues());

                    if (mediaAuthorListAdapter == null) {
                        mediaAuthorListAdapter = new MediaAuthorListAdapter(getActivity(), mediaAuthors);
                        recyclerView.setAdapter(mediaAuthorListAdapter);
                        mediaAuthorListAdapter.setOnItemClickListener(new MediaAuthorListAdapter.OnMediaRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(MediaAuthor mediaAuthor) {
                                PhotoAlbumListFragment photoAlbumListFragment = new PhotoAlbumListFragment();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("author", mediaAuthor);
                                photoAlbumListFragment.setArguments(bundle);
                                ViewDataUtil.switchFragment(photoAlbumListFragment, R.id.layoutContainer, getActivity());
                            }
                        });
                    } else {
                        mediaAuthorListAdapter.notifyDataSetChanged();
                    }
                }
                loadNetView.setVisibility(View.GONE);
                if (handlerMediaAuthorSize == 0) {
                    loadNetView.setVisibility(View.VISIBLE);
                    loadNetView.setlayoutVisily(Constants.NO_DATA);
                    return;
                }

        }
    };
}
