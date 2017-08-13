package com.act.quzhibo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.ui.activity.VipPloicyActivity;
import com.act.quzhibo.ui.activity.LoginActivity;
import com.act.quzhibo.ui.activity.MyFocusShowerActivity;
import com.act.quzhibo.ui.activity.GetVipPayActivity;
import com.act.quzhibo.ui.activity.VipOrdersActivity;
import com.act.quzhibo.ui.activity.WhoSeeMeActivity;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class PersonalFragment extends Fragment implements View.OnClickListener {

    RootUser rootUser;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_personal, null, false);

        view.findViewById(R.id.vip_policy).setOnClickListener(this);
        view.findViewById(R.id.get_vip).setOnClickListener(this);
        view.findViewById(R.id.vip_order_listlayout).setOnClickListener(this);
        view.findViewById(R.id.who_see_me).setOnClickListener(this);
        view.findViewById(R.id.myfocus_shower).setOnClickListener(this);
        view.findViewById(R.id.myfocus_person).setOnClickListener(this);
        view.findViewById(R.id.myfocus_shower).setOnClickListener(this);
        view.findViewById(R.id.myDetailayout).setOnClickListener(this);
        view.findViewById(R.id.noReslayout).setOnClickListener(this);
        view.findViewById(R.id.secretlayout).setOnClickListener(this);
        view.findViewById(R.id.myVideo_download_layout).setOnClickListener(this);
        view.findViewById(R.id.myIMG_download_layout).setOnClickListener(this);
        view.findViewById(R.id.myPostlayout).setOnClickListener(this);
        view.findViewById(R.id.logout).setOnClickListener(this);
        view.findViewById(R.id.noRes).setOnClickListener(this);
        return view;
    }


    private void queryWhoSeeMe() {
    }


    private void queryMyFocusShower() {

    }

    private void queryMyFocusPersons() {

    }

    private void Logout() {

    }

    @Override
    public void onClick(final View view) {
        view.setBackgroundColor(getResources().getColor(R.color.colorbg));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setBackgroundColor(getResources().getColor(R.color.white));

                if (rootUser == null) {
                    if (view.getId() == R.id.vip_policy) {
                        getActivity().startActivity(new Intent(getActivity(), VipPloicyActivity.class));
                    } else if (view.getId() == R.id.get_vip) {
                        getActivity().startActivity(new Intent(getActivity(), GetVipPayActivity.class));

                    } else if (view.getId() == R.id.noReslayout) {
                        getActivity().startActivity(new Intent(getActivity(), GetVipPayActivity.class));

                    } else if (view.getId() == R.id.secretlayout) {
                        getActivity().startActivity(new Intent(getActivity(), GetVipPayActivity.class));

                    } else {
                        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                    return;
                }
                switch (view.getId()) {
                    case R.id.vip_policy:
                        getActivity().startActivity(new Intent(getActivity(), VipPloicyActivity.class));
                        break;
                    case R.id.vip_order_listlayout:
                        getActivity().startActivity(new Intent(getActivity(), VipOrdersActivity.class));
                        break;
                    case R.id.get_vip:
                        getActivity().startActivity(new Intent(getActivity(), GetVipPayActivity.class));
                        break;
                    case R.id.who_see_me:
                        getActivity().startActivity(new Intent(getActivity(), WhoSeeMeActivity.class));
                        break;
                    case R.id.myfocus_shower:
                        getActivity().startActivity(new Intent(getActivity(), MyFocusShowerActivity.class));
                        break;
                    case R.id.myfocus_person:
                        getActivity().startActivity(new Intent(getActivity(), WhoSeeMeActivity.class));
                        break;
                    case R.id.myVideo_download_layout:
                        getActivity().startActivity(new Intent(getActivity(), WhoSeeMeActivity.class));
                        break;
                    case R.id.myIMG_download_layout:
                        getActivity().startActivity(new Intent(getActivity(), WhoSeeMeActivity.class));
                        break;
                    case R.id.myDetailayout:
                        getActivity().startActivity(new Intent(getActivity(), WhoSeeMeActivity.class));
                        break;
                    case R.id.myPostlayout:
                        getActivity().startActivity(new Intent(getActivity(), WhoSeeMeActivity.class));
                        break;
                    case R.id.secretlayout:
                        getActivity().startActivity(new Intent(getActivity(), WhoSeeMeActivity.class));
                        break;
                    case R.id.noReslayout:
                        getActivity().startActivity(new Intent(getActivity(), WhoSeeMeActivity.class));
                        break;
                    case R.id.logout:
                        rootUser.logOut();
                        getActivity().startActivityForResult(new Intent(getActivity(), LoginActivity.class), Constants.LOGIN);
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
            view.findViewById(R.id.logout).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.isLogin)).setText("已登录");
            ((TextView) view.findViewById(R.id.nickName)).setText(rootUser.getUsername() != null ? rootUser.getUsername() : "未设置昵称");
            ((TextView) view.findViewById(R.id.vip_type)).setText(rootUser.vipTypeName != null ? rootUser.vipTypeName : "您还不是VIP哦");
            String sexAndAge = (rootUser.sex != null && rootUser.sex ? "男" : "女") + "/" + (TextUtils.isEmpty(rootUser.age) ? "未知" : (rootUser.age + ""));
            ((TextView) view.findViewById(R.id.sexAndAge)).setText(sexAndAge);
        }
    }
}
