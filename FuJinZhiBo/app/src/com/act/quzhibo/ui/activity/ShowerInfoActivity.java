package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.act.quzhibo.R;
import com.act.quzhibo.common.Constants;
import com.act.quzhibo.common.OkHttpClientManager;
import com.act.quzhibo.download.event.FocusChangeEvent;
import com.act.quzhibo.bean.MyFocusShower;
import com.act.quzhibo.bean.RootUser;
import com.act.quzhibo.util.GlideImageLoader;
import com.act.quzhibo.bean.Room;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.custom.FragmentDialog;
import com.act.quzhibo.custom.LoadNetView;
import com.act.quzhibo.custom.TitleBarView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class ShowerInfoActivity extends FragmentActivity {
    private Room room;
    private LoadNetView loadNetView;
    private MyFocusShower mMyFocusShower;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shower_info);
        room = (Room) getIntent().getSerializableExtra("room");
        if (room != null) {
            getData(room.userId);
        }
        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("主 播 档 案");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowerInfoActivity.this.finish();
            }
        });
        loadNetView = (LoadNetView) findViewById(R.id.loadview);
        loadNetView.setReloadButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNetView.setlayoutVisily(Constants.LOAD);
                getData(room.userId);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BmobUser.getCurrentUser(RootUser.class) != null) {
            BmobQuery<MyFocusShower> query = new BmobQuery<>();
            query.setLimit(1);
            query.addWhereEqualTo("userId", room.userId);
            query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
            query.findObjects(new FindListener<MyFocusShower>() {
                @Override
                public void done(List<MyFocusShower> myFocusShowers, BmobException e) {
                    if (e == null) {
                        if (myFocusShowers.size() >= 1) {
                            mMyFocusShower = myFocusShowers.get(0);
                            ((TextView) findViewById(R.id.focus)).setText("取消关注");
                        }
                    }
                }
            });
        }


        findViewById(R.id.focus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BmobUser.getCurrentUser(RootUser.class) != null) {
                    if (!(((TextView) findViewById(R.id.focus)).getText().toString().trim()).equals("取消关注")) {

                        if (mMyFocusShower == null) {
                            MyFocusShower myFocusShower = new MyFocusShower();
                            myFocusShower.rootUser = BmobUser.getCurrentUser(RootUser.class);
                            myFocusShower.nickname = room.nickname;
                            myFocusShower.roomId = room.roomId;
                            myFocusShower.userId = room.userId;
                            myFocusShower.gender = room.gender;
                            myFocusShower.liveStream = room.liveStream;
                            myFocusShower.city = room.city;
                            if (getIntent().getBooleanExtra("FromChatFragment", false)) {
                                myFocusShower.portrait_path_1280 = getIntent().getStringExtra("photoUrl");
                            } else if (getIntent().getBooleanExtra("FromShowListActivity", false)) {
                                myFocusShower.portrait_path_1280 = "http://ures.kktv8.com/kktv" + room.portrait_path_1280;
                            } else {
                                myFocusShower.portrait_path_1280 = room.portrait_path_1280;
                            }

                            myFocusShower.save(new SaveListener<String>() {
                                @Override
                                public void done(String objectId, BmobException e) {
                                    if (e == null) {
                                        ((TextView) findViewById(R.id.focus)).setText("取消关注");
                                        EventBus.getDefault().post(new FocusChangeEvent(true));
                                        if (BmobUser.getCurrentUser(RootUser.class) != null) {
                                            BmobQuery<MyFocusShower> query = new BmobQuery<>();
                                            query.setLimit(1);
                                            query.addWhereEqualTo("userId", room.userId);
                                            query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
                                            query.findObjects(new FindListener<MyFocusShower>() {
                                                @Override
                                                public void done(List<MyFocusShower> myFocusShowers, BmobException e) {
                                                    if (e == null) {
                                                        if (myFocusShowers.size() >= 1) {
                                                            ShowerInfoActivity.this.mMyFocusShower = myFocusShowers.get(0);
                                                            ((TextView) findViewById(R.id.focus)).setText("取消关注");
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                        ToastUtil.showToast(ShowerInfoActivity.this, "关注成功");
                                    } else {
                                        ToastUtil.showToast(ShowerInfoActivity.this, "关注失败");
                                    }
                                }
                            });
                        } else {
                            mMyFocusShower.update(mMyFocusShower.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        ((TextView) findViewById(R.id.focus)).setText("取消关注");
                                        if (BmobUser.getCurrentUser(RootUser.class) != null) {
                                            BmobQuery<MyFocusShower> query = new BmobQuery<>();
                                            query.setLimit(1);
                                            query.addWhereEqualTo("userId", room.userId);
                                            query.addWhereEqualTo("rootUser", BmobUser.getCurrentUser(RootUser.class));
                                            query.findObjects(new FindListener<MyFocusShower>() {
                                                @Override
                                                public void done(List<MyFocusShower> myFocusShowers, BmobException e) {
                                                    if (e == null) {
                                                        if (myFocusShowers.size() >= 1) {
                                                            ShowerInfoActivity.this.mMyFocusShower = myFocusShowers.get(0);
                                                            ((TextView) findViewById(R.id.focus)).setText("取消关注");
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                        ToastUtil.showToast(ShowerInfoActivity.this, "关注成功");
                                    } else {
                                        ToastUtil.showToast(ShowerInfoActivity.this, "关注失败");
                                    }
                                }
                            });
                        }

                    } else {
                        FragmentDialog.newInstance(false, "是否取消关注", "真的要取消关注人家吗", "继续关注", "取消关注","","",false, new FragmentDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick(final Dialog dialog, boolean deleteFileSource) {
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegtiveClick(Dialog dialog) {
                                if (mMyFocusShower != null) {
                                    mMyFocusShower.delete(mMyFocusShower.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                mMyFocusShower = null;
                                                ((TextView) findViewById(R.id.focus)).setText("关注ta");
                                                ToastUtil.showToast(ShowerInfoActivity.this, "取消关注成功");
                                                EventBus.getDefault().post(new FocusChangeEvent(false));
                                            }

                                        }
                                    });
                                }
                                dialog.dismiss();
                            }
                        }).show(getSupportFragmentManager(), "");
                    }
                } else {
                    startActivity(new Intent(ShowerInfoActivity.this, LoginActivity.class));
                }
            }
        });
    }

    public void getData(String userId) {
        String url = CommonUtil.getToggle(this, Constants.SHOWER_INFO).getToggleObject().replace("USERID", userId);
        OkHttpClientManager.parseRequest(this, url, handler, Constants.REFRESH);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != Constants.NetWorkError) {
                try {
                    JSONObject jsonObject = new JSONObject((String) msg.obj);
                    String fansCount = jsonObject.getString("fansCount");
                    String nickname = jsonObject.getString("nickname");
                    String introduce = jsonObject.getString("introduce");
                    String portrait_img = jsonObject.getString("portrait_path_1280");
                    String gender = jsonObject.getString("gender");
                    JSONObject jsonObject1 = jsonObject.getJSONObject("getPhotoListResult");
                    if (msg.what != Constants.NetWorkError) {
                        ((TextView) findViewById(R.id.fansCount)).setText(fansCount != null ? "粉丝 " + fansCount : "");
                        if (!introduce.equals("") && introduce != null) {
                            findViewById(R.id.introduce).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.introduce)).setText(introduce);
                        }
                        ((TextView) findViewById(R.id.nickName)).setText(nickname != null ? nickname : "");

                        JSONArray photos = jsonObject1.getJSONArray("photoList");

                        List<String> urls = new ArrayList<>();
                        if (photos != null && photos.length() > 0) {
                            for (int i = 0; i < photos.length(); i++) {
                                JSONObject jsonObject2 = photos.getJSONObject(i);
                                String url = (String) jsonObject2.get("photo_path_original");
                                urls.add(url);
                            }
                        } else {
                            urls.clear();
                            urls.add(portrait_img);
                        }
                        final ImageView showerAvatar = (ImageView) findViewById(R.id.userImage);

                        if (gender.equals("0")) {
                            Glide.with(ShowerInfoActivity.this).load(portrait_img).asBitmap().placeholder(R.drawable.women).error(R.drawable.error_img).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    showerAvatar.setBackgroundDrawable(new BitmapDrawable(resource));
                                }

                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                }
                            });
                            ((Banner) findViewById(R.id.banner)).setImages(urls).setImageLoader(new GlideImageLoader(R.drawable.women)).start();
                        } else {
                            Glide.with(ShowerInfoActivity.this).load(portrait_img).asBitmap().placeholder(R.drawable.man).error(R.drawable.error_img).into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    showerAvatar.setBackgroundDrawable(new BitmapDrawable(resource));
                                }

                                @Override
                                public void onLoadStarted(Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                }
                            });
                            ((Banner) findViewById(R.id.banner)).setImages(urls).setImageLoader(new GlideImageLoader(R.drawable.man)).start();

                        }

                        loadNetView.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {

                } catch (Exception e) {

                }

            } else {
                loadNetView.setVisibility(View.VISIBLE);
                loadNetView.setlayoutVisily(Constants.RELOAD);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_OK);
        finish();
    }
}
