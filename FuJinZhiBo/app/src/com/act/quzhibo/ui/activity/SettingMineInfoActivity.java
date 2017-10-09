package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.CardBean;
import com.act.quzhibo.entity.JsonBean;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.util.CommonUtil;
import com.act.quzhibo.util.ToastUtil;
import com.act.quzhibo.view.FragmentDialog;
import com.act.quzhibo.view.LockIndicatorView;
import com.act.quzhibo.view.LockViewConfig;
import com.act.quzhibo.view.LockViewGroup;
import com.act.quzhibo.view.TitleBarView;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class SettingMineInfoActivity extends FragmentActivity {
    private OptionsPickerView ageOptions;
    private OptionsPickerView sexOptions;

    private ArrayList<CardBean> ageItems = new ArrayList<>();
    private TextView age_txt;
    private TextView sex_txt;

    private RootUser rootUser = BmobUser.getCurrentUser(RootUser.class);
    private RootUser updateUser = new RootUser();

    private TextView openSecret_txt;
    private Switch openSecret_switch;
    private TextView arealocation_txt;

    private ArrayList<CardBean> sexItems = new ArrayList<>();
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<CardBean> candateThingiItems = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private ArrayList<CardBean> datingThoughtItems = new ArrayList<>();
    private ArrayList<CardBean> disPurposeItems = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private boolean isLoaded = false;

    private OptionsPickerView datingThoughtOptions;
    private OptionsPickerView disPurposeOption;
    private TextView disPurpose_txt;
    private TextView datingThought_txt;
    private TextView candateThing_txt;
    private OptionsPickerView candateThingOptions;

    private LockIndicatorView mLockIndicator;
    private LockViewGroup mLockViewGroup;
    private TextView mTvTips;
    private LinearLayout secretView;

    @Override
    protected void onResume() {
        super.onResume();

        if (rootUser != null) {
            CommonUtil.fecth(this);
            openSecret_switch.setChecked(rootUser.secretScan);
            sex_txt.setText(TextUtils.isEmpty(rootUser.sex) ? "您的性别未设置" : "您的性别是" + rootUser.sex + "性");
            if (!TextUtils.isEmpty(rootUser.sex)) {
                sex_txt.setTextColor(Color.LTGRAY);
                findViewById(R.id.sex_rl).setVisibility(View.GONE);
            }
            openSecret_txt.setText(rootUser.secretScan ? "私密访问已开启" : "私密访问未开启");
            arealocation_txt.setText(TextUtils.isEmpty(rootUser.provinceAndcity) ? "省市区未设置" : "您的地区是" + rootUser.provinceAndcity);
            age_txt.setText(TextUtils.isEmpty(rootUser.age) ? "年龄未设置" : "您的年龄是" + rootUser.age + "岁");
            disPurpose_txt.setText(TextUtils.isEmpty(rootUser.disPurpose) ? "情感状态未设置" : "您现在是" + rootUser.disPurpose);
            datingThought_txt.setText(TextUtils.isEmpty(rootUser.datingthought) ? "交友想法未设置" : "您想要" + rootUser.datingthought);
            candateThing_txt.setText(TextUtils.isEmpty(rootUser.canDateThing) ? "是否可约未设置" : "您可以" + rootUser.canDateThing);
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_mine_info);
        age_txt = (TextView) findViewById(R.id.age_txt);
        openSecret_switch = (Switch) findViewById(R.id.openSecret_switch);
        openSecret_txt = (TextView) findViewById(R.id.openSecret_txt);
        sex_txt = (TextView) findViewById(R.id.sex_txt);
        disPurpose_txt = (TextView) findViewById(R.id.disPurpose_txt);
        datingThought_txt = (TextView) findViewById(R.id.datingThought_txt);
        candateThing_txt = (TextView) findViewById(R.id.candateThing_txt);
        arealocation_txt = (TextView) findViewById(R.id.arealocation_txt);
        arealocation_txt = (TextView) findViewById(R.id.arealocation_txt);
        arealocation_txt = (TextView) findViewById(R.id.arealocation_txt);
        secretView = (LinearLayout) findViewById(R.id.secret_view);

        mLockIndicator = (LockIndicatorView) findViewById(R.id.indicator);

        mTvTips = (TextView) findViewById(R.id.tv_tips);

        mLockViewGroup = (LockViewGroup) findViewById(R.id.lockgroup);


        mHandler.sendEmptyMessage(MSG_LOAD_DATA);

        initData();

        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("资 料 设 置");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingMineInfoActivity.this.finish();
            }
        });

        getSexData();
        getAgeData();
        getDatingThoughtData();
        getDisPurposeData();
        getCanDatingThingData();

        initSexOptionPicker();
        initAgeOptionPicker();
        initCanDatingThingOptionPicker();
        initDatingThoughtOptionPicker();
        initDisPurposeOptionPicker();
        findViewById(R.id.clear_cache_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(SettingMineInfoActivity.this).clearMemory();
                    }
                });
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(SettingMineInfoActivity.this).clearDiskCache();

                    }
                }).start();

                cleanInternalCache(SettingMineInfoActivity.this);
                cleanExternalCache(SettingMineInfoActivity.this);
                ToastUtil.showToast(SettingMineInfoActivity.this, "清除完成！");


            }
        });
        findViewById(R.id.modifyPSWlayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingMineInfoActivity.this, ResetPasswordActivity.class));
            }
        });
        arealocation_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoaded) {
                    ShowPickerView();
                } else {
                    ToastUtil.showToast(SettingMineInfoActivity.this, "请等待省市区数据解析完成！");
                }
            }
        });

        findViewById(R.id.sex_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sexOptions.show();
            }
        });

        disPurpose_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disPurposeOption.show();
            }
        });

        datingThought_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datingThoughtOptions.show();
            }
        });

        candateThing_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                candateThingOptions.show();
            }
        });
        age_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ageOptions.show();
            }
        });

        openSecret_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (rootUser != null) {
                    mLockIndicator.setAnswer(new int[]{});
                    if (isChecked) {
                        if (!rootUser.secretScan) {
                            mLockViewGroup.setAnswer(null);
                            secretView.setVisibility(View.VISIBLE);
                            secretView.setAnimation(AnimationUtils.makeInAnimation(SettingMineInfoActivity.this, true));
                        }
                    } else {
                        if (secretView.getVisibility() == View.VISIBLE) {
                            secretView.setVisibility(View.INVISIBLE);
                            secretView.setAnimation(AnimationUtils.makeOutAnimation(SettingMineInfoActivity.this, true));
                        }
                        updateUser.secretScan = false;
                        updateUser.secretPassword = "";
                        updateUser.update(rootUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    CommonUtil.fecth(SettingMineInfoActivity.this);
                                    ToastUtil.showToast(SettingMineInfoActivity.this, "私密访问已关闭");
                                    rootUser = BmobUser.getCurrentUser(RootUser.class);
                                } else {
                                    if (e.getErrorCode() == 206) {
                                        FragmentDialog.newInstance(false, "权限确认", "缓存即将过期，请退出重新登录后修改", "去登录", "取消修改", "", "", false, new FragmentDialog.OnClickBottomListener() {
                                            @Override
                                            public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                                rootUser.logOut();
                                                dialog.dismiss();
                                                SettingMineInfoActivity.this.finish();
                                                startActivity(new Intent(SettingMineInfoActivity.this, LoginActivity.class));
                                            }

                                            @Override
                                            public void onNegtiveClick(Dialog dialog) {
                                                dialog.dismiss();
                                            }
                                        }).show(getSupportFragmentManager(), "");
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });


    }

    /**
     * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
     *
     * @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }


    /**
     * * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * *
     *
     * @param directory
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    private void initAgeOptionPicker() {
        ageOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                age_txt.setText("您的年龄是" + ageItems.get(options1).getPickerViewText() + "岁");
                if (rootUser != null) {
                    updateUser.age = ageItems.get(options1).getPickerViewText() + "";
                    updateUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                CommonUtil.fecth(SettingMineInfoActivity.this);
                                ToastUtil.showToast(SettingMineInfoActivity.this, "年龄更新成功");
                            } else {
                                if (e.getErrorCode() == 206) {
                                    FragmentDialog.newInstance(false, "权限确认", "缓存已过期，请退出重新登录后修改", "去登录", "取消修改", "", "", false, new FragmentDialog.OnClickBottomListener() {
                                        @Override
                                        public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                            rootUser.logOut();
                                            dialog.dismiss();
                                            SettingMineInfoActivity.this.finish();
                                            startActivity(new Intent(SettingMineInfoActivity.this, LoginActivity.class));
                                        }

                                        @Override
                                        public void onNegtiveClick(Dialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show(getSupportFragmentManager(), "");
                                }
                            }
                        }
                    });
                }
            }
        }).setDividerColor(Color.BLACK).setTitleText("年龄选择").setTextColorCenter(Color.BLACK).setContentTextSize(22).setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                TextView ivCancel = (TextView) v.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ageOptions.returnData();
                        ageOptions.dismiss();
                    }
                });
                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ageOptions.dismiss();
                    }
                });
            }
        }).isDialog(true).build();
        ageOptions.setPicker(ageItems);
    }

    private void initCanDatingThingOptionPicker() {
        candateThingOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                candateThing_txt.setText("您可以" + candateThingiItems.get(options1).getPickerViewText());
                if (rootUser != null) {
                    updateUser.canDateThing = candateThingiItems.get(options1).getPickerViewText() + "";
                    updateUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                CommonUtil.fecth(SettingMineInfoActivity.this);
                                ToastUtil.showToast(SettingMineInfoActivity.this, "是否可约更新成功");
                            } else {
                                if (e.getErrorCode() == 206) {
                                    FragmentDialog.newInstance(false, "权限确认", "缓存已过期，请退出重新登录后修改", "去登录", "取消修改", "", "", false, new FragmentDialog.OnClickBottomListener() {
                                        @Override
                                        public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                            rootUser.logOut();
                                            dialog.dismiss();
                                            SettingMineInfoActivity.this.finish();
                                            startActivity(new Intent(SettingMineInfoActivity.this, LoginActivity.class));
                                        }

                                        @Override
                                        public void onNegtiveClick(Dialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show(getSupportFragmentManager(), "");
                                }
                            }
                        }
                    });
                }
            }
        }).setDividerColor(Color.BLACK).setTitleText("年龄选择").setTextColorCenter(Color.BLACK).setContentTextSize(22).setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                TextView ivCancel = (TextView) v.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        candateThingOptions.returnData();
                        candateThingOptions.dismiss();
                    }
                });
                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        candateThingOptions.dismiss();
                    }
                });
            }
        }).isDialog(true).build();
        candateThingOptions.setPicker(candateThingiItems);
    }

    private void initSexOptionPicker() {
        sexOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                sex_txt.setText("您的性别是" + sexItems.get(options1).getPickerViewText() + "性");

                if (rootUser != null) {
                    updateUser.sex = sexItems.get(options1).getPickerViewText() + "";
                    updateUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                sex_txt.setTextColor(Color.LTGRAY);
                                findViewById(R.id.sex_rl).setVisibility(View.GONE);
                                CommonUtil.fecth(SettingMineInfoActivity.this);
                                ToastUtil.showToast(SettingMineInfoActivity.this, rootUser.sex + "性更新成功");
                            } else {
                                if (e.getErrorCode() == 206) {
                                    FragmentDialog.newInstance(false, "权限确认", "缓存已过期，请退出重新登录后修改", "去登录", "取消修改", "", "", false, new FragmentDialog.OnClickBottomListener() {
                                        @Override
                                        public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                            rootUser.logOut();
                                            dialog.dismiss();
                                            SettingMineInfoActivity.this.finish();
                                            startActivity(new Intent(SettingMineInfoActivity.this, LoginActivity.class));
                                        }

                                        @Override
                                        public void onNegtiveClick(Dialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show(getSupportFragmentManager(), "");
                                }
                            }
                        }
                    });
                }
            }
        }).setDividerColor(Color.BLACK).setTitleText("性别选择").setTextColorCenter(Color.BLACK).setContentTextSize(22).setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                TextView ivCancel = (TextView) v.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sexOptions.returnData();
                        sexOptions.dismiss();
                    }
                });
                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sexOptions.dismiss();
                    }
                });
            }
        }).isDialog(true).build();
        sexOptions.setPicker(sexItems);
    }


    private void initDatingThoughtOptionPicker() {
        datingThoughtOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                datingThought_txt.setText("交友想法是" + datingThoughtItems.get(options1).getPickerViewText());
                if (rootUser != null) {
                    updateUser.datingthought = datingThoughtItems.get(options1).getPickerViewText() + "";
                    updateUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                CommonUtil.fecth(SettingMineInfoActivity.this);
                                ToastUtil.showToast(SettingMineInfoActivity.this, "交友想法更新成功");
                            } else {
                                if (e.getErrorCode() == 206) {
                                    FragmentDialog.newInstance(false, "权限确认", "缓存已过期，请退出重新登录后修改", "去登录", "取消修改", "", "", false, new FragmentDialog.OnClickBottomListener() {
                                        @Override
                                        public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                            rootUser.logOut();
                                            dialog.dismiss();
                                            SettingMineInfoActivity.this.finish();
                                            startActivity(new Intent(SettingMineInfoActivity.this, LoginActivity.class));
                                        }

                                        @Override
                                        public void onNegtiveClick(Dialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show(getSupportFragmentManager(), "");
                                }
                            }
                        }
                    });
                }
            }
        }).setDividerColor(Color.BLACK).setTitleText("交友想法选择").setTextColorCenter(Color.BLACK).setContentTextSize(22).setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                TextView ivCancel = (TextView) v.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datingThoughtOptions.returnData();
                        datingThoughtOptions.dismiss();
                    }
                });
                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datingThoughtOptions.dismiss();
                    }
                });
            }
        }).isDialog(true).build();
        datingThoughtOptions.setPicker(datingThoughtItems);
    }


    private void initDisPurposeOptionPicker() {
        disPurposeOption = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                disPurpose_txt.setText("您的状态是" + disPurposeItems.get(options1).getPickerViewText());
                if (rootUser != null) {
                    updateUser.disPurpose = disPurposeItems.get(options1).getPickerViewText() + "";
                    updateUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                CommonUtil.fecth(SettingMineInfoActivity.this);
                                ToastUtil.showToast(SettingMineInfoActivity.this, "情感状态更新成功");
                            } else {
                                if (e.getErrorCode() == 206) {
                                    FragmentDialog.newInstance(false, "权限确认", "缓存已过期，请退出重新登录后修改", "去登录", "取消修改", "", "", false, new FragmentDialog.OnClickBottomListener() {
                                        @Override
                                        public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                            rootUser.logOut();
                                            dialog.dismiss();
                                            SettingMineInfoActivity.this.finish();
                                            startActivity(new Intent(SettingMineInfoActivity.this, LoginActivity.class));
                                        }

                                        @Override
                                        public void onNegtiveClick(Dialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show(getSupportFragmentManager(), "");
                                }
                            }
                        }
                    });
                }
            }
        }).setDividerColor(Color.BLACK).setTitleText("情感状态选择").setTextColorCenter(Color.BLACK).setContentTextSize(22).setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                TextView ivCancel = (TextView) v.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        disPurposeOption.returnData();
                        disPurposeOption.dismiss();
                    }
                });
                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        disPurposeOption.dismiss();
                    }
                });
            }
        }).isDialog(true).build();
        disPurposeOption.setPicker(disPurposeItems);//添加数据
    }


    private void getDisPurposeData() {
        disPurposeItems.add(new CardBean("已经离异了"));
        disPurposeItems.add(new CardBean("已经结婚了"));
        disPurposeItems.add(new CardBean("刚刚交往中"));
        disPurposeItems.add(new CardBean("正在分手期"));
        disPurposeItems.add(new CardBean("正在热恋期"));
        disPurposeItems.add(new CardBean("未婚单身狗"));
    }


    private void getDatingThoughtData() {
        datingThoughtItems.add(new CardBean("找异性闺蜜"));
        datingThoughtItems.add(new CardBean("认真婚恋"));
        datingThoughtItems.add(new CardBean("极易兴奋"));
        datingThoughtItems.add(new CardBean("来者不拒"));
        datingThoughtItems.add(new CardBean("长期交往"));
        datingThoughtItems.add(new CardBean("短期交往"));
        datingThoughtItems.add(new CardBean("其他"));
    }

    private void getCanDatingThingData() {

        candateThingiItems.add(new CardBean("见面一起做爱做的事"));
        candateThingiItems.add(new CardBean("先在软件里聊天试试"));
        candateThingiItems.add(new CardBean("不想理任何人"));

    }


    private void getAgeData() {
        for (int index = 1; index <= 100; index++) {
            ageItems.add(new CardBean("" + index));
        }
    }

    private void getSexData() {

        sexItems.add(new CardBean("男"));
        sexItems.add(new CardBean("女"));
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;
                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;
                case MSG_LOAD_FAILED:
                    ToastUtil.showToast(SettingMineInfoActivity.this, "省市区数据解析失败，请稍候重试");

                    break;
            }
        }
    };


    private void ShowPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String text = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);
                arealocation_txt.setText("您的地区是" + text);
                if (rootUser != null) {
                    updateUser.provinceAndcity = text;
                    updateUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                ToastUtil.showToast(SettingMineInfoActivity.this, "省市区信息更新成功");
                            } else {
                                if (e.getErrorCode() == 206) {
                                    SettingMineInfoActivity.this.finish();
                                    FragmentDialog.newInstance(false, "权限确认", "缓存已过期，请退出重新登录后修改", "去登录", "取消修改", "", "", false, new FragmentDialog.OnClickBottomListener() {
                                        @Override
                                        public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                            rootUser.logOut();
                                            dialog.dismiss();
                                            startActivity(new Intent(SettingMineInfoActivity.this, LoginActivity.class));
                                        }

                                        @Override
                                        public void onNegtiveClick(Dialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show(getSupportFragmentManager(), "");
                                }
                            }
                        }
                    });
                }
            }
        }).setTitleText("城市选择").setDividerColor(Color.BLACK).setTextColorCenter(Color.BLACK).setContentTextSize(22).build();
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void initJsonData() {
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//获取assets目录下的json文件数据
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体
        options1Items = jsonBean;
        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市
                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表
                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {
                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);
                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }
            options2Items.add(CityList);
            options3Items.add(Province_AreaList);
        }
        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
    }

    public ArrayList<JsonBean> parseData(String result) {
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }


    private void initData() {


        mLockViewGroup.setMaxTryTimes(5);

        mLockViewGroup.setOnLockListener(new LockViewGroup.OnLockListener() {

            @Override
            public void onLockSelected(int id) {

            }

            @Override
            public void onLess4Points() {
                mLockViewGroup.clear2ResetDelay(1200L); //清除错误
                mTvTips.setTextColor(Color.RED);
                mTvTips.setText("至少连接4个点 , 请重新输入");
            }

            @Override
            public void onSaveFirstAnswer(int[] answer) {
                mTvTips.setTextColor(Color.GRAY);
                mTvTips.setText("再次绘制 , 确认解锁图案");
                // 设置给指示器view
                mLockIndicator.setAnswer(answer);

            }

            @Override
            public void onSucessed(int[] answers) {
                mTvTips.setTextColor(Color.BLACK);
                mTvTips.setText("验证成功");
                if (rootUser != null) {
                    updateUser.secretScan = true;
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int answer : answers) {
                        stringBuilder.append(answer).append(";");
                    }
                    updateUser.secretPassword = stringBuilder.toString();
                    updateUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                openSecret_txt.setText("私密访问已开启");
                                CommonUtil.fecth(SettingMineInfoActivity.this);
                                rootUser = BmobUser.getCurrentUser(RootUser.class);
                                ToastUtil.showToast(SettingMineInfoActivity.this, "私密访问开启成功,请牢记密码");
                                secretView.setVisibility(View.INVISIBLE);
                                secretView.setAnimation(AnimationUtils.makeOutAnimation(SettingMineInfoActivity.this, true));
                            } else {
                                openSecret_switch.setChecked(false);
                                if (e.getErrorCode() == 206) {
                                    FragmentDialog.newInstance(false, "权限确认", "缓存已过期，请退出重新登录后修改", "去登录", "取消修改", "", "", false, new FragmentDialog.OnClickBottomListener() {
                                        @Override
                                        public void onPositiveClick(Dialog dialog, boolean needDelete) {
                                            rootUser.logOut();
                                            dialog.dismiss();
                                            startActivity(new Intent(SettingMineInfoActivity.this, LoginActivity.class));
                                        }

                                        @Override
                                        public void onNegtiveClick(Dialog dialog) {
                                            dialog.dismiss();
                                        }
                                    }).show(getSupportFragmentManager(), "");
                                }

                            }
                        }
                    });
                }
                
            }

            @Override
            public void onFailed(int mTryTimes) {
                mLockViewGroup.clear2ResetDelay(1400L); //清除错误
                mLockViewGroup.setHapticFeedbackEnabled(true); //手机振动
                mTvTips.setTextColor(Color.RED);
                mTvTips.setText("与上一次绘制不一致 , 请重新绘制");

                if (mTryTimes > 0) {
                    Toast.makeText(SettingMineInfoActivity.this, "剩余尝试机会: " + mTryTimes + " 次", Toast.LENGTH_SHORT).show();
                } else {
                    ToastUtil.showToast(getApplicationContext(), "设置失败");
                    finish();
                }

                // 左右移动动画
                Animation shakeAnimation = AnimationUtils.loadAnimation(SettingMineInfoActivity.this, R.anim.shake);
                mTvTips.startAnimation(shakeAnimation);

            }

            @Override
            public void onSetAnswerInit() {
                mTvTips.setText("绘制解锁图案");
            }
        });

    }
}
