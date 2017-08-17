package com.act.quzhibo.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.act.quzhibo.R;
import com.act.quzhibo.entity.CardBean;
import com.act.quzhibo.entity.JsonBean;
import com.act.quzhibo.entity.RootUser;
import com.act.quzhibo.view.FragmentSecretDialog;
import com.act.quzhibo.view.TitleBarView;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;

public class SettingMineInfoActivity extends FragmentActivity {
    private OptionsPickerView ageOptions;
    private OptionsPickerView sexOptions;

    private ArrayList<CardBean> ageItems = new ArrayList<>();
    private TextView age_txt;
    private TextView sex_txt;
    private RootUser rootUser = BmobUser.getCurrentUser(RootUser.class);
    private TextView openSecret_txt;
    private Switch openSecret_switch;
    private TextView arealocation_txt;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private boolean isLoaded = false;
    private ArrayList<CardBean> sexItems = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        fecth();
        if (rootUser != null) {
            openSecret_switch.setChecked(rootUser.secretScan);
            sex_txt.setText(TextUtils.isEmpty(rootUser.sex) ? "您的性别未设置" : "您的性别已设置为：" + rootUser.sex);
            if (!TextUtils.isEmpty(rootUser.sex)) {
                sex_txt.setTextColor(Color.LTGRAY);
                findViewById(R.id.sex_rl).setVisibility(View.GONE);
            }
            openSecret_txt.setText(rootUser.secretScan ? "私密访问已开启" : "私密访问未开启");
            arealocation_txt.setText(TextUtils.isEmpty(rootUser.provinceAndcity) ? "省市区未设置" : "你的地址是" + rootUser.provinceAndcity);
            age_txt.setText(TextUtils.isEmpty(rootUser.age) ? "年龄未设置" : "你的年龄已设置为：" + rootUser.age);
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
        arealocation_txt = (TextView) findViewById(R.id.arealocation_txt);
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);


        TitleBarView titlebar = (TitleBarView) findViewById(R.id.titlebar);
        titlebar.setBarTitle("资 料 设 置");
        titlebar.setBackButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingMineInfoActivity.this.finish();
            }
        });

        getSexData();
        initSexOptionPicker();
        getAgeData();
        initAgeOptionPicker();


        if (rootUser != null) {
            openSecret_switch.setChecked(rootUser.secretScan);
            openSecret_txt.setText(rootUser.secretScan ? "私密访问已开启" : "私密访问未开启");
            arealocation_txt.setText(TextUtils.isEmpty(rootUser.provinceAndcity) ? "省市区未设置" : "你的地址是" + rootUser.provinceAndcity);
            age_txt.setText(TextUtils.isEmpty(rootUser.age) ? "年龄未设置" : "你的年龄已设置为：" + rootUser.age);
        }

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
                    Toast.makeText(SettingMineInfoActivity.this, "请等待省市区数据解析完成！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.sex_rl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sexOptions.show();
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
                    if (!rootUser.secretScan && isChecked) {

                        FragmentSecretDialog.newInstance(new FragmentSecretDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick(Dialog dialog, final String secretText) {

                                if (rootUser != null) {
                                    rootUser.secretScan = true;
                                    rootUser.update(rootUser.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                rootUser.secretPassword = secretText;
                                                openSecret_txt.setText("私密访问已开启");
                                                fecth();
                                                Toast.makeText(SettingMineInfoActivity.this, "私密访问开启成功,请牢记密码", Toast.LENGTH_SHORT).show();
                                            } else {
                                                openSecret_switch.setChecked(false);
                                                Toast.makeText(SettingMineInfoActivity.this, "私密访问开启失败，原因是：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegtiveClick(Dialog dialog) {
                                openSecret_switch.setChecked(false);
                                dialog.dismiss();
                            }
                        }).show(getSupportFragmentManager(), "secretDilog");
                    } else {
                        rootUser.secretScan = false;
                        rootUser.update(rootUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    openSecret_switch.setChecked(false);
                                    fecth();
                                } else {
                                    openSecret_switch.setChecked(true);
                                    Toast.makeText(SettingMineInfoActivity.this, "私密访问关闭失败，原因是：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });


    }

    private void fecth() {
        BmobUser.fetchUserInfo(new FetchUserInfoListener<RootUser>() {
            @Override
            public void done(RootUser user, BmobException e) {
                if (e == null) {
                    Toast.makeText(SettingMineInfoActivity.this, "缓存同步成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingMineInfoActivity.this, "缓存同步失败，请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initAgeOptionPicker() {
        ageOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                age_txt.setText("你的年龄已设置为：" + ageItems.get(options1).getPickerViewText() + "岁");
                if (rootUser != null) {
                    rootUser.age = ageItems.get(options1).getPickerViewText() + "";
                    rootUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                fecth();
                                Toast.makeText(SettingMineInfoActivity.this, "年龄更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingMineInfoActivity.this, "年龄更新失败，原因是：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
        ageOptions.setPicker(ageItems);//添加数据
    }

    private void initSexOptionPicker() {
        sexOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                sex_txt.setText("你的性别已设置为：" + sexItems.get(options1).getPickerViewText() + "性");

                if (rootUser != null) {
                    rootUser.sex = sexItems.get(options1).getPickerViewText() + "";
                    rootUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                sex_txt.setTextColor(Color.LTGRAY);
                                findViewById(R.id.sex_rl).setVisibility(View.GONE);
                                fecth();
                                Toast.makeText(SettingMineInfoActivity.this, rootUser.sex + "性更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingMineInfoActivity.this, rootUser.sex + "性更新失败，原因是：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
        sexOptions.setPicker(sexItems);//添加数据
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
                    if (thread == null) {//如果已创建就不再重新创建子线程了
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
                    Toast.makeText(SettingMineInfoActivity.this, "省市区数据解析失败，请稍候重试", Toast.LENGTH_SHORT).show();
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
                arealocation_txt.setText(text);
                if (rootUser != null) {
                    rootUser.provinceAndcity = text;
                    rootUser.update(rootUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(SettingMineInfoActivity.this, "省市区信息更新成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingMineInfoActivity.this, "省市区更新失败，原因是：" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

}
