package com.act.quzhibo.ui.activity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;


import com.act.quzhibo.R;
import com.act.quzhibo.bean.CommonCourse;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.bean.ShoppingCart;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.event.RefreshEvent;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.widget.FragmentDialog;
import com.act.quzhibo.widget.PageTwoWebView;
import com.act.quzhibo.widget.SlidingDetailsLayout;
import com.act.quzhibo.widget.TitleBarView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class CourseDetailActivity extends BaseActivity {
    @Bind(R.id.slidingDetailsLayout)
    SlidingDetailsLayout slidingDetailsLayout;
    @Bind(R.id.tishi)
    TextView tishi;
    @Bind(R.id.webview)
    PageTwoWebView webview;
    @Bind(R.id.titlebar)
    TitleBarView titlebar;
    ArrayList<String> courseOfCartObjectIds = new ArrayList<>();
    @Bind(R.id.courseCount)
    TextView courseCount;
    boolean startServieFlag;
    CommonCourse course;
    RootUser user;
    int count = 0;
    private String mUrl = "http://www.baidu.com/";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_goods_detail);
        titlebar.setBarTitle("课程详情");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        course = (CommonCourse) getIntent().getSerializableExtra(Constants.COURSE);
        initView();
        initWebView();
    }

    private void initWebView() {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDomStorageEnabled(true);// 设置可以使用localStorage
        webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        webview.loadUrl(mUrl);
        webview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //这句话说的意思告诉父View我自己的事件我自己处理
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String scheme = url.substring(0, 4).trim().toLowerCase();
                if (scheme.contains("https") || scheme.contains("http")) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                int w = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                int h = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                //重新测量
                if(webview!=null)
                webview.measure(w, h);
            }
        });
    }

    private void initView() {
        tishi.setText("上拉查看商品详情");
        slidingDetailsLayout.setPositionChangListener(new SlidingDetailsLayout.PositionChangListener() {
            @Override
            public void position(int positon) {
                if (positon == 0) {
                    tishi.setText("上拉查看商品详情");
                } else {
                    tishi.setText("下拉返回商品简介");

                }
            }

            @Override
            public void onBottom() {
                tishi.setText("松开，马上加载商品详情");
            }

            @Override
            public void backBottom() {
                tishi.setText("上拉查看商品详情");
            }

            @Override
            public void onTop() {
                tishi.setText("松开，返回商品简介");
            }

            @Override
            public void backTop() {
                tishi.setText("下拉返回商品简介");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = BmobUser.getCurrentUser(RootUser.class);
        if (user != null) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        EventBus.getDefault().post(new RefreshEvent());
                        BmobIM.getInstance().
                                updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                        user.getUsername(), user.photoFileUrl));
                    } else {
                        ToastUtil.showToast(CourseDetailActivity.this, e.getMessage());
                    }
                }
            });
            BmobQuery<ShoppingCart> query = new BmobQuery<>();
            query.addWhereEqualTo("user", user);
            query.findObjects(new FindListener<ShoppingCart>() {
                @Override
                public void done(List<ShoppingCart> list, BmobException e) {
                    if (e == null) {
                        if (list != null && list.size() > 0) {
                            courseOfCartObjectIds.clear();
                            for (ShoppingCart cart : list) {
                                courseOfCartObjectIds.add(cart.course.getObjectId());
                            }
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

    @OnClick(R.id.shopping_cart_layout)
    public void showShoppingCart() {
        if (user != null) {
            startActivity(new Intent(CourseDetailActivity.this, ShoppingCartActivity.class));
        } else {
            checkLogin();
        }
    }

    @OnClick(R.id.service_layout)
    public void getService() {
        if (user != null) {
            if (BmobIM.getInstance().getCurrentStatus().equals("connected")) {
                startService();
                startServieFlag = true;
            } else {
                BmobIM.connect(user.getObjectId(), new ConnectListener() {
                    @Override
                    public void done(String uid, BmobException e) {
                        if (e == null) {
                            EventBus.getDefault().post(new RefreshEvent());
                            BmobIM.getInstance().
                                    updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                            user.getUsername(), user.photoFileUrl));
                        } else {
                            ToastUtil.showToast(CourseDetailActivity.this, e.getMessage());
                        }
                    }
                });

                BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                    @Override
                    public void onChange(ConnectionStatus status) {
                        ToastUtil.showToast(CourseDetailActivity.this, status.getMsg());
                        if (status.getMsg().equals("connected") && startServieFlag == false) {
                            startService();
                            startServieFlag = true;
                        }
                    }
                });
            }
        } else {
            checkLogin();
        }
    }


    void startService() {
        BmobIMUserInfo info;
        info = new BmobIMUserInfo("mjozUUUk", "趣视客服", "http://bmob-cdn-13639.b0.upaiyun.com/2017/10/29/aab00f0b9d9a43ebb9cadebc2d34c9ec.jpg");
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
        Intent intent = new Intent(CourseDetailActivity.this, ChatActivity.class);
        intent.putExtra("c", conversationEntrance);
        startActivity(intent);
    }

    @OnClick(R.id.text_add)
    public void addToShoppingCart() {
        if (user != null) {
            if (courseOfCartObjectIds.size() == 0) {
                addToCart();
            } else {
                if (courseOfCartObjectIds.contains(course.getObjectId())) {
                    ToastUtil.showToast(this, "课程已经添加在购物车了");
                } else {
                    addToCart();
                }
            }
        } else {
            checkLogin();
        }
    }

    private void addToCart() {
        ToastUtil.showToast(this, "add");
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.5f, 1, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        animationSet.addAnimation(scaleAnimation);
        courseCount.startAnimation(animationSet);
        ShoppingCart shoppingCart = createEntity();
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
                                    courseOfCartObjectIds.clear();
                                    for (ShoppingCart cart : list) {
                                        courseOfCartObjectIds.add(cart.course.getObjectId());
                                    }
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
    }

    @NonNull
    private ShoppingCart createEntity() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.user = user;
        shoppingCart.course = course;
        return shoppingCart;
    }

    @OnClick(R.id.text_buynow)
    public void buyNow() {
        if (user != null) {
            if (courseOfCartObjectIds.size() == 0) {
                ToastUtil.showToast(this, "add");
                gotoShoppingCart();
            } else {
                if (courseOfCartObjectIds.contains(course.getObjectId())) {
                    startActivity(new Intent(CourseDetailActivity.this, ShoppingCartActivity.class));
                } else {
                    ToastUtil.showToast(this, "add2");
                    gotoShoppingCart();
                }
            }
        } else {
            checkLogin();
        }
    }

    private void gotoShoppingCart() {
        ShoppingCart shoppingCart = createEntity();

        shoppingCart.user = user;
        shoppingCart.course = course;
        shoppingCart.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    startActivity(new Intent(CourseDetailActivity.this, ShoppingCartActivity.class));
                }
            }
        });
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
}
