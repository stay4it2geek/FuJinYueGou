package com.act.quzhibo.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.base.BaseActivity;
import com.act.quzhibo.ui.fragment.BackHandledFragment;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ViewFindUtils;
import com.act.quzhibo.view.FragmentDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;

public abstract class TabSlideDifferentBaseActivity extends FragmentActivity implements BackHandledFragment.BackHandledInterface {
    protected MyPagerAdapter mAdapter;
    protected View decorView;
    private BackHandledFragment mBackHandedFragment;
    ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tab);
        mFragments=getFragments();
        decorView = getWindow().getDecorView();
        mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        CommonUtil.initView(getTitles(), decorView, (ViewPager) ViewFindUtils.find(decorView, R.id.viewpager), mAdapter, getActivityType());

    }

    protected void setPage(int positon) {
        ((ViewPager) ViewFindUtils.find(decorView, R.id.viewpager)).setCurrentItem(positon);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getTitles()[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }


    @Override
    public void onBackPressed() {
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if (isNeedShowBackDialog()) {
                    FragmentDialog.newInstance(false,"客官再看一会儿呗","还是留下来再看看吧",  "再欣赏下", "有事要忙","","",false, new FragmentDialog.OnClickBottomListener() {
                        @Override
                        public void onPositiveClick(Dialog dialog,boolean needDelete) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegtiveClick(Dialog dialog) {
                            dialog.dismiss();
                            TabSlideDifferentBaseActivity.super.onBackPressed();
                        }
                    }).show(getSupportFragmentManager(), "");
                } else {
                    TabSlideDifferentBaseActivity.super.onBackPressed();
                }
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    public abstract boolean getActivityType();

    protected abstract boolean isNeedShowBackDialog();

    protected abstract String[] getTitles();

    protected abstract ArrayList<Fragment> getFragments();
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Subscribe
    public void onEvent(Boolean empty){

    }

    protected void initView() {}

    protected void runOnMain(Runnable runnable) {
        runOnUiThread(runnable);
    }

    protected final static String NULL = "";
    private Toast toast;
    public void toast(final Object obj) {
        try {
            runOnMain(new Runnable() {

                @Override
                public void run() {
                    if (toast == null)
                        toast = Toast.makeText(TabSlideDifferentBaseActivity.this, NULL, Toast.LENGTH_SHORT);
                    toast.setText(obj.toString());
                    toast.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startActivity(Class<? extends Activity> target, Bundle bundle, boolean finish) {
        Intent intent = new Intent();
        intent.setClass(this, target);
        if (bundle != null)
            intent.putExtra(getPackageName(), bundle);
        startActivity(intent);
        if (finish)
            finish();
    }

    public Bundle getBundle() {
        if (getIntent() != null && getIntent().hasExtra(getPackageName()))
            return getIntent().getBundleExtra(getPackageName());
        else
            return null;
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**隐藏软键盘-一般是EditText.getWindowToken()
     * @param token
     */
    public void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
