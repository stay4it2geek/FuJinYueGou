package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestPlatesListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPlates;
import com.act.quzhibo.entity.InterestPlatesParentData;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.ui.activity.SquareActivity;
import com.act.quzhibo.util.CommonUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 情趣板块
 */
public class InterestPlatesFragment extends BackHandledFragment {
    private XRecyclerView recyclerview;
    private ArrayList<InterestPlates> interestPlates = new ArrayList<>();
    private InterestPlatesListAdapter adapter;
    private ArrayList<InterestPlates> details = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_square_interest_paltes, null, false);
        recyclerview = (XRecyclerView) view.findViewById(R.id.recycler_view);
        recyclerview.setHasFixedSize(true);
        recyclerview.setPullRefreshEnabled(false);
        recyclerview.setLoadingMoreEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(linearLayoutManager);
        getData();
        return view;
    }

    private void getData() {
        OkHttpUtils.get().url(CommonUtil.getToggle(getActivity(), Constants.SQUARE_INTERES_TAB).getToggleObject()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }
            @Override
            public void onResponse(String response, int id) {
                InterestPlatesParentData interestPlatesParentData = CommonUtil.parseJsonWithGson(response, InterestPlatesParentData.class);
                for (InterestPlates plate : interestPlatesParentData.result.plates ) {
                    if (!plate.pName.contains("视频")){
                        interestPlates.add(plate);
                    }
                }
                Message message = handler.obtainMessage();
                message.obj = interestPlates;
                handler.sendMessage(message);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            details.addAll((ArrayList<InterestPlates>) msg.obj);
            adapter = new InterestPlatesListAdapter(getContext(), details);
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
        }
    };

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
