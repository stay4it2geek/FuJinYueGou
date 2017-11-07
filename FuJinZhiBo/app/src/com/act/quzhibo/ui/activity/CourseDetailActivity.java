package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
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
import com.act.quzhibo.event.RefreshEvent;
import com.act.quzhibo.ui.fragment.CommentFragment;
import com.act.quzhibo.ui.fragment.GoodsDetailFragment;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.widget.FragmentDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class CourseDetailActivity extends TabSlideDifferentBaseActivity {

    @Bind(R.id.courseCount)
    TextView courseCount;
    ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();
    CommonCourse course;
    RootUser user;
    int count = 0;
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

    @Override
    protected void initView() {
        setContentView(R.layout.activity_course_detail);
        super.initView();
        user = BmobUser.getCurrentUser(RootUser.class);

        //登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        if (user != null) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //连接成功后再进行修改本地用户信息的操作，并查询本地用户信息
                        EventBus.getDefault().post(new RefreshEvent());
                        BmobIM.getInstance().
                                updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                        user.getUsername(), user.photoFileUrl));

                    } else {
                        ToastUtil.showToast(CourseDetailActivity.this, e.getMessage());
                    }
                }
            });
            //监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (user != null) {
            BmobQuery<ShoppingCart> query = new BmobQuery<>();
            course = (CommonCourse) getIntent().getSerializableExtra(Constants.COURSE);
            query.addWhereEqualTo("user", user);
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
    protected boolean getDetailContentViewFlag() {
        return true;
    }

    @OnClick(R.id.shopping_cart_layout)
    public void showShoppingCart() {
        if (user!= null) {
            startActivity(new Intent(CourseDetailActivity.this, ShoppingCartActivity.class));
        } else {
            checkLogin();
        }
    }

    @OnClick(R.id.service_layout)
    public void getService() {
        if (user!= null) {
            //创建一个常态会话入口，好友聊天
            if (BmobIM.getInstance().getCurrentStatus().equals("connected")) {
                BmobIMUserInfo info;
                info = new BmobIMUserInfo("mjozUUUk", "趣视客服", "http://bmob-cdn-13639.b0.upaiyun.com/2017/10/29/aab00f0b9d9a43ebb9cadebc2d34c9ec.jpg");
                BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("c", conversationEntrance);
                startActivity(intent);
            } else {
                ToastUtil.showToast(CourseDetailActivity.this, "正在连接客服，请稍等。。。");
            }

        } else {
            checkLogin();
        }
    }

    @OnClick(R.id.text_add)
    public void addToShoppingCart() {
        if (user != null) {
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
            shoppingCart.user = user;
            shoppingCart.course = course;
            shoppingCart.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        BmobQuery<ShoppingCart> query = new BmobQuery<>();
                        query.addWhereEqualTo("user", user);
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

                    }
                }
            });
        } else {
            checkLogin();
        }

    }


    @OnClick(R.id.text_buynow)
    public void buyNow() {

        if (user != null) {
            //        Intent intent = new Intent(CourseDetailActivity.this, BuyCourseActivity.class);
//        intent.putExtra("course", course);
//        startActivity(intent);
        } else {
            checkLogin();
        }
    }


     void checkLogin() {
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

    @Subscribe
    public void onEventMain(final MessageEvent event) {
    }

}