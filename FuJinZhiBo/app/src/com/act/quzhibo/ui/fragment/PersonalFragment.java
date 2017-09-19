package com.act.quzhibo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.download.activity.DownloadManagerActivity;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.ui.activity.MakeMoneyActivity;
import com.act.quzhibo.ui.activity.MyFocusPersonActivity;
import com.act.quzhibo.ui.activity.MyPostListActivity;
import com.act.quzhibo.ui.activity.RegisterActivity;
import com.act.quzhibo.ui.activity.SettingMineInfoActivity;
import com.act.quzhibo.ui.activity.TermOfUseActivity;
import com.act.quzhibo.ui.activity.VIPConisTableActivity;
import com.act.quzhibo.ui.activity.VipPolicyActivity;
import com.act.quzhibo.ui.activity.LoginActivity;
import com.act.quzhibo.ui.activity.MyFocusShowerActivity;
import com.act.quzhibo.ui.activity.GetVipPayActivity;
import com.act.quzhibo.ui.activity.VipOrdersActivity;
import com.act.quzhibo.ui.activity.WhoLikeThenSeeMeActivity;
import com.act.quzhibo.util.CommonUtil;

import cn.bmob.v3.BmobUser;

public class PersonalFragment extends Fragment implements View.OnClickListener {
    RootUser rootUser;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_personal, null, false);
        if (CommonUtil.getToggle(getActivity(), Constants.SQUARE_AND_MONEY).getIsOpen().equals("false")) {
            view.findViewById(R.id.vip_policy).setVisibility(View.GONE);
            view.findViewById(R.id.myVideo_download_layout).setVisibility(View.GONE);
            view.findViewById(R.id.myIMG_download_layout).setVisibility(View.GONE);
            view.findViewById(R.id.noReslayout).setVisibility(View.GONE);
            view.findViewById(R.id.myPostlayout).setVisibility(View.GONE);
            view.findViewById(R.id.who_see_me).setVisibility(View.GONE);
            view.findViewById(R.id.myfocus_person).setVisibility(View.GONE);
            view.findViewById(R.id.myfocus_shower).setVisibility(View.GONE);
        }

        view.findViewById(R.id.vipLevel).setOnClickListener(this);
        view.findViewById(R.id.vip_policy).setOnClickListener(this);
        view.findViewById(R.id.get_vip).setOnClickListener(this);
        view.findViewById(R.id.vip_order_listlayout).setOnClickListener(this);
        view.findViewById(R.id.who_see_me).setOnClickListener(this);
        view.findViewById(R.id.myfocus_person).setOnClickListener(this);
        view.findViewById(R.id.myfocus_shower).setOnClickListener(this);
        view.findViewById(R.id.settingDetailayout).setOnClickListener(this);
        view.findViewById(R.id.noReslayout).setOnClickListener(this);
        view.findViewById(R.id.myVideo_download_layout).setOnClickListener(this);
        view.findViewById(R.id.myIMG_download_layout).setOnClickListener(this);
        view.findViewById(R.id.myPostlayout).setOnClickListener(this);
        view.findViewById(R.id.logout).setOnClickListener(this);
        view.findViewById(R.id.noRes).setOnClickListener(this);
        view.findViewById(R.id.registerLayout).setOnClickListener(this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;     //截断事件的传递
            }
        });
        return view;
    }

    private void queryWhoSeeMe() {
    }

    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.vipLevel) {
            startActivity(new Intent(getActivity(), VIPConisTableActivity.class));
            return;
        }
        view.setBackgroundColor(getResources().getColor(R.color.colorbg));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setBackgroundColor(getResources().getColor(R.color.white));
                if (rootUser == null) {
                    if (view.getId() == R.id.vip_policy) {
                        getActivity().startActivity(new Intent(getActivity(), VipPolicyActivity.class));
                    } else if (view.getId() == R.id.get_vip) {
                        getActivity().startActivity(new Intent(getActivity(), GetVipPayActivity.class));
                    } else if (view.getId() == R.id.noReslayout) {
                        getActivity().startActivity(new Intent(getActivity(), TermOfUseActivity.class));
                    } else if (view.getId() == R.id.registerLayout) {
                        getActivity().startActivity(new Intent(getActivity(), RegisterActivity.class));
                    } else {
                        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                    return;
                }
                switch (view.getId()) {
                    case R.id.makemoneyLayout:
                        getActivity().startActivity(new Intent(getActivity(), MakeMoneyActivity.class));
                        break;
                    case  R.id.checkoutWalletLayout:
                        getActivity().startActivity(new Intent(getActivity(), VipPolicyActivity.class));
                        break;
                    case R.id.vip_policy:
                        getActivity().startActivity(new Intent(getActivity(), VipPolicyActivity.class));
                        break;
                    case R.id.vip_order_listlayout:
                        getActivity().startActivity(new Intent(getActivity(), VipOrdersActivity.class));
                        break;
                    case R.id.get_vip:
                        getActivity().startActivity(new Intent(getActivity(), GetVipPayActivity.class));
                        break;
                    case R.id.who_see_me:
                        getActivity().startActivity(new Intent(getActivity(), WhoLikeThenSeeMeActivity.class));
                        break;
                    case R.id.myfocus_shower:
                        getActivity().startActivity(new Intent(getActivity(), MyFocusShowerActivity.class));
                        break;
                    case R.id.myfocus_person:
                        getActivity().startActivity(new Intent(getActivity(), MyFocusPersonActivity.class));
                        break;
                    case R.id.myVideo_download_layout:
                        Intent videoIntent=new Intent();
                        videoIntent.putExtra(Constants.DOWN_LOAD_TYPE,Constants.VIDEO_ALBUM);
                        videoIntent.setClass(getActivity(),DownloadManagerActivity.class);
                        getActivity().startActivity(videoIntent);
                        break;
                    case R.id.myIMG_download_layout:
                        Intent photoIntent=new Intent();
                        photoIntent.putExtra(Constants.DOWN_LOAD_TYPE,Constants.PHOTO_ALBUM);
                        photoIntent.setClass(getActivity(),DownloadManagerActivity.class);
                        getActivity().startActivity(photoIntent);
                        break;
                    case R.id.settingDetailayout:
                        getActivity().startActivity(new Intent(getActivity(), SettingMineInfoActivity.class));
                        break;
                    case R.id.myPostlayout:
                        getActivity().startActivity(new Intent(getActivity(), MyPostListActivity.class));
                        break;
                    case R.id.noReslayout:
                        getActivity().startActivity(new Intent(getActivity(), TermOfUseActivity.class));
                        break;
                    case R.id.logout:
                        rootUser.logOut();
                        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                        break;
                    default:
                        break;
                }
            }
        }, 200);
    }

    @Override
    public void onResume() {
        super.onResume();
        rootUser = BmobUser.getCurrentUser(RootUser.class);
        if (rootUser != null) {
            CommonUtil.fecth(getActivity());
            view.findViewById(R.id.registerLayout).setVisibility(View.GONE);
            view.findViewById(R.id.logout).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.nickName)).setText(rootUser.getUsername() != null ? rootUser.getUsername() : "未设置昵称");
            ((TextView) view.findViewById(R.id.vip_coins)).setText(rootUser.vipConis != null && rootUser.vipConis > 0 ? rootUser.vipConis + "趣币" : "您趣币不足");
            String sexAndAge = (TextUtils.isEmpty(rootUser.sex) ? "性别" : rootUser.sex + "性") + "/" + (TextUtils.isEmpty(rootUser.age) ? "年龄" : rootUser.age + "岁");
            ((TextView) view.findViewById(R.id.sexAndAge)).setText(sexAndAge);
            if (0 < rootUser.vipConis && rootUser.vipConis < 3000) {
                ((TextView) view.findViewById(R.id.vipLevel)).setText("初级趣会员");
            } else if (3000 < rootUser.vipConis && rootUser.vipConis < 5000) {
                ((TextView) view.findViewById(R.id.vipLevel)).setText("中级趣会员");
            } else if (5000 < rootUser.vipConis && rootUser.vipConis < 8000) {
                ((TextView) view.findViewById(R.id.vipLevel)).setText("特级趣会员");
            } else if (rootUser.vipConis > 8000) {
                ((TextView) view.findViewById(R.id.vipLevel)).setText("超级趣会员");

            }
        } else {
            view.findViewById(R.id.registerLayout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.logout).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.nickName)).setText("未设置昵称");
            ((TextView) view.findViewById(R.id.vip_coins)).setText("您趣币不足");
            ((TextView) view.findViewById(R.id.sexAndAge)).setText("性别/年龄");
        }
    }


}
