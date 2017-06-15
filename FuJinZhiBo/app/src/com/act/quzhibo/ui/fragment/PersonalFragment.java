package com.act.quzhibo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;

public class PersonalFragment extends Fragment implements View.OnClickListener {
    private boolean login;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_personal, null, false);
        return view;
    }

    private void queryWhoSeeMe() {

    }


    private void queryHasBugBookOrders() {

    }


    private void queryWaitForPayBookOrders() {

    }

    private void querySystemMessage() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (login) {
            querySystemMessage();
        }
    }

    private void queryMyFocusShower() {

    }

    private void queryMyFocusPersons() {

    }

    private void Logout() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vip_buy:
                break;
            case R.id.who_see_me:
                break;
            case R.id.has_buy_order:
                break;
            case R.id.wait_pay_order:
                break;
            case R.id.system_message:
                break;
            case R.id.myfocus_shower:
                break;
            case R.id.myfocus_person:
                break;
            case R.id.secretlayout:
                break;
            case R.id.logout:
                break;
            default:
                break;

        }
    }
}
