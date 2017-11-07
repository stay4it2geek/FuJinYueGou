package com.act.quzhibo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestPlatesListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.OkHttpClientManager;
import com.act.quzhibo.bean.InterestPlates;
import com.act.quzhibo.bean.InterestPlatesParentData;
import com.act.quzhibo.download.event.DownloadStatusChanged;
import com.act.quzhibo.ui.activity.SquareActivity;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.widget.LoadNetView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class InterestPlatesFragment extends BackHandledFragment {
    private XRecyclerView recyclerview;
    private ArrayList<InterestPlates> interestPlates = new ArrayList<>();
    private InterestPlatesListAdapter adapter;
    private LoadNetView loadNetView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_square_interest_paltes, null, false);
        recyclerview = (XRecyclerView) view.findViewById(R.id.recyclerview);
        loadNetView = (LoadNetView) view.findViewById(R.id.loadview);
        view.findViewById(R.id.nearby_extran_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onNear();
            }
        });
        recyclerview.setPullRefreshEnabled(false);
        recyclerview.setLoadingMoreEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(linearLayoutManager);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                getData();
            }
        });
        getData();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SquareActivity) {
            callback = (OnNearByListner) context;
        }
    }

    OnNearByListner callback;

    public interface OnNearByListner {
        void onNear();
    }

    private void getData() {
        String url = CommonUtil.getToggle(getActivity(), Constants.SQUARE_INTERES_TAB).getToggleObject();
        OkHttpClientManager.parseRequest(getActivity(), url, handler, Constants.REFRESH);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            interestPlates.clear();
            if (msg.what != Constants.NetWorkError) {
                InterestPlatesParentData interestPlatesParentData = CommonUtil.parseJsonWithGson((String) msg.obj, InterestPlatesParentData.class);
                for (InterestPlates plate : interestPlatesParentData.result.plates) {
                    if (!plate.pName.contains("视频")) {
                        interestPlates.add(plate);
                    }
                }
                adapter = new InterestPlatesListAdapter(getContext(), interestPlates);
                adapter.setOnItemClickListener(new InterestPlatesListAdapter.OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, String pid) {
                        ((SquareActivity) getActivity()).setPid(pid);
                        CommonUtil.switchFragment(new InterestPostListFragment(), R.id.square_interest_plates_layout, getActivity());
                    }
                });
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerview.setLayoutManager(linearLayoutManager);
                recyclerview.setAdapter(adapter);
                loadNetView.setVisibility(View.GONE);
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
