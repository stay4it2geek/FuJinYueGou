package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.InterestListAdapter;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.InterestPlates;
import com.act.quzhibo.entity.InterestPlatesDetail;
import com.act.quzhibo.okhttp.OkHttpUtils;
import com.act.quzhibo.okhttp.callback.StringCallback;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewFindUtils;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;

import okhttp3.Call;

/**
 * 情趣板块
 */
public class InterestPlatesFragment extends Fragment {
    private PullLoadMoreRecyclerView recyclerview;
    private ArrayList<InterestPlatesDetail> interestPlatesDetails = new ArrayList<>();
    private View decorView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_square_common, null, false);
        recyclerview = (PullLoadMoreRecyclerView) view.findViewById(R.id.recycler_view);
        recyclerview.setLinearLayout();
        recyclerview.setPullRefreshEnable(false);
        recyclerview.setPushRefreshEnable(false);
        recyclerview.setGridLayout(1);
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
                InterestPlates interestPlates = CommonUtil.parseJsonWithGson(response, InterestPlates.class);
                interestPlatesDetails.addAll(interestPlates.result.plates);
                Message message=handler.obtainMessage();
                message.obj=interestPlatesDetails;
                handler.sendMessage(message);
            }
        });

    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<InterestPlatesDetail> details= (ArrayList<InterestPlatesDetail>) msg.obj;
            InterestListAdapter adapter=new InterestListAdapter(getContext(),details);
            adapter.setOnItemClickListener(new InterestListAdapter.OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position,String pid) {
                    initFragment(pid);
                }
            });
            recyclerview.setAdapter(adapter);
        }
    };


    InterestPostFragment interestPostFragment;

    private void initFragment(String pid){
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        //add,初始化fragment并添加到事务中，如果为null就new一个
        if(interestPostFragment == null){
            interestPostFragment = new InterestPostFragment();
            Bundle bundle=new Bundle();
            bundle.putString(Constants.PID,pid);
            interestPostFragment.setArguments(bundle);
            transaction.add(R.id.main_frame_layout, interestPostFragment);
        }
        transaction.addToBackStack("post");
        //提交事务
        transaction.commitAllowingStateLoss();

    }

}
