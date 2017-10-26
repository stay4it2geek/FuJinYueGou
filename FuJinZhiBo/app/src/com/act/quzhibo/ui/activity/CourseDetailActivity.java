package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.bean.CommonCourse;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.bean.ShoppingCart;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.ui.fragment.CommentFragment;
import com.act.quzhibo.ui.fragment.GoodsDetailFragment;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.widget.FragmentDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


public class CourseDetailActivity extends TabSlideDifferentBaseActivity {

    @Bind(R.id.courseCount)
    TextView courseCount;
    private CommonCourse course;

    int count = 0;

    @Override
    public boolean getIsMineActivityType() {
        return false;
    }

    @Override
    protected boolean isNeedShowBackDialog() {
        return false;
    }

    @Override
    protected String[] getTitles() {
        return new String[]{"商品详情", "评价"};
    }

    @Override
    protected ArrayList<Fragment> getFragments() {
        mFragments.add(GoodsDetailFragment.newInstance());
        mFragments.add(CommentFragment.newInstance());
        return mFragments;
    }

    ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();

    @Override
    protected void initView() {
        setContentView(R.layout.activity_course_detail);
        super.initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BmobUser.getCurrentUser(RootUser.class) != null) {
            BmobQuery<ShoppingCart> query = new BmobQuery<>();
            course = (CommonCourse) getIntent().getSerializableExtra(Constants.COURSE);
            query.addWhereEqualTo("user", BmobUser.getCurrentUser(RootUser.class));
            query.findObjects(new FindListener<ShoppingCart>() {
                @Override
                public void done(List<ShoppingCart> list, BmobException e) {
                    if (e == null) {
                        if (list != null && list.size() > 0) {
                            shoppingCarts.addAll(list);
                            courseCount.setText(list.size() + "");
                            count = list.size();
                        } else {
                            courseCount.setText("0");
                        }
                    }
                }
            });
        } else {
            courseCount.setText("0");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean getDetailContentViewFlag() {
        return true;
    }

    @OnClick(R.id.shopping_cart_layout)
    public void showShoppingCart() {
        if (BmobUser.getCurrentUser(RootUser.class) != null) {
            startActivity(new Intent(CourseDetailActivity.this, ShoppingCartActivity.class));
        } else {
            checkLogin();
        }
    }

    @OnClick(R.id.service_layout)
    public void getService() {
        if (BmobUser.getCurrentUser(RootUser.class) != null) {
            startActivity(new Intent(CourseDetailActivity.this, ChatActivity.class));
        } else {
            checkLogin();
        }
    }

    @OnClick(R.id.text_add)
    public void addToShoppingCart() {
        if (BmobUser.getCurrentUser(RootUser.class) != null) {
            startActivity(new Intent(CourseDetailActivity.this, ShoppingCartActivity.class));
        } else {
            checkLogin();
        }


        for (ShoppingCart shoppingCart : shoppingCarts) {
            if (shoppingCart.course.getObjectId().equals(course.getObjectId())) {
                ToastUtil.showToast(CourseDetailActivity.this, "课程已经添加在购物车了");
                return;
            }
        }
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.5f, 1, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        animationSet.addAnimation(scaleAnimation);
        CourseDetailActivity.this.courseCount.startAnimation(animationSet);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.price = Double.parseDouble(course.courseAppPrice);
        shoppingCart.user = BmobUser.getCurrentUser(RootUser.class);
        shoppingCart.course = (CommonCourse) getIntent().getSerializableExtra(Constants.COURSE);
        shoppingCart.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    count++;
                    courseCount.setText(count + "");
                } else {
                    if (e.getErrorCode() == 401) {
                        ToastUtil.showToast(CourseDetailActivity.this, "课程已经添加在购物车了");
                    }
                }
            }
        });


    }


    @OnClick(R.id.text_buynow)
    public void buyNow() {

        if (BmobUser.getCurrentUser(RootUser.class) != null) {
            //        Intent intent = new Intent(CourseDetailActivity.this, BuyCourseActivity.class);
//        intent.putExtra("course", course);
//        startActivity(intent);
        } else {
            checkLogin();
        }

    }


    private void checkLogin() {

        FragmentDialog.newInstance(false, "请先登录或注册", "登录用户才能下单购买哦", "登录", "注册", "", "", false, new FragmentDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick(Dialog dialog, boolean deleteFileSource) {
                startActivity(new Intent(CourseDetailActivity.this, LoginActivity.class));
            }

            @Override
            public void onNegtiveClick(Dialog dialog) {
                startActivity(new Intent(CourseDetailActivity.this, RegisterActivity.class));

            }
        }).show(getSupportFragmentManager(), "");

        return;
    }

}