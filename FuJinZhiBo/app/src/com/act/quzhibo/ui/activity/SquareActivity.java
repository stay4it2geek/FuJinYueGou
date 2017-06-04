package com.act.quzhibo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.act.quzhibo.ui.fragment.BackHandledFragment;
import com.act.quzhibo.ui.fragment.FunnyPersonFragment;
import com.act.quzhibo.ui.fragment.InterestPlatesFragment;

import java.util.ArrayList;

/**
 * 广场
 */
public class SquareActivity extends BaseActivity implements BackHandledFragment.BackHandledInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String[] getTitles() {
        return new String[]{"情趣板块", "附近达人"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
    ArrayList<Fragment> fragments=new ArrayList<>();
        fragments.add(new InterestPlatesFragment());
        fragments.add(new FunnyPersonFragment());
        return fragments;
    }

    private BackHandledFragment mBackHandedFragment;

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {
        if(mBackHandedFragment == null||!mBackHandedFragment.onBackPressed()){
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                super.onBackPressed();
            }else{
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    public String pid;
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
}
